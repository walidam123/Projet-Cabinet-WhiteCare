package ma.whitecare.repository.modules.UserManager.api;


import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Role;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role,Long> {


    Optional<Role> findByLibelle(LibelleRole libelle);
    Optional<Role> findByLibelleString(String libelle);


    Long findIdByLibelle(LibelleRole role);
    void assignRoleToUser(Long userId, Long roleId);



    void assignRoleLibelleToUser(Long userId, LibelleRole libelle);


    void assignRolesToUser(Long userId, List<Long> roleIds);
    void removeRoleFromUser(Long userId, Long roleId);

    void removeAllRolesFromUser(Long userId);
    void updateUserRoles(Long userId, List<Long> newRoleIds);

    // === QUERIES ROLES-UTILISATEURS ===
    List<Role> findRolesByUserId(Long userId);
    List<Utilisateur> findUsersByRoleId(Long roleId);

    List<Utilisateur> findUsersByRoleLibelle(LibelleRole libelle);



    List<LibelleRole> findRoleLibellesByUserId(Long userId);

    boolean userHasRole(Long userId, Long roleId);
    boolean userHasRoleLibelle(Long userId, LibelleRole libelle);

}
