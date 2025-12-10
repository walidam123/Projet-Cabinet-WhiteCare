package ma.whitecare.mvc.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
    public class UpdateStaffDTO extends UpdateUserDTO{

        // Informations générales staff
        private Double salaire;
        private Double prime;
        private LocalDate dateRecrutement;
        private Integer soldeConge;
        private Long cabinetMedicaleId;


    }

