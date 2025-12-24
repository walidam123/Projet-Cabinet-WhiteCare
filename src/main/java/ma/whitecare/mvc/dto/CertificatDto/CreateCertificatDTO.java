package ma.whitecare.mvc.dto.CertificatDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCertificatDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer duree;
    private String noteMedecin;
    private Long dossierMedicaleId;
    private Long consultationId;
    private String creePar;
    private String modifiePar;
}
