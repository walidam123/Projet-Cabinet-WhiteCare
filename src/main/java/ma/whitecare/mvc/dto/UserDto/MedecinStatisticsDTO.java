package ma.whitecare.mvc.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinStatisticsDTO {
    private Long totalMedecins;
    private Long totalActifs;
    private Long totalInactifs;
    private Map<String, Long> medecinsParSpecialite;
    private Map<Integer, Long> medecinsParAnneeExperience;
    private Double moyenneSalaire;
    private Double totalMasseSalariale;
    private Map<Long, Long> medecinsParCabinet;
}