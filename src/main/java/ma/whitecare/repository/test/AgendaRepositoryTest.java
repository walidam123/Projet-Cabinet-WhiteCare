package ma.whitecare.repository.test;


import ma.whitecare.entities.agenda.AgendaMensuel;
import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.enums.JourSemaine;
import ma.whitecare.entities.enums.Mois;
import ma.whitecare.entities.enums.Sexe;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.UserManager.impl.MedecinRepositoryImpl;
import ma.whitecare.repository.modules.agenda.api.AgendaRepository;
import ma.whitecare.repository.modules.agenda.impl.AgendaRepositoryImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class AgendaRepositoryTest {

    private static AgendaRepository repository=new AgendaRepositoryImpl();;
    private static Long testCabinetId = 1L;
    private static Long testMedecinId = 1L;
    private static AgendaMensuel agendaTest;
    private static Jour jourTest;
    private static Creneau creneauTest;

    public static void main(String[] args) {
        System.out.println("🚀 TEST COMPLET - AgendaRepository\n");



        try {
            testCRUDAgenda();
            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testGestionJours();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testGestionCreneaux();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            testRecherchesAvancees();

            try {
                // 2. Attendre 3 secondes
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*".repeat(50));

            //testValidations();

            System.out.println("\n🎉 TOUS LES TESTS TERMINÉS AVEC SUCCÈS!");

        } catch (Exception e) {
            System.out.println("❌ Erreur pendant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testCRUDAgenda() {
        System.out.println("📋 === TEST CRUD AGENDA ===\n");

        // 1. Test CREATE
        System.out.println("1. 📝 Test création agenda...");
        agendaTest = AgendaMensuel.builder()
                .mois(Mois.JANVIER)
                .medecinId(testMedecinId)
                .annee(2024)
                .medecinId(testMedecinId)
                .creePar("test_user")
                .modifiePar("test_user")
                .build();

        repository.create(agendaTest);
        System.out.println("✅ Agenda créé avec ID: " + agendaTest.getId());

        // 2. Test FIND BY ID
        System.out.println("\n2. 🔍 Test recherche par ID...");
        AgendaMensuel found = repository.findById(agendaTest.getId());
        assertNotNull(found, "Agenda doit être trouvé");
        assertEquals(Mois.JANVIER, found.getMois());
        System.out.println(found.toString());



        // 4. Test FIND BY MEDECIN
        System.out.println("\n4. 👨‍⚕️ Test recherche par médecin...");
        List<AgendaMensuel> agendasMedecin = repository.findByMedecinId(testMedecinId);
        System.out.println("✅ " + agendasMedecin.size() + " agendas trouvés pour le médecin");
        agendasMedecin.forEach(a-> System.out.println(a.toString()));



        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // 7. Test UPDATE
        System.out.println("\n7. ✏️ Test modification agenda...");
        agendaTest.setMois(Mois.FEVRIER);
        repository.update(agendaTest);

        AgendaMensuel updated = repository.findById(agendaTest.getId());
        assertEquals(Mois.FEVRIER, updated.getMois());
        System.out.println("✅ Agenda modifié: " + updated.getMois());


    }

    private static void testGestionJours() {
        System.out.println("\n\n📅 === TEST GESTION JOURS ===\n");

        // 1. Test AJOUTER JOUR
        System.out.println("1. ➕ Test ajout jour...");
        jourTest = Jour.builder()
                .date(LocalDate.of(2024, 2, 15))
                .jourSemaine(JourSemaine.JEUDI)
                .estDisponible(true)
                .raisonIndisponibilite(null)
                .build();

        repository.ajouterJour(agendaTest.getId(), jourTest);
        System.out.println("✅ Jour ajouté avec ID: " + jourTest.getId());

        // 2. Test FIND JOURS BY AGENDA
        System.out.println("\n2. 🔍 Test recherche jours par agenda...");
        List<Jour> jours = repository.findJoursByAgendaId(agendaTest.getId());
        assertTrue(jours.size() > 0, "Doit trouver au moins un jour");
        System.out.println("✅ " + jours.size() + " jours trouvés");

        // 3. Test FIND JOUR DISPONIBLES
        System.out.println("\n3. ✅ Test recherche jours disponibles...");
        List<Jour> joursDisponibles = repository.findJoursDisponiblesByAgendaId(agendaTest.getId());

        System.out.println("✅ " + joursDisponibles.size() + " jours disponibles");

        // 4. Test FIND JOUR NON DISPONIBLES
        System.out.println("\n4. ❌ Test recherche jours non disponibles...");
        List<Jour> joursNonDisponibles = repository.findJoursNonDisponiblesByAgendaId(agendaTest.getId());
        System.out.println("✅ " + joursNonDisponibles.size() + " jours non disponibles");

        // 5. Test FIND JOUR BY DATE
        System.out.println("\n5. 📆 Test recherche jour par date...");
        Optional<Jour> optJour = repository.findJourByDate(agendaTest.getId(),
                LocalDate.of(2024, 2, 15));
        assertTrue(optJour.isPresent(), "Doit trouver le jour par date");
        System.out.println("✅ Jour trouvé par date");

        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 6. Test UPDATE JOUR
        System.out.println("\n6. ✏️ Test modification jour...");
        jourTest.setEstDisponible(false);
        jourTest.setRaisonIndisponibilite("Congé");
        repository.mettreAJourJour(jourTest);

        Jour jourUpdated = repository.findJourByDate(agendaTest.getId(),
                LocalDate.of(2024, 2, 15)).orElse(null);

        System.out.println("✅ Jour modifié - Disponible: " + jourUpdated.isEstDisponible());

        // 7. Test EST JOUR DISPONIBLE
        System.out.println("\n7. 🔍 Test vérification disponibilité jour...");
        boolean disponible = repository.estJourDisponible(agendaTest.getId(),
                LocalDate.of(2024, 2, 15));
        assertFalse(disponible, "Jour doit être non disponible");
        System.out.println("✅ Vérification disponibilité OK");
    }

    private static void testGestionCreneaux() {
        System.out.println("\n\n⏰ === TEST GESTION CRENEAUX ===\n");

        // 1. Test AJOUTER CRENEAU
        System.out.println("1. ➕ Test ajout créneau...");
        creneauTest = Creneau.builder()
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(10, 0))
                .estDisponible(true)
                .motifIndisponibilite(null)
                .rendezVousId(null)
                .build();

        repository.ajouterCreneau(jourTest.getId(), creneauTest);
        System.out.println("✅ Créneau ajouté avec ID: " + creneauTest.getId());

        // 2. Test FIND CRENEAUX BY JOUR
        System.out.println("\n2. 🔍 Test recherche créneaux par jour...");
        List<Creneau> creneaux = repository.findCreneauxByJourId(jourTest.getId());
        assertTrue(creneaux.size() > 0, "Doit trouver au moins un créneau");
        System.out.println("✅ " + creneaux.size() + " créneaux trouvés");

        // 3. Test FIND CRENEAUX DISPONIBLES
        System.out.println("\n3. ✅ Test recherche créneaux disponibles...");
        List<Creneau> creneauxDisponibles = repository.findCreneauxDisponiblesByJourId(jourTest.getId());

        System.out.println("✅ " + creneauxDisponibles.size() + " créneaux disponibles");

        // 4. Test FIND CRENEAUX BY AGENDA AND DATE
        System.out.println("\n4. 📅 Test recherche créneaux par agenda et date...");
        List<Creneau> creneauxDate = repository.findCreneauxByAgendaAndDate(
                agendaTest.getId(), LocalDate.of(2024, 2, 15));
        assertTrue(creneauxDate.size() > 0, "Doit trouver des créneaux");
        System.out.println("✅ " + creneauxDate.size() + " créneaux trouvés pour la date");

        // 5. Test EST CRENEAU DISPONIBLE
        System.out.println("\n5. 🔍 Test vérification disponibilité créneau...");
        boolean creneauDisponible = repository.estCreneauDisponible(jourTest.getId(), creneauTest);
        assertTrue(creneauDisponible, "Créneau doit être disponible");
        System.out.println("✅ Créneau disponible");

        // 6. Test FIND CRENEAUX DISPONIBLES BY DATE
        System.out.println("\n6. 📆 Test recherche créneaux disponibles par date...");
        List<Creneau> creneauxLibres = repository.findCreneauxDisponiblesByDate(
                agendaTest.getId(), LocalDate.of(2024, 2, 15));
        System.out.println("✅ " + creneauxLibres.size() + " créneaux libres trouvés");

        System.out.println("-".repeat(25));
        try {
            // 2. Attendre 3 secondes
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // 7. Test UPDATE CRENEAU
        System.out.println("\n7. ✏️ Test modification créneau...");
        creneauTest.setEstDisponible(false);
        creneauTest.setMotifIndisponibilite("Rendez-vous");
        repository.mettreAJourCreneau(creneauTest);

        List<Creneau> creneauxUpdated = repository.findCreneauxByJourId(jourTest.getId());
        Creneau updatedCreneau = creneauxUpdated.get(0);

        System.out.println("✅ Créneau modifié - Disponible: " + updatedCreneau.isEstDisponible());

        // 8. Test AJOUTER SECOND CRENEAU
        System.out.println("\n8. ➕ Test ajout second créneau...");
        Creneau creneau2 = Creneau.builder()
                .heureDebut(LocalTime.of(14, 0))
                .heureFin(LocalTime.of(15, 0))
                .estDisponible(true)
                .build();

        repository.ajouterCreneau(jourTest.getId(), creneau2);
        System.out.println("✅ Second créneau ajouté avec ID: " + creneau2.getId());
    }

    private static void testRecherchesAvancees() {
        System.out.println("\n\n🔍 === TEST RECHERCHES AVANCÉES ===\n");


        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 28);



        // 3. Test FIND BY MOIS ANNEE MEDECIN
        System.out.println("\n3. 👨‍⚕️ Test recherche par mois/année/médecin...");
        Optional<AgendaMensuel> optAgendaMedecin = repository.findByMoisAndAnneeAndMedecin(
                Mois.FEVRIER, 2024, testMedecinId);
        assertTrue(optAgendaMedecin.isPresent(), "Doit trouver l'agenda du médecin");
        System.out.println("✅ Agenda du médecin trouvé");
    }



    private static void testValidations() {
        System.out.println("\n\n✅ === TEST VALIDATIONS ===\n");



        // 2. Test FIND ALL
        System.out.println("\n2. 📋 Test recherche tous les agendas...");
        List<AgendaMensuel> allAgendas = repository.findAll();
        assertTrue(allAgendas.size() > 0, "Doit trouver des agendas");
        System.out.println("✅ " + allAgendas.size() + " agendas au total");

        // 3. Test SUPPRESSION
        System.out.println("\n3. 🗑️ Test suppression complète...");

        // Supprimer créneaux d'abord (géré automatiquement)
        repository.supprimerCreneau(creneauTest.getId());
        System.out.println("✅ Créneau supprimé");

        // Supprimer jour
        repository.supprimerJour(jourTest.getId());
        System.out.println("✅ Jour supprimé");

        // Supprimer agenda
        repository.deleteById(agendaTest.getId());
        System.out.println("✅ Agenda supprimé");

        // Vérifier suppression
        AgendaMensuel deleted = repository.findById(agendaTest.getId());
        assertNull(deleted, "Agenda doit être supprimé");
        System.out.println("✅ Vérification suppression OK");
    }

    // Méthodes d'assertion simplifiées
    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("❌ " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("❌ Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void  createTestMedecin() {
        MedecinRepository repo = new MedecinRepositoryImpl();
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
        medecin.setCabinetMedicaleId(testCabinetId);
        medecin.setSpecialite("Orthodontie");
        medecin.setCreePar("user_test");
        System.out.println("Creation du medecin pour Test");
        repo.create(medecin);
        System.out.println("Medecin: " + medecin.getIdUser() + " , nom: " + medecin.getNom());
    }
}