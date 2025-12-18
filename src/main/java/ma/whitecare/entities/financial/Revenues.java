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

    private CabinetMedicale Cabinet;



    @Override
    public String toString() {
        return """
            Revenues {
                id = %d,
                titre = '%s',
                montant = %.2f,
                date = %s
            }
            """.formatted(id, titre, montant, date);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
