package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.StatutConsultation;
import ma.whitecare.mvc.dto.DossierMedicale.InterventionDTO;
import ma.whitecare.mvc.dto.DossierMedicale.PrescriptionDTO;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationCompleteDTO {

    private Long idConsultation;
    private LocalDate date;
    private StatutConsultation statut;
    private String observationMedecin;
    private Long dossierMedicalId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String createdBy;
    private String updatedBy;

    // Nested DTOs for related entities
    private List<InterventionDTO> interventions;
    private List<PrescriptionDTO> prescriptions;
}