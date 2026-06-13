package org.mryrt.airbnb.booking.dto;

import javax.annotation.processing.Generated;
import org.mryrt.airbnb.booking.model.Booking;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-13T11:58:34+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingDto toDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDto.BookingDtoBuilder bookingDto = BookingDto.builder();

        bookingDto.createdAt( booking.getCreatedDate() );
        bookingDto.updatedAt( booking.getLastModifiedDate() );
        bookingDto.id( booking.getId() );
        bookingDto.listingId( booking.getListingId() );
        bookingDto.guestId( booking.getGuestId() );
        bookingDto.status( booking.getStatus() );
        bookingDto.checkInDate( booking.getCheckInDate() );
        bookingDto.checkOutDate( booking.getCheckOutDate() );
        bookingDto.appliedAt( booking.getAppliedAt() );
        bookingDto.checkInAt( booking.getCheckInAt() );
        bookingDto.checkOutAt( booking.getCheckOutAt() );
        bookingDto.version( booking.getVersion() );

        return bookingDto.build();
    }
}
