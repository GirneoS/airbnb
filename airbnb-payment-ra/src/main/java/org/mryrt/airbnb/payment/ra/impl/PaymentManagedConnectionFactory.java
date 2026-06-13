package org.mryrt.airbnb.payment.ra.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConfigProperty;
import jakarta.resource.spi.ConnectionDefinition;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import org.mryrt.airbnb.payment.ra.api.PaymentConnection;
import org.mryrt.airbnb.payment.ra.api.PaymentConnectionFactory;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;

@ConnectionDefinition(
        connectionFactory = PaymentConnectionFactory.class,
        connectionFactoryImpl = PaymentConnectionFactoryImpl.class,
        connection = PaymentConnection.class,
        connectionImpl = PaymentConnectionImpl.class
)
public class PaymentManagedConnectionFactory implements ManagedConnectionFactory, Serializable {

    @ConfigProperty(defaultValue = "0", description = "YooKassa shop ID (0 = mock mode)")
    private Integer shopId = 0;

    @ConfigProperty(defaultValue = "", description = "YooKassa secret key")
    private String secretKey = "";

    @ConfigProperty(defaultValue = "true", description = "Use mock payment (no real API calls)")
    private Boolean mockMode = true;

    @ConfigProperty(defaultValue = "http://127.0.0.1:58144", description = "Monolith base URL for mock confirmation link")
    private String mockBaseUrl = "http://127.0.0.1:58144";

    @Override
    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {
        return new PaymentConnectionFactoryImpl(this, cm);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new PaymentConnectionFactoryImpl(this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cri) throws ResourceException {
        return new PaymentManagedConnection(this);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cri) throws ResourceException {
        if (connectionSet == null || connectionSet.isEmpty()) return null;
        return (ManagedConnection) connectionSet.iterator().next();
    }

    @Override
    public PrintWriter getLogWriter() { return null; }

    @Override
    public void setLogWriter(PrintWriter out) {}

    // --- getters / setters required by JCA spec ---

    public Integer getShopId() { return shopId; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public Boolean getMockMode() { return mockMode; }
    public void setMockMode(Boolean mockMode) { this.mockMode = mockMode; }

    public String getMockBaseUrl() { return mockBaseUrl; }
    public void setMockBaseUrl(String mockBaseUrl) { this.mockBaseUrl = mockBaseUrl; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PaymentManagedConnectionFactory other)) return false;
        return shopId.equals(other.shopId)
                && secretKey.equals(other.secretKey)
                && mockMode.equals(other.mockMode);
    }

    @Override
    public int hashCode() {
        return 31 * shopId + secretKey.hashCode() + Boolean.hashCode(mockMode);
    }
}
