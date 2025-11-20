package ma.whitecare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.CategorieStatistique;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Statistiques extends BaseEntity {

    private Long id;
    private String nom;
    private CategorieStatistique categorie;
    private Double chiffre;
    private LocalDate dateCalcul;


    private CabinetMedicale cabinetMedicale;

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
