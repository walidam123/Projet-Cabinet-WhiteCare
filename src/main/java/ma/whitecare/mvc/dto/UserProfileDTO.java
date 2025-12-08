package ma.whitecare.mvc.dto;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String login;
    private String cin;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private boolean actif;
    private List<LibelleRole> roles;
    private LocalDate dateCreation;
    private LocalDate derniereModification;
    private LocalDate derniereConnexion;
}