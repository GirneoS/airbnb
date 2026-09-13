package org.mryrt.airbnb.bitrix.ra.impl;

import jakarta.resource.Referenceable;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ManagedConnection;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnection;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnectionFactory;

import javax.naming.Reference;

public class BitrixConnectionFactoryImpl implements BitrixConnectionFactory, Referenceable {

    private final BitrixManagedConnectionFactory managedConnectionFactory;
    private final ConnectionManager connectionManager;
    private Reference reference;

    public BitrixConnectionFactoryImpl(
            BitrixManagedConnectionFactory managedConnectionFactory,
            ConnectionManager connectionManager
    ) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.connectionManager = connectionManager;
    }

    @Override
    public BitrixConnection getConnection() throws ResourceException {
        if (connectionManager != null) {
            return (BitrixConnection) connectionManager.allocateConnection(managedConnectionFactory, null);
        }
        ManagedConnection connection = managedConnectionFactory.createManagedConnection(null, null);
        return (BitrixConnection) connection.getConnection(null, null);
    }

    @Override
    public Reference getReference() {
        return reference;
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }
}
