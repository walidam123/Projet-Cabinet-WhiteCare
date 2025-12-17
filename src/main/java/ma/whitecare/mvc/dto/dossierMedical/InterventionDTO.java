package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterventionDTO {

    private Long idIM;
    private Double prixDePatient;
    private Integer numDent;
    private Long acteId;
    private Long consultationId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String createdBy;
    private String updatedBy;
}

