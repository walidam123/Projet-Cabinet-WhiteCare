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
    private Long dossierMedicaleid;
    private Long consultationid;
    @Override
    public int hashCode() {
        return idOrd != null ? idOrd.hashCode() : 0;
    }
}
