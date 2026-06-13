package org.mryrt.airbnb.listing.dto.request;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
=======
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
>>>>>>> b511a51 (done)
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<<<<<<< HEAD
=======
import java.math.BigDecimal;

>>>>>>> b511a51 (done)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateListingRequest {

    @NotBlank(message = "Listing.title не может быть пустым")
    @Size(max = 500)
    private String title;

    @Size(max = 2000)
    private String description;
<<<<<<< HEAD
=======

    @NotNull(message = "Listing.pricePerNight не может быть пустым")
    @DecimalMin(value = "0.01", message = "Listing.pricePerNight должна быть больше нуля")
    @Digits(integer = 10, fraction = 2, message = "Listing.pricePerNight должна содержать не более 10 целых и 2 дробных цифр")
    private BigDecimal pricePerNight;
>>>>>>> b511a51 (done)
}
