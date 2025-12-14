package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Ordonnance extends BaseEntity {
    private  Long idOrd ;
    private LocalDate date;


    private List<Prescription> prescriptionList;
    private DossierMedicale dossierMedicale;
    private Consultation consultation;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ordonnance)) return false;
        Ordonnance that = (Ordonnance) o;
        return idOrd != null && idOrd.equals(that.idOrd);
    }

    @Override
    public int hashCode() {
        return idOrd != null ? idOrd.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Ordonnance {
                id = %d,
                date = %s,
                prescriptionsCount = %d
            }
            """.formatted(idOrd, date,
                prescriptionList == null ? 0 : prescriptionList.size());
    }
}
