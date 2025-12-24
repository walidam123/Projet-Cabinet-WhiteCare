package ma.whitecare.mvc.dto.SimulationFinanciereDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationFinanciereDTO {
    private Long idSF;
    private Double totaleDesActes;
    private Double totalePaye;
    private Double credit;
    private Double reste;
    private StatutSituationFinanciere statut;
    private EnPromo enPromo;
    private Long dossierMedicaleId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String creePar;
    private String modifiePar;
}
