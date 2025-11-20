package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.cabinet.CabinetMedicale;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Charges extends BaseEntity {


    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;

    private CabinetMedicale cabinetMedicale;



}
