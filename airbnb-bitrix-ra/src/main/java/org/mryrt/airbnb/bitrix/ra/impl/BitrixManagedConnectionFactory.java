package org.mryrt.airbnb.bitrix.ra.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConfigProperty;
import jakarta.resource.spi.ConnectionDefinition;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnection;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnectionFactory;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@ConnectionDefinition(
        connectionFactory = BitrixConnectionFactory.class,
        connectionFactoryImpl = BitrixConnectionFactoryImpl.class,
        connection = BitrixConnection.class,
        connectionImpl = BitrixConnectionImpl.class
)
public class BitrixManagedConnectionFactory implements ManagedConnectionFactory, Serializable {

    @ConfigProperty(description = "Base URL of a Bitrix24 incoming webhook")
    private String webhookUrl;

    private transient PrintWriter logWriter;

    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager) {
        return new BitrixConnectionFactoryImpl(this, connectionManager);
    }

    @Override
    public Object createConnectionFactory() {
        return new BitrixConnectionFactoryImpl(this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo requestInfo)
            throws ResourceException {
        validateWebhookUrl();
        return new BitrixManagedConnection(this, normalizedWebhookUrl());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ManagedConnection matchManagedConnections(
            Set connectionSet,
            Subject subject,
            ConnectionRequestInfo requestInfo
    ) {
        if (connectionSet == null) {
            return null;
        }
        for (Object candidate : connectionSet) {
            if (candidate instanceof BitrixManagedConnection connection
                    && equals(connection.getManagedConnectionFactory())) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public PrintWriter getLogWriter() { return logWriter; }

    @Override
    public void setLogWriter(PrintWriter logWriter) { this.logWriter = logWriter; }

    public String getWebhookUrl() { return webhookUrl; }

    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    @Override
    public boolean equals(Object other) {
        return other instanceof BitrixManagedConnectionFactory factory
                && Objects.equals(normalizedWebhookUrl(), factory.normalizedWebhookUrl());
    }

    @Override
    public int hashCode() { return Objects.hash(normalizedWebhookUrl()); }

    private void validateWebhookUrl() throws ResourceException {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new ResourceException("Bitrix24 webhookUrl is not configured");
        }
        if (!webhookUrl.startsWith("https://") && !webhookUrl.startsWith("http://")) {
            throw new ResourceException("Bitrix24 webhookUrl must use HTTP or HTTPS");
        }
    }

    private String normalizedWebhookUrl() {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return webhookUrl;
        }
        return webhookUrl.endsWith("/") ? webhookUrl : webhookUrl + "/";
    }
}
