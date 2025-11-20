package ma.whitecare.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Sexe;
import java.time.LocalDate;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public  class  Utilisateur extends BaseEntity {
    protected Long idUser;
    protected String nom;
    protected String email;
    protected String adresse;
    protected String cin;
    protected String tel;
    protected Sexe sexe;
    protected String login;
    protected String motDePass;
    protected LocalDate LastLoginDate;
    protected LocalDate dateNaissance;

    protected List<Role> roles;
}
