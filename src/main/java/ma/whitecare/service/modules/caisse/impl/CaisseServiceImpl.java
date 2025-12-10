package ma.whitecare.service.modules.caisse.impl;



import ma.whitecare.service.modules.caisse.api.CaisseService;
import ma.whitecare.service.modules.caisse.api.ChargesService;
import ma.whitecare.service.modules.caisse.api.RevenuesService;

import java.time.LocalDateTime;

public class CaisseServiceImpl implements CaisseService {

    private final RevenuesService revenuesService;
    private final ChargesService chargesService;

    public CaisseServiceImpl(RevenuesService revenuesService,
                             ChargesService chargesService) {
        this.revenuesService = revenuesService;
        this.chargesService = chargesService;
    }

    @Override
    public Double getTotalRevenues(Long cabinetId) {
        return revenuesService.getTotalRevenues(cabinetId);
    }

    @Override
    public Double getTotalCharges(Long cabinetId) {
        return chargesService.getTotalCharges(cabinetId);
    }

    @Override
    public Double getSolde(Long cabinetId) {
        return getTotalRevenues(cabinetId) - getTotalCharges(cabinetId);
    }

    @Override
    public Double getTotalRevenuesByPeriod(Long cabinetId, String start, String end) {
        LocalDateTime s = LocalDateTime.parse(start);
        LocalDateTime e = LocalDateTime.parse(end);
        return revenuesService.getTotalRevenuesByPeriod(cabinetId, s, e);
    }

    @Override
    public Double getTotalChargesByPeriod(Long cabinetId, String start, String end) {
        LocalDateTime s = LocalDateTime.parse(start);
        LocalDateTime e = LocalDateTime.parse(end);
        return chargesService.getTotalChargesByPeriod(cabinetId, s, e);
    }

    @Override
    public Double getSoldeByPeriod(Long cabinetId, String start, String end) {
        return getTotalRevenuesByPeriod(cabinetId, start, end)
                - getTotalChargesByPeriod(cabinetId, start, end);
    }
}


