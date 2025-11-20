package ma.whitecare.entities.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ordonnance extends BaseEntity {
    private  Long idOrd ;
    private LocalDate date;


    private List<Prescription> prescriptionList;
    private DossierMedicale dossierMedicale;
    private Consultation consultation;
}
