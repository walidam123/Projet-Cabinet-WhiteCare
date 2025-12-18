package ma.whitecare.mvc.dto.OrdonnanceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrdonnanceDTO {
    private LocalDate date;
    private Long dossierMedicaleId;
    private Long consultationId;
    private String modifiePar;
}
