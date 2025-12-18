package ma.whitecare.mvc.dto.UserDto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSecretaireDTO extends UpdateStaffDTO {

    // Informations spécifiques à la secrétaire
    private String numCNSS;
    private Double commission;



}
