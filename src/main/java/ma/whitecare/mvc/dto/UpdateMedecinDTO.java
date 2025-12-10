package ma.whitecare.mvc.dto;



import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedecinDTO extends UpdateStaffDTO {

    // Informations spécifiques au médecin
    private String specialite;



}
