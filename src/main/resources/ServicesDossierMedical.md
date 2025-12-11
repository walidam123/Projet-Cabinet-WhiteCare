# Documentation : Module Dossier Médical

Ce document décrit l'architecture, les services, les repositories et les tests du module de gestion des dossiers médicaux.

## 1. Services du Dossier Médical

Le module est composé de quatre services principaux, chacun gérant un aspect spécifique du dossier patient.

### A. DossierMedicalService
Ce service gère le cycle de vie du dossier médical lui-même.

**Fonctions principales :**
*   **CRUD** : `createDossierMedical`, `updateDossierMedical`, `deleteDossierMedical`, `getDossierMedicalById`, `getAllDossiersMedicaux`.
*   **Recherches** :
    *   `findByPatientId(Long patientId)` : Retrouver les dossiers d'un patient.
    *   `findByMedecinId(Long medecinId)` : Retrouver les dossiers gérés par un médecin.
    *   `findByDateCreation(LocalDate date)` : Rechercher par date de création.
    *   `findByDateCreationBetween(LocalDate startDate, LocalDate endDate)` : Rechercher par période.
*   **Validation** : `existsByPatientId`, `existsById`.
*   **Statistiques** : `countAllDossiers`, `countByPatientId`, `countByMedecinId`.

### B. ConsultationService
Ce service gère les visites médicales associées à un dossier.

**Fonctions principales :**
*   **CRUD** : `createConsultation`, `updateConsultation`, `deleteConsultation`, `getConsultationById`, `getAllConsultations`.
*   **Recherches** :
    *   `findByDossierMedicalId(Long dossierId)` : Lister les consultations d'un dossier.
    *   `findByStatut(StatutConsultation statut)` : Filtrer par statut (EN_ATTENTE, EN_COURS, TERMINEE, etc.).
    *   `findByDate(LocalDate date)` : Rechercher par date.
    *   `findByDateBetween(LocalDate startDate, LocalDate endDate)` : Rechercher par période.
    *   `findByDossierAndDate` : Rechercher une consultation spécifique.
*   **Gestion Statut** : `changeStatut(Long consultationId, StatutConsultation nouveauStatut)`.
*   **Statistiques** : `countAllConsultations`, `countByStatut`, `countByDossierMedicalId`.

### C. InterventionService
Ce service gère les actes médicaux réalisés durant une consultation.

**Fonctions principales :**
*   **CRUD** : `createIntervention`, `updateIntervention`, `deleteIntervention`, `getInterventionById`, `getAllInterventions`.
*   **Recherches** :
    *   `findByConsultationId(Long consultationId)` : Lister les interventions d'une consultation.
    *   `findByActeId(Long acteId)` : Retrouver les interventions par type d'acte.
    *   `findByNumDent(Integer numDent)` : Rechercher par numéro de dent traitée.
*   **Calculs** : `calculateTotalByConsultation(Long consultationId)` (Coût total des interventions d'une consultation).
*   **Statistiques** : `countAllInterventions`, `countByConsultationId`, `countByActeId`.

### D. PrescriptionService
Ce service gère les prescriptions médicamenteuses (liées à une ordonnance).

**Fonctions principales :**
*   **CRUD** : `createPrescription`, `updatePrescription`, `deletePrescription`, `getPrescriptionById`, `getAllPrescriptions`.
*   **Recherches** :
    *   `findByOrdonnanceId(Long ordonnanceId)` : Lister les médicaments prescrits sur une ordonnance.
    *   `findByMedicamentId(Long medicamentId)` : Retrouver les prescriptions d'un médicament spécifique.
    *   `findByDureeSuperieure(Integer dureeMin)` : Filtrer par durée de traitement.
*   **Calculs** : `calculateCoutTotalOrdonnance(Long ordonnanceId)` (Estimation du coût total de l'ordonnance).
*   **Statistiques** : `countAllPrescriptions`, `countByOrdonnanceId`, `countByMedicamentId`.

## 2. Implémentation

Les services sont définis par des interfaces (`api`) et implémentés dans des classes concrètes (`impl`).
*   `DossierMedicalServiceImpl`
*   `ConsultationServiceImpl`
*   `InterventionServiceImpl`
*   `PrescriptionServiceImpl`

Ces implémentations intègrent la logique métier, les validations (vérification d'existence des entités liées, règles de gestion) et délèguent la persistance aux repositories.

## 3. Repositories Nécessaires

Pour fonctionner, ces services s'appuient sur la couche d'accès aux données (DAO) suivante :

*   **DossierMedicalRepository** : Persistance des dossiers médicaux.
*   **ConsultationRepository** : Persistance des consultations.
*   **InterventionRepository** : Persistance des interventions.
*   **PrescriptionRepository** : Persistance des prescriptions.
*   **OrdonnanceRepository** : Nécessaire pour lier les prescriptions aux ordonnances.
*   **MedicamentRepository** : Nécessaire pour lier les prescriptions aux médicaments.
*   **PatientRepository** : Nécessaire pour lier les dossiers aux patients.
*   **MedecinRepository** : Nécessaire pour lier les dossiers aux médecins.
*   **ActeRepository** : Nécessaire pour lier les interventions aux actes médicaux.

## 4. Tests

Chaque service dispose d'une classe de test d'intégration dédiée qui vérifie l'ensemble de ses fonctionnalités en conditions réelles (avec base de données).

| Classe de Test | Couverture |
|----------------|------------|
| **`TestDossierMedicalService`** | Couvre le cycle de vie complet d'un dossier, les recherches par critères et les statistiques. |
| **`TestConsultationService`** | Vérifie la création de consultations, la gestion des statuts, les filtres de recherche et les statistiques. |
| **`TestInterventionService`** | Valide l'ajout d'interventions, le calcul des coûts par consultation et les liens avec les actes. |
| **`TestPrescriptionService`** | Teste la création de prescriptions, les calculs de coûts d'ordonnance et les recherches par médicament. |

Tous les tests ont été exécutés et validés avec succès.
