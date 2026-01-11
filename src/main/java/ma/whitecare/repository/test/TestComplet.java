package ma.whitecare.repository.test;

import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.cabinet.CabinetMedicale;
import ma.whitecare.entities.cabinet.Statistiques;
import ma.whitecare.entities.enums.*;
import ma.whitecare.entities.financial.Charges;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.financial.Revenues;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.medical.*;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.conf.ApplicationContext;
import ma.whitecare.repository.modules.actes.api.ActeRepository;
import ma.whitecare.repository.modules.agenda.api.AgendaRepository;
import ma.whitecare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.whitecare.repository.modules.cabinet.api.ChargesRepository;
import ma.whitecare.repository.modules.cabinet.api.RevenuesRepository;
import ma.whitecare.repository.modules.certificat.api.CertificatRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.SituationFinanciereRepository;
import ma.whitecare.repository.modules.facture.FactureRepository;
import ma.whitecare.repository.modules.Medicament.MedicamentRepository;
import ma.whitecare.repository.modules.Ordonnance.OrdonnanceRepository;
import ma.whitecare.repository.modules.patient.api.AntecedentRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.repository.modules.rdv.api.RDVRepository;
import ma.whitecare.repository.modules.statistiques.api.StatistiqueRepository;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.UserManager.api.RoleRepository;
import ma.whitecare.repository.modules.UserManager.api.SecretaireRepository;
import ma.whitecare.repository.modules.UserManager.api.StaffRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;
import ma.whitecare.entities.user.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Test complet qui teste tous les méthodes CRUD + méthodes utilitaires
 * des repositories suivant une logique cohérente de création d'un système médical complet
 */
public class TestComplet {

    // Repositories
    private static CabinetMedicaleRepository cabinetRepository;
    private static ChargesRepository chargesRepository;
    private static RevenuesRepository revenuesRepository;
    private static MedecinRepository medecinRepository;
    private static SecretaireRepository secretaireRepository;
    private static StaffRepository staffRepository;
    private static UtilisateurRepository utilisateurRepository;
    private static PatientRepository patientRepository;
    private static AntecedentRepository antecedentRepository;
    private static DossierMedicalRepository dossierRepository;
    private static ConsultationRepository consultationRepository;
    private static InterventionRepository interventionRepository;
    private static CertificatRepository certificatRepository;
    private static OrdonnanceRepository ordonnanceRepository;
    private static PrescriptionRepository prescriptionRepository;
    private static FactureRepository factureRepository;
    private static SituationFinanciereRepository situationFinanciereRepository;
    private static MedicamentRepository medicamentRepository;
    private static ActeRepository acteRepository;
    private static AgendaRepository agendaRepository;
    private static RoleRepository roleRepository;
    private static RDVRepository rdvRepository;
    private static StatistiqueRepository statistiqueRepository;

    // IDs créés pour les relations
    private static Long cabinetId;
    private static Long chargeId;
    private static Long revenueId;
    private static Long medecinId;
    private static Long secretaireId;
    private static Long staffId;
    private static Long utilisateurId;
    private static Long patientId;
    private static Long antecedentId;
    private static Long dossierId;
    private static Long consultationId;
    private static Long interventionId;
    private static Long certificatId;
    private static Long ordonnanceId;
    private static Long prescriptionId;
    private static Long factureId;
    private static Long situationFinanciereId;
    private static Long medicamentId;
    private static Long acteId;
    private static Long agendaId;
    private static Long roleMedecinId;
    private static Long roleAdminId;
    private static Long antecedent2Id;
    private static Long rdvId;
    private static Long statistiqueId;

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DU TEST COMPLET ===\n");



        // Initialisation des repositories
        initializeRepositories();

        try {
            // 1. INSERT PROCESS - Création de toutes les entités
            insertProcess();

            // 2. SELECT PROCESS - Recherche et affichage
            selectProcess();

            // 3. UPDATE PROCESS - Mise à jour
            updateProcess();

            // 4. DELETE PROCESS - Suppression (optionnel, commenté pour garder les données)
            // deleteProcess();

            System.out.println("\n=== FIN DU TEST COMPLET - SUCCÈS ===");
        } catch (Exception e) {
            // Vérifier si c'est une SQLException encapsulée dans une RuntimeException
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                System.err.println("\n❌ ERREUR SQL lors du test:");
                System.err.println("Message: " + sqlEx.getMessage());
                if (sqlEx.getCause() != null) {
                    System.err.println("Cause: " + sqlEx.getCause().getMessage());
                }
                System.err.println("\nVérifiez:");
                System.err.println("  - Que MySQL est démarré");
                System.err.println("  - Que la base de données 'WhiteCare' existe");
                System.err.println("  - Que les tables sontservices que jai cree  créées (exécutez schema.sql)");
            } else {
                System.err.println("\n❌ ERREUR lors du test: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            // Fermer proprement la connexion via ApplicationContext
            try {
                ApplicationContext.getInstance().closeConnection();
            } catch (Exception e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }



    private static void initializeRepositories() {
        System.out.println("Initialisation des repositories via ApplicationContext...");
        
        // Récupérer l'instance d'ApplicationContext
        ApplicationContext context = ApplicationContext.getInstance();
        
        // Charger tous les repositories depuis ApplicationContext (sans utiliser 'new')
        // Utilisation de getBean(Class<T>) pour récupération par interface
        cabinetRepository = context.getBean(CabinetMedicaleRepository.class);
        chargesRepository = context.getBean(ChargesRepository.class);
        medecinRepository = context.getBean(MedecinRepository.class);
        patientRepository = context.getBean(PatientRepository.class);
        antecedentRepository = context.getBean(AntecedentRepository.class);
        dossierRepository = context.getBean(DossierMedicalRepository.class);
        consultationRepository = context.getBean(ConsultationRepository.class);
        interventionRepository = context.getBean(InterventionRepository.class);
        certificatRepository = context.getBean(CertificatRepository.class);
        ordonnanceRepository = context.getBean(OrdonnanceRepository.class);
        prescriptionRepository = context.getBean(PrescriptionRepository.class);
        factureRepository = context.getBean(FactureRepository.class);
        situationFinanciereRepository = context.getBean(SituationFinanciereRepository.class);
        medicamentRepository = context.getBean(MedicamentRepository.class);
        acteRepository = context.getBean(ActeRepository.class);
        agendaRepository = context.getBean(AgendaRepository.class);
        roleRepository = context.getBean(RoleRepository.class);
        
        System.out.println("✓ " + context.getBeanNames().size() + " repository(s) initialisé(s) depuis ApplicationContext et beans.properties\n");
    }

    private static void insertProcess() {
        System.out.println("=== PROCESSUS D'INSERTION ===\n");

        // 1. Créer un Cabinet Médical
        System.out.println("1. Création du Cabinet Médical...");
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Cabinet Dentaire Dr. Smith")
                .email("contact@cabinet-smith.ma")
                .adresse("123 A Casablanca")
                .cin("C123456")
                .tel1("0522123456")
                .tel2("0522123457")
                .logo("testetst")
                .siteWeb("www.cabinet-smith.ma")
                .instagram("@cabinet_smith")
                .facebook("CmithDental")
                .description("Cabinet dentaire moderne avec équipements de pointe")
                .creePar("system")
                .modifiePar("system")
                .build();
        cabinetRepository.create(cabinet);
        cabinetId = cabinet.getId();
        System.out.println("✓ Cabinet créé avec ID: " + cabinetId);

        // 2. Créer des Charges
        System.out.println("\n2. Création des Charges...");
        Charges charge1 = Charges.builder()
                .titre("Loyer mensuel")
                .description("Paiement du loyer du cabinet")
                .montant(15000.0)
                .date(LocalDateTime.now())
                .creePar("system")
                .modifiePar("system")
                .build();
        CabinetMedicale cabinetRef = new CabinetMedicale();
        cabinetRef.setId(cabinetId);
        charge1.setCabinet(cabinetRef);
        chargesRepository.create(charge1);
        chargeId = charge1.getId();
        System.out.println("✓ Charge créée avec ID: " + chargeId);

        // 3. Créer un Médecin (Utilisateur + Staff)
        System.out.println("\n3. Création du Médecin...");
        Medecin medecin = Medecin.builder()
                .nom("Smith")
                .prenom("John")
                .email("j.smith@cabinet.ma")
                .adresse("456 B , Casablanca")
                .cin("M789012")
                .tel("0522987654")
                .sexe(Sexe.HOMME)
                .login("dr.smith")
                .motDePass("password123")
                .dateNaissance(LocalDate.of(1980, 5, 15))
                .actif(true)
                .salaire(25000.0)
                .prime(5000.0)
                .dateRecrutement(LocalDate.of(2020, 1, 1))
                .soldeConge(25)
                .specialite("Chirurgie dentaire")
                .cabinetMedicaleId(cabinetId)
                .creePar("system")
                .modifiePar("system")
                .build();
        medecinRepository.create(medecin);
        medecinId = medecin.getIdUser();
        System.out.println("✓ Médecin créé avec ID: " + medecinId);

        // 3.1. Créer des Rôles et les assigner au Médecin (Many-to-Many)
        System.out.println("\n3.1. Création et assignation de Rôles au Médecin (Many-to-Many)...");
        
        // Vérifier si le rôle MEDECIN existe, sinon le créer
        var roleMedecinOpt = roleRepository.findByLibelle(LibelleRole.MEDECIN);
        if (roleMedecinOpt.isPresent()) {
            roleMedecinId = roleMedecinOpt.get().getIdRole();
            System.out.println("✓ Rôle MEDECIN trouvé avec ID: " + roleMedecinId);
        } else {
            Role roleMedecin = Role.builder()
                    .libelle(LibelleRole.MEDECIN)
                    .creePar("system")
                    .modifiePar("system")
                    .build();
            roleRepository.create(roleMedecin);
            roleMedecinId = roleMedecin.getIdRole();
            System.out.println("✓ Rôle MEDECIN créé avec ID: " + roleMedecinId);
        }
        
        // Assigner le rôle MEDECIN au médecin
        roleRepository.assignRoleToUser(medecinId, roleMedecinId);
        System.out.println("✓ Rôle MEDECIN assigné au médecin");

        // Vérifier si le rôle ADMIN existe, sinon le créer
        var roleAdminOpt = roleRepository.findByLibelle(LibelleRole.ADMIN);
        if (roleAdminOpt.isPresent()) {
            roleAdminId = roleAdminOpt.get().getIdRole();
            System.out.println("✓ Rôle ADMIN trouvé avec ID: " + roleAdminId);
        } else {
            Role roleAdmin = Role.builder()
                    .libelle(LibelleRole.ADMIN)
                    .creePar("system")
                    .modifiePar("system")
                    .build();
            roleRepository.create(roleAdmin);
            roleAdminId = roleAdmin.getIdRole();
            System.out.println("✓ Rôle ADMIN créé avec ID: " + roleAdminId);
        }
        
        // Assigner aussi le rôle ADMIN au médecin (utilisateur peut avoir plusieurs rôles)
        roleRepository.assignRoleToUser(medecinId, roleAdminId);
        System.out.println("✓ Rôle ADMIN assigné au médecin");

        // Vérifier les rôles assignés
        List<Role> rolesDuMedecin = roleRepository.findRolesByUserId(medecinId);
        System.out.println("✓ Le médecin a " + rolesDuMedecin.size() + " rôle(s) assigné(s)");
        for (Role role : rolesDuMedecin) {
            System.out.println("  - " + role.getLibelle());
        }

        // 4. Créer un Patient
        System.out.println("\n4. Création du Patient...");
        Patient patient = Patient.builder()
                .nom("Alami")
                .prenom("Ahmed")
                .adresse("789 Rue,Rabat")
                .telephone("0612345678")
                .email("a.alami@email.com")
                .dateNaissance(LocalDate.of(1990, 3, 20))
                .sexe(Sexe.HOMME)
                .assurance(Assurance.CNOPS)
                .creePar("system")
                .modifiePar("system")
                .build();
        patientRepository.create(patient);
        patientId = patient.getId_Patient();
        System.out.println("✓ Patient créé avec ID: " + patientId);

        // 5. Créer des Antécédents et les lier au Patient (Many-to-Many)
        System.out.println("\n5. Création d'Antécédents et liaison au Patient (Many-to-Many)...");
        
        // Créer le premier antécédent
        Antecedents antecedent = Antecedents.builder()
                .nom("Diabète")
                .categorie("Métabolique")
                .niveauDeRisque(NiveauDeRisque.MOYEN)
                .creePar("system")
                .modifiePar("system")
                .build();
        antecedentRepository.create(antecedent);
        antecedentId = antecedent.getId_Antecedent();
        System.out.println("✓ Antécédent 1 créé avec ID: " + antecedentId + " (Diabète)");

        // Créer un deuxième antécédent
        Antecedents antecedent2 = Antecedents.builder()
                .nom("Hypertension")
                .categorie("Cardiovasculaire")
                .niveauDeRisque(NiveauDeRisque.ELEVE)
                .creePar("system")
                .modifiePar("system")
                .build();
        antecedentRepository.create(antecedent2);
        antecedent2Id = antecedent2.getId_Antecedent();
        System.out.println("✓ Antécédent 2 créé avec ID: " + antecedent2Id + " (Hypertension)");

        // Lier les antécédents au patient (Many-to-Many)
        patientRepository.addAntecedentToPatient(patientId, antecedentId);
        System.out.println("✓ Antécédent 1 (Diabète) lié au patient");
        
        patientRepository.addAntecedentToPatient(patientId, antecedent2Id);
        System.out.println("✓ Antécédent 2 (Hypertension) lié au patient");

        // Vérifier les antécédents du patient
        List<Antecedents> antecedentsDuPatient = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("✓ Le patient a " + antecedentsDuPatient.size() + " antécédent(s)");
        for (Antecedents ant : antecedentsDuPatient) {
            System.out.println("  - " + ant.getNom() + " (" + ant.getCategorie() + ")");
        }

        // 6. Créer un Dossier Médical
        System.out.println("\n6. Création du Dossier Médical...");
        DossierMedicale dossier = DossierMedicale.builder()
                .dateDeCreation(LocalDate.now())
                .creePar("system")
                .modifiePar("system")
                .build();
        Patient patientRef = new Patient();
        patientRef.setId_Patient(patientId);
        dossier.setPatient(patientRef);
        Medecin medecinRef = new Medecin();
        medecinRef.setIdUser(medecinId);
        dossier.setMedecin(medecinRef);
        dossierRepository.create(dossier);
        dossierId = dossier.getIdDM();
        System.out.println("✓ Dossier médical créé avec ID: " + dossierId);

        // 7. Créer une Situation Financière
        System.out.println("\n7. Création de la Situation Financière...");
        SituationFinanciere situationFinanciere = SituationFinanciere.builder()
                .totaleDesActes(5000.0)
                .totalePaye(2000.0)
                .credit(0.0)
                .statut(StatutSituationFinanciere.PARTIEL)
                .enPromo(EnPromo.NON)
                .creePar("system")
                .modifiePar("system")
                .build();
        DossierMedicale dossierRef = new DossierMedicale();
        dossierRef.setIdDM(dossierId);
        situationFinanciere.setDossierMedicale(dossierRef);
        situationFinanciereRepository.create(situationFinanciere);
        situationFinanciereId = situationFinanciere.getIdSF();
        System.out.println("✓ Situation financière créée avec ID: " + situationFinanciereId);

        // 8. Créer un Acte
        System.out.println("\n8. Création d'un Acte...");
        Acte acte = Acte.builder()
                .libelle("Détartrage")
                .categorie("Soins préventifs")
                .prixDeBase(300.0)
                .creePar("system")
                .modifiePar("system")
                .build();
        acteRepository.create(acte);
        acteId = acte.getIdActe();
        System.out.println("✓ Acte créé avec ID: " + acteId);

        // 9. Créer un Médicament
        System.out.println("\n9. Création d'un Médicament...");
        Medicament medicament = Medicament.builder()
                .nom("Paracétamol 500mg")
                .laboratoire("PharmaMaroc")
                .type("Analgésique")
                .forme(FormeMedicament.COMPRIME)
                .remboursable(true)
                .prixUnitaire(15.0)
                .description("Antalgique et antipyrétique")
                .creePar("system")
                .modifiePar("system")
                .build();
        medicamentRepository.create(medicament);
        medicamentId = medicament.getIdMct();
        System.out.println("✓ Médicament créé avec ID: " + medicamentId);

        // 10. Créer une Consultation
        System.out.println("\n10. Création d'une Consultation...");
        Consultation consultation = Consultation.builder()
                .Date(LocalDate.now())
                .statut(StatutConsultation.EN_COURS)
                .observationMedecin("Examen dentaire de routine")
                .creePar("system")
                .modifiePar("system")
                .build();
        dossierRef = new DossierMedicale();
        dossierRef.setIdDM(dossierId);
        consultation.setDossierMedicale(dossierRef);
        consultationRepository.create(consultation);
        consultationId = consultation.getIdConsultation();
        System.out.println("✓ Consultation créée avec ID: " + consultationId);

        // 11. Créer une Intervention Médecin
        System.out.println("\n11. Création d'une Intervention Médecin...");
        InterventionMedecin intervention = InterventionMedecin.builder()
                .prixDePatient(300.0)
                .numDent(16)
                .creePar("system")
                .modifiePar("system")
                .build();
        Consultation consultationRef = new Consultation();
        consultationRef.setIdConsultation(consultationId);
        intervention.setConsultation(consultationRef);
        Acte acteRef = new Acte();
        acteRef.setIdActe(acteId);
        intervention.setActe(acteRef);
        interventionRepository.create(intervention);
        interventionId = intervention.getIdIM();
        System.out.println("✓ Intervention créée avec ID: " + interventionId);

        // 12. Créer un Certificat
        System.out.println("\n12. Création d'un Certificat...");
        Certificat certificat = Certificat.builder()
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(3))
                .duree(3)
                .noteMedecin("Repos médical prescrit")
                .creePar("system")
                .modifiePar("system")
                .build();
        dossierRef = new DossierMedicale();
        dossierRef.setIdDM(dossierId);
        certificat.setDossierMedicale(dossierRef);
        consultationRef = new Consultation();
        consultationRef.setIdConsultation(consultationId);
        certificat.setConsultation(consultationRef);
        certificatRepository.create(certificat);
        certificatId = certificat.getIdCertif();
        System.out.println("✓ Certificat créé avec ID: " + certificatId);

        // 13. Créer une Ordonnance
        System.out.println("\n13. Création d'une Ordonnance...");
        Ordonnance ordonnance = Ordonnance.builder()
                .date(LocalDate.now())
                .creePar("system")
                .modifiePar("system")
                .build();
        dossierRef = new DossierMedicale();
        dossierRef.setIdDM(dossierId);
        ordonnance.setDossierMedicale(dossierRef);
        consultationRef = new Consultation();
        consultationRef.setIdConsultation(consultationId);
        ordonnance.setConsultation(consultationRef);
        ordonnanceRepository.create(ordonnance);
        ordonnanceId = ordonnance.getIdOrd();
        System.out.println("✓ Ordonnance créée avec ID: " + ordonnanceId);

        // 14. Créer une Prescription
        System.out.println("\n14. Création d'une Prescription...");
        Prescription prescription = Prescription.builder()
                .quantite(2)
                .frequence("3 fois par jour")
                .dureeEnJours(5)
                .creePar("system")
                .modifiePar("system")
                .build();
        Ordonnance ordonnanceRef = new Ordonnance();
        ordonnanceRef.setIdOrd(ordonnanceId);
        prescription.setOrdonnance(ordonnanceRef);
        Medicament medicamentRef = new Medicament();
        medicamentRef.setIdMct(medicamentId);
        prescription.setMedicament(medicamentRef);
        prescriptionRepository.create(prescription);
        prescriptionId = prescription.getIdPr();
        System.out.println("✓ Prescription créée avec ID: " + prescriptionId);

        // 15. Créer une Facture
        System.out.println("\n15. Création d'une Facture...");
        Facture facture = Facture.builder()
                .totaleFacture(300.0)
                .totalePayé(100.0)
                .Reste(200.0)
                .statut(StatutFacture.PARTIELLE)
                .dateFacture(LocalDateTime.now())
                .creePar("system")
                .modifiePar("system")
                .build();
        SituationFinanciere situationRef = new SituationFinanciere();
        situationRef.setIdSF(situationFinanciereId);
        facture.setSituationFinanciere(situationRef);
        consultationRef = new Consultation();
        consultationRef.setIdConsultation(consultationId);
        facture.setConsultation(consultationRef);
        factureRepository.create(facture);
        factureId = facture.getIdFature();
        System.out.println("✓ Facture créée avec ID: " + factureId);

        // 16. Créer un Agenda Mensuel
        System.out.println("\n16. Création d'un Agenda Mensuel...");
        AgendaMensuel agenda = AgendaMensuel.builder()
                .mois(Mois.JANVIER)
                .annee(2024)
                .medecinId(medecinId)
                .creePar("system")
                .modifiePar("system")
                .build();
        agendaRepository.create(agenda);
        agendaId = agenda.getId();
        System.out.println("✓ Agenda mensuel créé avec ID: " + agendaId);

        // 17. Créer un Jour dans l'Agenda
        System.out.println("\n17. Création d'un Jour dans l'Agenda...");
        Jour jour = Jour.builder()
                .date(LocalDate.now())
                .jourSemaine(JourSemaine.LUNDI)
                .estDisponible(true)
                .build();
        agendaRepository.ajouterJour(agendaId, jour);
        System.out.println("✓ Jour ajouté à l'agenda");

        // 18. Créer un Créneau
        System.out.println("\n18. Création d'un Créneau...");
        List<Jour> jours = agendaRepository.findJoursByAgendaId(agendaId);
        if (!jours.isEmpty()) {
            Long jourId = jours.get(0).getId();
            Creneau creneau = Creneau.builder()
                    .heureDebut(LocalTime.of(9, 0))
                    .heureFin(LocalTime.of(9, 30))
                    .estDisponible(true)
                    .build();
            agendaRepository.ajouterCreneau(jourId, creneau);
            System.out.println("✓ Créneau ajouté au jour");
        }

        // 19. Créer un Revenu
        System.out.println("\n19. Création d'un Revenu...");
        Revenues revenue = Revenues.builder()
                .titre("Consultation patient")
                .description("Revenu de consultation")
                .montant(500.0)
                .date(LocalDateTime.now())
                .creePar("system")
                .modifiePar("system")
                .build();
        CabinetMedicale cabinetRefRevenue = new CabinetMedicale();
        cabinetRefRevenue.setId(cabinetId);
        revenue.setCabinet(cabinetRefRevenue);
        revenuesRepository.create(revenue);
        revenueId = revenue.getId();
        System.out.println("✓ Revenu créé avec ID: " + revenueId);

        // 20. Créer un Secrétaire
        System.out.println("\n20. Création d'un Secrétaire...");
        Secretaire secretaire = Secretaire.builder()
                .nom("Bennani")
                .prenom("Fatima")
                .email("f.bennani@cabinet.ma")
                .adresse("789 C, Casablanca")
                .cin("S456789")
                .tel("0522111222")
                .sexe(Sexe.FEMME)
                .login("secretaire.fatima")
                .motDePass("password123")
                .dateNaissance(LocalDate.of(1995, 8, 10))
                .actif(true)
                .salaire(12000.0)
                .prime(2000.0)
                .dateRecrutement(LocalDate.of(2021, 3, 1))
                .soldeConge(20)
                .numCNSS("CNSS123456")
                .commission(5.0)
                .cabinetMedicaleId(cabinetId)
                .creePar("system")
                .modifiePar("system")
                .build();
        secretaireRepository.create(secretaire);
        secretaireId = secretaire.getIdUser();
        System.out.println("✓ Secrétaire créé avec ID: " + secretaireId);

        // 21. Créer un RDV
        System.out.println("\n21. Création d'un RDV...");
        RDV rdv = RDV.builder()
                .Date(LocalDate.now().plusDays(1))
                .heure(LocalTime.of(10, 0))
                .motif("Consultation de suivi")
                .statut(StatutRendezVous.PLANIFIE)
                .noteMedecin("Rendez-vous de routine")
                .creePar("system")
                .modifiePar("system")
                .build();
        DossierMedicale dossierRefRDV = new DossierMedicale();
        dossierRefRDV.setIdDM(dossierId);
        rdv.setDossierMedicale(dossierRefRDV);
        Consultation consultationRefRDV = new Consultation();
        consultationRefRDV.setIdConsultation(consultationId);
        rdv.setConsultation(consultationRefRDV);
        rdvRepository.create(rdv);
        rdvId = rdv.getIdRDV();
        System.out.println("✓ RDV créé avec ID: " + rdvId);

        // 22. Créer une Statistique
        System.out.println("\n22. Création d'une Statistique...");
        Statistiques statistique = Statistiques.builder()
                .nom("Nombre de consultations mensuelles")
                .categorie(CategorieStatistique.CONSULTATIONS)
                .chiffre(150.0)
                .dateCalcul(LocalDate.now())
                .creePar("system")
                .modifiePar("system")
                .build();
        CabinetMedicale cabinetRefStat = new CabinetMedicale();
        cabinetRefStat.setId(cabinetId);
        statistique.setCabinet(cabinetRefStat);
        statistiqueRepository.create(statistique);
        statistiqueId = statistique.getId();
        System.out.println("✓ Statistique créée avec ID: " + statistiqueId);

        System.out.println("\n=== FIN DU PROCESSUS D'INSERTION ===\n");
    }

    private static void selectProcess() {
        System.out.println("=== PROCESSUS DE SÉLECTION ===\n");

        // 1. Rechercher toutes les entités
        System.out.println("1. Recherche de toutes les entités...");
        List<CabinetMedicale> cabinets = cabinetRepository.findAll();
        System.out.println("✓ " + cabinets.size() + " cabinet(s) trouvé(s)");

        List<Patient> patients = patientRepository.findAll();
        System.out.println("✓ " + patients.size() + " patient(s) trouvé(s)");

        List<Medecin> medecins = medecinRepository.findAll();
        System.out.println("✓ " + medecins.size() + " médecin(s) trouvé(s)");

        // 2. Recherche par ID
        System.out.println("\n2. Recherche par ID...");
        CabinetMedicale cabinet = cabinetRepository.findById(cabinetId);
        System.out.println("✓ Cabinet trouvé: " + cabinet.getNom());

        Patient patient = patientRepository.findById(patientId);
        System.out.println("✓ Patient trouvé: " + patient.getNom() + " " + patient.getPrenom());

        // 3. Recherche par relations
        System.out.println("\n3. Recherche par relations...");
        List<DossierMedicale> dossiers = dossierRepository.findByPatientId(patientId);
        System.out.println("✓ " + dossiers.size() + " dossier(s) trouvé(s) pour le patient");

        List<Consultation> consultations = consultationRepository.findByDossierMedicalId(dossierId);
        System.out.println("✓ " + consultations.size() + " consultation(s) trouvée(s) pour le dossier");

        List<InterventionMedecin> interventions = interventionRepository.findByConsultationId(consultationId);
        System.out.println("✓ " + interventions.size() + " intervention(s) trouvée(s) pour la consultation");

        List<Facture> factures = factureRepository.findBySituationFinanciereId(situationFinanciereId);
        System.out.println("✓ " + factures.size() + " facture(s) trouvée(s) pour la situation financière");

        List<Prescription> prescriptions = prescriptionRepository.findByOrdonnanceId(ordonnanceId);
        System.out.println("✓ " + prescriptions.size() + " prescription(s) trouvée(s) pour l'ordonnance");

        // 4. Recherche par statut
        System.out.println("\n4. Recherche par statut...");
        List<Consultation> consultationsEnCours = consultationRepository.findByStatut(StatutConsultation.EN_COURS);
        System.out.println("✓ " + consultationsEnCours.size() + " consultation(s) en cours");

        List<Facture> facturesPartielles = factureRepository.findByStatut(StatutFacture.PARTIELLE);
        System.out.println("✓ " + facturesPartielles.size() + " facture(s) partielle(s)");

        // 5. Recherche par critères multiples
        System.out.println("\n5. Recherche par critères multiples...");
        List<Facture> facturesBySituationAndStatut = factureRepository.findBySituationFinanciereIdAndStatut(
                situationFinanciereId, StatutFacture.PARTIELLE);
        System.out.println("✓ " + facturesBySituationAndStatut.size() + " facture(s) trouvée(s)");

        // 6. Comptage
        System.out.println("\n6. Comptage...");
        long countPatients = patientRepository.count();
        System.out.println("✓ Nombre total de patients: " + countPatients);

        long countConsultations = consultationRepository.countAll();
        System.out.println("✓ Nombre total de consultations: " + countConsultations);

        long countFactures = factureRepository.countAll();
        System.out.println("✓ Nombre total de factures: " + countFactures);

        // 7. Recherche d'antécédents du patient (Many-to-Many)
        System.out.println("\n7. Recherche des antécédents du patient (Many-to-Many)...");
        List<Antecedents> antecedents = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("✓ " + antecedents.size() + " antécédent(s) trouvé(s) pour le patient");
        for (Antecedents ant : antecedents) {
            System.out.println("  - " + ant.getNom() + " (" + ant.getCategorie() + ")");
        }

        // Recherche des patients par antécédent (relation inverse)
        List<Patient> patientsAvecDiabete = patientRepository.getPatientsByAntecedent(antecedentId);
        System.out.println("✓ " + patientsAvecDiabete.size() + " patient(s) trouvé(s) avec l'antécédent Diabète");

        // 8. Recherche des rôles d'un utilisateur (Many-to-Many)
        System.out.println("\n8. Recherche des rôles du médecin (Many-to-Many)...");
        List<Role> rolesDuMedecin = roleRepository.findRolesByUserId(medecinId);
        System.out.println("✓ " + rolesDuMedecin.size() + " rôle(s) trouvé(s) pour le médecin");
        for (Role role : rolesDuMedecin) {
            System.out.println("  - " + role.getLibelle());
        }

        // Vérifier si l'utilisateur a un rôle spécifique
        boolean hasMedecinRole = roleRepository.userHasRoleLibelle(medecinId, LibelleRole.MEDECIN);
        System.out.println("✓ Le médecin a le rôle MEDECIN: " + hasMedecinRole);

        boolean hasAdminRole = roleRepository.userHasRoleLibelle(medecinId, LibelleRole.ADMIN);
        System.out.println("✓ Le médecin a le rôle ADMIN: " + hasAdminRole);

        // Recherche des utilisateurs par rôle (relation inverse)
        List<Utilisateur> medecinss = roleRepository.findUsersByRoleLibelle(LibelleRole.MEDECIN);
        System.out.println("✓ " + medecinss.size() + " utilisateur(s) trouvé(s) avec le rôle MEDECIN");

        // 9. Calculs
        System.out.println("\n9. Calculs...");
        Double totalCharges = chargesRepository.calculateTotalCharges(cabinetId);
        System.out.println("✓ Total des charges du cabinet: " + totalCharges + " MAD");

        Double totalInterventions = interventionRepository.calculateTotalByConsultation(consultationId);
        System.out.println("✓ Total des interventions pour la consultation: " + totalInterventions + " MAD");

        Double coutOrdonnance = prescriptionRepository.calculateCoutTotalOrdonnance(ordonnanceId);
        System.out.println("✓ Coût total de l'ordonnance: " + coutOrdonnance + " MAD");

        // 10. Tests RevenuesRepository
        System.out.println("\n10. Tests RevenuesRepository...");
        List<Revenues> revenues = revenuesRepository.findByCabinetMedicaleId(cabinetId);
        System.out.println("✓ " + revenues.size() + " revenu(s) trouvé(s) pour le cabinet");
        Double totalRevenues = revenuesRepository.calculateTotalRevenues(cabinetId);
        System.out.println("✓ Total des revenus du cabinet: " + totalRevenues + " MAD");
        Long countRevenues = revenuesRepository.countRevenuesByCabinet(cabinetId);
        System.out.println("✓ Nombre de revenus: " + countRevenues);

        // 11. Tests SecretaireRepository
        System.out.println("\n11. Tests SecretaireRepository...");
        List<Secretaire> secretaires = secretaireRepository.findAll();
        System.out.println("✓ " + secretaires.size() + " secrétaire(s) trouvé(s)");
        List<Secretaire> secretairesCabinet = secretaireRepository.findByCabinetId(cabinetId);
        System.out.println("✓ " + secretairesCabinet.size() + " secrétaire(s) trouvé(s) pour le cabinet");
        List<Secretaire> secretairesRDV = secretaireRepository.getSecretairesAvecRDVEnCours();
        System.out.println("✓ " + secretairesRDV.size() + " secrétaire(s) avec RDV en cours");

        // 12. Tests StaffRepository
        System.out.println("\n12. Tests StaffRepository...");
        List<Staff> staffs = staffRepository.findByCabinetMedicaleId(cabinetId);
        System.out.println("✓ " + staffs.size() + " membre(s) du staff trouvé(s) pour le cabinet");
        List<Medecin> medecinsCabinet = staffRepository.findMedecinsByCabinet(cabinetId);
        System.out.println("✓ " + medecinsCabinet.size() + " médecin(s) trouvé(s) pour le cabinet");
        List<Secretaire> secretairesStaff = staffRepository.findSecretairesByCabinet(cabinetId);
        System.out.println("✓ " + secretairesStaff.size() + " secrétaire(s) trouvé(s) pour le cabinet");
        Long countStaff = staffRepository.countStaffByCabinet(cabinetId);
        System.out.println("✓ Nombre total de staff: " + countStaff);

        // 13. Tests UtilisateurRepository
        System.out.println("\n13. Tests UtilisateurRepository...");
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        System.out.println("✓ " + utilisateurs.size() + " utilisateur(s) trouvé(s)");
        List<Utilisateur> utilisateursActifs = utilisateurRepository.findActifs();
        System.out.println("✓ " + utilisateursActifs.size() + " utilisateur(s) actif(s)");
        List<Utilisateur> medecinsUsers = utilisateurRepository.findMedecins();
        System.out.println("✓ " + medecinsUsers.size() + " médecin(s) trouvé(s)");
        List<Utilisateur> secretairesUsers = utilisateurRepository.findSecretaires();
        System.out.println("✓ " + secretairesUsers.size() + " secrétaire(s) trouvé(s)");
        long countUsers = utilisateurRepository.countAll();
        System.out.println("✓ Nombre total d'utilisateurs: " + countUsers);

        // 14. Tests RDVRepository
        System.out.println("\n14. Tests RDVRepository...");
        List<RDV> rdvs = rdvRepository.findAll();
        System.out.println("✓ " + rdvs.size() + " RDV trouvé(s)");
        List<RDV> rdvsConsultation = rdvRepository.findByConsultationId(consultationId);
        System.out.println("✓ " + rdvsConsultation.size() + " RDV trouvé(s) pour la consultation");
        List<RDV> rdvsDossier = rdvRepository.findByDossierMedicaleId(dossierId);
        System.out.println("✓ " + rdvsDossier.size() + " RDV trouvé(s) pour le dossier médical");
        List<RDV> rdvsStatut = rdvRepository.findByStatut(StatutRendezVous.PLANIFIE);
        System.out.println("✓ " + rdvsStatut.size() + " RDV avec statut PLANIFIE");
        List<RDV> rdvsDate = rdvRepository.findByDate(LocalDate.now().plusDays(1));
        System.out.println("✓ " + rdvsDate.size() + " RDV trouvé(s) pour demain");
        long countRDV = rdvRepository.countByStatut(StatutRendezVous.PLANIFIE);
        System.out.println("✓ Nombre de RDV PLANIFIE: " + countRDV);
        boolean existsRDV = rdvRepository.existsById(rdvId);
        System.out.println("✓ RDV existe: " + existsRDV);

        // 15. Tests StatistiqueRepository
        System.out.println("\n15. Tests StatistiqueRepository...");
        List<Statistiques> statistiques = statistiqueRepository.findAll();
        System.out.println("✓ " + statistiques.size() + " statistique(s) trouvée(s)");
        List<Statistiques> statsCabinet = statistiqueRepository.findByCabinetMedicaleId(cabinetId);
        System.out.println("✓ " + statsCabinet.size() + " statistique(s) trouvée(s) pour le cabinet");
        List<Statistiques> statsCategorie = statistiqueRepository.findByCategorie(CategorieStatistique.CONSULTATIONS);
        System.out.println("✓ " + statsCategorie.size() + " statistique(s) de type CONSULTATIONS");
        Double moyenne = statistiqueRepository.calculateMoyenneByCategorie(CategorieStatistique.CONSULTATIONS, cabinetId);
        System.out.println("✓ Moyenne par catégorie: " + (moyenne != null ? moyenne : 0.0));

        System.out.println("\n=== FIN DU PROCESSUS DE SÉLECTION ===\n");
    }

    private static void updateProcess() {
        System.out.println("=== PROCESSUS DE MISE À JOUR ===\n");

        // 1. Mise à jour du Cabinet
        System.out.println("1. Mise à jour du Cabinet...");
        CabinetMedicale cabinet = cabinetRepository.findById(cabinetId);
        cabinet.setDescription("Cabinet dentaire moderne avec équipements de pointe - Mis à jour");
        cabinet.setModifiePar("admin");
        cabinetRepository.update(cabinet);
        System.out.println("✓ Cabinet mis à jour");

        // 2. Mise à jour du Patient
        System.out.println("\n2. Mise à jour du Patient...");
        Patient patient = patientRepository.findById(patientId);
        patient.setTelephone("0619999999");
        patient.setModifiePar("admin");
        patientRepository.update(patient);
        System.out.println("✓ Patient mis à jour");

        // 3. Mise à jour du statut de la Consultation
        System.out.println("\n3. Mise à jour du statut de la Consultation...");
        Consultation consultation = consultationRepository.findById(consultationId);
        consultation.setStatut(StatutConsultation.TERMINEE);
        consultation.setObservationMedecin("Consultation terminée avec succès");
        consultation.setModifiePar("admin");
        consultationRepository.update(consultation);
        System.out.println("✓ Consultation mise à jour");

        // 4. Mise à jour du statut de la Facture
        System.out.println("\n4. Mise à jour du statut de la Facture...");
        factureRepository.updateStatut(factureId, StatutFacture.REGLEE);
        System.out.println("✓ Statut de la facture mis à jour à REGLEE");

        // 5. Mise à jour du statut de la Situation Financière
        System.out.println("\n5. Mise à jour du statut de la Situation Financière...");
        situationFinanciereRepository.updateStatut(situationFinanciereId, StatutSituationFinanciere.SOLDE);
        System.out.println("✓ Statut de la situation financière mis à jour à SOLDE");

        // 6. Mise à jour de la Facture (montants)
        System.out.println("\n6. Mise à jour des montants de la Facture...");
        Facture facture = factureRepository.findById(factureId);
        facture.setTotalePayé(300.0);
        facture.setReste(0.0);
        facture.setModifiePar("admin");
        factureRepository.update(facture);
        System.out.println("✓ Montants de la facture mis à jour");

        // 7. Mise à jour de l'Acte
        System.out.println("\n7. Mise à jour du prix de l'Acte...");
        acteRepository.updatePrix(acteId, 350.0);
        System.out.println("✓ Prix de l'acte mis à jour");

        // 8. Mise à jour du Médecin
        System.out.println("\n8. Mise à jour du Médecin...");
        medecinRepository.updateSpecialite(medecinId, "Orthodontie");
        System.out.println("✓ Spécialité du médecin mise à jour");

        // 9. Tests Many-to-Many - Retirer un rôle du médecin
        System.out.println("\n9. Tests Many-to-Many - Retirer un rôle du médecin...");
        roleRepository.removeRoleFromUser(medecinId, roleAdminId);
        System.out.println("✓ Rôle ADMIN retiré du médecin");
        
        // Vérifier que le rôle a été retiré
        List<Role> rolesApresRetrait = roleRepository.findRolesByUserId(medecinId);
        System.out.println("✓ Le médecin a maintenant " + rolesApresRetrait.size() + " rôle(s)");
        for (Role role : rolesApresRetrait) {
            System.out.println("  - " + role.getLibelle());
        }

        // Réassigner le rôle ADMIN
        roleRepository.assignRoleToUser(medecinId, roleAdminId);
        System.out.println("✓ Rôle ADMIN réassigné au médecin");

        // 10. Tests Many-to-Many - Retirer un antécédent du patient
        System.out.println("\n10. Tests Many-to-Many - Retirer un antécédent du patient...");
        patientRepository.removeAntecedentFromPatient(patientId, antecedent2Id);
        System.out.println("✓ Antécédent 2 (Hypertension) retiré du patient");
        
        // Vérifier que l'antécédent a été retiré
        List<Antecedents> antecedentsApresRetrait = patientRepository.getAntecedentsOfPatient(patientId);
        System.out.println("✓ Le patient a maintenant " + antecedentsApresRetrait.size() + " antécédent(s)");
        for (Antecedents ant : antecedentsApresRetrait) {
            System.out.println("  - " + ant.getNom() + " (" + ant.getCategorie() + ")");
        }

        // Réassigner l'antécédent
        patientRepository.addAntecedentToPatient(patientId, antecedent2Id);
        System.out.println("✓ Antécédent 2 (Hypertension) réassigné au patient");

        // 11. Test assignRolesToUser (assigner plusieurs rôles en une fois)
        System.out.println("\n11. Test assignRolesToUser (assigner plusieurs rôles en une fois)...");
        // Retirer tous les rôles d'abord
        roleRepository.removeAllRolesFromUser(medecinId);
        System.out.println("✓ Tous les rôles retirés du médecin");
        
        // Assigner plusieurs rôles en une fois
        List<Long> roleIds = List.of(roleMedecinId, roleAdminId);
        roleRepository.assignRolesToUser(medecinId, roleIds);
        System.out.println("✓ " + roleIds.size() + " rôle(s) assigné(s) au médecin en une fois");
        
        // Vérifier
        List<Role> rolesFinaux = roleRepository.findRolesByUserId(medecinId);
        System.out.println("✓ Le médecin a maintenant " + rolesFinaux.size() + " rôle(s)");
        for (Role role : rolesFinaux) {
            System.out.println("  - " + role.getLibelle());
        }

        System.out.println("\n=== FIN DU PROCESSUS DE MISE À JOUR ===\n");
    }

    private static void deleteProcess() {
        System.out.println("=== PROCESSUS DE SUPPRESSION ===\n");
        System.out.println("⚠️  ATTENTION: La suppression est désactivée pour préserver les données de test");
        System.out.println("Décommentez les lignes suivantes pour activer la suppression:\n");

        /*
        // Suppression dans l'ordre inverse des dépendances
        System.out.println("1. Suppression de la Prescription...");
        prescriptionRepository.deleteById(prescriptionId);
        System.out.println("✓ Prescription supprimée");

        System.out.println("\n2. Suppression de l'Ordonnance...");
        ordonnanceRepository.deleteById(ordonnanceId);
        System.out.println("✓ Ordonnance supprimée");

        System.out.println("\n3. Suppression du Certificat...");
        certificatRepository.deleteById(certificatId);
        System.out.println("✓ Certificat supprimé");

        System.out.println("\n4. Suppression de l'Intervention...");
        interventionRepository.deleteById(interventionId);
        System.out.println("✓ Intervention supprimée");

        System.out.println("\n5. Suppression de la Facture...");
        factureRepository.deleteById(factureId);
        System.out.println("✓ Facture supprimée");

        System.out.println("\n6. Suppression de la Consultation...");
        consultationRepository.deleteById(consultationId);
        System.out.println("✓ Consultation supprimée");

        System.out.println("\n7. Suppression de la Situation Financière...");
        situationFinanciereRepository.deleteById(situationFinanciereId);
        System.out.println("✓ Situation financière supprimée");

        System.out.println("\n8. Suppression du Dossier Médical...");
        dossierRepository.deleteById(dossierId);
        System.out.println("✓ Dossier médical supprimé");

        System.out.println("\n9. Suppression de la relation Antécédent-Patient...");
        patientRepository.removeAntecedentFromPatient(patientId, antecedentId);
        System.out.println("✓ Relation supprimée");

        System.out.println("\n10. Suppression de l'Antécédent...");
        antecedentRepository.deleteById(antecedentId);
        System.out.println("✓ Antécédent supprimé");

        System.out.println("\n11. Suppression du Patient...");
        patientRepository.deleteById(patientId);
        System.out.println("✓ Patient supprimé");

        System.out.println("\n12. Suppression du Médecin...");
        medecinRepository.deleteById(medecinId);
        System.out.println("✓ Médecin supprimé");

        System.out.println("\n13. Suppression de la Charge...");
        chargesRepository.deleteById(chargeId);
        System.out.println("✓ Charge supprimée");

        System.out.println("\n14. Suppression du Cabinet...");
        cabinetRepository.deleteById(cabinetId);
        System.out.println("✓ Cabinet supprimé");

        System.out.println("\n15. Suppression du Médicament...");
        medicamentRepository.deleteById(medicamentId);
        System.out.println("✓ Médicament supprimé");

        System.out.println("\n16. Suppression de l'Acte...");
        acteRepository.deleteById(acteId);
        System.out.println("✓ Acte supprimé");
        */

        System.out.println("\n=== FIN DU PROCESSUS DE SUPPRESSION ===\n");
    }
}
