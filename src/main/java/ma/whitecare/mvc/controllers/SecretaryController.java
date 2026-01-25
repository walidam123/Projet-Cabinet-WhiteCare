package ma.whitecare.mvc.controllers;

import ma.whitecare.mvc.ui.secretaire.*;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.service.modules.patient.api.PatientService;
import ma.whitecare.service.modules.rdv.api.RDVService;
import ma.whitecare.service.modules.facture.api.FactureService;
import ma.whitecare.service.modules.patient.api.AntecedentService;
import ma.whitecare.service.modules.caisse.api.CaisseService;
import ma.whitecare.service.modules.dossierMedical.api.DossierMedicalService;
import ma.whitecare.service.modules.situationFinanciere.api.SituationFinanciereService;
import ma.whitecare.mvc.dto.financial.CaisseStatsDTO;
import ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import java.awt.Desktop;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import java.util.List;
import java.util.stream.Collectors;

public class SecretaryController {
    private final SecretaryDashboardView view;
    private final PatientService patientService;
    private final RDVService rdvService;
    private final FactureService factureService;
    private final AntecedentService antecedentService;
    private final CaisseService caisseService;
    private final DossierMedicalService dmService;
    private final SituationFinanciereService sfService;
    private final ma.whitecare.service.modules.UserManager.api.MedecinService medecinService;
    private final ma.whitecare.entities.user.Secretaire currentSecretary;
    private final ma.whitecare.service.modules.dossierMedical.api.ConsultationService consultationService;
    private final ma.whitecare.service.modules.ordonnance.api.OrdonnanceService ordonnanceService;
    private final ma.whitecare.service.modules.certificat.api.CertificatService certificatService;
    private final ma.whitecare.service.modules.UserManager.api.UserService userService;
    private final ma.whitecare.mvc.dto.UserDto.UserDTO currentUser;

    public SecretaryController(SecretaryDashboardView view,
            PatientService patientService,
            RDVService rdvService,
            FactureService factureService,
            AntecedentService antecedentService,
            CaisseService caisseService,
            DossierMedicalService dmService,
            SituationFinanciereService sfService,
            ma.whitecare.service.modules.UserManager.api.MedecinService medecinService,
            ma.whitecare.entities.user.Secretaire currentSecretary,
            ma.whitecare.service.modules.dossierMedical.api.ConsultationService consultationService,
            ma.whitecare.service.modules.ordonnance.api.OrdonnanceService ordonnanceService,
            ma.whitecare.service.modules.certificat.api.CertificatService certificatService,
            ma.whitecare.service.modules.UserManager.api.UserService userService,
            ma.whitecare.mvc.dto.UserDto.UserDTO currentUser) {
        this.view = view;
        this.patientService = patientService;
        this.rdvService = rdvService;
        this.factureService = factureService;
        this.antecedentService = antecedentService;
        this.caisseService = caisseService;
        this.dmService = dmService;
        this.sfService = sfService;
        this.medecinService = medecinService;
        this.currentSecretary = currentSecretary;
        this.consultationService = consultationService;
        this.ordonnanceService = ordonnanceService;
        this.certificatService = certificatService;
        this.userService = userService;
        this.currentUser = currentUser;

        initController();
        refreshAll();
    }

    private void initController() {
        view.getLogoutBtn().addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(view, "Voulez-vous vous déconnecter ?", "Déconnexion",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                view.dispose();
                LoginController.showLogin();
            }
        });

        // Profile Action
        if (view.getProfileBtn() != null) {
            view.getProfileBtn().addActionListener(e -> handleShowProfile());
        }

        // Patient Panel
        view.getPatientPanel().getAddBtn().addActionListener(e -> handleAddPatient());
        view.getPatientPanel().getEditBtn().addActionListener(e -> handleEditPatient());
        view.getPatientPanel().getDeleteBtn().addActionListener(e -> handleDeletePatient());
        view.getPatientPanel().getViewRecordBtn().addActionListener(e -> handleViewMedicalRecord());
        view.getPatientPanel().getAssignAntBtn().addActionListener(e -> handleAssignAntecedents());
        view.getPatientPanel().getSearchField().addActionListener(e -> refreshPatients());

        // RDV Panel
        view.getRdvPanel().getScheduleBtn().addActionListener(e -> handleScheduleRDV());
        view.getRdvPanel().getEditBtn().addActionListener(e -> handleEditRDV());
        view.getRdvPanel().getCancelBtn().addActionListener(e -> handleCancelRDV());
        view.getRdvPanel().getWaitListBtn().addActionListener(e -> handleWaitList());
        view.getRdvPanel().getFilterBtn().addActionListener(e -> handleFilterRDVByDate());
        view.getRdvPanel().getCancelBtn().addActionListener(e -> handleCancelRDV());

        // Finance Panel
        view.getFinancePanel().getCreateFactureBtn().addActionListener(e -> handleCreateFacture());
        view.getFinancePanel().getEditBtn().addActionListener(e -> handleEditFacture());
        view.getFinancePanel().getCancelBtn().addActionListener(e -> handleCancelFacture());
        view.getFinancePanel().getViewCaisseBtn().addActionListener(e -> handleViewCaisse());
        view.getFinancePanel().getReportBtn().addActionListener(e -> handleExportReport());
        view.getFinancePanel().getResetSFBtn().addActionListener(e -> handleResetSF());
        view.getFinancePanel().getSearchSFBtn().addActionListener(e -> refreshSituations());
        view.getFinancePanel().getSearchSFField().addActionListener(e -> refreshSituations());

        // Facture Search
        view.getFinancePanel().getFactureSearchBtn().addActionListener(e -> refreshFactures());
        view.getFinancePanel().getFactureSearchField().addActionListener(e -> refreshFactures());
        view.getFinancePanel().getFactureDateField().addActionListener(e -> refreshFactures());

        // Antecedent Panel
        view.getAntecedentPanel().getAddBtn().addActionListener(e -> handleAddAntecedent());
        view.getAntecedentPanel().getEditBtn().addActionListener(e -> handleEditAntecedent());
        view.getAntecedentPanel().getDeleteBtn().addActionListener(e -> handleDeleteAntecedent());
        view.getAntecedentPanel().getSearchBtn().addActionListener(e -> handleSearchAntecedent());
        view.getAntecedentPanel().getSearchField().addActionListener(e -> handleSearchAntecedent());

        // Agenda Panel
        view.getAgendaPanel().getSaveBtn().addActionListener(e -> handleSaveAgenda());

        // Auto-refresh Dashboard every 60 seconds to update alerts
        Timer timer = new Timer(60000, e -> {
            if (view.isVisible()) {
                refreshDashboard();
            }
        });
        timer.start();
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

    private void handleSaveAgenda() {
        // Simulation of saving agenda configuration
        JOptionPane.showMessageDialog(view, "Configuration de l'agenda enregistrée avec succès.\n" +
                "(Note: Simulation - Nécessite une entité AgendaConfig en BDD)");
    }

    // ... Finance Logic ...

    private void refreshAll() {
        refreshDashboard();
        refreshPatients();
        refreshRDVs();
        refreshFactures();
        refreshSituations();
        refreshAntecedents();
    }

    private void refreshDashboard() {
        // 1. Update KPIs
        long patientCount = patientService.getAllPatients().size();
        view.getHomePanel().setTotalPatients(patientCount);

        // Calculate Daily Revenue (from Invoices created today)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();

        double dailyRev = factureService.findAll().stream()
                .filter(f -> f.getDateFacture() != null && f.getDateFacture().toLocalDate().equals(today))
                .mapToDouble(f -> f.getTotalePayé()) // Assuming revenue is what is paid
                .sum();
        view.getHomePanel().setDailyRevenue(dailyRev);

        // 2. Populate Queue (Today's RDVs) & Daily Stats
        List<RDV> todaysRdvs = rdvService.findAll().stream()
                .filter(r -> r.getDate() != null && r.getDate().equals(today))
                .sorted((r1, r2) -> r1.getHeure().compareTo(r2.getHeure()))
                .collect(Collectors.toList());

        view.getHomePanel().setRdvTodayCount(todaysRdvs.size());

        // Count Completed Consultations Today
        // Since we don't have direct access to filtered consultations easily without
        // querying service,
        // we can filter through existing ones if available or assume RDV status
        // 'TERMINE' implies consultation done.
        // Better: Query consultation service if possible or count RDVs with status
        // TERMINE as a proxy for today.
        long completedCons = todaysRdvs.stream()
                .filter(r -> r.getStatut() == StatutRendezVous.TERMINE)
                .count();
        view.getHomePanel().setCompletedConsultationsCount(completedCons);

        DefaultTableModel qModel = view.getHomePanel().getQueueModel();
        qModel.setRowCount(0);

        DefaultListModel<String> aModel = view.getHomePanel().getAlertsModel();
        aModel.clear();

        for (RDV r : todaysRdvs) {
            String pName = "Inconnu";
            if (r.getDossierMedicale() != null) {
                ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO dmDto = dmService
                        .getDossierMedicalById(r.getDossierMedicale().getIdDM());
                if (dmDto != null && dmDto.getPatientId() != null) {
                    Patient p = patientService.getPatientById(dmDto.getPatientId());
                    if (p != null) {
                        pName = p.getNom() + " " + p.getPrenom();
                    }
                }
            }
            qModel.addRow(new Object[] { r.getHeure(), pName, r.getMotif(), r.getStatut() });

            // -- ALERTS LOGIC --

            // 1. Cancellation Alerts
            if (r.getStatut() == StatutRendezVous.ANNULE) {
                aModel.addElement("❌ RDV Annulé : " + pName + " (" + r.getHeure() + ")");
            }

            // 2. Waiting Time Alerts (Only for not yet started/completed appointments)
            if (r.getStatut() != StatutRendezVous.TERMINE &&
                    r.getStatut() != StatutRendezVous.EN_CONSULTATION &&
                    r.getStatut() != StatutRendezVous.ANNULE &&
                    r.getHeure().isBefore(now)) {

                long minutesLate = java.time.temporal.ChronoUnit.MINUTES.between(r.getHeure(), now);

                if (minutesLate >= 10) {
                    aModel.addElement("🔴 Retard +10 min : " + pName + " (Prévu: " + r.getHeure() + ")");
                } else if (minutesLate >= 5) {
                    aModel.addElement("⚠️ Retard +5 min : " + pName + " (Prévu: " + r.getHeure() + ")");
                }
            }
        }

        // Alert: Unpaid Invoices
        long unpaidCount = factureService.findAll().stream()
                .filter(f -> f.getReste() > 0)
                .count();
        if (unpaidCount > 0) {
            aModel.addElement("💰 " + unpaidCount + " Facture(s) impayée(s)");
        }

        // Alert: Pending RDVs (Future ones mostly)
        long pendingRdv = rdvService.findAll().stream()
                .filter(r -> r.getStatut() == StatutRendezVous.EN_ATTENTE)
                .count();
        if (pendingRdv > 0) {
            aModel.addElement("🕒 " + pendingRdv + " Rendez-vous en attente de confirmation");
        }
    }

    private void refreshAntecedents() {
        List<Antecedents> all = antecedentService.getAllAntecedents();
        populateAntecedentTable(all);
    }

    private void populateAntecedentTable(List<Antecedents> list) {
        DefaultTableModel model = view.getAntecedentPanel().getTableModel();
        model.setRowCount(0);
        for (Antecedents a : list) {
            model.addRow(new Object[] { a.getId_Antecedent(), a.getNom(), a.getCategorie() });
        }
    }

    // ... (Existing Refresh Methods) ...

    private void refreshPatients() {
        String query = view.getPatientPanel().getSearchField().getText().toLowerCase();
        List<Patient> patients = patientService.getAllPatients();
        DefaultTableModel model = view.getPatientPanel().getTableModel();
        model.setRowCount(0);
        for (Patient p : patients) {
            if (query.isEmpty() || p.getNom().toLowerCase().contains(query)
                    || p.getPrenom().toLowerCase().contains(query)) {
                model.addRow(new Object[] { p.getId_Patient(), p.getNom(), p.getPrenom(), p.getTelephone(),
                        p.getEmail(), p.getSexe(), p.getAssurance() });
            }
        }
    }

    private void refreshRDVs() {
        // Filter by selected date in RDV panel, defaulting to today if empty or invalid
        java.time.LocalDate filterDate = java.time.LocalDate.now();
        String dateText = view.getRdvPanel().getDateField().getText().trim();

        if (!dateText.isEmpty()) {
            try {
                filterDate = java.time.LocalDate.parse(dateText);
            } catch (Exception e) {
                // If invalid, fallback to today (or show error but fallback ensures usability)
            }
        } else {
            view.getRdvPanel().getDateField().setText(filterDate.toString());
        }

        final java.time.LocalDate targetDate = filterDate;
        List<RDV> rdvs = rdvService.findAll().stream()
                .filter(r -> r.getDate() != null && r.getDate().equals(targetDate))
                .sorted((r1, r2) -> r1.getHeure().compareTo(r2.getHeure()))
                .collect(Collectors.toList());
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
        // Get Search Criteria
        String nameQuery = view.getFinancePanel().getFactureSearchField().getText().trim().toLowerCase();
        String dateQuery = view.getFinancePanel().getFactureDateField().getText().trim();
        java.time.LocalDate searchDate = null;
        if (!dateQuery.isEmpty()) {
            try {
                searchDate = java.time.LocalDate.parse(dateQuery);
            } catch (Exception e) {
                // Ignore invalid date or show error (optional, for now just ignore filter)
            }
        }

        List<Facture> factures = factureService.findAll();
        DefaultTableModel model = view.getFinancePanel().getTableModel();
        model.setRowCount(0);

        for (Facture f : factures) {
            String patientName = "Inconnu";

            // Try to get patient name via SituationFinanciere -> DossierMedicale -> Patient
            if (f.getSituationFinanciere() != null) {
                SituationFinanciere sf = sfService.getById(f.getSituationFinanciere().getIdSF());
                if (sf != null && sf.getDossierMedicale() != null) {
                    // Fetch full dossier to get patient ID if not present
                    ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO dmDto = dmService
                            .getDossierMedicalById(sf.getDossierMedicale().getIdDM());
                    if (dmDto != null && dmDto.getPatientId() != null) {
                        Patient p = patientService.getPatientById(dmDto.getPatientId());
                        if (p != null) {
                            patientName = p.getNom() + " " + p.getPrenom();
                        }
                    }
                }
            }

            // FILTERS
            boolean matchName = nameQuery.isEmpty() || patientName.toLowerCase().contains(nameQuery);
            boolean matchDate = searchDate == null ||
                    (f.getDateFacture() != null && f.getDateFacture().toLocalDate().equals(searchDate));

            if (matchName && matchDate) {
                model.addRow(new Object[] { f.getIdFature(), f.getDateFacture(), patientName, f.getTotaleFacture(),
                        f.getTotalePayé(), f.getReste(), f.getStatut() });
            }
        }
    }

    private void refreshSituations() {
        String query = view.getFinancePanel().getSearchSFField().getText().trim().toLowerCase();
        List<SituationFinanciere> list = sfService.getAll();
        DefaultTableModel model = view.getFinancePanel().getSfModel();
        model.setRowCount(0);
        for (SituationFinanciere sf : list) {
            String pName = "Inconnu";
            if (sf.getDossierMedicale() != null) {
                ma.whitecare.mvc.dto.DossierMedicale.DossierMedicalDTO dmDto = dmService
                        .getDossierMedicalById(sf.getDossierMedicale().getIdDM());
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
                        sf.getTotaleDesActes(),
                        sf.getTotalePaye(),
                        sf.getCredit(),
                        sf.getStatut()
                });
            }
        }
    }

    private void handleAddPatient() {
        PatientFormDialog dialog = new PatientFormDialog(view, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Patient p = Patient.builder().nom(dialog.getNom()).prenom(dialog.getPrenom()).telephone(dialog.getTel())
                    .email(dialog.getEmail()).adresse(dialog.getAdresse()).dateNaissance(dialog.getDateNaissance())
                    .sexe(dialog.getSexe()).assurance(dialog.getAssurance()).build();
            try {
                // 1. Créer le patient
                patientService.createPatient(p);
                // Le patient retourné n'a pas forcément l'ID si createPatient est void, on
                // suppose qu'on doit reload ou que createPatient met à jour l'ID.
                // Vérification: PatientRepository.create met-il à jour l'ID ?
                // Si non, on doit récupérer le patient par email/tel ou autre unique, ou
                // modifier le service pour retourner l'objet.
                // Supposons pour l'instant qu'on peut récupérer par email/login ou que l'objet
                // p est mis à jour (JPA style but standard JDBC often doesn't unless explicit).
                // On va re-récupérer le patient le plus récent ou par ses attributs uniques
                // pour être sûr d'avoir l'ID.
                // Mieux: utiliser p.getId_Patient() si le repo le set.

                // Pour sécuriser, on recharge la liste et on prend le dernier ou on cherche.
                // Mais regardons PatientServiceImpl.createPatient... elle est void.
                // On va supposer que le repository MAJ l'id (souvent le cas avec les
                // implementations JDBC correcte).
                // Risque: ID null.

                // Workaround: Chercher le patient par nom/prenom/date/email pour avoir l'ID
                // pour le dossier.
                List<Patient> candidates = patientService.findByEmail(p.getEmail());
                Patient createdPatient = candidates.stream().findFirst().orElse(null);

                if (createdPatient != null) {
                    // 2. <<include>> : Créer Dossier Médical Automatiquement
                    ma.whitecare.entities.medical.DossierMedicale dm = ma.whitecare.entities.medical.DossierMedicale
                            .builder()
                            .patient(createdPatient)
                            .dateDeCreation(java.time.LocalDate.now())
                            .build();

                    // Associer un médecin (le premier trouvé par défaut ou via logique cabinet)
                    List<ma.whitecare.entities.user.Medecin> medecins = medecinService
                            .getMedecinsByCabinet(currentSecretary.getCabinetMedicaleId());
                    if (!medecins.isEmpty()) {
                        dm.setMedecin(medecins.get(0));
                    } else {
                        // Fallback if no specific cabinet logic or empty, try getting all
                        List<ma.whitecare.entities.user.Medecin> allMedecins = medecinService.getAllMedecins();
                        if (!allMedecins.isEmpty()) {
                            dm.setMedecin(allMedecins.get(0));
                        }
                    }

                    dmService.saveDossier(dm);

                    // 3. <<extend>> : Affecter Antécédent
                    int response = JOptionPane.showConfirmDialog(view,
                            "Patient et Dossier Médical créés avec succès.\nVoulez-vous affecter des antécédents médicaux maintenant ?",
                            "Affectation Antécédents",
                            JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.YES_OPTION) {
                        // On doit sélectionner ce patient dans la table pour que
                        // handleAssignAntecedents fonctionne
                        refreshPatients();
                        // On simule la sélection ou on refait la logique avec le patient direct
                        List<Antecedents> all = antecedentService.getAllAntecedents();
                        AssignAntecedentDialog antDialog = new AssignAntecedentDialog(view, createdPatient, all);
                        antDialog.setVisible(true);
                        if (antDialog.isConfirmed()) {
                            createdPatient.setAntecedents(antDialog.getSelectedAntecedents());
                            patientService.updatePatient(createdPatient);
                            JOptionPane.showMessageDialog(view, "Antécédents mis à jour.");
                        }
                    } else {
                        refreshPatients();
                    }
                } else {
                    refreshPatients();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
            }
        }
    }

    private void handleEditPatient() {
        int row = view.getPatientPanel().getPatientTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getPatientPanel().getPatientTable().getValueAt(row, 0);
        Patient p = patientService.getPatientById(id);
        if (p != null) {
            PatientFormDialog dialog = new PatientFormDialog(view, p);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                p.setNom(dialog.getNom());
                p.setPrenom(dialog.getPrenom());
                p.setTelephone(dialog.getTel());
                p.setEmail(dialog.getEmail());
                p.setAdresse(dialog.getAdresse());
                p.setDateNaissance(dialog.getDateNaissance());
                p.setSexe(dialog.getSexe());
                p.setAssurance(dialog.getAssurance());
                try {
                    patientService.updatePatient(p);
                    refreshPatients();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
                }
            }
        }
    }

    private void handleDeletePatient() {
        int row = view.getPatientPanel().getPatientTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getPatientPanel().getPatientTable().getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(view, "Supprimer ?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                patientService.deletePatientById(id);
                refreshPatients();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
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

            List<ma.whitecare.entities.medical.Consultation> consultations = consultationService
                    .getConsultationsByDossierId(dm.getIdDM());

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

            MedicalRecordView recordView = new MedicalRecordView(view, p, rdvs, factures, consultations, ordonnances,
                    certificats);
            recordView.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(view, "Ce patient n'a pas encore de dossier médical.");
        }
    }

    private void handleAssignAntecedents() {
        int row = view.getPatientPanel().getPatientTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getPatientPanel().getPatientTable().getValueAt(row, 0);
        Patient p = patientService.getPatientById(id);
        List<Antecedents> all = antecedentService.getAllAntecedents();
        AssignAntecedentDialog dialog = new AssignAntecedentDialog(view, p, all);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            p.setAntecedents(dialog.getSelectedAntecedents());
            try {
                patientService.updatePatient(p);
                JOptionPane.showMessageDialog(view, "Antécédents mis à jour.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
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
                // Assign a doctor (default logic: prioritize same cabinet, then any)
                if (currentSecretary != null && currentSecretary.getCabinetMedicaleId() != null) {
                    List<ma.whitecare.entities.user.Medecin> allDocs = medecinService.getAllMedecins();
                    java.util.Optional<ma.whitecare.entities.user.Medecin> cabinetDoc = allDocs.stream()
                            .filter(m -> m.getCabinetMedicaleId() != null &&
                                    m.getCabinetMedicaleId()
                                            .equals(currentSecretary.getCabinetMedicaleId()))
                            .findFirst();
                    if (cabinetDoc.isPresent()) {
                        dm.setMedecin(cabinetDoc.get());
                    } else if (!allDocs.isEmpty()) {
                        dm.setMedecin(allDocs.get(0));
                    }
                } else {
                    // Fallback if secretary has no cabinet or is unknown
                    List<ma.whitecare.entities.user.Medecin> allDocs = medecinService.getAllMedecins();
                    if (!allDocs.isEmpty()) {
                        dm.setMedecin(allDocs.get(0));
                    }
                }
                dmService.saveDossier(dm);
                dm = dmService.getDossierByPatientId(p.getId_Patient());
            }

            // 1. Check Availability
            if (rdvService.existsByDateAndHeure(dialog.getDate(), dialog.getHeure())) {
                JOptionPane.showMessageDialog(view, "Ce créneau est déjà réservé.", "Créneau indisponible",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if slot is blocked by doctor
            boolean isBlocked = rdvService.findAll().stream()
                    .anyMatch(r -> r.getDate().equals(dialog.getDate()) &&
                            r.getHeure().equals(dialog.getHeure()) &&
                            r.getStatut() == ma.whitecare.entities.enums.StatutRendezVous.BLOQUE);

            if (isBlocked) {
                JOptionPane.showMessageDialog(view, "Ce créneau est bloqué par le médecin.", "Créneau indisponible",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // 2. Create Consultation (EN_ATTENTE)
                ma.whitecare.mvc.dto.DossierMedicale.CreateConsultationDTO consDto = ma.whitecare.mvc.dto.DossierMedicale.CreateConsultationDTO
                        .builder()
                        .date(dialog.getDate())
                        .dateConsultation(java.time.LocalDateTime.of(dialog.getDate(), dialog.getHeure()))
                        .statut(ma.whitecare.entities.enums.StatutConsultation.EN_ATTENTE)
                        .dossierMedicalId(dm.getIdDM())
                        .medecinId(dm.getMedecin() != null ? dm.getMedecin().getIdUser() : null)
                        .build();

                ma.whitecare.mvc.dto.DossierMedicale.ConsultationDTO createdCons = consultationService
                        .createConsultation(consDto);
                System.out.println("DEBUG: Consultation created with ID: " + createdCons.getIdConsultation());

                if (createdCons.getIdConsultation() == null) {
                    throw new RuntimeException(
                            "L'ID de la consultation créée est null. Vérifiez la génération de clé en BDD.");
                }

                // 3. Create RDV with Consultation ID
                ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO dto = ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO.builder()
                        .date(dialog.getDate())
                        .heure(dialog.getHeure())
                        .motif(dialog.getMotif())
                        .statut(dialog.getStatut())
                        .dossierMedicaleId(dm.getIdDM())
                        .consultationId(createdCons.getIdConsultation())
                        .build();

                System.out.println("DEBUG: Creating RDV with DTO: " + dto);
                rdvService.createRDV(dto);
                System.out.println("DEBUG: RDV created successfully");
                refreshRDVs();
            } catch (Exception e) {
                e.printStackTrace();
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
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Erreur: " + e.getMessage());
                }
            }
        }
    }

    private void handleWaitList() {
        List<RDV> waiting = rdvService.findAll().stream()
                .filter(r -> r.getStatut() == StatutRendezVous.EN_ATTENTE)
                .collect(Collectors.toList());
        populateRDVTable(waiting);
        JOptionPane.showMessageDialog(view, "Affichage de la liste d'attente uniquement.");
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

            double reste = dialog.getTotal() - dialog.getPaye();
            ma.whitecare.entities.enums.StatutFacture finalStatut;

            if (reste <= 0.01) {
                finalStatut = ma.whitecare.entities.enums.StatutFacture.REGLEE;
            } else if (dialog.getPaye() > 0) {
                finalStatut = ma.whitecare.entities.enums.StatutFacture.PARTIELLE;
            } else {
                finalStatut = ma.whitecare.entities.enums.StatutFacture.IMPAYEE;
            }

            Facture f = Facture.builder()
                    .totaleFacture(dialog.getTotal())
                    .totalePayé(dialog.getPaye())
                    .Reste(reste)
                    .statut(finalStatut)
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
                } else if (f.getTotalePayé() > 0) {
                    f.setStatut(ma.whitecare.entities.enums.StatutFacture.PARTIELLE);
                } else {
                    f.setStatut(ma.whitecare.entities.enums.StatutFacture.IMPAYEE);
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

    private void handleExportReport() {
        int row = view.getFinancePanel().getFactureTable().getSelectedRow();
        if (row == -1)
            return;
        Long id = (Long) view.getFinancePanel().getFactureTable().getValueAt(row, 0);
        try {
            byte[] pdf = factureService.generatePDF(id);
            String path = "Facture_" + id + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(path)) {
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

    // ========== GESTION ANTÉCÉDENTS (CRUD) ==========

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

        Antecedents a = antecedentService.getAntecedentById(id);
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
        List<Antecedents> result = antecedentService.getAllAntecedents().stream()
                .filter(a -> a.getNom().toLowerCase().contains(query))
                .collect(Collectors.toList());
        populateAntecedentTable(result);
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
                r.setStatut(ma.whitecare.entities.enums.StatutRendezVous.ANNULE);
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

    private void handleViewCaisse() {
        CaisseStatsDialog dialog = new CaisseStatsDialog(view,
                (ma.whitecare.service.modules.caisse.api.CaisseService) caisseService);
        dialog.setVisible(true);
    }

    private void handleResetSF() {
        int row = view.getFinancePanel().getSfTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Sélectionnez une situation financière.", "Attention",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) view.getFinancePanel().getSfTable().getValueAt(row, 0);
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
}
