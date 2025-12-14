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
    private CabinetMedicale cabinet;





    @Override
    public String toString() {
        return """
            Statistiques {
                id = %d,
                nom = '%s',
                categorie = %s,
                chiffre = %.2f,
                dateCalcul = %s
            }
            """.formatted(id, nom, categorie, chiffre, dateCalcul);
    }
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
