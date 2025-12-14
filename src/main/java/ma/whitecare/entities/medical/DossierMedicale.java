package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DossierMedicale extends BaseEntity {

    private Long idDM;
    private LocalDate dateDeCreation;

    private Patient patient;
    private SituationFinanciere situationFinanciere;
     private List<Ordonnance> ordonnanceList;
     private List<Certificat> certificatList;
     private Medecin medecin;
     private List<RDV> rdvList;
     private List<Consultation> consultationList;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DossierMedicale)) return false;
        DossierMedicale that = (DossierMedicale) o;
        return idDM != null && idDM.equals(that.idDM);
    }

    @Override
    public int hashCode() {
        return idDM != null ? idDM.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            DossierMedical {
                id = %d,
                patient = %d,
                dateCreation = %s,
                consultationsCount = %d
            }
            """.formatted(idDM, patient.getNom(), dateCreation,
                consultationList == null ? 0 : consultationList.size());
    }
}
