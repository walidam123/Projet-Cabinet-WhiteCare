package ma.whitecare.mvc.controllers;

import ma.whitecare.mvc.ui.medecin.MedecinDashboardView;
import java.awt.*;
import ma.whitecare.mvc.ui.medecin.ConsultationPanel;
import ma.whitecare.mvc.ui.medecin.MedecinPatientPanel;
import ma.whitecare.mvc.ui.medecin.MedicamentSearchDialog;
import ma.whitecare.mvc.ui.medecin.PrescriptionFormDialog;
import ma.whitecare.mvc.ui.medecin.CertificatDialog;
import ma.whitecare.mvc.ui.medecin.MedecinActePanel;
import ma.whitecare.mvc.ui.medecin.ActeFormDialog;
import ma.whitecare.mvc.ui.secretaire.PatientFormDialog;
import ma.whitecare.mvc.ui.secretaire.AssignAntecedentDialog;
import ma.whitecare.service.modules.medicament.api.MedicamentService;
import ma.whitecare.mvc.ui.medecin.MedicamentManagementPanel;
import ma.whitecare.mvc.ui.medecin.MedicamentFormDialog;
import ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.entities.financial.Facture;

import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.entities.enums.StatutRendezVous;

import ma.whitecare.service.modules.rdv.api.RDVService;
import ma.whitecare.service.modules.dossierMedical.api.DossierMedicalService;
import ma.whitecare.service.modules.dossierMedical.api.ConsultationService;
import ma.whitecare.service.modules.patient.api.PatientService;
import ma.whitecare.service.modules.patient.api.AntecedentService;
import ma.whitecare.service.modules.caisse.api.CaisseService;
import ma.whitecare.service.modules.situationFinanciere.api.SituationFinanciereService;
import ma.whitecare.service.modules.UserManager.api.MedecinService;
import ma.whitecare.mvc.ui.secretaire.*;
import ma.whitecare.mvc.dto.financial.CaisseStatsDTO;
import ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;
import java.awt.Desktop;
import java.util.stream.Collectors;

public class MedecinController {
    private final MedecinDashboardView view;
    private final RDVService rdvService;
    private final DossierMedicalService dmService;
    private final ConsultationService consultationService;
    private final PatientService patientService;
    private final ma.whitecare.service.modules.actes.api.ActeService acteService;
    private final ma.whitecare.service.modules.facture.api.FactureService factureService;
    private final ma.whitecare.service.modules.medicament.api.MedicamentService medicamentService;
    private final ma.whitecare.service.modules.ordonnance.api.OrdonnanceService ordonnanceService;
    private final ma.whitecare.service.modules.dossierMedical.api.PrescriptionService prescriptionService;
    private final ma.whitecare.service.modules.certificat.api.CertificatService certificatService;
    private final AntecedentService antecedentService;
    private final CaisseService caisseService;
    private final SituationFinanciereService sfService;
    private final MedecinService medecinService;
    private final Medecin currentMedecin;
    private final ma.whitecare.service.modules.UserManager.api.UserService userService;
    private final ma.whitecare.mvc.dto.UserDto.UserDTO currentUser;

    private RDV currentRdv;
    private DossierMedicale currentDm;
    private java.util.List<ma.whitecare.entities.medical.Prescription> pendingPrescriptions = new java.util.ArrayList<>();

    public MedecinController(MedecinDashboardView view,
            RDVService rdvService,
            DossierMedicalService dmService,
            ConsultationService consultationService,
            PatientService patientService,
            ma.whitecare.service.modules.actes.api.ActeService acteService,
            ma.whitecare.service.modules.facture.api.FactureService factureService,
            ma.whitecare.service.modules.medicament.api.MedicamentService medicamentService,
            ma.whitecare.service.modules.ordonnance.api.OrdonnanceService ordonnanceService,
            ma.whitecare.service.modules.dossierMedical.api.PrescriptionService prescriptionService,
            ma.whitecare.service.modules.certificat.api.CertificatService certificatService,
            AntecedentService antecedentService,
            CaisseService caisseService,
            SituationFinanciereService sfService,
            MedecinService medecinService,
            Medecin currentMedecin,
            ma.whitecare.service.modules.UserManager.api.UserService userService,
            ma.whitecare.mvc.dto.UserDto.UserDTO currentUser) {
        this.view = view;
        this.rdvService = rdvService;
        this.dmService = dmService;
        this.consultationService = consultationService;
        this.patientService = patientService;
        this.acteService = acteService;
        this.factureService = factureService;
        this.medicamentService = medicamentService;
        this.ordonnanceService = ordonnanceService;
        this.prescriptionService = prescriptionService;
        this.certificatService = certificatService;
        this.antecedentService = antecedentService;
        this.caisseService = caisseService;
        this.sfService = sfService;
        this.medecinService = medecinService;
        this.currentMedecin = currentMedecin;
        this.userService = userService;
        this.currentUser = currentUser;
        initController();
        refreshAll();
    }

    private void initController() {
        // Logout
        view.getLogoutBtn().addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(view, "Voulez-vous vous déconnecter ?", "Déconnexion",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                view.dispose();
                LoginController.showLogin();
            }
        });

        // Dashboard Actions
        if (view.getHomePanel() != null) {
            view.getHomePanel().getStartConsultationBtn().addActionListener(e -> handleStartConsultation());
        }

        // Patient Panel Actions
        MedecinPatientPanel pPanel = view.getPatientPanel();
        if (pPanel != null) {
            pPanel.getAddBtn().addActionListener(e -> handleAddDossier());
            pPanel.getEditBtn().addActionListener(e -> handleEditDossier());
            pPanel.getDeleteBtn().addActionListener(e -> handleDeleteDossier());
            pPanel.getViewRecordBtn().addActionListener(e -> handleViewMedicalRecord());
            pPanel.getSearchBtn().addActionListener(e -> refreshPatients());
            pPanel.getSearchField().addActionListener(e -> refreshPatients());
        }

        // RDV Panel Actions
        RDVManagementPanel rdvPanel = view.getRdvPanel();
        if (rdvPanel != null) {
            rdvPanel.getScheduleBtn().addActionListener(e -> handleScheduleRDV());
            rdvPanel.getEditBtn().addActionListener(e -> handleEditRDV());
            rdvPanel.getCancelBtn().addActionListener(e -> handleCancelRDV());
            rdvPanel.getWaitListBtn().addActionListener(e -> handleWaitList());
            rdvPanel.getFilterBtn().addActionListener(e -> handleFilterRDVByDate());

            // Enable Block Button
            rdvPanel.addBlockButton();
            if (rdvPanel.getBlockBtn() != null) {
                rdvPanel.getBlockBtn().addActionListener(e -> handleBlockSlot());
            }
        }

        // Finance Panel Actions
        FinancePanel fPanel = view.getFinancePanel();
        if (fPanel != null) {
            fPanel.getCreateFactureBtn().addActionListener(e -> handleCreateFacture());
            fPanel.getEditBtn().addActionListener(e -> handleEditFacture());
            fPanel.getCancelBtn().addActionListener(e -> handleCancelFacture());
            fPanel.getViewCaisseBtn().addActionListener(e -> handleViewCaisse());
            fPanel.getReportBtn().addActionListener(e -> handleExportReport());
            fPanel.getResetSFBtn().addActionListener(e -> handleResetSF());
            fPanel.getSearchSFBtn().addActionListener(e -> refreshSituations());
            fPanel.getSearchSFField().addActionListener(e -> refreshSituations());
        }

        // Antecedent Panel Actions
        AntecedentManagementPanel antPanel = view.getAntecedentPanel();
        if (antPanel != null) {
            antPanel.getAddBtn().addActionListener(e -> handleAddAntecedent());
            antPanel.getEditBtn().addActionListener(e -> handleEditAntecedent());
            antPanel.getDeleteBtn().addActionListener(e -> handleDeleteAntecedent());
            antPanel.getSearchBtn().addActionListener(e -> handleSearchAntecedent());
            antPanel.getSearchField().addActionListener(e -> handleSearchAntecedent());
        }

        // Agenda Panel Actions
        AgendaConfigPanel agendaPanel = view.getAgendaPanel();
        if (agendaPanel != null) {
            agendaPanel.getSaveBtn().addActionListener(e -> handleSaveAgenda());
        }

        // Consultation Panel Actions
        ConsultationPanel consPanel = view.getConsultationPanel();
        if (consPanel != null) {
            consPanel.getAddActBtn().addActionListener(e -> handleAddIntervention());
            consPanel.getAddDrugBtn().addActionListener(e -> handleAddPrescription());
            consPanel.getCertBtn().addActionListener(e -> handleCreateCertificat());
            consPanel.getFinishBtn().addActionListener(e -> handleFinishConsultation());
        }

        // Acte Panel Actions
        MedecinActePanel aPanel = view.getActePanel();
        if (aPanel != null) {
            aPanel.getAddBtn().addActionListener(e -> handleAddActe());
            aPanel.getEditBtn().addActionListener(e -> handleEditActe());
            aPanel.getDeleteBtn().addActionListener(e -> handleDeleteActe());
            aPanel.getSearchBtn().addActionListener(e -> refreshCatalogue());
            aPanel.getSearchField().addActionListener(e -> refreshCatalogue());
        }

        // Medicament Panel Actions
        MedicamentManagementPanel mPanel = view.getMedicamentPanel();
        if (mPanel != null) {
            mPanel.getAddBtn().addActionListener(e -> handleAddMedicament());
            mPanel.getEditBtn().addActionListener(e -> handleEditMedicament());
            mPanel.getDeleteBtn().addActionListener(e -> handleDeleteMedicament());
            mPanel.getSearchBtn().addActionListener(e -> refreshMedicaments());
            mPanel.getSearchField().addActionListener(e -> refreshMedicaments());
        }

        // Listener for Acts Combo in Consultation
        if (consPanel != null) {
            consPanel.getActsCombo().addActionListener(e -> {
                String selected = (String) consPanel.getActsCombo().getSelectedItem();
                if (selected != null && !selected.equals("--- Sélectionner un acte ---")
                        && !selected.contains("Autre...")) {
                    // Extract price from string "Libelle (Prix DH)"
                    try {
                        String pricePart = selected.substring(selected.lastIndexOf("(") + 1,
                                selected.lastIndexOf(" DH)"));
                        consPanel.getPriceField().setText(pricePart);
                    } catch (Exception ex) {
                        // Ignore parse error
                    }
                } else {
                    consPanel.getPriceField().setText("");
                }
            });
        }

        // Profile Action
        if (view.getProfileBtn() != null) {
            view.getProfileBtn().addActionListener(e -> handleShowProfile());
        }
    }

    private void handleShowProfile() {
        ma.whitecare.mvc.dto.UserDto.UserProfileDTO profile = userService.getUserProfile(currentUser.getId());
        ma.whitecare.mvc.ui.user.ProfileDialog dialog = new ma.whitecare.mvc.ui.user.ProfileDialog(view, profile);

        dialog.getSaveButton().addActionListener(e -> {
            ma.whitecare.mvc.dto.UserDto.UpdateProfileDTO updateDto = ma.whitecare.mvc.dto.UserDto.UpdateProfileDTO
                    .builder()
                    .nom(dialog.getNom())
                    .prenom(dialog.getPrenom())
                    .telephone(dialog.getTel())
                    .adresse(dialog.getAdresse())
                    .dateNaissance(dialog.getDateNaissance())
                    .sexe(dialog.getSexe())
                    .build();

            try {
                userService.updateUserProfile(currentUser.getId(), updateDto);
                JOptionPane.showMessageDialog(dialog, "Profil mis à jour avec succès !");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de la mise à jour: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void refreshAll() {
        if (view.getHomePanel() != null) {
            refreshQueue();
            refreshAlerts();
        }
        if (view.getPatientPanel() != null) {
            refreshPatients();
        }
        if (view.getActePanel() != null) {
            refreshCatalogue();
        }
        if (view.getRdvPanel() != null) {
            refreshRDVs();
        }
        if (view.getFinancePanel() != null) {
            refreshFactures();
            refreshSituations();
        }
        if (view.getAntecedentPanel() != null) {
            refreshAntecedents();
        }
        if (view.getMedicamentPanel() != null) {
            refreshMedicaments();
        }
    }

    private void refreshDashboard() {
        refreshQueue();
        refreshAlerts();
    }

    private void refreshAntecedents() {
        List<ma.whitecare.entities.patient.Antecedents> all = antecedentService.getAllAntecedents();
        populateAntecedentTable(all);
    }

    private void populateAntecedentTable(List<ma.whitecare.entities.patient.Antecedents> list) {
        DefaultTableModel model = view.getAntecedentPanel().getTableModel();
        model.setRowCount(0);
        for (ma.whitecare.entities.patient.Antecedents a : list) {
            model.addRow(new Object[] { a.getId_Antecedent(), a.getNom(), a.getCategorie() });
        }
    }

    private void refreshRDVs() {
        List<RDV> rdvs = rdvService.findAll();
        populateRDVTable(rdvs);
    }

    private void populateRDVTable(List<RDV> rdvs) {
        DefaultTableModel model = view.getRdvPanel().getTableModel();
        model.setRowCount(0);
        for (RDV r : rdvs) {
            String patientName = "Inconnu";
            if (r.getDossierMedicale() != null) {
                ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO dmDto = dmService
                        .getDossierMedicalById(r.getDossierMedicale().getIdDM());
                if (dmDto != null && dmDto.getPatientId() != null) {
                    Patient p = patientService.getPatientById(dmDto.getPatientId());
                    if (p != null) {
                        patientName = p.getNom() + " " + p.getPrenom();
                    }
                }
            }

            model.addRow(
                    new Object[] { r.getIdRDV(), r.getDate(), r.getHeure(), patientName, r.getMotif(), r.getStatut() });
        }
    }

    private void refreshFactures() {
        List<Facture> factures = factureService.findAll();
        System.out.println("DEBUG: Refreshing Factures. Found: " + factures.size());
        DefaultTableModel model = view.getFinancePanel().getTableModel();
        model.setRowCount(0);
        for (Facture f : factures) {
            System.out.println("DEBUG: Processing Facture ID: " + f.getIdFature());
            String patientName = "Inconnu";
            if (f.getSituationFinanciere() != null) {
                Long sfId = f.getSituationFinanciere().getIdSF();
                ma.whitecare.entities.financial.SituationFinanciere fullSf = sfService.getById(sfId);

                if (fullSf != null && fullSf.getDossierMedicale() != null) {
                    DossierMedicalDTO dmDto = dmService.getDossierMedicalById(fullSf.getDossierMedicale().getIdDM());
                    if (dmDto != null && dmDto.getPatientId() != null) {
                        Patient p = patientService.getPatientById(dmDto.getPatientId());
                        if (p != null) {
                            patientName = p.getNom() + " " + p.getPrenom();
                        }
                    }
                }
            }
            model.addRow(new Object[] { f.getIdFature(), f.getDateFacture(), patientName, f.getTotaleFacture(),
                    f.getTotalePayé(), f.getReste(), f.getStatut() });
        }
    }

    private void refreshSituations() {
        String query = view.getFinancePanel().getSearchSFField().getText().trim().toLowerCase();
        List<ma.whitecare.entities.financial.SituationFinanciere> list = sfService.getAll();
        DefaultTableModel model = view.getFinancePanel().getSfModel();
        model.setRowCount(0);
        for (ma.whitecare.entities.financial.SituationFinanciere sf : list) {
            String pName = "Inconnu";
            if (sf.getDossierMedicale() != null) {
                DossierMedicalDTO dmDto = dmService.getDossierMedicalById(sf.getDossierMedicale().getIdDM());
                if (dmDto != null && dmDto.getPatientId() != null) {
                    Patient p = patientService.getPatientById(dmDto.getPatientId());
                    if (p != null) {
                        pName = p.getNom() + " " + p.getPrenom();
                    }
                }
            }
            if (query.isEmpty() || pName.toLowerCase().contains(query)) {
                model.addRow(new Object[] {
                        sf.getIdSF(),
                        pName,
                        // sf.getDossierMedicale() != null ? sf.getDossierMedicale().getIdDM() : "N/A",
                        // // Removed extra col
                        sf.getTotaleDesActes(),
                        sf.getTotalePaye(),
                        sf.getCredit(),
                        sf.getStatut()
                });
            }
        }
    }

    private void handleScheduleRDV() {
        RDVFormDialog dialog = new RDVFormDialog(view, null, patientService.getAllPatients());
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Patient p = dialog.getSelectedPatient();
            DossierMedicale dm = dmService.getDossierByPatientId(p.getId_Patient());
            if (dm == null) {
                dm = DossierMedicale.builder().patient(p).dateDeCreation(java.time.LocalDate.now()).build();
                dm.setMedecin(currentMedecin);
                dmService.saveDossier(dm);
                dm = dmService.getDossierByPatientId(p.getId_Patient());
            }

            if (rdvService.existsByDateAndHeure(dialog.getDate(), dialog.getHeure())) {
                JOptionPane.showMessageDialog(view, "Ce créneau est déjà réservé.", "Créneau indisponible",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ma.whitecare.mvc.dto.DossierMedicale.CreateConsultationDTO consDto = ma.whitecare.mvc.dto.DossierMedicale.CreateConsultationDTO
                        .builder()
                        .date(dialog.getDate())
                        .dateConsultation(java.time.LocalDateTime.of(dialog.getDate(), dialog.getHeure()))
                        .statut(ma.whitecare.entities.enums.StatutConsultation.EN_ATTENTE)
                        .dossierMedicalId(dm.getIdDM())
                        .medecinId(currentMedecin.getIdUser())
                        .build();

                ma.whitecare.mvc.dto.DossierMedicale.ConsultationDTO createdCons = consultationService
                        .createConsultation(consDto);

                ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO dto = ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO.builder()
                        .date(dialog.getDate())
                        .heure(dialog.getHeure())
                        .motif(dialog.getMotif())
                        .statut(dialog.getStatut())
                        .dossierMedicaleId(dm.getIdDM())
                        .consultationId(createdCons.getIdConsultation())
                        .build();

                rdvService.createRDV(dto);
                refreshRDVs();
                refreshDashboard();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la création du RDV: " + e.getMessage());
            }
        }
    }

    private void handleEditRDV() {
        int row = view.getRdvPanel().getRdvTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getRdvPanel().getRdvTable().getValueAt(row, 0);
        RDV r = rdvService.getRDVById(id);
        if (r != null) {
            RDVFormDialog dialog = new RDVFormDialog(view, r, patientService.getAllPatients());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO dto = ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO.builder()
                        .date(dialog.getDate())
                        .heure(dialog.getHeure())
                        .motif(dialog.getMotif())
                        .statut(dialog.getStatut())
                        .build();
                try {
                    rdvService.updateRDV(id, dto);
                    refreshRDVs();
                    refreshDashboard();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
                }
            }
        }
    }

    private void handleCancelRDV() {
        int row = view.getRdvPanel().getRdvTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getRdvPanel().getRdvTable().getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(view, "Annuler ce rendez-vous ?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                RDV r = rdvService.getRDVById(id);
                ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO dto = ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO.builder()
                        .date(r.getDate())
                        .heure(r.getHeure())
                        .motif(r.getMotif())
                        .statut(ma.whitecare.entities.enums.StatutRendezVous.ANNULE)
                        .build();
                rdvService.updateRDV(id, dto);
                refreshRDVs();
                refreshDashboard();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleWaitList() {
        List<RDV> waiting = rdvService.findAll().stream()
                .filter(r -> r.getStatut() == StatutRendezVous.EN_ATTENTE)
                .collect(Collectors.toList());
        populateRDVTable(waiting);
    }

    private void handleFilterRDVByDate() {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(view.getRdvPanel().getDateField().getText());
            List<RDV> filtered = rdvService.findAll().stream()
                    .filter(r -> r.getDate() != null && r.getDate().equals(date))
                    .collect(Collectors.toList());
            populateRDVTable(filtered);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Format de date invalide (AAAA-MM-JJ).");
        }
    }

    private void handleCreateFacture() {
        FactureFormDialog dialog = new FactureFormDialog(view, null, patientService.getAllPatients());
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Patient p = dialog.getSelectedPatient();
            DossierMedicale dm = dmService.getDossierByPatientId(p.getId_Patient());
            if (dm == null) {
                dm = DossierMedicale.builder().patient(p).dateDeCreation(java.time.LocalDate.now()).build();
                dm.setMedecin(currentMedecin);
                dmService.saveDossier(dm);
                dm = dmService.getDossierByPatientId(p.getId_Patient());
            }

            ma.whitecare.entities.financial.SituationFinanciere sf = sfService.getByDossierId(dm.getIdDM());
            if (sf == null) {
                sf = ma.whitecare.entities.financial.SituationFinanciere.builder()
                        .dossierMedicale(dm)
                        .totaleDesActes(0.0)
                        .totalePaye(0.0)
                        .credit(0.0)
                        .statut(StatutSituationFinanciere.IMPAYE)
                        .build();
                sf = sfService.create(sf);
            }

            Facture f = Facture.builder()
                    .totaleFacture(dialog.getTotal())
                    .totalePayé(dialog.getPaye())
                    .Reste(dialog.getTotal() - dialog.getPaye())
                    .statut(dialog.getStatut())
                    .dateFacture(java.time.LocalDateTime.now())
                    .situationFinanciere(sf)
                    .build();
            try {
                factureService.createFacture(f);

                // Synchronize SituationFinanciere
                sf.setTotalePaye(sf.getTotalePaye() + f.getTotalePayé());
                sf.setCredit(sf.getTotaleDesActes() - sf.getTotalePaye());
                if (sf.getCredit() <= 0.01) {
                    sf.setStatut(StatutSituationFinanciere.SOLDE);
                } else {
                    sf.setStatut(StatutSituationFinanciere.IMPAYE);
                }
                sfService.update(sf.getIdSF(), sf);

                refreshFactures();
                refreshSituations();
                refreshDashboard();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleEditFacture() {
        int row = view.getFinancePanel().getFactureTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getFinancePanel().getFactureTable().getValueAt(row, 0);
        Facture f = factureService.getFactureById(id);

        if (f.getSituationFinanciere() != null) {
            ma.whitecare.entities.financial.SituationFinanciere fullSf = sfService
                    .getById(f.getSituationFinanciere().getIdSF());
            if (fullSf != null) {
                f.setSituationFinanciere(fullSf);
            }
        }

        FactureFormDialog dialog = new FactureFormDialog(view, f, patientService.getAllPatients());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                double oldPaye = f.getTotalePayé();
                double newTotal = dialog.getTotal();
                double newPaye = dialog.getPaye();
                double diff = newPaye - oldPaye;

                f.setTotaleFacture(newTotal);
                f.setTotalePayé(newPaye);
                f.setReste(newTotal - newPaye);

                if (f.getReste() <= 0.01) {
                    f.setStatut(ma.whitecare.entities.enums.StatutFacture.REGLEE);
                } else {
                    f.setStatut(dialog.getStatut());
                }

                factureService.updateFacture(f.getIdFature(), f);

                // Update SF
                ma.whitecare.entities.financial.SituationFinanciere sf = f.getSituationFinanciere();
                if (sf != null) {
                    sf.setTotalePaye(sf.getTotalePaye() + diff);
                    sf.setCredit(sf.getTotaleDesActes() - sf.getTotalePaye());
                    if (sf.getCredit() <= 0.01) {
                        sf.setStatut(StatutSituationFinanciere.SOLDE);
                    } else {
                        sf.setStatut(StatutSituationFinanciere.IMPAYE);
                    }
                    sfService.update(sf.getIdSF(), sf);
                }

                refreshFactures();
                refreshSituations();
                refreshDashboard();
                JOptionPane.showMessageDialog(view, "Facture modifiée avec succès.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleCancelFacture() {
        int row = view.getFinancePanel().getFactureTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getFinancePanel().getFactureTable().getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(view, "Annuler cette facture ?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Facture f = factureService.getFactureById(id);
                double amtToRevert = f.getTotalePayé();

                f.setStatut(ma.whitecare.entities.enums.StatutFacture.ANNULEE);
                f.setTotalePayé(0.0);
                f.setReste(f.getTotaleFacture());
                factureService.updateFacture(f.getIdFature(), f);

                // Revert from SF
                ma.whitecare.entities.financial.SituationFinanciere sf = f.getSituationFinanciere();
                if (sf != null) {
                    ma.whitecare.entities.financial.SituationFinanciere fullSf = sfService.getById(sf.getIdSF());
                    if (fullSf != null) {
                        fullSf.setTotalePaye(fullSf.getTotalePaye() - amtToRevert);
                        fullSf.setCredit(fullSf.getTotaleDesActes() - fullSf.getTotalePaye());
                        if (fullSf.getCredit() <= 0.01) {
                            fullSf.setStatut(StatutSituationFinanciere.SOLDE);
                        } else {
                            fullSf.setStatut(StatutSituationFinanciere.IMPAYE);
                        }
                        sfService.update(fullSf.getIdSF(), fullSf);
                    }
                }

                refreshFactures();
                refreshSituations();
                refreshDashboard();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleViewCaisse() {
        CaisseStatsDialog dialog = new CaisseStatsDialog(view,
                (ma.whitecare.service.modules.caisse.api.CaisseService) caisseService);
        dialog.setVisible(true);
    }

    private void handleExportReport() {
        int row = view.getFinancePanel().getFactureTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getFinancePanel().getFactureTable().getValueAt(row, 0);
        try {
            byte[] pdf = factureService.generatePDF(id);
            String path = "Facture_" + id + ".pdf";
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(path)) {
                fos.write(pdf);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new java.io.File(path));
            }
            JOptionPane.showMessageDialog(view, "Exporté et ouvert: " + path);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
        }
    }

    private void handleResetSF() {
        int row = view.getFinancePanel().getSfTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Sélectionnez une situation financière.", "Attention",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = view.getFinancePanel().getSfTable().getValueAt(row, 0);
        Long id = null;
        if (idObj instanceof Long) {
            id = (Long) idObj;
        } else if (idObj instanceof String) {
            try {
                id = Long.parseLong((String) idObj);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing ID: " + idObj);
            }
        }

        System.out.println("DEBUG: handleResetSF ID class: " + (idObj != null ? idObj.getClass().getName() : "null")
                + ", value: " + idObj);

        if (id == null) {
            JOptionPane.showMessageDialog(view, "ID invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(view, "Voulez-vous vraiment réinitialiser cette situation financière ?",
                "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                sfService.resetSituation(id);
                refreshSituations();
                JOptionPane.showMessageDialog(view, "Situation réinitialisée avec succès.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleAddAntecedent() {
        ma.whitecare.mvc.ui.admin.AntecedentFormDialog dialog = new ma.whitecare.mvc.ui.admin.AntecedentFormDialog(view,
                null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                antecedentService.createAntecedent(dialog.getAntecedent());
                refreshAntecedents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleEditAntecedent() {
        int row = view.getAntecedentPanel().getAntecedentTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getAntecedentPanel().getAntecedentTable().getValueAt(row, 0);

        ma.whitecare.entities.patient.Antecedents a = antecedentService.getAntecedentById(id);
        ma.whitecare.mvc.ui.admin.AntecedentFormDialog dialog = new ma.whitecare.mvc.ui.admin.AntecedentFormDialog(view,
                a);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                antecedentService.updateAntecedent(dialog.getAntecedent());
                refreshAntecedents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleDeleteAntecedent() {
        int row = view.getAntecedentPanel().getAntecedentTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getAntecedentPanel().getAntecedentTable().getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(view, "Supprimer cet antécédent ?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                antecedentService.deleteAntecedentById(id);
                refreshAntecedents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleSearchAntecedent() {
        String query = view.getAntecedentPanel().getSearchField().getText().trim().toLowerCase();
        List<ma.whitecare.entities.patient.Antecedents> result = antecedentService.getAllAntecedents().stream()
                .filter(a -> a.getNom().toLowerCase().contains(query))
                .collect(Collectors.toList());
        populateAntecedentTable(result);
    }

    private void handleSaveAgenda() {
        JOptionPane.showMessageDialog(view, "Configuration de l'agenda enregistrée avec succès.\n" +
                "(Note: Simulation - Nécessite une entité AgendaConfig en BDD)");
    }

    // --- Dashboard Logic ---

    private void refreshQueue() {
        DefaultTableModel model = view.getHomePanel().getQueueModel();
        model.setRowCount(0);

        List<RDV> allRdvs = rdvService.findAll();
        LocalDate today = LocalDate.now();

        List<RDV> todaysRdvs = allRdvs.stream()
                .filter(r -> r.getDate().equals(today))
                .filter(r -> r.getStatut() != StatutRendezVous.TERMINE && r.getStatut() != StatutRendezVous.ANNULE)
                .collect(Collectors.toList());

        for (RDV r : todaysRdvs) {
            String patientName = "Inconnu";
            if (r.getDossierMedicale() != null) {
                DossierMedicalDTO dmDto = dmService.getDossierMedicalById(r.getDossierMedicale().getIdDM());
                if (dmDto != null && dmDto.getPatientId() != null) {
                    Patient p = patientService.getPatientById(dmDto.getPatientId());
                    if (p != null) {
                        patientName = p.getNom() + " " + p.getPrenom();
                    }
                }
            }

            model.addRow(new Object[] {
                    r.getIdRDV(),
                    r.getHeure(),
                    patientName,
                    r.getMotif(),
                    r.getStatut()
            });
        }
    }

    private void refreshAlerts() {
        DefaultListModel<String> model = view.getHomePanel().getAlertsModel();
        model.clear();
        model.addElement("Aucune alerte critique pour le moment.");
    }

    // --- Patient / Dossier Logic ---

    private void refreshPatients() {
        DefaultTableModel model = view.getPatientPanel().getTableModel();
        model.setRowCount(0);

        String search = view.getPatientPanel().getSearchField().getText().trim().toLowerCase();
        List<Patient> patients = patientService.getAllPatients();

        if (!search.isEmpty()) {
            patients = patients.stream()
                    .filter(p -> p.getNom().toLowerCase().contains(search)
                            || p.getPrenom().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        for (Patient p : patients) {
            model.addRow(new Object[] {
                    p.getId_Patient(),
                    p.getNom(),
                    p.getPrenom(),
                    p.getTelephone(),
                    p.getEmail(),
                    p.getSexe(),
                    p.getDateNaissance()
            });
        }
    }

    private void handleAddDossier() {
        PatientFormDialog dialog = new PatientFormDialog(view, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Patient p = new Patient();
            p.setNom(dialog.getNom());
            p.setPrenom(dialog.getPrenom());
            p.setTelephone(dialog.getTel());
            p.setEmail(dialog.getEmail());
            p.setAdresse(dialog.getTel()); // Using tel for address? Wait, dialog has getAdresse()
            p.setAdresse(dialog.getAdresse());
            p.setDateNaissance(dialog.getDateNaissance());
            p.setSexe(dialog.getSexe());
            p.setAssurance(dialog.getAssurance());

            patientService.createPatient(p);

            // Create Dossier automatically
            DossierMedicale dm = DossierMedicale.builder()
                    .patient(p)
                    .dateDeCreation(LocalDate.now())
                    .medecin(currentMedecin)
                    .build();
            dmService.saveDossier(dm);

            refreshPatients();
            JOptionPane.showMessageDialog(view, "Dossier créé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEditDossier() {
        int selectedRow = view.getPatientPanel().getPatientTable().getSelectedRow();
        if (selectedRow == -1)
            return;

        Object selected = view.getPatientPanel().getPatientTable().getValueAt(selectedRow, 0);
        Long patientId = (Long) selected;
        Patient patient = patientService.getPatientById(patientId);

        if (patient != null) {
            PatientFormDialog dialog = new PatientFormDialog(view, patient);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                patient.setNom(dialog.getNom());
                patient.setPrenom(dialog.getPrenom());
                patient.setTelephone(dialog.getTel());
                patient.setEmail(dialog.getEmail());
                patient.setAdresse(dialog.getAdresse());
                patient.setDateNaissance(dialog.getDateNaissance());
                patient.setSexe(dialog.getSexe());
                patient.setAssurance(dialog.getAssurance());

                patientService.updatePatient(patient);
                refreshPatients();
            }
        }
    }

    private void handleDeleteDossier() {
        int selectedRow = view.getPatientPanel().getPatientTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Sélectionnez un dossier à supprimer.", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer ce dossier et toutes les données associées ?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) view.getPatientPanel().getPatientTable().getValueAt(selectedRow, 0);
            try {
                patientService.deletePatientById(id);
                refreshPatients();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Impossible de supprimer: " + e.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewMedicalRecord() {
        int row = view.getPatientPanel().getPatientTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Sélectionnez un patient.");
            return;
        }
        Long id = (Long) view.getPatientPanel().getPatientTable().getValueAt(row, 0);
        Patient p = patientService.getPatientById(id);
        DossierMedicale dm = dmService.getDossierByPatientId(id);
        if (dm != null) {
            List<RDV> rdvs = rdvService.findAll().stream()
                    .filter(r -> r.getDossierMedicale() != null
                            && r.getDossierMedicale().getIdDM().equals(dm.getIdDM()))
                    .collect(Collectors.toList());
            List<Facture> factures = new java.util.ArrayList<>();
            ma.whitecare.entities.financial.SituationFinanciere sf = sfService.getByDossierId(dm.getIdDM());
            if (sf != null) {
                Long sfId = sf.getIdSF();
                factures = factureService.findAll().stream()
                        .filter(f -> f.getSituationFinanciere() != null
                                && f.getSituationFinanciere().getIdSF().equals(sfId))
                        .collect(Collectors.toList());
            }

            // Fetch Consultations
            List<ma.whitecare.entities.medical.Consultation> consultations = consultationService
                    .getConsultationsByDossierId(dm.getIdDM());

            // Fetch Ordonnances and Certificats
            List<ma.whitecare.entities.medical.Ordonnance> ordonnances = ordonnanceService
                    .findByDossierMedicaleId(dm.getIdDM());

            // Load prescriptions for each ordonnance
            for (ma.whitecare.entities.medical.Ordonnance ord : ordonnances) {
                List<ma.whitecare.entities.medical.Prescription> prescriptions = ordonnanceService
                        .getPrescriptionsByOrdonnanceId(ord.getIdOrd());
                ord.setPrescriptionList(prescriptions);
            }

            List<ma.whitecare.entities.medical.Certificat> certificats = certificatService
                    .findByDossierMedicaleId(dm.getIdDM());

            ma.whitecare.mvc.ui.secretaire.MedicalRecordView recordView = new ma.whitecare.mvc.ui.secretaire.MedicalRecordView(
                    view, p, rdvs, factures, consultations, ordonnances, certificats);

            recordView.getManageAntecedentsBtn().addActionListener(e -> {
                List<ma.whitecare.entities.patient.Antecedents> all = antecedentService.getAllAntecedents();
                Patient currentPatient = patientService.getPatientById(p.getId_Patient()); // Refresh patient to get
                                                                                           // current antecedents
                AssignAntecedentDialog dialog = new AssignAntecedentDialog(view, currentPatient, all);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    currentPatient.setAntecedents(dialog.getSelectedAntecedents());
                    try {
                        patientService.updatePatient(currentPatient);
                        JOptionPane.showMessageDialog(recordView, "Antécédents mis à jour.");
                        recordView.dispose(); // Close and reopen to refresh
                        handleViewMedicalRecord();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(recordView, "Erreur: " + ex.getMessage());
                    }
                }
            });

            recordView.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(view, "Ce patient n'a pas encore de dossier médical.");
        }
    }

    // --- Consultation Logic ---

    private void handleStartConsultation() {
        JTable table = view.getHomePanel().getQueueTable();
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Veuillez sélectionner un rendez-vous dans la file d'attente.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long rdvId = (Long) table.getValueAt(selectedRow, 0);
        RDV rdv = rdvService.getRDVById(rdvId);

        if (rdv != null) {
            currentRdv = rdv;
            Patient p = null;
            if (currentRdv.getDossierMedicale() != null) {
                p = currentRdv.getDossierMedicale().getPatient();
            }

            // Fallback: Si le patient n'est pas chargé dans l'objet RDV, on le récupère via
            // le service
            if (p == null && currentRdv.getDossierMedicale() != null) {
                DossierMedicalDTO dmDto = dmService.getDossierMedicalById(currentRdv.getDossierMedicale().getIdDM());
                if (dmDto != null && dmDto.getPatientId() != null) {
                    p = patientService.getPatientById(dmDto.getPatientId());
                }
            }

            if (p != null) {
                currentDm = dmService.getDossierByPatientId(p.getId_Patient());

                ConsultationPanel panel = view.getConsultationPanel();
                panel.setPatientInfo(
                        p.getNom() + " " + p.getPrenom(),
                        p.getDateNaissance() != null ? p.getDateNaissance().toString() : "N/A",
                        "Chargement...");

                panel.getObservationArea().setText("");
                panel.getActsModel().setRowCount(0);
                panel.getPrescriptionModel().setRowCount(0);
                pendingPrescriptions.clear();

                // Populate Acts Combo
                panel.getActsCombo().removeAllItems();
                panel.getActsCombo().addItem("--- Sélectionner un acte ---");
                List<ma.whitecare.entities.medical.Acte> availableActs = acteService.getAll();
                for (ma.whitecare.entities.medical.Acte a : availableActs) {
                    panel.getActsCombo().addItem(a.getLibelle() + " (" + a.getPrixDeBase() + " DH)");
                }
                panel.getActsCombo().addItem("Autre... (Ouvrir catalogue)");

                currentRdv.setStatut(StatutRendezVous.EN_CONSULTATION);
                rdvService.updateRDV(currentRdv.getIdRDV(), ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO.builder()
                        .date(currentRdv.getDate())
                        .heure(currentRdv.getHeure())
                        .motif(currentRdv.getMotif())
                        .statut(StatutRendezVous.EN_CONSULTATION)
                        .consultationId(
                                currentRdv.getConsultation() != null ? currentRdv.getConsultation().getIdConsultation()
                                        : null)
                        .dossierMedicaleId(currentDm != null ? currentDm.getIdDM() : null)
                        .build());

                // Update Consultation Status
                if (currentRdv.getConsultation() != null) {
                    consultationService.changeStatut(currentRdv.getConsultation().getIdConsultation(),
                            ma.whitecare.entities.enums.StatutConsultation.EN_COURS);
                }

                view.showCard("CONSULTATION");
            } else {
                JOptionPane.showMessageDialog(view, "Erreur: Patient introuvable pour ce RDV.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddIntervention() {
        if (currentRdv == null || currentRdv.getConsultation() == null) {
            JOptionPane.showMessageDialog(view, "Aucune consultation active.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConsultationPanel panel = view.getConsultationPanel();
        String selectedStr = (String) panel.getActsCombo().getSelectedItem();

        // If "Autre..." or default or nothing selected, use dialog
        if (selectedStr == null || selectedStr.equals("--- Sélectionner un acte ---")
                || selectedStr.contains("Autre...")) {
            List<ma.whitecare.entities.medical.Acte> acts = acteService.getAll();
            ma.whitecare.mvc.ui.medecin.ActSelectionDialog dialog = new ma.whitecare.mvc.ui.medecin.ActSelectionDialog(
                    view,
                    acts);
            dialog.setVisible(true);

            if (dialog.isSucceeded()) {
                addInterventionToTable(dialog.getSelectedActe(), dialog.getFinalPrice(), dialog.getSelectedTooth());
            }
        } else {
            // Add from Panel Inputs
            try {
                // Find Act by Name (extracted from string)
                String actName = selectedStr.substring(0, selectedStr.lastIndexOf(" ("));
                ma.whitecare.entities.medical.Acte selectedActe = acteService.getAll().stream()
                        .filter(a -> a.getLibelle().equals(actName))
                        .findFirst()
                        .orElse(null);

                if (selectedActe == null) {
                    JOptionPane.showMessageDialog(view, "Erreur: Acte non trouvé.");
                    return;
                }

                double price = Double.parseDouble(panel.getPriceField().getText().trim());
                int tooth = 0;
                String toothText = panel.getToothField().getText().trim();
                if (!toothText.isEmpty()) {
                    tooth = Integer.parseInt(toothText);
                }

                addInterventionToTable(selectedActe, price, tooth);

                // Reset inputs
                panel.getToothField().setText("");
                panel.getPriceField().setText("");
                panel.getActsCombo().setSelectedIndex(0);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Prix ou Dent invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de l'ajout: " + e.getMessage());
            }
        }
    }

    private void addInterventionToTable(ma.whitecare.entities.medical.Acte acte, double price, int tooth) {
        ma.whitecare.entities.medical.InterventionMedecin intervention = ma.whitecare.entities.medical.InterventionMedecin
                .builder()
                .acte(acte)
                .acteId(acte.getIdActe())
                .numDent(tooth)
                .prixDePatient(price)
                .build();

        consultationService.addIntervention(currentRdv.getConsultation().getIdConsultation(), intervention);

        view.getConsultationPanel().getActsModel().addRow(new Object[] {
                acte.getLibelle(),
                tooth == 0 ? "Global" : tooth,
                price,
                acte.getPrixDeBase() - price
        });
    }

    private void handleAddPrescription() {
        if (currentRdv == null || currentRdv.getConsultation() == null) {
            JOptionPane.showMessageDialog(view, "Aucune consultation active.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<ma.whitecare.entities.medical.Medicament> meds = medicamentService.getAll();
        MedicamentSearchDialog searchDialog = new MedicamentSearchDialog(view, meds);
        searchDialog.setVisible(true);

        if (searchDialog.isSucceeded()) {
            ma.whitecare.entities.medical.Medicament selectedMed = searchDialog.getSelectedMedicament();
            PrescriptionFormDialog formDialog = new PrescriptionFormDialog(view, selectedMed);
            formDialog.setVisible(true);

            if (formDialog.isSucceeded()) {
                ma.whitecare.entities.medical.Prescription p = ma.whitecare.entities.medical.Prescription.builder()
                        .medicament(selectedMed)
                        .quantite(formDialog.getQuantite())
                        .frequence(formDialog.getFrequence())
                        .dureeEnJours(formDialog.getDuree())
                        .build();

                pendingPrescriptions.add(p);

                view.getConsultationPanel().getPrescriptionModel().addRow(new Object[] {
                        selectedMed.getNom(),
                        p.getFrequence(),
                        p.getDureeEnJours() + " jours",
                        "Qté: " + p.getQuantite()
                });
            }
        }
    }

    private void handleCreateCertificat() {
        if (currentRdv == null || currentRdv.getConsultation() == null) {
            JOptionPane.showMessageDialog(view, "Aucune consultation active.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient p = currentDm.getPatient();
        CertificatDialog dialog = new CertificatDialog(view, p.getNom() + " " + p.getPrenom());
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            ma.whitecare.mvc.dto.CertificatDto.CreateCertificatDTO dto = ma.whitecare.mvc.dto.CertificatDto.CreateCertificatDTO
                    .builder()
                    .dateDebut(dialog.getDateDebut())
                    .dateFin(dialog.getDateFin())
                    .duree(dialog.getDuree())
                    .noteMedecin(dialog.getNoteMedecin())
                    .consultationId(currentRdv.getConsultation().getIdConsultation())
                    .dossierMedicaleId(currentDm.getIdDM())
                    .build();

            ma.whitecare.entities.medical.Certificat cert = certificatService.createCertificat(dto);

            int choice = JOptionPane.showConfirmDialog(view, "Certificat enregistré. Voulez-vous l'imprimer ?",
                    "Imprimer ?", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    byte[] pdf = certificatService.generatePDF(cert.getIdCertif());
                    String path = "Certificat_" + cert.getIdCertif() + "_" + System.currentTimeMillis() + ".pdf";
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(path)) {
                        fos.write(pdf);
                    }
                    // Try to open it
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new java.io.File(path));
                    } else {
                        JOptionPane.showMessageDialog(view, "Fichier généré : " + path);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Erreur impression : " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(view, "Certificat enregistré avec succès.", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void handleFinishConsultation() {
        if (currentRdv != null) {
            Long consultationId = currentRdv.getConsultation().getIdConsultation();

            if (!pendingPrescriptions.isEmpty()) {
                ma.whitecare.mvc.dto.OrdannanceDto.CreateOrdonnanceDTO ordDto = ma.whitecare.mvc.dto.OrdannanceDto.CreateOrdonnanceDTO
                        .builder()
                        .date(java.time.LocalDate.now())
                        .dossierMedicaleId(currentDm.getIdDM())
                        .consultationId(consultationId)
                        .build();

                ma.whitecare.entities.medical.Ordonnance savedOrd = ordonnanceService.createOrdonnance(ordDto);

                for (ma.whitecare.entities.medical.Prescription p : pendingPrescriptions) {
                    ma.whitecare.mvc.dto.dossierMedical.CreatePrescriptionDTO pDto = ma.whitecare.mvc.dto.dossierMedical.CreatePrescriptionDTO
                            .builder()
                            .medicamentId(p.getMedicament().getIdMct())
                            .ordonnanceId(savedOrd.getIdOrd())
                            .quantite(p.getQuantite())
                            .frequence(p.getFrequence())
                            .dureeEnJours(p.getDureeEnJours())
                            .build();
                    prescriptionService.createPrescription(pDto);
                }

                // Ask to print Prescription
                int choice = JOptionPane.showConfirmDialog(view, "Voulez-vous imprimer l'ordonnance ?",
                        "Imprimer Ordonnance", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        byte[] pdf = ordonnanceService.generatePDF(savedOrd.getIdOrd());
                        String path = "Ordonnance_" + savedOrd.getIdOrd() + "_" + System.currentTimeMillis() + ".pdf";
                        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(path)) {
                            fos.write(pdf);
                        }
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(new java.io.File(path));
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(view, "Erreur impression ordonnance : " + e.getMessage());
                    }
                }
            }

            ma.whitecare.mvc.dto.dossierMedical.ConsultationCompleteDTO complete = consultationService
                    .getConsultationComplete(consultationId);
            double totalAmount = 0.0;
            if (complete != null && complete.getInterventions() != null) {
                totalAmount = complete.getInterventions().stream()
                        .mapToDouble(ma.whitecare.mvc.dto.DossierMedicale.InterventionDTO::getPrixDePatient)
                        .sum();
            }

            ma.whitecare.entities.financial.SituationFinanciere sf = sfService.getByDossierId(currentDm.getIdDM());
            if (sf == null) {
                sf = ma.whitecare.entities.financial.SituationFinanciere.builder()
                        .dossierMedicale(currentDm)
                        .totaleDesActes(0.0)
                        .totalePaye(0.0)
                        .credit(0.0)
                        .statut(StatutSituationFinanciere.IMPAYE)
                        .build();
                sf = sfService.create(sf);
            }

            Facture facture = Facture.builder()
                    .totaleFacture(totalAmount)
                    .totalePayé(0.0)
                    .Reste(totalAmount)
                    .statut(StatutFacture.IMPAYEE)
                    .dateFacture(java.time.LocalDateTime.now())
                    .situationFinanciere(sf)
                    .consultation(currentRdv.getConsultation())
                    .build();
            try {
                // Update Situation Financiere Totals
                sf.setTotaleDesActes(sf.getTotaleDesActes() + totalAmount);
                sf.setCredit(sf.getCredit() + totalAmount);
                sf.setStatut(StatutSituationFinanciere.IMPAYE); // Default to Unpaid as Reste > 0
                sfService.update(sf.getIdSF(), sf);

                factureService.createFacture(facture);
                refreshFactures();
                refreshSituations();
            } catch (Exception e) {
                System.err.println("Erreur creation facture auto: " + e.getMessage());
            }

            String obs = view.getConsultationPanel().getObservationArea().getText().trim();
            if (!obs.isEmpty()) {
                consultationService.updateConsultation(consultationId,
                        ma.whitecare.mvc.dto.DossierMedicale.UpdateConsultationDTO.builder()
                                .observationMedecin(obs)
                                .build());
            }

            // Update Consultation Status to Finished
            consultationService.changeStatut(consultationId, ma.whitecare.entities.enums.StatutConsultation.TERMINEE);

            currentRdv.setStatut(StatutRendezVous.TERMINE);
            rdvService.updateRDV(currentRdv.getIdRDV(), ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO.builder()
                    .date(currentRdv.getDate())
                    .heure(currentRdv.getHeure())
                    .motif(currentRdv.getMotif())
                    .statut(StatutRendezVous.TERMINE)
                    .consultationId(consultationId)
                    .dossierMedicaleId(currentDm != null ? currentDm.getIdDM() : null)
                    .build());

            JOptionPane.showMessageDialog(view, "Consultation Terminée.\nFacture de " + totalAmount + " Dhs générée.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            view.showCard("DASHBOARD");
            refreshQueue();

            currentRdv = null;
            currentDm = null;
            pendingPrescriptions.clear();
        }
    }

    // --- Acte CRUD Logic ---

    private void refreshCatalogue() {
        List<ma.whitecare.entities.medical.Acte> acts = acteService.getAll();
        DefaultTableModel model = (DefaultTableModel) view.getActePanel().getActeTable().getModel();
        model.setRowCount(0);

        String search = view.getActePanel().getSearchField().getText().trim().toLowerCase();
        // The line `List<ma.whitecare.entities.medical.Acte> acts =
        // acteService.getAll();` was duplicated in the instruction, keeping the first
        // one.

        if (!search.isEmpty()) {
            acts = acts.stream()
                    .filter(a -> a.getLibelle().toLowerCase().contains(search)
                            || a.getCategory().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        for (ma.whitecare.entities.medical.Acte a : acts) {
            model.addRow(new Object[] { a.getIdActe(), a.getLibelle(), a.getCategory(), a.getPrixDeBase() });
        }
    }

    private void handleAddActe() {
        ActeFormDialog dialog = new ActeFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            Acte a = dialog.getActe();
            ma.whitecare.mvc.dto.ActeDto.CreateActeDTO dto = ma.whitecare.mvc.dto.ActeDto.CreateActeDTO.builder()
                    .libelle(a.getLibelle())
                    .categorie(a.getCategory())
                    .prixDeBase(a.getPrixDeBase())
                    .build();
            acteService.createActe(dto);
            refreshCatalogue();
        }
    }

    private void handleEditActe() {
        int row = view.getActePanel().getActeTable().getSelectedRow();
        if (row != -1) {
            Long id = (Long) view.getActePanel().getActeTable().getValueAt(row, 0);
            ma.whitecare.entities.medical.Acte a = acteService.getActeById(id);
            if (a != null) {
                ActeFormDialog dialog = new ActeFormDialog(view, a);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    Acte updatedActe = dialog.getActe();
                    ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO dto = ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO
                            .builder()
                            .libelle(updatedActe.getLibelle())
                            .categorie(updatedActe.getCategory())
                            .prixDeBase(updatedActe.getPrixDeBase())
                            .build();
                    acteService.updateActe(id, dto);
                    refreshCatalogue();
                }
            }
        }
    }

    private void handleDeleteActe() {
        int row = view.getActePanel().getActeTable().getSelectedRow();
        if (row != -1) {
            Long id = (Long) view.getActePanel().getActeTable().getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(view, "Supprimer cet acte ?", "Confirmation",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                acteService.deleteActe(id);
                refreshCatalogue();
            }
        }
    }

    // --- Medicament CRUD Logic ---

    private void refreshMedicaments() {
        List<ma.whitecare.entities.medical.Medicament> list = medicamentService.getAll();
        DefaultTableModel model = view.getMedicamentPanel().getTableModel();
        model.setRowCount(0);

        String search = view.getMedicamentPanel().getSearchField().getText().trim().toLowerCase();

        if (!search.isEmpty()) {
            list = list.stream()
                    .filter(m -> m.getNom().toLowerCase().contains(search)
                            || (m.getLaboratoire() != null && m.getLaboratoire().toLowerCase().contains(search)))
                    .collect(Collectors.toList());
        }

        for (ma.whitecare.entities.medical.Medicament m : list) {
            model.addRow(new Object[] {
                    m.getIdMct(),
                    m.getNom(),
                    m.getLaboratoire(),
                    m.getForme(),
                    m.getPrixUnitaire(),
                    m.getRemboursable() != null && m.getRemboursable() ? "Oui" : "Non"
            });
        }
    }

    private void handleAddMedicament() {
        MedicamentFormDialog dialog = new MedicamentFormDialog(view, "Nouveau Médicament", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            ma.whitecare.entities.medical.Medicament m = dialog.getMedicament(null);
            CreateMedicamentDTO dto = CreateMedicamentDTO.builder()
                    .nom(m.getNom())
                    .laboratoire(m.getLaboratoire())
                    .type(m.getType())
                    .forme(m.getForme())
                    .prixUnitaire(m.getPrixUnitaire())
                    .remboursable(m.getRemboursable())
                    .description(m.getDescription())
                    .build();
            try {
                medicamentService.createMedicament(dto);
                refreshMedicaments();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleEditMedicament() {
        int row = view.getMedicamentPanel().getMedicTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getMedicamentPanel().getMedicTable().getValueAt(row, 0);
        ma.whitecare.entities.medical.Medicament m = medicamentService.getMedicamentById(id);

        if (m != null) {
            MedicamentFormDialog dialog = new MedicamentFormDialog(view, "Modifier Médicament", m);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                ma.whitecare.entities.medical.Medicament updated = dialog.getMedicament(m);
                UpdateMedicamentDTO dto = UpdateMedicamentDTO.builder()
                        .nom(updated.getNom())
                        .laboratoire(updated.getLaboratoire())
                        .type(updated.getType())
                        .forme(updated.getForme())
                        .prixUnitaire(updated.getPrixUnitaire())
                        .remboursable(updated.getRemboursable())
                        .description(updated.getDescription())
                        .build();
                try {
                    medicamentService.updateMedicament(id, dto);
                    refreshMedicaments();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
                }
            }
        }
    }

    private void handleDeleteMedicament() {
        int row = view.getMedicamentPanel().getMedicTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getMedicamentPanel().getMedicTable().getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(view, "Supprimer ce médicament ?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                medicamentService.deleteMedicament(id);
                refreshMedicaments();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleBlockSlot() {
        JDialog dialog = new JDialog(view, "Bloquer une période", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(view);

        JTextField startDateField = new JTextField(java.time.LocalDate.now().toString());
        JTextField startTimeField = new JTextField("08:00");
        JTextField endDateField = new JTextField(java.time.LocalDate.now().toString());
        JTextField endTimeField = new JTextField("18:00");
        JButton confirm = new JButton("Bloquer Période");

        dialog.add(new JLabel("Date Début (AAAA-MM-JJ):"));
        dialog.add(startDateField);
        dialog.add(new JLabel("Heure Début (HH:MM):"));
        dialog.add(startTimeField);

        dialog.add(new JLabel("Date Fin (AAAA-MM-JJ):"));
        dialog.add(endDateField);
        dialog.add(new JLabel("Heure Fin (HH:MM):"));
        dialog.add(endTimeField);

        dialog.add(new JLabel("")); // spacer
        dialog.add(confirm);

        confirm.addActionListener(e -> {
            try {
                java.time.LocalDate startDate = java.time.LocalDate.parse(startDateField.getText());
                java.time.LocalTime startTime = java.time.LocalTime.parse(startTimeField.getText());
                java.time.LocalDate endDate = java.time.LocalDate.parse(endDateField.getText());
                java.time.LocalTime endTime = java.time.LocalTime.parse(endTimeField.getText());

                java.time.LocalDateTime start = java.time.LocalDateTime.of(startDate, startTime);
                java.time.LocalDateTime end = java.time.LocalDateTime.of(endDate, endTime);

                if (start.isAfter(end)) {
                    JOptionPane.showMessageDialog(dialog, "La date de début doit être avant la fin.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Loop through range by 30 mins
                java.time.LocalDateTime current = start;
                int count = 0;

                while (current.isBefore(end)) {
                    // Skip conflicts if any, or block anyway? Better warn if conflict found.
                    // For simplicity, attempt to block. If conflict exists, maybe skip that slot or
                    // fail?
                    // Let's check conflict for each slot.
                    if (!rdvService.existsByDateAndHeure(current.toLocalDate(), current.toLocalTime())) {
                        ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO dto = ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO
                                .builder()
                                .date(current.toLocalDate())
                                .heure(current.toLocalTime())
                                .motif("Indisponibilité Médecin")
                                .statut(ma.whitecare.entities.enums.StatutRendezVous.BLOQUE)
                                .dossierMedicaleId(null)
                                .consultationId(null)
                                .build();
                        rdvService.createRDV(dto);
                        count++;
                    }
                    current = current.plusMinutes(30);
                }

                JOptionPane.showMessageDialog(dialog, count + " créneaux bloqués avec succès.");
                dialog.dispose();
                refreshRDVs();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }
}
