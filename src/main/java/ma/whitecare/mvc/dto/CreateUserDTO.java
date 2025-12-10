package ma.whitecare.mvc.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "Le login est obligatoire")
    @Size(min = 3, max = 30, message = "Le login doit contenir entre 3 et 30 caractères")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Le login ne peut contenir que des lettres, chiffres et underscores")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @NotBlank(message = "Le CIN est obligatoire")
    @Size(min = 6, max = 20, message = "Le CIN doit contenir entre 6 et 20 caractères")
    private String cin;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s\\-]{8,}$", message = "Le téléphone doit être valide")
    private String telephone;

    private String adresse;
    private LocalDate dateNaissance;
    private Sexe sexe;

    @NotNull(message = "Au moins un rôle doit être spécifié")
    @Size(min = 1, message = "Au moins un rôle doit être spécifié")
    private List<LibelleRole> roles;

    private boolean actif = true;
}
