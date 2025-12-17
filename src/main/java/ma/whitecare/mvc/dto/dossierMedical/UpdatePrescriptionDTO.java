package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrescriptionDTO {

    private Long ordonnanceId;
    private Long medicamentId;
    
    @Positive(message = "La quantité doit être positive")
    private Integer quantité;
    
    private String fréquence;
    
    @Min(value = 1, message = "La durée en jours doit être au moins 1")
    private Integer duréeEnJours;
}

