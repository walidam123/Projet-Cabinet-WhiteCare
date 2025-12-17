package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.StatutConsultation;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {

    private Long idConsultation;
    private LocalDate date;
    private StatutConsultation statut;
    private String observationMedecin;
    private Long dossierMedicalId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String createdBy;
    private String updatedBy;
}

