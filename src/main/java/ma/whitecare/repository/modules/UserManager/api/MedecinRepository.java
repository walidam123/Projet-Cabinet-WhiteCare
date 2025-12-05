package ma.whitecare.repository.modules.UserManager.api;

import ma.whitecare.entities.user.Medecin;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedecinRepository extends CrudRepository<Medecin, Long> {


    List<Medecin> findByNomPrenom(String nom, String prenom);
    List<Medecin> findBySpecialite(String specialite);

    // ========== GESTION DU CABINET ==========
    List<Medecin> findByCabinetId(Long cabinetId);
    void affecterAuCabinet(Long medecinId, Long cabinetId);
    void retirerDuCabinet(Long medecinId);

    // ========== GESTION DES DISPONIBILITÉS ==========
    List<Medecin> findAvailableMedecins();
    List<Medecin> findAvailableByDate(LocalDate date);
    void updateDisponibilite(Long medecinId, boolean disponible);

    // ========== GESTION AGENDA ==========
    //List<Medecin> findByAgendaMensuelId(Long agendaId);
    //void updateAgendaMensuel(Long medecinId, Long agendaId);

    List<Medecin> findPageByCabinet(Long cabinetId, int limit, int offset);

    // ========== MÉTHODES DE MISE À JOUR SPÉCIFIQUES ==========
    void updateSpecialite(Long medecinId, String nouvelleSpecialite);
    void updateNumeroOrdre(Long medecinId, String nouveauNumero);
    void updateSalaire(Long medecinId, Double nouveauSalaire);
    void updatePrime(Long medecinId, Double nouvellePrime);
    void updateSoldeConge(Long medecinId, Integer nouveauSolde);
}
