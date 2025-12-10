package ma.whitecare.mvc.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

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
