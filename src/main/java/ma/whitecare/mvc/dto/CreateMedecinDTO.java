package ma.whitecare.mvc.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.enums.LibelleRole;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedecinDTO extends CreateStaffDTO {  // ← Hérite de CreateStaffDTO
    // Informations spécifiques au médecin
    private String specialite;
    private String numeroRPPS;
    private String diplome;
    private Integer anneeExperience;

    // Surcharger le rôle pour toujours être MEDECIN
    @Override
    public List<LibelleRole> getRoles() {
        return List.of(LibelleRole.MEDECIN);
    }
}