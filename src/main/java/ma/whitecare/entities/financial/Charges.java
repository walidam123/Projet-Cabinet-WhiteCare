package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.cabinet.CabinetMedicale;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Charges extends BaseEntity {

    private Long id;
    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;

    private CabinetMedicale cabinetMedicale;

    @Override
    public String toString() {
        return String.format(
                "Charge{id=%d, titre='%s', montant=%.2f MAD, date=%s, cabinet=%s,description:%s}",
                id != null ? id : 0,
                titre != null ? titre : "N/A",
                montant != null ? montant : 0.0,
                date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                cabinetMedicale != null ? cabinetMedicale.getNom() : "N/A",
                description != null ? description : "N/A"
        );
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}


