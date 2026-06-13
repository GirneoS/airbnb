package org.mryrt.airbnb.resolution.dto;

import javax.annotation.processing.Generated;
import org.mryrt.airbnb.resolution.model.ResolutionWindow;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-13T11:58:34+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class ResolutionMapperImpl implements ResolutionMapper {

    @Override
    public ResolutionDto toDto(ResolutionWindow window) {
        if ( window == null ) {
            return null;
        }

        ResolutionDto.ResolutionDtoBuilder resolutionDto = ResolutionDto.builder();

        resolutionDto.createdAt( window.getCreatedDate() );
        resolutionDto.updatedAt( window.getLastModifiedDate() );
        resolutionDto.id( window.getId() );
        resolutionDto.bookingId( window.getBookingId() );
        resolutionDto.status( window.getStatus() );
        resolutionDto.openedAt( window.getOpenedAt() );
        resolutionDto.closedAt( window.getClosedAt() );
        resolutionDto.moneyRequestedAt( window.getMoneyRequestedAt() );
        resolutionDto.refusedAt( window.getRefusedAt() );
        resolutionDto.amountRequested( window.getAmountRequested() );
        resolutionDto.complaintDescription( window.getComplaintDescription() );
        resolutionDto.version( window.getVersion() );

        return resolutionDto.build();
    }
}
