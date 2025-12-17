package ma.whitecare.repository.test;


import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.SecretaireRepositoryImpl;
import ma.whitecare.repository.modules.UserManager.impl.StaffRepositoryImpl;

import java.time.LocalDate;
import java.util.List;

public class SimpleRepositoriesTest {

    private static MedecinRepositoryImpl medecinRepo;
    private static SecretaireRepositoryImpl secretaireRepo;
    private static StaffRepositoryImpl staffRepo;

    private static Long medecinId;
    private static Long secretaireId;
    private static final Long CABINET_ID = 1L;

    public static void main(String[] args) {
        System.out.println("🚀 Démarrage des tests des Repositories\n");

        initRepositories();

        testMedecinRepository();
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testSecretaireRepository();
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testStaffRepository();
        System.out.println("*".repeat(50));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testDeleteOperations();

        System.out.println("\n✅ Tous les tests sont terminés avec succès!");
    }

    private static void initRepositories() {
        medecinRepo = new MedecinRepositoryImpl();
        secretaireRepo = new SecretaireRepositoryImpl();
        staffRepo = new StaffRepositoryImpl();
        System.out.println("📦 Repositories initialisés\n");
    }

    private static void testMedecinRepository() {
        System.out.println("=== TEST MÉDECIN REPOSITORY ===");

        // CREATE
        Medecin medecin = createTestMedecin();
        medecinRepo.create(medecin);
        System.out.println("✓ create() - Médecin créé");

        // FIND ALL
        List<Medecin> medecins = medecinRepo.findAll();
        medecinId = medecins.get(0).getIdUser();
        System.out.println("✓ findAll() - " + medecins.size() + " médecins trouvés");

        // FIND BY ID
        Medecin found = medecinRepo.findById(medecinId);
        if (found != null) {
            System.out.println("✓ findById() - Médecin trouvé: " + found.getNom());
        }

        // FIND BY SPECIALITE
        List<Medecin> bySpecialite = medecinRepo.findBySpecialite("Orthodontie");
        System.out.println("✓ findBySpecialite() - " + bySpecialite.size() + " médecins");

        // FIND BY CABINET
        List<Medecin> byCabinet = medecinRepo.findByCabinetId(CABINET_ID);
        System.out.println("✓ findByCabinetId() - " + byCabinet.size() + " médecins");

        // UPDATE OPERATIONS
        medecinRepo.updateSpecialite(medecinId, "Parodontologie");
        System.out.println("✓ updateSpecialite() - Spécialité mise à jour");

        medecinRepo.updateSalaire(medecinId, 13000.0);
        System.out.println("✓ updateSalaire() - Salaire mis à jour");
        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        medecinRepo.updateDisponibilite(medecinId, false);
        System.out.println("✓ updateDisponibilite() - Disponibilité mise à jour");
        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // AFFECTATION CABINET
        medecinRepo.retirerDuCabinet(medecinId);
        medecinRepo.affecterAuCabinet(medecinId, CABINET_ID);
        System.out.println("✓ affecterAuCabinet()/retirerDuCabinet() - Cabinet géré");

        System.out.println();
    }

    private static void testSecretaireRepository() {
        System.out.println("=== TEST SECRÉTAIRE REPOSITORY ===");

        // CREATE
        Secretaire secretaire = createTestSecretaire();
        secretaireRepo.create(secretaire);
        System.out.println("✓ create() - Secrétaire créée");

        // FIND ALL
        List<Secretaire> secretaires = secretaireRepo.findAll();
        secretaireId = secretaires.get(0).getIdUser();
        System.out.println("✓ findAll() - " + secretaires.size() + " secrétaires trouvées");

        // FIND BY ID
        Secretaire found = secretaireRepo.findById(secretaireId);
        if (found != null) {
            System.out.println("✓ findById() - Secrétaire trouvée: " + found.getNom());
        }

        // FIND BY CNSS
        List<Secretaire> byCNSS = secretaireRepo.findByNumeroCRSS("CNSS12345");
        System.out.println("✓ findByNumeroCRSS() - " + byCNSS.size() + " secrétaires");

        // UPDATE OPERATIONS
        secretaireRepo.updateCommission(secretaireId, 5.0);
        System.out.println("✓ updateCommission() - Commission mise à jour");
        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        secretaireRepo.updateSalaire(secretaireId, 7000.0);
        System.out.println("✓ updateSalaire() - Salaire mis à jour");

        // AFFECTATION CABINET
        secretaireRepo.retirerDuCabinet(secretaireId);
        secretaireRepo.affecterAuCabinet(secretaireId, CABINET_ID);
        System.out.println("✓ affecterAuCabinet()/retirerDuCabinet() - Cabinet géré");

        System.out.println();
    }

    private static void testStaffRepository() {
        System.out.println("=== TEST STAFF REPOSITORY ===");

        // FIND ALL STAFF
        List<Staff> allStaff = staffRepo.findAll();
        System.out.println("✓ findAll() - " + allStaff.size() + " staff trouvés");
allStaff.forEach(s-> System.out.println(s.toString()));
        // FIND BY CABINET
        List<Staff> byCabinet = staffRepo.findByCabinetMedicaleId(CABINET_ID);
        System.out.println("✓ findByCabinetMedicaleId() - " + byCabinet.size() + " staff dans le cabinet");

        // FIND MÉDECINS
        List<Medecin> medecins = staffRepo.findMedecinsByCabinet(CABINET_ID);
        System.out.println("✓ findMedecinsByCabinet() - " + medecins.size() + " médecins");

        // FIND SECRÉTAIRES
        List<Secretaire> secretaires = staffRepo.findSecretairesByCabinet(CABINET_ID);
        System.out.println("✓ findSecretairesByCabinet() - " + secretaires.size() + " secrétaires");

        // COUNT STAFF
        Long count = staffRepo.countStaffByCabinet(CABINET_ID);
        System.out.println("✓ countStaffByCabinet() - " + count + " staff au total");
        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // UPDATE VIA STAFF REPO
        if (medecinId != null) {
            staffRepo.updateSalaire(medecinId, 14000.0);
            System.out.println("✓ updateSalaire() via StaffRepo - Salaire mis à jour");
        }

        System.out.println();
    }

    private static void testDeleteOperations() {
        System.out.println("=== TEST SUPPRESSION ===");

        if (medecinId != null) {
            medecinRepo.deleteById(medecinId);
            System.out.println("✓ deleteById() - Médecin supprimé");
        }

        if (secretaireId != null) {
            secretaireRepo.deleteById(secretaireId);
            System.out.println("✓ deleteById() - Secrétaire supprimée");
        }

        // Vérification
        if (medecinId != null && medecinRepo.findById(medecinId) == null) {
            System.out.println("✓ Vérification - Médecin correctement supprimé");
        }

        if (secretaireId != null && secretaireRepo.findById(secretaireId) == null) {
            System.out.println("✓ Vérification - Secrétaire correctement supprimée");
        }
    }

    private static Medecin createTestMedecin() {
        Medecin medecin = new Medecin();
        medecin.setNom("TEST_NOM");
        medecin.setPrenom("TEST_PRENOM");
        medecin.setMotDePass("medmdp");
        medecin.setEmail("test.medecin@example.com");
        medecin.setSexe(Sexe.FEMME);
        medecin.setLogin("medecin1233");
        medecin.setActif(true);
        medecin.setSalaire(12000.0);
        medecin.setPrime(2000.0);
        medecin.setDateRecrutement(LocalDate.now());
        medecin.setSoldeConge(25);
        medecin.setCabinetMedicaleId(CABINET_ID);
        medecin.setSpecialite("Orthodontie");
        medecin.setCreePar("user_test");
        return medecin;
    }

    private static Secretaire createTestSecretaire() {
        Secretaire secretaire = new Secretaire();
        secretaire.setNom("TEST_SEC_NOM");
        secretaire.setPrenom("TEST_SEC_PRENOM");
        secretaire.setEmail("test.secretaire@example.com");
        secretaire.setMotDePass("secmdp");
        secretaire.setLogin("sec1233");
        secretaire.setActif(true);
        secretaire.setSalaire(6000.0);
        secretaire.setPrime(500.0);
        secretaire.setDateRecrutement(LocalDate.now());
        secretaire.setSoldeConge(20);
        secretaire.setSexe(Sexe.FEMME);
        secretaire.setCabinetMedicaleId(CABINET_ID);
        secretaire.setNumCNSS("CNSS12345");
        secretaire.setCreePar("user_test");
        secretaire.setCommission(3.5);
        return secretaire;
    }
}