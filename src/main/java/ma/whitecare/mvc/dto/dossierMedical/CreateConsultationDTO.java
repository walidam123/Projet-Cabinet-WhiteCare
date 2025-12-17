package ma.whitecare.mvc.dto.dossierMedical;

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

    private LocalDate date;
    private StatutConsultation statut;
    private String observationMedecin;
}

