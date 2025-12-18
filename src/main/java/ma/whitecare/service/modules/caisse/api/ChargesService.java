package ma.whitecare.service.modules.caisse.api;

import ma.whitecare.entities.financial.Charges;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargesService {

    List<Charges> findAll();
    Charges findById(Long id);

    Charges create(Charges charge);
    Charges update(Charges charge);
    void delete(Long id);

    List<Charges> findByCabinetId(Long cabinetId);
    List<Charges> findByTitre(String titre);
    List<Charges> findByDescription(String description);

    Double getTotalCharges(Long cabinetId);
    Double getTotalChargesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end);
    Double getTotalChargesByTitre(Long cabinetId, String titre);

    Long countCharges(Long cabinetId);
    Long countChargesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end);

    List<Charges> findChargesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end);
}



