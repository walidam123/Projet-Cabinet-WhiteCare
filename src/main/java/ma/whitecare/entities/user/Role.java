package ma.whitecare.entities.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.whitecare.entities.base.BaseEntity;
import ma.whitecare.entities.enums.LibelleRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {


    private Long idRole;
    private LibelleRole libelle;


    private List<Long> utilisateursId;


    @Override
    public String toString() {
        return String.format(
                "Role{id=%d, libellé='%s', privilèges=%d, utilisateurs=%d}",
                idRole != null ? idRole : 0,
                libelle != null ? libelle : "N/A",
                utilisateursId != null ? utilisateursId.size() : 0
        );
    }

    @Override
    public int hashCode() {
        return idRole != null ? idRole.hashCode() : 0;
    }

}
