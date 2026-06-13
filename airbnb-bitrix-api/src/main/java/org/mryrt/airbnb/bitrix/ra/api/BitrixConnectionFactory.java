package org.mryrt.airbnb.bitrix.ra.api;

import jakarta.resource.ResourceException;

import java.io.Serializable;

public interface BitrixConnectionFactory extends Serializable {

    BitrixConnection getConnection() throws ResourceException;
}
