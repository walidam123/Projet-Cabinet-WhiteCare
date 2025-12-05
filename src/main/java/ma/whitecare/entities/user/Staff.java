package ma.whitecare.entities.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.cabinet.CabinetMedicale;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class Staff extends Utilisateur{

    protected Double salaire;
    protected Double prime;
    protected LocalDate dateRecrutement;
    protected Integer soldeConge;
    protected Long cabinetMedicaleId;
}
