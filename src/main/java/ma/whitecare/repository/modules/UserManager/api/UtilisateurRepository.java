package ma.whitecare.repository.modules.UserManager.api;


import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur,Long> {


    // === FIND BY ATTRIBUTES ===
    Optional<Utilisateur> findByLogin(String login);
    Optional<Utilisateur> findByCin(String cin);

    List<Utilisateur> findByNomAndPrenom(String nom, String prenom);


    // === FIND BY ENUM ===
    List<Utilisateur> findBySexe(Sexe sexe);
    List<Utilisateur> findHommes();
    List<Utilisateur> findFemmes();

    List<Utilisateur> findByActif(boolean actif);

    List<Utilisateur> findActifs();
    List<Utilisateur> findInactifs();

    List<Utilisateur> findAdmins();
    List<Utilisateur> findMedecins();
    List<Utilisateur> findSecretaires();
    // === STATISTICS ===
    long countAll();
    boolean existsById(Long userId);
    List<Utilisateur> findWithPagination(int offset, int limit);

    void updatePassword(Long userId, String newPasswordHash);

    void activateUsers(List<Long> userIds);

    void deactivateUsers(List<Long> userIds);
}
