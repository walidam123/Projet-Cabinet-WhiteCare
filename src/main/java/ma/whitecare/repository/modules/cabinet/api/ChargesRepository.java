package ma.whitecare.repository.modules.cabinet.api;


import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChargesRepository extends CrudRepository<Charges, Long> {

    // === FIND METHODS ===
    List<Charges> findByCabinetMedicaleId(Long cabinetId);
    List<Charges> findByTitreContainingIgnoreCase(String titre);
    List<Charges> findByDescriptionContainingIgnoreCase(String description);
    Optional<Charges> findByTitreAndCabinetId(String titre, Long cabinetId);
    // === CALCUL METHODS ===
    Double calculateTotalCharges(Long cabinetId);
    Double calculateTotalChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);

    Double calculateTotalChargesByTitre(Long cabinetId, String titre);

    // === STATISTICS METHODS ===
    Long countChargesByCabinet(Long cabinetId);
    Long countChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);

    List<Charges> findChargesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
}
