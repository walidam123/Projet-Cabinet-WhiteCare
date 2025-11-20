package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.entities.medical.Consultation;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facture extends BaseEntity {
    private Long idFature;
    private Double totaleFacture;
    private Double totalePayé;
    private Double Reste;
    private StatutFacture statut;
    private LocalDateTime dateFacture;

    private SituationFinanciere situationFinanciere;
    private Consultation consultation;
}
