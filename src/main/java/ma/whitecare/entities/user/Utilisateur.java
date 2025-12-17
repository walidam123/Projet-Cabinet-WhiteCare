package ma.whitecare.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Sexe;
import java.time.LocalDate;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @SuperBuilder
public    class  Utilisateur extends BaseEntity {
    protected Long idUser;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String adresse;
    protected String cin;
    protected String tel;
    protected Sexe sexe;
    protected String login;
    protected String motDePass;
    protected LocalDate LastLoginDate;
    protected LocalDate dateNaissance;
    protected Boolean actif;


    @Override
    public String toString() {
        return String.format(
                "Utilisateur{id=%d, nom='%s', email='%s', login='%s'}",
                idUser != null ? idUser : 0,
                nom != null ? nom : "N/A",
                email != null ? email : "N/A",
                login != null ? login : "N/A"
        );
    }
}
