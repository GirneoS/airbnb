package org.mryrt.airbnb.bitrix.service;

import org.mryrt.airbnb.auth.model.User;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnection;
import org.mryrt.airbnb.bitrix.ra.api.BitrixConnectionFactory;
import org.mryrt.airbnb.bitrix.ra.api.BitrixDealRequest;
import org.mryrt.airbnb.bitrix.ra.api.BitrixDealUpdate;
import org.mryrt.airbnb.booking.model.Booking;
import org.mryrt.airbnb.booking.model.BookingStatus;
import org.mryrt.airbnb.listing.model.Listing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class BitrixServiceImpl implements BitrixService {

    @Value("${airbnb.bitrix.jndi:java:/eis/Bitrix24}")
    private String bitrixJndi;

    @Override
    public long createBookingDeal(Booking booking, Listing listing, User guest) throws Exception {
        BitrixDealRequest request = new BitrixDealRequest(
                "Airbnb booking #" + booking.getId() + " — " + listing.getTitle(),
                stageFor(booking.getStatus()),
                buildComments(booking, listing, guest),
                booking.getTotalPrice(),
                "RUB"
        );
        BitrixConnectionFactory factory = lookupFactory();
        try (BitrixConnection connection = factory.getConnection()) {
            return connection.createDeal(request);
        }
    }

    @Override
    public void updateBookingDeal(Booking booking) throws Exception {
        if (booking.getBitrixDealId() == null) {
            return;
        }
        BitrixConnectionFactory factory = lookupFactory();
        try (BitrixConnection connection = factory.getConnection()) {
            connection.updateDeal(
                    booking.getBitrixDealId(),
                    new BitrixDealUpdate(stageFor(booking.getStatus()), null)
            );
        }
    }

    private BitrixConnectionFactory lookupFactory() throws Exception {
        return new JndiTemplate().lookup(bitrixJndi, BitrixConnectionFactory.class);
    }

    private String stageFor(BookingStatus status) {
        return switch (status) {
            case APPLIED -> "NEW";
            case APPROVED -> "PREPARATION";
            case REJECTED -> "LOSE";
            case CHECKED_IN -> "EXECUTING";
            case CHECKED_OUT -> "WON";
        };
    }

    private String buildComments(Booking booking, Listing listing, User guest) {
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return "Airbnb listing #" + listing.getId()
                + "\nGuest: " + guest.getUsername() + " (" + guest.getEmail() + ")"
                + "\nCheck-in: " + booking.getCheckInDate()
                + "\nCheck-out: " + booking.getCheckOutDate()
                + "\nNights: " + nights
                + "\nPrice per night: " + listing.getPricePerNight() + " RUB"
                + "\nTotal price: " + booking.getTotalPrice() + " RUB";
    }
}
