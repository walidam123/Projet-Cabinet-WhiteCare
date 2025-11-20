package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.entities.medical.DossierMedicale;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SituationFinanciere extends BaseEntity {

    private Long idSF;
    private Double totaleDesActes;
    private Double totalePayé;
    private Double crédit;
    private StatutSituationFinanciere statut;
    private EnPromo enPromo;

    private List<Facture> factureList;
    private DossierMedicale dossierMedicale;


    @Override
    public int hashCode() {
        return idSF != null ? idSF.hashCode() : 0;
    }
}
