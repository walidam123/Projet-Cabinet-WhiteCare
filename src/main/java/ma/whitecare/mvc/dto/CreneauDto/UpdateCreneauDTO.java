package ma.whitecare.mvc.dto.CreneauDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCreneauDTO {
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Boolean estDisponible;
    private String motifIndisponibilite;
    private Long rendezVousId;
    private String modifiePar;
}
