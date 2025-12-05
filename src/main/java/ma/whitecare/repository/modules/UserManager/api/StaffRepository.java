package ma.whitecare.repository.modules.UserManager.api;

import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;

public interface StaffRepository extends CrudRepository<Staff, Long> {


        List<Staff> findByCabinetMedicaleId(Long cabinetId);
        List<Staff> findByCabinetAndActif(Long cabinetId, boolean actif);
        List<Staff> findPageByCabinet(Long cabinetId, int limit, int offset);


        // === MÉTHODES POUR MÉDECINS ===
        List<Medecin> findMedecinsByCabinet(Long cabinetId);
        Medecin findMedecinById(Long id);

        // === MÉTHODES POUR SECRÉTAIRES ===
        List<Secretaire> findSecretairesByCabinet(Long cabinetId);
        Secretaire findSecretaireById(Long id);

        // === MISE À JOUR SPÉCIFIQUE ===
        void updateSalaire(Long staffId, Double nouveauSalaire);
        void updatePrime(Long staffId, Double nouvellePrime);
        void updateSoldeConge(Long staffId, Integer nouveauSolde);
        void assignToCabinet(Long staffId, Long cabinetId);
        Long countStaffByCabinet(Long cabinetId);
}
crud
regles
        valider les donnes
recuperer les donnes gestion exceptions