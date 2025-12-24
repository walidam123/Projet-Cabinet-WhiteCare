package ma.whitecare.mvc.dto.SimulationFinanciereDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSimulationFinanciereDTO {
    private Double totaleDesActes;
    private Double totalePaye;
    private Double credit;
    private StatutSituationFinanciere statut;
    private EnPromo enPromo;
    private Long dossierMedicaleId;
    private String modifiePar;
}
