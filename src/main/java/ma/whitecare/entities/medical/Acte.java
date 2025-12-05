package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Acte extends BaseEntity {
    private Long idActe;

    private String libelle;

    private String categorie;

    private Double prixDeBase;
    private List<Long> interventionMedecinlist=null;

    @Override
    public String toString() {
        return "Acte{" +
                ", idActe=" + idActe +
                ", libelle='" + libelle + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prixDeBase=" + prixDeBase +
                '}';
    }

    @Override
    public int hashCode() {
        return idActe != null ? idActe.hashCode() : 0;
    }
}
