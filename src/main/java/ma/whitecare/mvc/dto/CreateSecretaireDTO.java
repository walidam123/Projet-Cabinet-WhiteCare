package ma.whitecare.mvc.dto;



import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateSecretaireDTO extends CreateStaffDTO {

    // Informations spécifiques à la secrétaire
    private String numCNSS;
    private Double commission;


    // Surcharger le rôle pour toujours être SECRETAIRE
    @Override
    public List<LibelleRole> getRoles() {
        return List.of(LibelleRole.SECRETAIRE);
    }
}