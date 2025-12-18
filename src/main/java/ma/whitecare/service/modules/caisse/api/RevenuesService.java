


package ma.whitecare.service.modules.caisse.api;


import ma.whitecare.entities.financial.Revenues;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RevenuesService {

    List<Revenues> findAll();
    Revenues findById(Long id);

    Revenues create(Revenues revenue);
    Revenues update(Revenues revenue);
    void delete(Long id);

    List<Revenues> findByCabinetId(Long cabinetId);
    List<Revenues> findByTitle(String title);
    List<Revenues> findByDescription(String description);

    Optional<Revenues> findByTitleAndCabinetId(String title, Long cabinetId);

    Double getTotalRevenues(Long cabinetId);
    Double getTotalRevenuesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end);
    Double getTotalRevenuesByTitle(Long cabinetId, String title);
    Double getAverageRevenue(Long cabinetId);

    Long countRevenues(Long cabinetId);
    Long countRevenuesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end);

    List<Revenues> findRevenuesByPeriod(Long cabinetId, LocalDateTime start, LocalDateTime end);
}



