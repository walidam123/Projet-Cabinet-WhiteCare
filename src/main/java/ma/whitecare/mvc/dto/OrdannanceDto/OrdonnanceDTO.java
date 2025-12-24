package ma.whitecare.mvc.dto.OrdannanceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceDTO {
    private Long idOrd;
    private LocalDate date;
    private Long dossierMedicaleId;
    private Long consultationId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String creePar;
    private String modifiePar;
}