package org.mryrt.airbnb.payment.ra.impl;

import jakarta.resource.NotSupportedException;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEvent;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PaymentManagedConnection implements ManagedConnection {

    private final PaymentManagedConnectionFactory mcf;
    private final List<ConnectionEventListener> listeners = new ArrayList<>();
    private PaymentConnectionImpl handle;
    private PrintWriter logWriter;

    public PaymentManagedConnection(PaymentManagedConnectionFactory mcf) {
        this.mcf = mcf;
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cri) throws ResourceException {
        handle = new PaymentConnectionImpl(this);
        return handle;
    }

    @Override
    public void destroy() {}

    @Override
    public void cleanup() { handle = null; }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (connection instanceof PaymentConnectionImpl c) {
            c.setManagedConnection(this);
            handle = c;
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) { listeners.add(listener); }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) { listeners.remove(listener); }

    @Override
    public XAResource getXAResource() throws ResourceException { throw new NotSupportedException("XA not supported"); }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException { throw new NotSupportedException("Local transactions not supported"); }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new ManagedConnectionMetaData() {
            public String getEISProductName() { return "YooKassa Payment Gateway"; }
            public String getEISProductVersion() { return "1.0"; }
            public int getMaxConnections() { return 0; }
            public String getUserName() { return ""; }
        };
    }

    @Override
    public PrintWriter getLogWriter() { return logWriter; }

    @Override
    public void setLogWriter(PrintWriter out) { logWriter = out; }

    void closeHandle(PaymentConnectionImpl closedHandle) {
        ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        event.setConnectionHandle(closedHandle);
        new ArrayList<>(listeners).forEach(l -> l.connectionClosed(event));
    }

    PaymentManagedConnectionFactory getMcf() { return mcf; }
}
