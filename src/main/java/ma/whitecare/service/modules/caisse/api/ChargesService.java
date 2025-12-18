package ma.whitecare.service.modules.caisse.api;

import ma.whitecare.entities.financial.Charges;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargesService {
    // === CRUD OPERATIONS ===
    Charges createCharge(Charges charge);
    Charges getChargeById(Long chargeId);
    List<Charges> getAllCharges();
    Charges updateCharge(Charges charge);
    void deleteCharge(Long chargeId);

    // === FIND METHODS ===
    List<Charges> getChargesByCabinet(Long cabinetId);
    List<Charges> searchChargesByTitre(String titre);
    List<Charges> searchChargesByDescription(String description);

    // === CALCULATION METHODS ===
    Double calculateTotalCharges(Long cabinetId);
    Double calculateTotalChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
    Double calculateTotalChargesByTitre(Long cabinetId, String titre);

    // === STATISTICS METHODS ===
    Long countChargesByCabinet(Long cabinetId);
    Long countChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
    List<Charges> getChargesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
}
