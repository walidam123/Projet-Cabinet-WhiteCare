package ma.whitecare.entities.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Secretaire extends Utilisateur {

    private String numCNSS;
    private Double commission;
}
