package ma.whitecare.mvc.dto.ActeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateActeDTO {
    private String libelle;
    private String categorie;
    private Double prixDeBase;
    private String creePar;
    private String modifiePar;
}
