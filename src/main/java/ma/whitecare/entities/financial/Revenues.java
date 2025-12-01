package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.cabinet.CabinetMedicale;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Revenues extends BaseEntity {
    private Long id ;
    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;


    private Long cabinetMedicaleId;


    @Override
    public String toString() {
        return String.format(
                "Revenues{id=%d, title='%s', montant=%.2f MAD, date=%s, cabinetId=%d}",
                id != null ? id : 0,
                titre != null ? titre : "N/A",
                montant != null ? montant : 0.0,
                date != null ? date.toLocalDate().toString() : "N/A",
                cabinetMedicaleId != null ? cabinetMedicaleId : 0
        );
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
