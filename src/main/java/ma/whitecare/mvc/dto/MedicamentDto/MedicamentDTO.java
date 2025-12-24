package ma.whitecare.mvc.dto.MedicamentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.FormeMedicament;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentDTO {
    private Long idMct;
    private String nom;
    private String laboratoire;
    private String type;
    private FormeMedicament forme;
    private Boolean remboursable;
    private Double prixUnitaire;
    private String description;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String creePar;
    private String modifiePar;
}