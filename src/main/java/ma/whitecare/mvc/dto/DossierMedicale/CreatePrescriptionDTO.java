package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrescriptionDTO {

    @NotNull(message = "L'ID de l'ordonnance est obligatoire")
    private Long ordonnanceId;

    @NotNull(message = "L'ID du médicament est obligatoire")
    private Long medicamentId;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;

    @NotBlank(message = "La fréquence est obligatoire")
    private String frequence;

    @NotNull(message = "La durée en jours est obligatoire")
    @Min(value = 1, message = "La durée en jours doit être au moins 1")
    private Integer dureeEnJours;
}