package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.entities.medical.DossierMedicale;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SituationFinanciere extends BaseEntity {

    private Long idSF;
    private Double totaleDesActes;
    private Double totalePaye;
    private Double crédit;
    private StatutSituationFinanciere statut;
    private EnPromo enPromo;


    private DossierMedicale dossierMedicale;

    private List<Facture> factures = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationFinanciere)) return false;
        SituationFinanciere that = (SituationFinanciere) o;
        return idSF != null && idSF.equals(that.idSF);
    }

    @Override
    public int hashCode() {
        return idSF != null ? idSF.hashCode() : 0;
    }

    public Double getReste() {
        if (totaleDesActes == null || totalePaye == null) return 0.0;
        return totaleDesActes - totalePaye;
    }

    @Override
    public String toString() {
        return """
            SituationFinanciere {
                id = %d,
                totaleDesActes = %.2f,
                totalePaye = %.2f,
                reste = %.2f,
                statut = %s
            }
            """.formatted(idSF,
                totaleDesActes != null ? totaleDesActes : 0.0,
                totalePaye != null ? totalePaye : 0.0,
                getReste(),
                statut);
    }

}
