package ma.whitecare.entities.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medecin extends Utilisateur{
    private String specialite;
    //private AgendaMensuel agendaDocteur;
}
