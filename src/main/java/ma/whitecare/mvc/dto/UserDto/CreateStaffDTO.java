package ma.whitecare.mvc.dto.UserDto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffDTO extends CreateUserDTO {
    // Informations salariales et professionnelles
    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private Integer soldeConge;
    private Long cabinetMedicaleId;
}