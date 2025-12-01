package ma.whitecare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.CategorieStatistique;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Statistiques extends BaseEntity {

    private Long id;
    private String nom;
    private CategorieStatistique categorie;
    private Double chiffre;
    private LocalDate dateCalcul;
    Long cabinetMedicaleId;





    @Override
    public String toString() {
        return String.format(
                "Statistiques{id=%d, nom='%s', categorie=%s, valeur=%.2f, date=%s, cabinetId=%d}",
                id != null ? id : 0,
                nom != null ? nom : "N/A",
                categorie != null ? categorie.name() : "N/A",
                chiffre,
                dateCalcul != null ? dateCalcul.toString() : "N/A",
                cabinetMedicaleId != null ? cabinetMedicaleId : 0
        );
    }
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
