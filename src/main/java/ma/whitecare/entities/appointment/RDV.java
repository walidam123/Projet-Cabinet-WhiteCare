package ma.whitecare.entities.appointment;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.medical.InterventionMedecin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RDV extends BaseEntity {
    private Long idRDV;
    private LocalDate Date;
    private LocalTime heure;
    private String motif;
    private StatutRendezVous statut;
    private String noteMedecin;

    private DossierMedicale dossierMedicale;
    private Consultation consultation;

    @Override
    public String toString() {
        return "RDV{" +
                ", idRDV=" + idRDV +
                ", date=" + Date +
                ", heure=" + heure +
                ", motif='" + motif + '\'' +
                ", statut=" + statut +
                ", dossierMedicale=" + (dossierMedicale != null ? dossierMedicale.getIdDM() : "null") +
                ", consultation=" + (consultation != null ? consultation.getIdConsultation() : "null") +
                ", noteMedecin="
                + (noteMedecin != null
                        ? "'" + (noteMedecin.length() > 20 ? noteMedecin.substring(0, 20) + "..." : noteMedecin) + "'"
                        : "null")
                +
                '}';
    }

    @Override
    public int hashCode() {
        return idRDV != null ? idRDV.hashCode() : 0;
    }
}
