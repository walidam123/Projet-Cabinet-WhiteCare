package ma.whitecare.repository.modules.cabinet.api;

import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RevenuesRepository extends CrudRepository<Revenues, Long> {


    // === FIND METHODS ===
    List<Revenues> findByCabinetMedicaleId(Long cabinetId);
    List<Revenues> findByTitleContainingIgnoreCase(String title);
    List<Revenues> findByDescriptionContainingIgnoreCase(String description);
    Optional<Revenues> findByTitleAndCabinetId(String title, Long cabinetId);

    // === CALCUL METHODS ===
    Double calculateTotalRevenues(Long cabinetId);
    Double calculateTotalRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
    Double calculateTotalRevenuesByTitle(Long cabinetId, String title);
    Double calculateAverageRevenue(Long cabinetId);


    // === STATISTICS METHODS ===
    Long countRevenuesByCabinet(Long cabinetId);
    Long countRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
    // === SEARCH METHODS ===
    List<Revenues> findRevenuesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);

}
