package ma.whitecare.mvc.dto.UserDto;



import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDTO extends StaffDTO {
    private String specialite;

}