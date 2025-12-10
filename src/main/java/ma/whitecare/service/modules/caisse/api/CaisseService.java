package ma.whitecare.service.modules.caisse.api;


public interface CaisseService {

    Double getTotalRevenues(Long cabinetId);

    Double getTotalCharges(Long cabinetId);

    Double getSolde(Long cabinetId);  // revenus - charges

    Double getTotalRevenuesByPeriod(Long cabinetId, String start, String end);

    Double getTotalChargesByPeriod(Long cabinetId, String start, String end);

    Double getSoldeByPeriod(Long cabinetId, String start, String end);
}

