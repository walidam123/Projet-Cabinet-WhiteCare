package ma.whitecare.service.modules.caisse.impl;

import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.service.modules.caisse.api.ChargesService;
import java.time.LocalDateTime;
import java.util.List;

public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepo;

    public ChargesServiceImpl(ChargesRepository chargesRepo) {
        this.chargesRepo = chargesRepo;
    }

    @Override
    public List<Charges> findAll() {
        return chargesRepo.findAll();
    }

    @Override
    public Charges findById(Long id) {
        return chargesRepo.findById(id);
    }

    @Override
    public Charges create(Charges charge) {
        if (charge == null) throw new IllegalArgumentException("Charge ne peut pas être null");
        chargesRepo.create(charge);
        return charge;
    }

    @Override
    public Charges update(Charges charge) {
        if (charge == null || charge.getId() == null)
            throw new IllegalArgumentException("Charge invalide");

        chargesRepo.update(charge);
        return charge;
    }

    @Override
    public void delete(Long id) {
        chargesRepo.deleteById(id);
    }

    @Override
    public List<Charges> findByCabinetId(Long cabinetId) {
        return chargesRepo.findByCabinetMedicaleId(cabinetId);
    }

    @Override
    public List<Charges> findByTitre(String titre) {
        return chargesRepo.findByTitreContainingIgnoreCase(titre);
    }

    @Override
    public List<Charges> findByDescription(String description) {
        return chargesRepo.findByDescriptionContainingIgnoreCase(description);
    }

    @Override
    public Double getTotalCharges(Long cabinetId) {
        return chargesRepo.calculateTotalCharges(cabinetId);
    }

    @Override
    public Double getTotalChargesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        return chargesRepo.calculateTotalChargesByPeriod(cabinetId, start, end);
    }

    @Override
    public Double getTotalChargesByTitre(Long cabinetId, String titre) {
        return chargesRepo.calculateTotalChargesByTitre(cabinetId, titre);
    }

    @Override
    public Long countCharges(Long cabinetId) {
        return chargesRepo.countChargesByCabinet(cabinetId);
    }

    @Override
    public Long countChargesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        return chargesRepo.countChargesByPeriod(cabinetId, start, end);
    }

    @Override
    public List<Charges> findChargesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        return chargesRepo.findChargesByCabinetAndPeriod(cabinetId, start, end);
    }
}


