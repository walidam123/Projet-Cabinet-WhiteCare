package ma.whitecare.mvc.dto.ActeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActeDTO {
    private Long idActe;
    private String libelle;
    private String categorie;
    private Double prixDeBase;
    private LocalDate dateCreation;
    private LocalDate dateDerniereModification;
    private String creePar;
    private String modifiePar;
}
