package org.mryrt.airbnb.bitrix.ra.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.resource.NotSupportedException;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEvent;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;
import org.mryrt.airbnb.bitrix.ra.api.BitrixDealRequest;
import org.mryrt.airbnb.bitrix.ra.api.BitrixDealUpdate;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BitrixManagedConnection implements ManagedConnection {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private final BitrixManagedConnectionFactory managedConnectionFactory;
    private final String webhookUrl;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();
    private final List<ConnectionEventListener> listeners = new ArrayList<>();
    private final Set<BitrixConnectionImpl> handles = new HashSet<>();
    private PrintWriter logWriter;

    BitrixManagedConnection(BitrixManagedConnectionFactory managedConnectionFactory, String webhookUrl) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo requestInfo) {
        BitrixConnectionImpl handle = new BitrixConnectionImpl(this);
        handles.add(handle);
        return handle;
    }

    @Override
    public void destroy() {
        cleanup();
        listeners.clear();
    }

    @Override
    public void cleanup() {
        new HashSet<>(handles).forEach(BitrixConnectionImpl::invalidate);
        handles.clear();
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof BitrixConnectionImpl handle)) {
            throw new ResourceException("Unsupported Bitrix24 connection handle: " + connection);
        }
        handle.associate(this);
        handles.add(handle);
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) { listeners.add(listener); }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) { listeners.remove(listener); }

    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException("Bitrix24 adapter does not support XA transactions");
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("Bitrix24 adapter does not support local transactions");
    }

    @Override
    public ManagedConnectionMetaData getMetaData() {
        return new ManagedConnectionMetaData() {
            public String getEISProductName() { return "Bitrix24 CRM"; }
            public String getEISProductVersion() { return "REST API"; }
            public int getMaxConnections() { return 0; }
            public String getUserName() { return "webhook-user"; }
        };
    }

    @Override
    public PrintWriter getLogWriter() { return logWriter; }

    @Override
    public void setLogWriter(PrintWriter logWriter) { this.logWriter = logWriter; }

    long createDeal(BitrixDealRequest request) throws ResourceException {
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResourceException("Bitrix24 deal title is required");
        }
        JsonObject fields = new JsonObject();
        fields.addProperty("TITLE", request.getTitle());
        addIfPresent(fields, "STAGE_ID", request.getStageId());
        addIfPresent(fields, "COMMENTS", request.getComments());
        if (request.getOpportunity() != null) {
            fields.addProperty("OPPORTUNITY", request.getOpportunity());
            addIfPresent(fields, "CURRENCY_ID", request.getCurrencyId());
        }

        JsonObject body = new JsonObject();
        body.add("FIELDS", fields);
        JsonObject response = invoke("crm.deal.add.json", body);
        if (!response.has("result") || !response.get("result").isJsonPrimitive()) {
            throw new ResourceException("Bitrix24 did not return a deal ID");
        }
        return response.get("result").getAsLong();
    }

    void updateDeal(long dealId, BitrixDealUpdate request) throws ResourceException {
        if (dealId <= 0) {
            throw new ResourceException("Bitrix24 deal ID must be positive");
        }
        if (request == null) {
            throw new ResourceException("Bitrix24 deal update is required");
        }
        JsonObject fields = new JsonObject();
        addIfPresent(fields, "STAGE_ID", request.getStageId());
        addIfPresent(fields, "COMMENTS", request.getComments());

        JsonObject body = new JsonObject();
        body.addProperty("ID", dealId);
        body.add("FIELDS", fields);
        invoke("crm.deal.update.json", body);
    }

    void closeHandle(BitrixConnectionImpl handle) {
        handles.remove(handle);
        ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        event.setConnectionHandle(handle);
        new ArrayList<>(listeners).forEach(listener -> listener.connectionClosed(event));
    }

    BitrixManagedConnectionFactory getManagedConnectionFactory() {
        return managedConnectionFactory;
    }

    private JsonObject invoke(String method, JsonObject body) throws ResourceException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(webhookUrl + method))
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = parseResponse(response.body());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResourceException("Bitrix24 HTTP error " + response.statusCode() + ": " + errorMessage(json));
            }
            if (json.has("error")) {
                throw new ResourceException("Bitrix24 API error: " + errorMessage(json));
            }
            return json;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResourceException("Bitrix24 request was interrupted", exception);
        } catch (ResourceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResourceException("Bitrix24 request failed", exception);
        }
    }

    private JsonObject parseResponse(String body) throws ResourceException {
        try {
            return JsonParser.parseString(body).getAsJsonObject();
        } catch (Exception exception) {
            throw new ResourceException("Bitrix24 returned malformed JSON", exception);
        }
    }

    private String errorMessage(JsonObject response) {
        if (response.has("error_description")) {
            return response.get("error_description").getAsString();
        }
        if (response.has("error")) {
            return response.get("error").getAsString();
        }
        return "unknown error";
    }

    private void addIfPresent(JsonObject object, String name, String value) {
        if (value != null && !value.isBlank()) {
            object.addProperty(name, value);
        }
    }
}
