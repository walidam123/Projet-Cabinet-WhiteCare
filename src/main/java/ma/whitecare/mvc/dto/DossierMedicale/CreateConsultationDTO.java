package ma.whitecare.mvc.dto.DossierMedicale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.StatutConsultation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultationDTO {

    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long dossierMedicalId;
    private Long medecinId;
    private java.time.LocalDateTime dateConsultation; // Matches usage in controller
    private LocalDate date; // Keeping original date for compatibility or deprecation? Controller uses
                            // dateConsultation
    private StatutConsultation statut;
    private String observationMedecin;
    // Removing TypeConsultation for now as it doesn't exist in enums and user code
    // failed importing it.
    // If controller uses it, I must fix controller to remove it or use existing
    // enums.
}