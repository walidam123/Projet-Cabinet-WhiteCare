package ma.whitecare.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretaireStatisticsDTO {
    private Long totalSecretaires;
    private Long totalActifs;
    private Long totalInactifs;
    private Long avecCommission;
    private Long sansCommission;
    private Double commissionMoyenne;
    private Double commissionTotale;
    private Map<String, Long> secretairesParNiveauEtude;
    private Map<Long, Long> secretairesParCabinet;
}
