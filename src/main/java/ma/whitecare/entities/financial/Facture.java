package ma.whitecare.entities.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.entities.medical.Consultation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facture extends BaseEntity {
    private Long idFature;
    private Double totaleFacture;
    private Double totalePayé;
    private Double Reste;
    private StatutFacture statut;
    private LocalDateTime dateFacture;

    private SituationFinanciere situationFinanciere;
    private Consultation consultation;

    @Override
    public String toString() {
        return String.format(
                "Facture{id=%d, totale=%.2f MAD, payé=%.2f MAD, reste=%.2f MAD, statut=%s, date=%s}",
                idFature != null ? idFature : 0,
                totaleFacture != null ? totaleFacture : 0.0,
                totalePayé != null ? totalePayé : 0.0,
                Reste != null ? Reste : 0.0,
                statut != null ? statut : "N/A",
                dateFacture != null ? dateFacture.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"
        );
    }
    @Override
    public int hashCode() {
        return idFature != null ? idFature.hashCode() : 0;
    }
}
