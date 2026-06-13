package org.mryrt.airbnb.bitrix.ra.api;

import jakarta.resource.ResourceException;

public interface BitrixConnection extends AutoCloseable {

    long createDeal(BitrixDealRequest request) throws ResourceException;

    void updateDeal(long dealId, BitrixDealUpdate request) throws ResourceException;

    @Override
    void close();
}
