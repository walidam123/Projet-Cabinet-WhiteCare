package ma.whitecare.mvc.dto.DossierMedicale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DossierMedicalDTO {

    private Long idDM;
    private LocalDate dateDeCreation;
    private Long patientId;
    private Long medecinId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String createdBy;
    private String updatedBy;
}