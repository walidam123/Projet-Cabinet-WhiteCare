package ma.whitecare.repository.modules.cabinet.api;

import ma.whitecare.entities.cabinet.CabinetMedicale;

import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CabinetMedicaleRepository extends CrudRepository<CabinetMedicale, Long> {

    Optional<CabinetMedicale> findByNom(String nom);

    Optional<CabinetMedicale> findByEmail(String email);

    Optional<CabinetMedicale> findByCin(String cin);
    boolean existsById(Long cabinetId);
//test//test//test
    Double calculateTotalCharges(Long cabinetId);

    Double calculateChargesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);



    Double calculateTotalRevenues( Long cabinetId);


    Double calculateRevenuesByPeriod(Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);

    Double calculateProfit(Long cabinetId);

    Double calculateProfitByPeriod( Long cabinetId, LocalDateTime startDate, LocalDateTime endDate);

    Long countStaffByCabinet(Long cabinetId);



}
