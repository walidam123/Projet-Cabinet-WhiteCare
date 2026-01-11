package ma.whitecare.mvc.dto.AgendaDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.enums.Mois;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgendaDTO {
    private Mois mois;
    private Integer annee;
    private Long medecinId;
    private String modifiePar;
}
