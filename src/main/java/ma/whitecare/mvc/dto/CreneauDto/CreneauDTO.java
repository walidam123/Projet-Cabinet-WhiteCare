package ma.whitecare.mvc.dto.CreneauDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreneauDTO {
    private Long id;
    private Long jourId;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Boolean estDisponible;
    private String motifIndisponibilite;
    private Long rendezVousId;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String creePar;
    private String modifiePar;
}
