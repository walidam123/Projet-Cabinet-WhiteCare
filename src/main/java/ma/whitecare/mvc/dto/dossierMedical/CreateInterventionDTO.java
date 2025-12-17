package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterventionDTO {

    @NotNull(message = "L'ID de la consultation est obligatoire")
    private Long consultationId;

    @NotNull(message = "L'ID de l'acte est obligatoire")
    private Long acteId;

    @NotNull(message = "Le prix est obligatoire")
    @PositiveOrZero(message = "Le prix doit être positif ou nul")
    private Double prixDePatient;

    @Min(value = 1, message = "Le numéro de dent doit être entre 1 et 32")
    @Max(value = 32, message = "Le numéro de dent doit être entre 1 et 32")
    private Integer numDent;
}

