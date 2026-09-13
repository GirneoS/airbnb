package org.mryrt.airbnb.booking.repository;

import org.mryrt.airbnb.booking.model.Booking;
import org.mryrt.airbnb.booking.model.BookingStatus;
import org.mryrt.airbnb.booking.repository.filter.BookingFilter;
import org.mryrt.airbnb.booking.repository.specifications.BookingSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.guestId = :guestId AND b.listingId = :listingId AND b.status IN :statuses " +
            "AND b.checkInDate IS NOT NULL AND b.checkOutDate IS NOT NULL " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    boolean existsOverlappingByGuestAndListing(@Param("guestId") Long guestId, @Param("listingId") Long listingId,
                                                @Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate,
                                                @Param("statuses") List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.listingId = :listingId AND b.status = org.mryrt.airbnb.booking.model.BookingStatus.APPLIED " +
            "AND b.id <> :excludeId AND b.checkInDate IS NOT NULL AND b.checkOutDate IS NOT NULL " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingAppliedByListingExcluding(@Param("listingId") Long listingId,
                                                           @Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate,
                                                           @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.listingId = :listingId AND b.id <> :excludeId " +
            "AND b.status IN (org.mryrt.airbnb.booking.model.BookingStatus.APPROVED, org.mryrt.airbnb.booking.model.BookingStatus.CHECKED_IN) " +
            "AND b.checkInDate IS NOT NULL AND b.checkOutDate IS NOT NULL " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    boolean existsOverlappingApprovedOrCheckedIn(@Param("listingId") Long listingId,
                                                 @Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate,
                                                 @Param("excludeId") Long excludeId);

    default Page<Booking> findWithFilter(BookingFilter filter, Pageable pageable, Long scopeUserId) {
        Specification<Booking> spec = BookingSpecifications.withFilter(filter, scopeUserId);
        return findAll(spec, pageable);
    }

    @Query("SELECT b.id FROM Booking b, Listing l WHERE b.listingId = l.id AND (b.guestId = :userId OR l.ownerId = :userId)")
    List<Long> findBookingIdsByGuestOrOwner(@Param("userId") Long userId);
}
