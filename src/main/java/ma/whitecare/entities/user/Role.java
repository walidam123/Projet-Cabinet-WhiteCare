package ma.whitecare.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.LibelleRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity {


    private Long idRole;
    private LibelleRole libelle;
    private List<String> privileges;

    //private List<Utilisateur> utilisateurs;
}
