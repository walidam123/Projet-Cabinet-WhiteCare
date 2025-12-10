package ma.whitecare.service.modules.caisse.impl;


import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;
import ma.whitecare.service.modules.caisse.api.RevenuesService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RevenuesServiceImpl implements RevenuesService {

    private final RevenuesRepository repo;

    public RevenuesServiceImpl(RevenuesRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Revenues> findAll() {
        return repo.findAll();
    }

    @Override
    public Revenues findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Revenues create(Revenues revenue) {
        if (revenue == null) throw new IllegalArgumentException("Revenue ne peut pas être null");
        repo.create(revenue);
        return revenue;
    }

    @Override
    public Revenues update(Revenues revenue) {
        if (revenue == null || revenue.getId() == null)
            throw new IllegalArgumentException("Revenue invalide");

        repo.update(revenue);
        return revenue;
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Revenues> findByCabinetId(Long cabinetId) {
        return repo.findByCabinetMedicaleId(cabinetId);
    }

    @Override
    public List<Revenues> findByTitle(String title) {
        return repo.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Revenues> findByDescription(String description) {
        return repo.findByDescriptionContainingIgnoreCase(description);
    }

    @Override
    public Optional<Revenues> findByTitleAndCabinetId(String title, Long cabinetId) {
        return repo.findByTitleAndCabinetId(title, cabinetId);
    }

    @Override
    public Double getTotalRevenues(Long cabinetId) {
        return repo.calculateTotalRevenues(cabinetId);
    }

    @Override
    public Double getTotalRevenuesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        return repo.calculateTotalRevenuesByPeriod(cabinetId, start, end);
    }

    @Override
    public Double getTotalRevenuesByTitle(Long cabinetId, String title) {
        return repo.calculateTotalRevenuesByTitle(cabinetId, title);
    }

    @Override
    public Double getAverageRevenue(Long cabinetId) {
        return repo.calculateAverageRevenue(cabinetId);
    }

    @Override
    public Long countRevenues(Long cabinetId) {
        return repo.countRevenuesByCabinet(cabinetId);
    }

    @Override
    public Long countRevenuesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        return repo.countRevenuesByPeriod(cabinetId, start, end);
    }

    @Override
    public List<Revenues> findRevenuesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        return repo.findRevenuesByCabinetAndPeriod(cabinetId, start, end);
    }
}

