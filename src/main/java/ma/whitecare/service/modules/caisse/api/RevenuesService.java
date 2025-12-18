package ma.whitecare.service.modules.caisse.api;

import ma.whitecare.entities.financial.Revenues;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenuesService {
    // === CRUD OPERATIONS ===
    Revenues createRevenue(Revenues revenue);
    Revenues getRevenueById(Long revenueId);
    List<Revenues> getAllRevenues();
    Revenues updateRevenue(Revenues revenue);
    void deleteRevenue(Long revenueId);

    // === FIND METHODS ===
    List<Revenues> getRevenuesByCabinet(Long cabinetId);
    List<Revenues> searchRevenuesByTitle(String title);
    List<Revenues> searchRevenuesByDescription(String description);

    // === CALCULATION METHODS ===
    Double calculateTotalRevenues(Long cabinetId);
    Double calculateTotalRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
    Double calculateTotalRevenuesByTitle(Long cabinetId, String title);
    Double calculateAverageRevenue(Long cabinetId);

    // === STATISTICS METHODS ===
    Long countRevenuesByCabinet(Long cabinetId);
    Long countRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
    List<Revenues> getRevenuesByCabinetAndPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);
}
