package org.mryrt.airbnb.bitrix.ra.impl;

import jakarta.resource.ResourceException;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnection;
import org.mryrt.airbnb.bitrix.ra.api.BitrixDealRequest;
import org.mryrt.airbnb.bitrix.ra.api.BitrixDealUpdate;

public class BitrixConnectionImpl implements BitrixConnection {

    private BitrixManagedConnection managedConnection;

    public BitrixConnectionImpl(BitrixManagedConnection managedConnection) {
        this.managedConnection = managedConnection;
    }

    @Override
    public long createDeal(BitrixDealRequest request) throws ResourceException {
        return requireManagedConnection().createDeal(request);
    }

    @Override
    public void updateDeal(long dealId, BitrixDealUpdate request) throws ResourceException {
        requireManagedConnection().updateDeal(dealId, request);
    }

    @Override
    public void close() {
        if (managedConnection != null) {
            BitrixManagedConnection current = managedConnection;
            managedConnection = null;
            current.closeHandle(this);
        }
    }

    public void associate(BitrixManagedConnection managedConnection) {
        this.managedConnection = managedConnection;
    }

    public void invalidate() {
        managedConnection = null;
    }

    private BitrixManagedConnection requireManagedConnection() throws ResourceException {
        if (managedConnection == null) {
            throw new ResourceException("Bitrix24 connection is closed");
        }
        return managedConnection;
    }
}
