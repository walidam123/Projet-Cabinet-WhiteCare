package ma.whitecare.service.modules.caisse.impl;

import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;
import ma.whitecare.repository.modules.facture.FactureRepository;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.mvc.dto.financial.CaisseStatsDTO;
import ma.whitecare.service.modules.caisse.api.CaisseService;

import java.time.LocalDateTime;
import java.util.List;

public class CaisseServiceImpl implements CaisseService {

        private final FactureRepository factureRepo;
        private final ChargesRepository chargesRepo;
        private final RevenuesRepository revenuesRepo;

        public CaisseServiceImpl(FactureRepository factureRepo,
                        ChargesRepository chargesRepo,
                        RevenuesRepository revenuesRepo) {
                this.factureRepo = factureRepo;
                this.chargesRepo = chargesRepo;
                this.revenuesRepo = revenuesRepo;
        }

        @Override
        public CaisseStatsDTO getGlobalStats() {
                List<Facture> factures = factureRepo.findAll();
                List<Charges> charges = chargesRepo.findAll();
                List<Revenues> revenues = revenuesRepo.findAll();

                return calculateStats(factures, charges, revenues);
        }

        @Override
        public CaisseStatsDTO getStatsByPeriod(LocalDateTime start, LocalDateTime end) {
                List<Facture> factures = factureRepo.findByDateBetween(start, end);
                // Assuming findByDateBetween exists in Charges/Revenues too, or filtering in
                // memory
                List<Charges> charges = chargesRepo.findAll().stream()
                                .filter(c -> !c.getDate().isBefore(start) && !c.getDate().isAfter(end))
                                .toList();
                List<Revenues> revenues = revenuesRepo.findAll().stream()
                                .filter(r -> !r.getDate().isBefore(start) && !r.getDate().isAfter(end))
                                .toList();

                return calculateStats(factures, charges, revenues);
        }

        @Override
        public CaisseStatsDTO getStats() {
                return getGlobalStats();
        }

        private CaisseStatsDTO calculateStats(List<Facture> factures, List<Charges> charges, List<Revenues> revenues) {
                double totalInvoiced = factures.stream()
                                .mapToDouble(f -> f.getTotaleFacture() != null ? f.getTotaleFacture() : 0.0).sum();
                double totalRevenueFactures = factures.stream()
                                .mapToDouble(f -> f.getTotalePayé() != null ? f.getTotalePayé() : 0.0).sum();
                double totalExtraRevenue = revenues.stream()
                                .mapToDouble(r -> r.getMontant() != null ? r.getMontant() : 0.0)
                                .sum();
                double totalCharges = charges.stream().mapToDouble(c -> c.getMontant() != null ? c.getMontant() : 0.0)
                                .sum();

                double totalRevenue = totalRevenueFactures + totalExtraRevenue;
                double balance = totalRevenue - totalCharges;

                return CaisseStatsDTO.builder()
                                .totalInvoiced(totalInvoiced)
                                .totalRevenue(totalRevenue)
                                .totalCharges(totalCharges)
                                .balance(balance)
                                .transactionCount((long) (factures.size() + revenues.size() + charges.size()))
                                .build();
        }
}
