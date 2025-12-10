package ma.whitecare.mvc.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecretaireDTO extends StaffDTO {
    private String numCNSS;
    private Double commission;
    private String niveauEtude;
    private String competences;


}
