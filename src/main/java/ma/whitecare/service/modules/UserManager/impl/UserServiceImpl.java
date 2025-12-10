package ma.whitecare.service.modules.UserManager.impl;

import ma.whitecare.common.exceptions.UserAlreadyExistsException;
import ma.whitecare.common.exceptions.UserNotFoundException;
import ma.whitecare.common.validators.UserValidator;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.mvc.dto.*;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.service.modules.UserManager.api.UserService;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;

public class UserServiceImpl implements UserService {
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;


    public UserServiceImpl(UtilisateurRepository utilisateurRepository,
                           RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;

    }


    @Override
    public Utilisateur createUser(CreateUserDTO userDTO) {
        // Validation
        validateUserData(userDTO);

        // Vérifier unicité
        if (!isLoginAvailable(userDTO.getLogin())) {
            throw new UserAlreadyExistsException("login", userDTO.getLogin());
        }

        if (!isCinAvailable(userDTO.getCin())) {
            throw new UserAlreadyExistsException("CIN", userDTO.getCin());
        }



        // Créer l'utilisateur
        Utilisateur user = new Utilisateur();
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setLogin(userDTO.getLogin());
        user.setCin(userDTO.getCin());
        user.setEmail(userDTO.getEmail());
        user.setTel(userDTO.getTelephone());
        user.setAdresse(userDTO.getAdresse());
        user.setDateNaissance(userDTO.getDateNaissance());
        user.setSexe(userDTO.getSexe());
        user.setActif(userDTO.isActif());
        user.setMotDePass(userDTO.getPassword());
        // Set audit fields (hérités de BaseEntity)
        user.setCreePar("system"); // À remplacer par l'utilisateur connecté
        user.setModifiePar("system");




        // Sauvegarder l'utilisateur
        utilisateurRepository.create(user);

        // Assigner les rôles
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            for (LibelleRole role : userDTO.getRoles()) {
                roleRepository.assignRoleLibelleToUser(user.getIdUser(), role);
            }
        }

        return user;}

    @Override
    public Utilisateur updateUser(Long userId, UpdateUserDTO userDTO) {
        Utilisateur user = getUserById(userId);

        // Validation
        List<String> errors = UserValidator.validateUpdateUser(userDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Mettre à jour les champs
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setEmail(userDTO.getEmail());
        user.setTel(userDTO.getTelephone());
        user.setAdresse(userDTO.getAdresse());
        user.setDateNaissance(userDTO.getDateNaissance());
        user.setSexe(userDTO.getSexe());
        user.setActif(userDTO.getActif());

        // Mettre à jour les champs d'audit
        user.setModifiePar("system"); // À remplacer par l'utilisateur connecté


        // Mettre à jour les rôles si fournis
        if (userDTO.getRoles() != null) {
            updateUserRoles(userId, userDTO.getRoles());
        }

        utilisateurRepository.update(user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // Supprimer d'abord les associations de rôles
        roleRepository.removeAllRolesFromUser(userId);

        // Puis supprimer l'utilisateur
        utilisateurRepository.deleteById(userId);
    }

    @Override
    public Utilisateur getUserById(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    @Override
    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }
    // ========== RECHERCHES SPÉCIFIQUES ==========

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        return utilisateurRepository.findByLogin(login);
    }

    @Override
    public Optional<Utilisateur> findByCin(String cin) {
        return utilisateurRepository.findByCin(cin);
    }



    @Override
    public List<Utilisateur> findByNomAndPrenom(String nom, String prenom) {
        return utilisateurRepository.findByNomAndPrenom(nom, prenom);
    }

    // ========== RECHERCHES PAR ENUM ==========

    @Override
    public List<Utilisateur> findBySexe(Sexe sexe) {
        return utilisateurRepository.findBySexe(sexe);
    }

    @Override
    public List<Utilisateur> findByActif(boolean actif) {
        return utilisateurRepository.findByActif(actif);
    }

    @Override
    public List<Utilisateur> findActifs() {
        return utilisateurRepository.findActifs();
    }

    @Override
    public List<Utilisateur> findInactifs() {
        return utilisateurRepository.findInactifs();
    }

    @Override
    public List<Utilisateur> findUsersByRole(LibelleRole role) {
        return roleRepository.findUsersByRoleLibelle(role);
    }

    // ========== GESTION RÔLES ==========

    @Override
    public void assignRoleToUser(Long userId, LibelleRole role) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        roleRepository.assignRoleLibelleToUser(userId, role);
    }

    @Override
    public void assignRolesToUser(Long userId, List<LibelleRole> roles) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // Convertir List<LibelleRole> en List<Long> de roleIds
        // Note: Cette conversion dépend de votre implémentation
        // Pour l'exemple, je suppose que vous avez une méthode pour récupérer les IDs des rôles
        List<Long> roleIds = new ArrayList<>();
        for (LibelleRole role : roles) {
            // Vous aurez besoin d'une méthode pour récupérer l'ID d'un rôle par son libellé
             roleRepository.findIdByLibelle(role);
        }

        if (!roleIds.isEmpty()) {
            roleRepository.assignRolesToUser(userId, roleIds);
        }
    }

    @Override
    public void removeRoleFromUser(Long userId, LibelleRole role) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        roleRepository.removeRoleFromUser(userId, getRoleIdByLibelle(role));
    }

    @Override
    public void removeAllRolesFromUser(Long userId) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        roleRepository.removeAllRolesFromUser(userId);
    }

    @Override
    public void updateUserRoles(Long userId, List<LibelleRole> roles) {
// Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // Convertir les libellés en IDs
        List<Long> roleIds = new ArrayList<>();
        for (LibelleRole role : roles) {
            roleIds.add(getRoleIdByLibelle(role));
        }

        roleRepository.updateUserRoles(userId, roleIds);
    }

    @Override
    public List<LibelleRole> getUserRoles(Long userId) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return roleRepository.findRoleLibellesByUserId(userId);
    }

    @Override
    public boolean userHasRole(Long userId, LibelleRole role) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return roleRepository.userHasRoleLibelle(userId, role);
    }

    // ========== GESTION COMPTE ==========

    @Override
    public void activateUser(Long userId) {
        Utilisateur user = getUserById(userId);
        user.setActif(true);
        user.setModifiePar("system");

        utilisateurRepository.update(user);
    }

    @Override
    public void deactivateUser(Long userId) {
        Utilisateur user = getUserById(userId);
        user.setActif(false);
        user.setModifiePar("system");

        utilisateurRepository.update(user);
    }

    @Override
    public void activateUsers(List<Long> userIds) {
        utilisateurRepository.activateUsers(userIds);
    }

    @Override
    public void deactivateUsers(List<Long> userIds) {
        utilisateurRepository.deactivateUsers(userIds);
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        Utilisateur user = getUserById(userId);

        // Valider le nouveau mot de passe
        List<String> errors = UserValidator.validatePassword(newPassword);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }


        utilisateurRepository.updatePassword(userId, newPassword);

        // Mettre à jour les champs d'audit
        user.setModifiePar("system");

        utilisateurRepository.update(user);
    }

    @Override
    public List<Utilisateur> findWithPagination(int page, int size) {
        int offset = page * size;
        return utilisateurRepository.findWithPagination(offset, size);
    }



    @Override
    public UserStatisticsDTO getStatistics() {
        long totalUsers = countAllUsers();
        long activeUsers = utilisateurRepository.findActifs().size();
        long inactiveUsers = utilisateurRepository.findInactifs().size();
        long hommes = countBySexe(Sexe.HOMME);
        long femmes = countBySexe(Sexe.FEMME);

        // Compter les utilisateurs par rôle
        Map<LibelleRole, Long> usersByRole = new HashMap<>();
        for (LibelleRole role : LibelleRole.values()) {
            usersByRole.put(role, countByRole(role));
        }

        // Statistiques par groupe d'âge (exemple)
        Map<Integer, Long> usersByAgeGroup = calculateUsersByAgeGroup();

        // Statistiques d'inscription par mois (exemple)
        Map<Integer, Long> registrationsByMonth = calculateRegistrationsByMonth();

        return UserStatisticsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .hommes(hommes)
                .femmes(femmes)
                .usersByRole(usersByRole)
                .usersByAgeGroup(usersByAgeGroup)
                .registrationsByMonth(registrationsByMonth)
                .build();
    }

    @Override
    public long countAllUsers() {
        return utilisateurRepository.countAll();
    }

    @Override
    public long countBySexe(Sexe sexe) {
        return utilisateurRepository.findBySexe(sexe).size();
    }

    @Override
    public long countByActif(boolean actif) {
        return utilisateurRepository.findByActif(actif).size();
    }

    @Override
    public long countByRole(LibelleRole role) {
        return roleRepository.findUsersByRoleLibelle(role).size();
    }

    // ========== VALIDATION ==========

    @Override
    public boolean isLoginAvailable(String login) {
        return utilisateurRepository.findByLogin(login).isEmpty();
    }

    @Override
    public boolean isCinAvailable(String cin) {
        return utilisateurRepository.findByCin(cin).isEmpty();
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return false;
    }


    @Override
    public void validateUserData(CreateUserDTO userDTO) {
        List<String> errors = UserValidator.validateCreateUser(userDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        Utilisateur user = getUserById(userId);
        List<LibelleRole> roles = getUserRoles(userId);

        return UserProfileDTO.builder()
                .id(user.getIdUser())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .login(user.getLogin())
                .cin(user.getCin())
                .email(user.getEmail())
                .telephone(user.getTel())
                .adresse(user.getAdresse())
                .dateNaissance(user.getDateNaissance())
                .sexe(user.getSexe())
                .actif(user.getActif())
                .roles(roles)
                .dateCreation(user.getDateCreation())
                .derniereModification(user.getDateDerniereModification())
                .derniereConnexion(user.getLastLoginDate()) // Si votre entité a ce champ
                .build();
    }

    @Override
    public UserProfileDTO updateUserProfile(Long userId, UpdateProfileDTO profileDTO) {
        Utilisateur user = getUserById(userId);

        // Validation
        List<String> errors = UserValidator.validateUpdateProfile(profileDTO);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        // Mettre à jour les champs
        user.setNom(profileDTO.getNom());
        user.setPrenom(profileDTO.getPrenom());
        user.setTel(profileDTO.getTelephone());
        user.setAdresse(profileDTO.getAdresse());
        user.setDateNaissance(profileDTO.getDateNaissance());
        user.setSexe(profileDTO.getSexe());

        // Mettre à jour les champs d'audit
        user.setModifiePar("system"); // À remplacer par l'utilisateur connecté


        utilisateurRepository.update(user);

        return getUserProfile(userId);
    }
    private Map<Integer, Long> calculateUsersByAgeGroup() {
        Map<Integer, Long> ageGroups = new HashMap<>();

        // Exemple: calculer les groupes d'âge
        // 0-18, 19-30, 31-45, 46-60, 61+
        List<Utilisateur> allUsers = getAllUsers();

        for (Utilisateur user : allUsers) {
            if (user.getDateNaissance() != null) {
                int age = calculateAge(user.getDateNaissance());
                int ageGroup = getAgeGroup(age);
                ageGroups.put(ageGroup, ageGroups.getOrDefault(ageGroup, 0L) + 1);
            }
        }

        return ageGroups;
    }

    private Map<Integer, Long> calculateRegistrationsByMonth() {
        Map<Integer, Long> registrationsByMonth = new HashMap<>();

        // Exemple: compter les inscriptions par mois de l'année en cours
        List<Utilisateur> allUsers = getAllUsers();
        int currentYear = LocalDate.now().getYear();

        for (Utilisateur user : allUsers) {
            if (user.getDateCreation() != null &&
                    user.getDateCreation().getYear() == currentYear) {
                int month = user.getDateCreation().getMonthValue();
                registrationsByMonth.put(month, registrationsByMonth.getOrDefault(month, 0L) + 1);
            }
        }

        return registrationsByMonth;
    }
    private Long getRoleIdByLibelle(LibelleRole libelle) {
        // Cette méthode doit récupérer l'ID d'un rôle par son libellé
        // À adapter selon votre implémentation de RoleRepository
        return roleRepository.findByLibelle(libelle)
                .map(role -> role.getIdRole())
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + libelle));
    }
    private int calculateAge(LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    private int getAgeGroup(int age) {
        if (age <= 18) return 0;
        if (age <= 30) return 1;
        if (age <= 45) return 2;
        if (age <= 60) return 3;
        return 4;
    }
}
