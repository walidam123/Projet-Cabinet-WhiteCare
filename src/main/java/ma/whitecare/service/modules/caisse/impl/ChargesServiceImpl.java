package ma.whitecare.service.modules.caisse.impl;

import ma.whitecare.common.exceptions.CabinetNotFoundException;
import ma.whitecare.common.exceptions.ChargesNotFoundException;
import ma.whitecare.common.exceptions.InvalidChargeException;
import ma.whitecare.common.validators.ChargesValidator;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.service.modules.caisse.api.ChargesService;

import java.time.LocalDateTime;
import java.util.List;

public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepository;
    private final CabinetMedicaleRepository cabinetRepository;

    public ChargesServiceImpl(ChargesRepository chargesRepository,
                              CabinetMedicaleRepository cabinetRepository) {
        this.chargesRepository = chargesRepository;
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public Charges createCharge(Charges charge) {
        // Validation des données
        ChargesValidator.validateChargeOrThrow(charge);

        // Vérifier que le cabinet existe
        validateCabinetExists(charge.getCabinetMedicaleId());

        // Créer la charge
        chargesRepository.create(charge);
        return charge;
    }

    @Override
    public Charges getChargeById(Long chargeId) {
        if (chargeId == null || chargeId <= 0) {
            throw new InvalidChargeException("L'ID de la charge doit être positif");
        }

        Charges charge = chargesRepository.findById(chargeId);
        if (charge == null) {
            throw new ChargesNotFoundException(chargeId);
        }
        return charge;
    }

    @Override
    public List<Charges> getAllCharges() {
        return chargesRepository.findAll();
    }

    @Override
    public Charges updateCharge(Charges charge) {
        // Validation des données pour la mise à jour
        ChargesValidator.validateChargeForUpdateOrThrow(charge);

        // Vérifier que la charge existe
        Charges existingCharge = getChargeById(charge.getId());

        // Vérifier que le cabinet existe si modifié
        if (!existingCharge.getCabinetMedicaleId().equals(charge.getCabinetMedicaleId())) {
            validateCabinetExists(charge.getCabinetMedicaleId());
        }

        // Mettre à jour la charge
        chargesRepository.update(charge);
        return charge;
    }

    @Override
    public void deleteCharge(Long chargeId) {
        // Vérifier que la charge existe
        getChargeById(chargeId);

        // Supprimer la charge
        chargesRepository.deleteById(chargeId);
    }

    @Override
    public List<Charges> getChargesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return chargesRepository.findByCabinetMedicaleId(cabinetId);
    }

    @Override
    public List<Charges> searchChargesByTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new InvalidChargeException("Le titre de recherche ne peut pas être vide");
        }
        return chargesRepository.findByTitreContainingIgnoreCase(titre);
    }

    @Override
    public List<Charges> searchChargesByDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidChargeException("La description de recherche ne peut pas être vide");
        }
        return chargesRepository.findByDescriptionContainingIgnoreCase(description);
    }

    @Override
    public Double calculateTotalCharges(Long cabinetId) {
        validateCabinetExists(cabinetId);
        Double total = chargesRepository.calculateTotalCharges(cabinetId);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calculateTotalChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCabinetExists(cabinetId);
        validateDateRange(startDate, endDate);
        Double total = chargesRepository.calculateTotalChargesByPeriod(cabinetId, startDate, endDate);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calculateTotalChargesByTitre(Long cabinetId, String titre) {
        validateCabinetExists(cabinetId);
        if (titre == null || titre.trim().isEmpty()) {
            throw new InvalidChargeException("Le titre ne peut pas être vide");
        }
        Double total = chargesRepository.calculateTotalChargesByTitre(cabinetId, titre);
        return total != null ? total : 0.0;
    }

    @Override
    public Long countChargesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return chargesRepository.countChargesByCabinet(cabinetId);
    }

    @Override
    public Long countChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCabinetExists(cabinetId);
        validateDateRange(startDate, endDate);
        return chargesRepository.countChargesByPeriod(cabinetId, startDate, endDate);
    }

    @Override
    public List<Charges> getChargesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCabinetExists(cabinetId);
        validateDateRange(startDate, endDate);
        return chargesRepository.findChargesByCabinetAndPeriod(cabinetId, startDate, endDate);
    }

    // === PRIVATE HELPER METHODS ===

    private void validateCabinetExists(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new InvalidChargeException("L'ID du cabinet médical doit être positif");
        }
        if (!cabinetRepository.existsById(cabinetId)) {
            throw new CabinetNotFoundException(cabinetId);
        }
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            throw new InvalidChargeException("La date de début est obligatoire");
        }
        if (endDate == null) {
            throw new InvalidChargeException("La date de fin est obligatoire");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidChargeException("La date de début doit être antérieure à la date de fin");
        }
    }
}
