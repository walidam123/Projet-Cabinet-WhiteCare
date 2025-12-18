package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Certificat extends BaseEntity {

    private Long idCertif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer duree;
    private String noteMedecin;


    private DossierMedicale dossierMedicale;
    private Consultation consultation;


    @Override
    public String toString() {
        return String.format(
                "Certificat{id=%d, du %s au %s, durée=%d jours, consultation=%s}",
                idCertif != null ? idCertif : 0,
                dateDebut != null ? dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                dateFin != null ? dateFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                duree != null ? duree : 0,
                consultation != null ? consultation.getIdConsultation() : "N/A"
        );
    }
    @Override
    public int hashCode() {
        return idCertif != null ? idCertif.hashCode() : 0;
    }
}
