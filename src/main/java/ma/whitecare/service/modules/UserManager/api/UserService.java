package ma.whitecare.service.modules.UserManager.api;

import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.mvc.dto.UserDto.*;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // ========== CRUD UTILISATEUR ==========
    Utilisateur createUser(CreateUserDTO userDTO);

    Utilisateur updateUser(Long userId, UpdateUserDTO userDTO);

    void deleteUser(Long userId);

    Utilisateur getUserById(Long userId);

    List<Utilisateur> getAllUsers();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    Optional<Utilisateur> findByLogin(String login);

    Optional<Utilisateur> findByCin(String cin);

    List<Utilisateur> findByNomAndPrenom(String nom, String prenom);

    // ========== RECHERCHES PAR ENUM ==========
    List<Utilisateur> findBySexe(Sexe sexe);

    List<Utilisateur> findByActif(boolean actif);

    List<Utilisateur> findActifs();

    List<Utilisateur> findInactifs();
    // ========== RECHERCHES PAR RÔLE ==========

    List<Utilisateur> findUsersByRole(LibelleRole role);

    // ========== GESTION RÔLES ==========
    void assignRoleToUser(Long userId, LibelleRole role);

    void assignRolesToUser(Long userId, List<LibelleRole> roles);

    void removeRoleFromUser(Long userId, LibelleRole role);

    void removeAllRolesFromUser(Long userId);

    void updateUserRoles(Long userId, List<LibelleRole> roles);

    List<LibelleRole> getUserRoles(Long userId);

    boolean userHasRole(Long userId, LibelleRole role);

    // ========== GESTION COMPTE ==========
    void activateUser(Long userId);

    void deactivateUser(Long userId);

    void activateUsers(List<Long> userIds);

    void deactivateUsers(List<Long> userIds);

    void updatePassword(Long userId, String newPassword);

    // ========== PAGINATION ==========
    List<Utilisateur> findWithPagination(int page, int size);

    // ========== STATISTIQUES ==========
    UserStatisticsDTO getStatistics();

    long countAllUsers();

    long countBySexe(Sexe sexe);

    long countByActif(boolean actif);

    long countByRole(LibelleRole role);

    // ========== VALIDATION ==========
    boolean isLoginAvailable(String login);

    boolean isCinAvailable(String cin);

    boolean isEmailAvailable(String email);

    void validateUserData(CreateUserDTO userDTO);

    // ========== PROFIL UTILISATEUR ==========
    UserProfileDTO getUserProfile(Long userId);

    UserProfileDTO updateUserProfile(Long userId, UpdateProfileDTO profileDTO);

    void updateFirstLoginStatus(Long userId, boolean status);
}
