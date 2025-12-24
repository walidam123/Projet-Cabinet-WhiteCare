package ma.whitecare.mvc.dto.DossierMedicale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInterventionDTO {

    private Long consultationId;
    private Long acteId;

    @PositiveOrZero(message = "Le prix doit être positif ou nul")
    private Double prixDePatient;

    @Min(value = 1, message = "Le numéro de dent doit être entre 1 et 32")
    @Max(value = 32, message = "Le numéro de dent doit être entre 1 et 32")
    private Integer numDent;
}