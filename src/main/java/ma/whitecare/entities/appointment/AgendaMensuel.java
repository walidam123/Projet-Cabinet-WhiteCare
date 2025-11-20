package ma.whitecare.entities.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.user.Medecin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgendaMensuel extends BaseEntity {

    private Mois mois;
    private List<LocalDate> joursNonDisponible;

    private Medecin medecin;

}
