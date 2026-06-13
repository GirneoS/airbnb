package org.mryrt.airbnb.payment.ra.api;

import jakarta.resource.ResourceException;

import java.io.Serializable;

public interface PaymentConnectionFactory extends Serializable {

    PaymentConnection getConnection() throws ResourceException;
}
