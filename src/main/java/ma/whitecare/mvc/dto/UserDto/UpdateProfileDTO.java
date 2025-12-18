package ma.whitecare.mvc.dto.UserDto;

import lombok.*;
import ma.whitecare.entities.enums.Sexe;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Pattern(regexp = "^\\+?[0-9\\s\\-]{8,}$", message = "Le téléphone doit être valide")
    private String telephone;

    private String adresse;
    private LocalDate dateNaissance;
    private Sexe sexe;
}