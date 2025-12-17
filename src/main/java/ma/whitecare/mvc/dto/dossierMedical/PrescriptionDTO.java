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
public class PrescriptionDTO {

    private Long idPr;
    private Integer quantité;
    private String fréquence;
    private Integer duréeEnJours;
    private Long medicamentId;
    private Long ordonnanceId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String createdBy;
    private String updatedBy;
}

