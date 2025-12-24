package ma.whitecare.mvc.dto.ActeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActeDTO {
    private String libelle;
    private String categorie;
    private Double prixDeBase;
    private String modifiePar;
}
