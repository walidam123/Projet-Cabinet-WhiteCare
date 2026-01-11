package ma.whitecare.mvc.dto.RDVDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.StatutRendezVous;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRDVDTO {
    private LocalDate date;
    private LocalTime heure;
    private String motif;
    private StatutRendezVous statut;
    private String noteMedecin;
    private Long consultationId;
    private Long dossierMedicaleId;
    private String modifiePar;
}
