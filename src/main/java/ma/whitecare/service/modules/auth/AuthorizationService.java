package ma.whitecare.service.modules.auth;

import ma.whitecare.common.exceptions.UnauthorizedAccessException;
import ma.whitecare.entities.enums.LibelleRole;
import ma.whitecare.entities.user.Utilisateur;

import java.util.Arrays;
import java.util.List;

public class AuthorizationService {

    private static AuthorizationService instance;

    public AuthorizationService() {
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique.
     * 
     * @param user Utilisateur à vérifier
     * @param role Rôle requis
     * @return true si l'utilisateur a le rôle
     */
    public boolean hasRole(Utilisateur user, LibelleRole role) {
        if (user == null || user.getRoles() == null || role == null) {
            return false;
        }
        return user.getRoles().stream()
                .anyMatch(r -> r.getLibelle() == role);
    }

    /**
     * Vérifie si l'utilisateur a l'un des rôles spécifiés.
     * 
     * @param user  Utilisateur à vérifier
     * @param roles Liste des rôles autorisés
     * @return true si l'utilisateur a au moins un des rôles
     */
    public boolean hasAnyRole(Utilisateur user, LibelleRole... roles) {
        if (user == null || user.getRoles() == null || roles == null) {
            return false;
        }
        List<LibelleRole> requiredRoles = Arrays.asList(roles);
        return user.getRoles().stream()
                .anyMatch(r -> requiredRoles.contains(r.getLibelle()));
    }

    /**
     * Vérifie si l'utilisateur a le rôle ADMIN.
     */
    public boolean isAdmin(Utilisateur user) {
        return hasRole(user, LibelleRole.ADMIN);
    }

    /**
     * Vérifie si l'utilisateur a le rôle MEDECIN.
     */
    public boolean isMedecin(Utilisateur user) {
        return hasRole(user, LibelleRole.MEDECIN);
    }

    /**
     * Vérifie si l'utilisateur a le rôle SECRETAIRE.
     */
    public boolean isSecretaire(Utilisateur user) {
        return hasRole(user, LibelleRole.SECRETAIRE);
    }

    /**
     * Vérifie le rôle et lance une exception si refusé.
     * 
     * @param user Utilisateur
     * @param role Rôle requis
     * @throws UnauthorizedAccessException si l'utilisateur n'a pas le rôle
     */
    public void checkRole(Utilisateur user, LibelleRole role) {
        if (!hasRole(user, role)) {
            throw new UnauthorizedAccessException("Accès refusé. Rôle requis : " + role);
        }
    }

    /**
     * Vérifie si l'utilisateur a l'un des rôles et lance une exception sinon.
     */
    public void checkAnyRole(Utilisateur user, LibelleRole... roles) {
        if (!hasAnyRole(user, roles)) {
            throw new UnauthorizedAccessException("Accès refusé. Rôles requis : " + Arrays.toString(roles));
        }
    }
}
