package org.mryrt.airbnb.bitrix.service;

import org.mryrt.airbnb.auth.model.User;
import org.mryrt.airbnb.booking.model.Booking;
import org.mryrt.airbnb.listing.model.Listing;

public interface BitrixService {

    long createBookingDeal(Booking booking, Listing listing, User guest) throws Exception;

    void updateBookingDeal(Booking booking) throws Exception;
}
