package org.mryrt.airbnb.booking.access;


public interface BookingAccessService {

    void requireCanRead(Long bookingId);

    void requireCanUpdate(Long bookingId);

    void requireCanCheckInCheckOut(Long bookingId);

    void requireCanDecide(Long bookingId);

    Long getScopeUserIdForList();
}
