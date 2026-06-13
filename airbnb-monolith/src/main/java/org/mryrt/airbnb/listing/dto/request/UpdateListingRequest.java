package org.mryrt.airbnb.listing.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mryrt.airbnb.listing.model.ListingStatus;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateListingRequest {

    @Size(max = 500)
    private String title;

    @Size(max = 2000)
    private String description;

    @DecimalMin(value = "0.01", message = "Listing.pricePerNight должна быть больше нуля")
    @Digits(integer = 10, fraction = 2, message = "Listing.pricePerNight должна содержать не более 10 целых и 2 дробных цифр")
    private BigDecimal pricePerNight;

    private ListingStatus status;
}
