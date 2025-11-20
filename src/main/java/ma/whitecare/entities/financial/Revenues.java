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
public class Revenues extends BaseEntity {
    private Long id ;
    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;


    private CabinetMedicale cabinetMedicale;


    @Override
    public String toString() {
        return String.format(
                "Revenues[id=%d, titre=%s, montant=%s, date=%s, cabinet=%s]",
                id,
                titre,
                montant != null ? String.format("+%.2f MAD", montant) : "+0.00 MAD",
                date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd à HH:mm")) : "non définie",
                cabinetMedicale != null ? cabinetMedicale.getNom() : "aucun cabinet"
        );
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
