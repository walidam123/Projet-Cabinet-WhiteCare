package ma.whitecare.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Sexe;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Utilisateur extends BaseEntity {
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
    @Builder.Default
    protected Boolean firstLogin = true;
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @Override
    public String toString() {
        return """
                Utilisateur {
                    id = %d,
                    nom = '%s',
                    email = '%s',
                    login = '%s',
                    rolesCount = %d
                }
                """.formatted(idUser, nom, email, login, roles == null ? 0 : roles.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Utilisateur))
            return false;
        Utilisateur that = (Utilisateur) o;
        return idUser != null && idUser.equals(that.idUser);
    }

    @Override
    public int hashCode() {
        return idUser != null ? idUser.hashCode() : 0;
    }

}
