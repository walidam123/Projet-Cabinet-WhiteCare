package ma.whitecare.entities.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.agenda.AgendaMensuel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Medecin extends Staff{
    private String specialite;






    @Override
    public String toString() {
        return String.format(
                "Médecin{%s, spécialité='%s', salaire=%s}",
                super.toString(), // Appel du toString() de Utilisateur
                specialite != null ? specialite : "N/A",
                getSalaire() != null ? String.format("+%.2f MAD", getSalaire()) : "+0.00 MAD"
        );
    }
    @Override
    public int hashCode() {
        return getIdUser() != null ? getIdUser().hashCode() : 0;
    }
}
