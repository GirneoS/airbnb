package org.mryrt.airbnb.listing.dto;

import javax.annotation.processing.Generated;
import org.mryrt.airbnb.listing.model.Listing;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
<<<<<<< HEAD
    date = "2026-06-13T11:58:34+0300",
=======
    date = "2026-09-07T00:44:44+0300",
>>>>>>> b511a51 (done)
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class ListingMapperImpl implements ListingMapper {

    @Override
    public ListingDto toDto(Listing listing) {
        if ( listing == null ) {
            return null;
        }

        ListingDto.ListingDtoBuilder listingDto = ListingDto.builder();

        listingDto.createdAt( listing.getCreatedDate() );
        listingDto.updatedAt( listing.getLastModifiedDate() );
        listingDto.id( listing.getId() );
        listingDto.ownerId( listing.getOwnerId() );
        listingDto.title( listing.getTitle() );
        listingDto.description( listing.getDescription() );
<<<<<<< HEAD
=======
        listingDto.pricePerNight( listing.getPricePerNight() );
>>>>>>> b511a51 (done)
        listingDto.status( listing.getStatus() );
        listingDto.version( listing.getVersion() );

        return listingDto.build();
    }
}
