package ma.whitecare.mvc.dto.UserDto;



import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedecinDTO extends UpdateStaffDTO {

    // Informations spécifiques au médecin
    private String specialite;



}
