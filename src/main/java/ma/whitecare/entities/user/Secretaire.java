package ma.whitecare.entities.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Secretaire extends Staff {

    private String numCNSS;
    private Double commission;

    @Override
    public String toString() {
        return String.format(
                "Secrétaire{%s, CNSS='%s', commission=%.2f%%,salaire=%s}",
                super.toString(), // Appel du toString() de Utilisateur
                numCNSS != null ? numCNSS : "N/A",
                commission != null ? commission : 0.0,
                getSalaire() != null ? String.format("+%.2f MAD", getSalaire()) : "+0.00 MAD"
        );
    }

    @Override
    public int hashCode() {
        return getIdUser() != null ? getIdUser().hashCode() : 0;
    }
}
