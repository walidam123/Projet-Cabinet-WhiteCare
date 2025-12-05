package ma.whitecare.repository.modules.UserManager.api;

import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SecretaireRepository extends CrudRepository<Secretaire, Long> {

    List<Secretaire> findByNumeroCRSS(String numeroCRSS);

    Optional<Secretaire> findByEmail(String email);
    List<Secretaire> findByNomPrenom(String nom, String prenom);
    List<Secretaire> findByCabinetId(Long cabinetId);


    // ========== GESTION DU CABINET ==========

    void affecterAuCabinet(Long secretaireId, Long cabinetId);
    void retirerDuCabinet(Long secretaireId);
    List<Secretaire> findSecretairesDisponiblesByCabinet(Long cabinetId);


    void updateCommission(Long secretaireId, Double nouvelleCommission);
    void updateStatutDisponibilite(Long secretaireId, boolean disponible);

    List<Secretaire> findPageByCabinet(Long cabinetId, int limit, int offset);

    // ========== GESTION DES RENDEZ-VOUS ==========
    List<Secretaire> getSecretairesAvecRDVEnCours();


    // ========== MÉTHODES DE MISE À JOUR SPÉCIFIQUES ==========
    void updateSalaire(Long secretaireId, Double nouveauSalaire);
    void updatePrime(Long secretaireId, Double nouvellePrime);
    void updateSoldeConge(Long secretaireId, Integer nouveauSolde);

}
