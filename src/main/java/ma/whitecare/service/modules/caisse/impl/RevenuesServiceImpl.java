package ma.whitecare.service.modules.caisse.impl;

import ma.whitecare.common.exceptions.CabinetNotFoundException;
import ma.whitecare.common.exceptions.InvalidRevenueException;
import ma.whitecare.common.exceptions.RevenuesNotFoundException;
import ma.whitecare.common.validators.RevenuesValidator;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;
import ma.whitecare.service.modules.caisse.api.RevenuesService;

import java.time.LocalDateTime;
import java.util.List;

public class RevenuesServiceImpl implements RevenuesService {

    private final RevenuesRepository revenuesRepository;
    private final CabinetMedicaleRepository cabinetRepository;

    public RevenuesServiceImpl(RevenuesRepository revenuesRepository,
                                CabinetMedicaleRepository cabinetRepository) {
        this.revenuesRepository = revenuesRepository;
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public Revenues createRevenue(Revenues revenue) {
        // Validation des données
        RevenuesValidator.validateRevenueOrThrow(revenue);

        // Vérifier que le cabinet existe
        validateCabinetExists(revenue.getCabinetMedicaleId());

        // Créer le revenu
        revenuesRepository.create(revenue);
        return revenue;
    }

    @Override
    public Revenues getRevenueById(Long revenueId) {
        if (revenueId == null || revenueId <= 0) {
            throw new InvalidRevenueException("L'ID du revenu doit être positif");
        }

        Revenues revenue = revenuesRepository.findById(revenueId);
        if (revenue == null) {
            throw new RevenuesNotFoundException(revenueId);
        }
        return revenue;
    }

    @Override
    public List<Revenues> getAllRevenues() {
        return revenuesRepository.findAll();
    }

    @Override
    public Revenues updateRevenue(Revenues revenue) {
        // Validation des données pour la mise à jour
        RevenuesValidator.validateRevenueForUpdateOrThrow(revenue);

        // Vérifier que le revenu existe
        Revenues existingRevenue = getRevenueById(revenue.getId());

        // Vérifier que le cabinet existe si modifié
        if (!existingRevenue.getCabinetMedicaleId().equals(revenue.getCabinetMedicaleId())) {
            validateCabinetExists(revenue.getCabinetMedicaleId());
        }

        // Mettre à jour le revenu
        revenuesRepository.update(revenue);
        return revenue;
    }

    @Override
    public void deleteRevenue(Long revenueId) {
        // Vérifier que le revenu existe
        getRevenueById(revenueId);

        // Supprimer le revenu
        revenuesRepository.deleteById(revenueId);
    }

    @Override
    public List<Revenues> getRevenuesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return revenuesRepository.findByCabinetMedicaleId(cabinetId);
    }

    @Override
    public List<Revenues> searchRevenuesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidRevenueException("Le titre de recherche ne peut pas être vide");
        }
        return revenuesRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Revenues> searchRevenuesByDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidRevenueException("La description de recherche ne peut pas être vide");
        }
        return revenuesRepository.findByDescriptionContainingIgnoreCase(description);
    }

    @Override
    public Double calculateTotalRevenues(Long cabinetId) {
        validateCabinetExists(cabinetId);
        Double total = revenuesRepository.calculateTotalRevenues(cabinetId);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calculateTotalRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCabinetExists(cabinetId);
        validateDateRange(startDate, endDate);
        Double total = revenuesRepository.calculateTotalRevenuesByPeriod(cabinetId, startDate, endDate);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calculateTotalRevenuesByTitle(Long cabinetId, String title) {
        validateCabinetExists(cabinetId);
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidRevenueException("Le titre ne peut pas être vide");
        }
        Double total = revenuesRepository.calculateTotalRevenuesByTitle(cabinetId, title);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calculateAverageRevenue(Long cabinetId) {
        validateCabinetExists(cabinetId);
        Double average = revenuesRepository.calculateAverageRevenue(cabinetId);
        return average != null ? average : 0.0;
    }

    @Override
    public Long countRevenuesByCabinet(Long cabinetId) {
        validateCabinetExists(cabinetId);
        return revenuesRepository.countRevenuesByCabinet(cabinetId);
    }

    @Override
    public Long countRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCabinetExists(cabinetId);
        validateDateRange(startDate, endDate);
        return revenuesRepository.countRevenuesByPeriod(cabinetId, startDate, endDate);
    }

    @Override
    public List<Revenues> getRevenuesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate) {
        validateCabinetExists(cabinetId);
        validateDateRange(startDate, endDate);
        return revenuesRepository.findRevenuesByCabinetAndPeriod(cabinetId, startDate, endDate);
    }

    // === PRIVATE HELPER METHODS ===

    private void validateCabinetExists(Long cabinetId) {
        if (cabinetId == null || cabinetId <= 0) {
            throw new InvalidRevenueException("L'ID du cabinet médical doit être positif");
        }
        if (!cabinetRepository.existsById(cabinetId)) {
            throw new CabinetNotFoundException(cabinetId);
        }
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            throw new InvalidRevenueException("La date de début est obligatoire");
        }
        if (endDate == null) {
            throw new InvalidRevenueException("La date de fin est obligatoire");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidRevenueException("La date de début doit être antérieure à la date de fin");
        }
    }
}
