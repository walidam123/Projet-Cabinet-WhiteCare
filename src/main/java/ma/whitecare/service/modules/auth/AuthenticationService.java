package ma.whitecare.service.modules.auth;

import ma.whitecare.common.exceptions.InvalidCredentialsException;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.mvc.dto.AuthDto.LoginRequestDto;
import ma.whitecare.mvc.dto.AuthDto.LoginResponseDto;
import ma.whitecare.mvc.dto.UserDto.UserDTO;
import ma.whitecare.service.modules.UserManager.api.UserService;
import ma.whitecare.common.validators.AuthenticationValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class AuthenticationService {

    private final UserService userService;
    private static AuthenticationService instance;
    private Utilisateur currentUser;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Authentifie un utilisateur via login et mot de passe.
     * 
     * @param request DTO contenant login et mot de passe brut
     * @return LoginResponseDto contenant le token (fictif pour l'instant) et les
     *         infos utilisateur
     * @throws InvalidCredentialsException si authentification échoue
     */
    public LoginResponseDto login(LoginRequestDto request) {
        // 0. Validation de la requête
        List<String> errors = AuthenticationValidator.validateLoginRequest(request);
        if (!errors.isEmpty()) {
            throw new InvalidCredentialsException(String.join(", ", errors));
        }

        // 1. Chercher l'utilisateur par login
        Optional<Utilisateur> userOpt = userService.findByLogin(request.getLogin());

        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException("Login ou mot de passe incorrect");
        }

        Utilisateur user = userOpt.get();

        // 2. Vérifier si le compte est actif
        if (!user.getActif()) {
            throw new InvalidCredentialsException("Ce compte est désactivé. Veuillez contacter l'administrateur.");
        }

        // 3. Vérifier le mot de passe avec BCrypt
        if (!BCrypt.checkpw(request.getPassword(), user.getMotDePass())) {
            throw new InvalidCredentialsException("Login ou mot de passe incorrect");
        }

        // 4. Authentification réussie - Définir l'utilisateur courant
        this.currentUser = user;

        // 5. Préparer la réponse
        UserDTO userDto = convertToDto(user);

        // Générer un token (Simulé pour l'instant, pourrait être un JWT plus tard)
        String sessionToken = java.util.UUID.randomUUID().toString();

        return new LoginResponseDto(sessionToken, userDto);
    }

    /**
     * Déconnecte l'utilisateur courant.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Récupère l'utilisateur actuellement connecté.
     * 
     * @return Utilisateur ou null si personne n'est connecté
     */
    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifie si un utilisateur est connecté.
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    private UserDTO convertToDto(Utilisateur user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getIdUser());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setActif(user.getActif() != null ? user.getActif() : false);
        dto.setFirstLogin(user.getFirstLogin() != null ? user.getFirstLogin() : true);
        // Explicitly fetch roles as they are not loaded by findByLogin
        java.util.List<ma.whitecare.entities.enums.LibelleRole> roles = userService.getUserRoles(user.getIdUser());
        dto.setRoles(roles);

        // Ne jamais renvoyer le mot de passe, même hashé, dans le DTO
        return dto;
    }
}
