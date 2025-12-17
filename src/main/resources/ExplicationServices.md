# Guide Complet : Module Dossier Médical

Ce document fournit une explication complète du module Dossier Médical pour présentation au professeur. Il couvre l'architecture, les repositories, les services, les implémentations et les tests.

---

## 1. Architecture Générale (3 Couches)

### Vue d'Ensemble

Le module suit une architecture en **3 couches** avec séparation claire des responsabilités :

```
┌─────────────────────────────────────┐
│   COUCHE TEST (Test*Service.java)   │  ← Tests d'intégration
│   - Crée les données de test         │
│   - Appelle les services             │
│   - Vérifie les résultats            │
└─────────────────────────────────────┘
              ↓ utilise
┌─────────────────────────────────────┐
│   COUCHE SERVICE (*ServiceImpl.java) │  ← Logique métier
│   - Validation des données           │
│   - Règles de gestion                │
│   - Orchestration                    │
└─────────────────────────────────────┘
              ↓ utilise
┌─────────────────────────────────────┐
│   COUCHE REPOSITORY (*RepositoryImpl)│  ← Accès aux données
│   - Requêtes SQL                     │
│   - Mapping ResultSet → Objets        │
│   - Gestion des connexions           │
└─────────────────────────────────────┘
              ↓ utilise
┌─────────────────────────────────────┐
│         BASE DE DONNÉES              │
└─────────────────────────────────────┘
```

### Principes Clés

1. **Séparation des Responsabilités** : Chaque couche a un rôle précis
2. **Inversion de Dépendances** : Les services dépendent d'interfaces (API), pas d'implémentations
3. **Pattern Interface/Implémentation** : 
   - `*Repository` (interface) : Définit le contrat
   - `*RepositoryImpl` (classe) : Implémente avec SQL
   - `*Service` (interface) : Définit les opérations métier
   - `*ServiceImpl` (classe) : Implémente la logique

---

## 2. Couche Repository (Accès aux Données)

### 2.1 Structure des Repositories

Chaque repository suit cette structure :

```java
// API (Interface) - Définit le contrat
public interface DossierMedicalRepository extends CrudRepository<DossierMedicale, Long> {
    // Méthodes CRUD héritées de CrudRepository
    // + Méthodes spécifiques personnalisées
    List<DossierMedicale> findByPatientId(Long patientId);
    boolean existsByPatientId(Long patientId);
    long countAll();
    // ...
}

// Implémentation - Code SQL réel
public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {
    @Override
    public List<DossierMedicale> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM dossierMedicale WHERE patient_id = ?";
        // Exécution SQL + Mapping
    }
}
```

### 2.2 CrudRepository (Interface de Base)

Tous les repositories étendent `CrudRepository<T, ID>` qui fournit 6 méthodes standard :

```java
public interface CrudRepository<T, ID> {
    List<T> findAll();           // Récupérer tous les enregistrements
    T findById(ID id);           // Récupérer par ID
    void create(T newElement);   // Créer un nouvel enregistrement
    void update(T newValuesElement); // Mettre à jour
    void delete(T oldElement);   // Supprimer par objet
    void deleteById(ID id);      // Supprimer par ID
}
```

**Avantage** : Évite la duplication de code, standardise les opérations CRUD.

### 2.3 Repositories du Module Dossier Médical

#### A. DossierMedicalRepository

**Rôle** : Gère la persistance des dossiers médicaux

**Méthodes personnalisées** :
- `findByPatientId(Long patientId)` : Trouve les dossiers d'un patient
- `findByMedecinId(Long medecinId)` : Trouve les dossiers gérés par un médecin
- `findByDateCreation(LocalDate date)` : Recherche par date
- `findByDateCreationBetween(...)` : Recherche par période
- `existsByPatientId(Long)` : Vérifie l'existence (optimisé avec `SELECT 1`)
- `countAll()`, `countByPatientId(...)`, `countByMedecinId(...)` : Statistiques

**Exemple d'implémentation** :
```java
@Override
public List<DossierMedicale> findByPatientId(Long patientId) {
    String sql = "SELECT * FROM dossierMedicale WHERE patient_id = ? 
                  ORDER BY dateDecreation DESC, idDM";
    // Utilise PreparedStatement pour éviter les injections SQL
    // Utilise RowMappers pour convertir ResultSet → Objet
}
```

**Points importants** :
- Utilise `PreparedStatement` pour la sécurité (anti-injection SQL)
- Utilise `try-with-resources` pour la gestion automatique des connexions
- Utilise `RowMappers` pour le mapping automatique ResultSet → Objet
- Gestion d'erreurs avec messages explicites

#### B. ConsultationRepository

**Rôle** : Gère la persistance des consultations (visites médicales)

**Méthodes personnalisées** :
- `findByDossierMedicalId(Long)` : Consultations d'un dossier
- `findByStatut(StatutConsultation)` : Filtrer par statut (EN_ATTENTE, EN_COURS, TERMINEE, etc.)
- `findByDate(LocalDate)` : Consultations d'une date
- `findByDateBetween(...)` : Consultations dans une période
- `findByDossierAndDate(...)` : Recherche combinée
- `countByStatut(...)`, `countByDossierMedicalId(...)` : Statistiques

**Exemple d'implémentation** :
```java
@Override
public List<Consultation> findByStatut(StatutConsultation statut) {
    String sql = "SELECT * FROM consultation WHERE statut = ? 
                  ORDER BY date DESC, id_consultation";
    ps.setString(1, statut.name()); // Conversion enum → String
}
```

#### C. InterventionRepository

**Rôle** : Gère la persistance des interventions (actes médicaux)

**Méthodes personnalisées** :
- `findByConsultationId(Long)` : Interventions d'une consultation
- `findByActeId(Long)` : Interventions d'un type d'acte
- `findByNumDent(Integer)` : Recherche par numéro de dent (1-32)
- `findByConsultationAndActe(...)` : Recherche combinée
- `calculateTotalByConsultation(Long)` : **Calcul SQL** du total (SUM)
- `countByConsultationId(...)`, `countByActeId(...)` : Statistiques

**Point important - Calcul SQL** :
```java
@Override
public Double calculateTotalByConsultation(Long consultationId) {
    String sql = "SELECT SUM(prix_de_patient) FROM intervention_medecin 
                  WHERE consultation_id = ?";
    // Le calcul se fait en SQL, pas en Java → Performance optimale
    // Retourne 0.0 si aucun résultat (rs.wasNull())
}
```

**Pourquoi calculer en SQL ?**
- Performance : Le calcul se fait côté base de données
- Moins de données transférées : Seul le résultat est retourné
- Utilise les index de la base de données

#### D. PrescriptionRepository

**Rôle** : Gère la persistance des prescriptions (médicaments)

**Méthodes personnalisées** :
- `findByOrdonnanceId(Long)` : Prescriptions d'une ordonnance
- `findByMedicamentId(Long)` : Prescriptions d'un médicament
- `findByDureeSuperieure(Integer)` : Filtrer par durée minimale
- `findByOrdonnanceAndMedicament(...)` : Recherche combinée
- `calculateCoutTotalOrdonnance(Long)` : **Calcul avec JOIN** (prix × quantité)
- `countByOrdonnanceId(...)`, `countByMedicamentId(...)` : Statistiques

**Point important - Calcul avec JOIN** :
```java
@Override
public Double calculateCoutTotalOrdonnance(Long ordonnanceId) {
    String sql = """
        SELECT SUM(p.quantite * m.prixUnitaire) 
        FROM prescription p 
        JOIN medicament m ON p.medicament_id = m.idMct 
        WHERE p.ordonnance_id = ?
        """;
    // JOIN pour accéder au prix du médicament
    // Calcul : quantité × prix unitaire pour chaque prescription
    // SUM pour additionner tous les résultats
}
```

### 2.4 Patterns Importants dans les Repositories

#### Pattern : Gestion des Connexions
```java
try (Connection c = SessionFactory.getInstance().getConnection();
     PreparedStatement ps = c.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // Code ici
} catch (SQLException e) {
    throw new RuntimeException("Message explicite", e);
}
```
- `try-with-resources` : Fermeture automatique des ressources
- `SessionFactory` : Pattern Singleton pour la connexion
- Gestion d'erreurs : Conversion SQLException → RuntimeException avec message

#### Pattern : Mapping ResultSet → Objet
```java
while (rs.next()) {
    out.add(RowMappers.mapDossierMedicale(rs));
}
```
- `RowMappers` : Classe utilitaire centralisée pour le mapping
- Évite la duplication de code de mapping
- Facilite la maintenance

#### Pattern : Optimisation des Requêtes
```java
// ❌ MAUVAIS : Charge tout en mémoire puis filtre
List<Consultation> all = findAll();
return all.stream().filter(c -> c.getStatut() == statut).toList();

// ✅ BON : Filtre en SQL
String sql = "SELECT * FROM consultation WHERE statut = ?";
```
**Pourquoi ?** Performance : Moins de données transférées, utilise les index SQL.

---

## 3. Couche Service (Logique Métier)

### 3.1 Rôle des Services

Les services contiennent :
- **Logique métier** : Règles de gestion spécifiques au domaine
- **Validation** : Vérification de la cohérence des données
- **Orchestration** : Coordination entre plusieurs repositories
- **Gestion des erreurs** : Messages d'erreur métier

### 3.2 Structure des Services

```java
// Interface - Contrat
public interface DossierMedicalService {
    DossierMedicale createDossierMedical(DossierMedicale dossier);
    // ...
}

// Implémentation - Logique
public class DossierMedicalServiceImpl implements DossierMedicalService {
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    
    // Injection par constructeur
    public DossierMedicalServiceImpl(...) {
        this.dossierMedicalRepository = dossierMedicalRepository;
        // ...
    }
}
```

**Injection de Dépendances** : Les repositories sont passés au constructeur. Avantages :
- Facilite les tests (on peut injecter des mocks)
- Découple le service des implémentations concrètes
- Respecte le principe d'inversion de dépendances

### 3.3 Services du Module

#### A. DossierMedicalService

**Rôle** : Gère le cycle de vie du dossier médical (point d'entrée principal)

**Méthode clé : `createDossierMedical`**

```java
public DossierMedicale createDossierMedical(DossierMedicale dossierMedical) {
    // 1. VALIDATION
    validateDossierMedical(dossierMedical);
    
    // 2. VÉRIFICATION DES DÉPENDANCES
    if (patientRepository.findById(...) == null) {
        throw new ValidationException("Le patient n'existe pas");
    }
    if (medecinRepository.findById(...) == null) {
        throw new ValidationException("Le médecin n'existe pas");
    }
    
    // 3. RÈGLE MÉTIER : Un patient = Un seul dossier
    if (existsByPatientId(...)) {
        throw new ValidationException("Un dossier existe déjà pour ce patient");
    }
    
    // 4. VALEURS PAR DÉFAUT
    if (dossierMedical.getDateDeCreation() == null) {
        dossierMedical.setDateDeCreation(LocalDate.now());
    }
    
    // 5. AUDIT
    dossierMedical.setCreePar("system");
    dossierMedical.setModifiePar("system");
    
    // 6. PERSISTANCE
    dossierMedicalRepository.create(dossierMedical);
    return dossierMedical;
}
```

**Règles métier importantes** :
1. **Unicité** : Un patient ne peut avoir qu'un seul dossier médical
2. **Intégrité référentielle** : Le patient et le médecin doivent exister
3. **Audit** : Enregistrement de qui a créé/modifié et quand

**Méthodes de recherche** :
- `findByPatientId` : Utilise le repository (requête SQL optimisée)
- `findByMedecinId` : Permet à un médecin de voir "ses patients"
- `findByDateCreationBetween` : Statistiques par période

#### B. ConsultationService

**Rôle** : Gère les visites médicales (rendez-vous)

**Méthode clé : `createConsultation`**

```java
public Consultation createConsultation(Consultation consultation) {
    // 1. Validation
    validateConsultation(consultation);
    
    // 2. Vérification du dossier médical
    DossierMedicale dossier = dossierMedicalRepository.findById(...);
    if (dossier == null) {
        throw new ValidationException("Le dossier médical n'existe pas");
    }
    
    // 3. Valeurs par défaut
    if (consultation.getDate() == null) {
        consultation.setDate(LocalDate.now());
    }
    if (consultation.getStatut() == null) {
        consultation.setStatut(StatutConsultation.EN_ATTENTE);
    }
    
    // 4. Persistance
    consultationRepository.create(consultation);
    return consultation;
}
```

**Méthode clé : `changeStatut`**

```java
public void changeStatut(Long consultationId, StatutConsultation nouveauStatut) {
    Consultation consultation = getConsultationById(consultationId);
    StatutConsultation ancienStatut = consultation.getStatut();
    
    // RÈGLE MÉTIER : Contrôle des transitions
    if (ancienStatut == StatutConsultation.TERMINEE && 
        nouveauStatut != StatutConsultation.TERMINEE) {
        throw new ValidationException(
            "Une consultation terminée ne peut pas changer de statut");
    }
    if (ancienStatut == StatutConsultation.ANNULEE && 
        nouveauStatut != StatutConsultation.ANNULEE) {
        throw new ValidationException(
            "Une consultation annulée ne peut pas changer de statut");
    }
    
    consultation.setStatut(nouveauStatut);
    consultationRepository.update(consultation);
}
```

**Règles métier** :
- Une consultation terminée ne peut pas être réactivée
- Une consultation annulée ne peut pas être réactivée
- Les transitions de statut sont contrôlées

**Méthodes de recherche** :
- `findByStatut` : Utile pour le tableau de bord (voir qui est en attente)
- `findByDate` : Planification quotidienne
- `findByDossierMedicalId` : Historique des consultations d'un patient

#### C. InterventionService

**Rôle** : Gère les actes médicaux réalisés pendant une consultation

**Méthode clé : `createIntervention`**

```java
public InterventionMedecin createIntervention(InterventionMedecin intervention) {
    // 1. Validation
    validateIntervention(intervention);
    
    // 2. Vérification de la consultation
    Consultation consultation = consultationRepository.findById(...);
    if (consultation == null) {
        throw new ValidationException("La consultation n'existe pas");
    }
    
    // 3. Vérification de l'acte
    Acte acte = acteRepository.findById(...);
    if (acte == null) {
        throw new ValidationException("L'acte n'existe pas");
    }
    
    // 4. VALIDATION MÉTIER : Prix et numéro de dent
    if (intervention.getPrixDePatient() == null || 
        intervention.getPrixDePatient() < 0) {
        throw new ValidationException("Le prix doit être positif ou nul");
    }
    if (intervention.getNumDent() != null && 
        (intervention.getNumDent() < 1 || intervention.getNumDent() > 32)) {
        throw new ValidationException("Le numéro de dent doit être entre 1 et 32");
    }
    
    // 5. Persistance
    interventionRepository.create(intervention);
    return intervention;
}
```

**Méthode clé : `calculateTotalByConsultation`**

```java
public Double calculateTotalByConsultation(Long consultationId) {
    // Délègue au repository qui fait le calcul en SQL
    return interventionRepository.calculateTotalByConsultation(consultationId);
}
```

**Pourquoi déléguer au repository ?**
- Le calcul se fait en SQL (performance)
- Le service reste simple et lisible
- Séparation des responsabilités : Repository = données, Service = métier

#### D. PrescriptionService

**Rôle** : Gère les prescriptions de médicaments

**Méthode clé : `createPrescription`**

```java
public Prescription createPrescription(Prescription prescription) {
    // 1. Validation
    validatePrescription(prescription);
    
    // 2. Vérification de l'ordonnance
    Ordonnance ordonnance = ordonnanceRepository.findById(...);
    if (ordonnance == null) {
        throw new ValidationException("L'ordonnance n'existe pas");
    }
    
    // 3. Vérification du médicament
    Medicament medicament = medicamentRepository.findById(...);
    if (medicament == null) {
        throw new ValidationException("Le médicament n'existe pas");
    }
    
    // 4. VALIDATION MÉTIER : Quantité et durée
    if (prescription.getQuantité() <= 0) {
        throw new ValidationException("La quantité doit être positive");
    }
    if (prescription.getDuréeEnJours() <= 0) {
        throw new ValidationException("La durée doit être positive");
    }
    if (prescription.getFrequence() == null || 
        prescription.getFrequence().trim().isEmpty()) {
        throw new ValidationException("La fréquence est requise");
    }
    
    // 5. Persistance
    prescriptionRepository.create(prescription);
    return prescription;
}
```

**Méthode clé : `calculateCoutTotalOrdonnance`**

```java
public Double calculateCoutTotalOrdonnance(Long ordonnanceId) {
    // Délègue au repository qui fait un JOIN et un calcul SQL
    return prescriptionRepository.calculateCoutTotalOrdonnance(ordonnanceId);
}
```

**Calcul complexe en SQL** :
- JOIN entre `prescription` et `medicament` pour obtenir le prix
- Calcul : `quantité × prix_unitaire` pour chaque prescription
- SUM pour additionner tous les résultats

### 3.4 Patterns Importants dans les Services

#### Pattern : Validation en Cascade
```java
// 1. Validation de base
validateConsultation(consultation);

// 2. Validation des dépendances
if (consultation.getDossierMedicale() == null) {
    throw new ValidationException("Le dossier médical est requis");
}

// 3. Vérification d'existence
DossierMedicale dossier = dossierMedicalRepository.findById(...);
if (dossier == null) {
    throw new ValidationException("Le dossier n'existe pas");
}
```

#### Pattern : Valeurs par Défaut
```java
if (consultation.getDate() == null) {
    consultation.setDate(LocalDate.now());
}
if (consultation.getStatut() == null) {
    consultation.setStatut(StatutConsultation.EN_ATTENTE);
}
```

#### Pattern : Audit Trail
```java
consultation.setCreePar("system"); // À remplacer par l'utilisateur connecté
consultation.setModifiePar("system");
// Enregistre qui a créé/modifié et quand (via les champs de la base)
```

#### Pattern : Délégation au Repository
```java
// Service : Validation + Logique métier
public List<Consultation> findByStatut(StatutConsultation statut) {
    if (statut == null) {
        throw new ValidationException("Le statut ne peut pas être null");
    }
    // Délègue au repository pour la requête SQL
    return consultationRepository.findByStatut(statut);
}
```

---

## 4. Couche Test (Tests d'Intégration)

### 4.1 Structure des Tests

Chaque test suit cette structure :

```java
public class TestDossierMedicalService {
    public static void main(String[] args) {
        // 1. INITIALISATION
        DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
        PatientRepository patientRepo = new PatientRepositoryImpl();
        MedecinRepository medecinRepo = new MedecinRepositoryImpl();
        DossierMedicalServiceImpl service = new DossierMedicalServiceImpl(
            dossierRepo, patientRepo, medecinRepo);
        
        // 2. PRÉPARATION DES DONNÉES
        Patient testPatient = new Patient();
        // ... configuration
        patientRepo.create(testPatient);
        
        // 3. TESTS
        // Test 1: Création
        DossierMedicale created = service.createDossierMedical(dossier);
        
        // Test 2: Lecture
        DossierMedicale found = service.getDossierMedicalById(created.getIdDM());
        
        // Test 3: Recherche
        List<DossierMedicale> byPatient = service.findByPatientId(...);
        
        // Test 4: Mise à jour
        service.updateDossierMedical(...);
        
        // Test 5: Suppression
        service.deleteDossierMedical(...);
        
        // 4. VÉRIFICATION
        // Vérifie que l'objet n'existe plus après suppression
    }
}
```

### 4.2 Types de Tests Effectués

#### Tests CRUD Complets
1. **Create** : Création d'une entité et vérification de l'ID généré
2. **Read** : Récupération par ID et vérification des données
3. **Update** : Mise à jour et vérification des changements
4. **Delete** : Suppression et vérification de l'inexistence

#### Tests de Recherche
- Recherche par critères simples (ID, date, statut)
- Recherche par critères combinés (dossier + date)
- Recherche par période (date entre X et Y)

#### Tests de Calcul
- Calcul du total d'une consultation (somme des interventions)
- Calcul du coût d'une ordonnance (somme des prescriptions)

#### Tests de Statistiques
- Comptage total
- Comptage par critères (par statut, par dossier, etc.)

#### Tests de Validation
- Vérification des règles métier (ex: un patient = un dossier)
- Vérification des contraintes (ex: prix positif, numéro de dent 1-32)

### 4.3 Points Importants des Tests

**Utilisation des Repositories pour la Préparation** :
```java
// Les repositories sont utilisés pour créer les données de test
patientRepo.create(testPatient);
dossierRepo.create(dossier);
// C'est acceptable car c'est de la préparation, pas du test
```

**Utilisation des Services pour les Tests** :
```java
// Les services sont utilisés pour les tests réels
service.createDossierMedical(dossier);
service.findByPatientId(patientId);
// Teste la logique métier complète
```

**Gestion des Erreurs** :
```java
try {
    service.getDossierMedicalById(deletedId);
    System.out.println("✗ ERREUR: Devrait être supprimé");
} catch (Exception e) {
    System.out.println("✓ Suppression confirmée (exception attendue)");
}
```

---

## 5. Points Techniques Importants

### 5.1 Performance et Optimisation

#### Requêtes SQL Optimisées
```java
// ❌ MAUVAIS : Charge tout puis filtre en Java
List<Consultation> all = findAll();
return all.stream().filter(c -> c.getStatut() == statut).toList();

// ✅ BON : Filtre en SQL
String sql = "SELECT * FROM consultation WHERE statut = ?";
```

**Avantages** :
- Moins de données transférées
- Utilise les index de la base de données
- Calculs effectués côté serveur (SUM, COUNT)

#### Calculs en SQL
```java
// Calcul du total en SQL (pas en Java)
String sql = "SELECT SUM(prix_de_patient) FROM intervention_medecin 
              WHERE consultation_id = ?";
```

**Avantages** :
- Performance : Calcul côté base de données
- Moins de mémoire utilisée
- Utilise les optimisations de la base

### 5.2 Sécurité

#### Protection contre les Injections SQL
```java
// ✅ Utilise PreparedStatement (sécurisé)
PreparedStatement ps = c.prepareStatement(sql);
ps.setLong(1, patientId); // Paramètre typé et échappé

// ❌ NE JAMAIS FAIRE : Concaténation de strings
String sql = "SELECT * FROM patient WHERE id = " + patientId; // DANGEREUX
```

#### Validation des Entrées
```java
if (patientId == null) {
    throw new ValidationException("L'ID ne peut pas être null");
}
if (numDent < 1 || numDent > 32) {
    throw new ValidationException("Le numéro de dent doit être entre 1 et 32");
}
```

### 5.3 Gestion des Erreurs

#### Messages d'Erreur Explicites
```java
catch (SQLException e) {
    throw new RuntimeException(
        "Erreur lors de la recherche par patient ID: " + patientId, e);
}
```

**Pourquoi ?**
- Facilite le débogage
- Messages compréhensibles pour l'utilisateur
- Conserve la stack trace originale

#### Gestion des Ressources
```java
try (Connection c = SessionFactory.getInstance().getConnection();
     PreparedStatement ps = c.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // Code ici
} // Fermeture automatique même en cas d'erreur
```

### 5.4 Patterns de Code

#### Pattern Repository
- Interface pour définir le contrat
- Implémentation avec SQL
- Facilite les tests (mocks possibles)
- Découple le code métier de la base de données

#### Pattern Service
- Logique métier centralisée
- Validation et règles de gestion
- Orchestration entre repositories
- Point d'entrée unique pour les opérations

#### Pattern DTO/Mapping
- `RowMappers` : Conversion automatique ResultSet → Objet
- Évite la duplication de code
- Facilite la maintenance

---

## 6. Règles Métier Importantes

### 6.1 Dossier Médical

1. **Unicité** : Un patient ne peut avoir qu'un seul dossier médical
   ```java
   if (existsByPatientId(patientId)) {
       throw new ValidationException("Un dossier existe déjà");
   }
   ```

2. **Intégrité Référentielle** : Le patient et le médecin doivent exister
   ```java
   if (patientRepository.findById(patientId) == null) {
       throw new ValidationException("Le patient n'existe pas");
   }
   ```

### 6.2 Consultation

1. **Statut par Défaut** : `EN_ATTENTE` si non spécifié
2. **Transitions de Statut** : 
   - Une consultation `TERMINEE` ne peut pas changer de statut
   - Une consultation `ANNULEE` ne peut pas changer de statut
3. **Dépendance** : Doit être liée à un dossier médical existant

### 6.3 Intervention

1. **Prix** : Doit être positif ou nul
2. **Numéro de Dent** : Doit être entre 1 et 32 (si spécifié)
3. **Dépendances** : Consultation et Acte doivent exister

### 6.4 Prescription

1. **Quantité** : Doit être positive (> 0)
2. **Durée** : Doit être positive (> 0)
3. **Fréquence** : Requise et non vide
4. **Dépendances** : Ordonnance et Médicament doivent exister

---

## 7. Exemple de Lecture de Code (Code Walkthrough)

### Exemple : `updateDossierMedical`

Si le professeur demande d'expliquer cette méthode :

```java
public DossierMedicale updateDossierMedical(Long dossierId, 
                                           DossierMedicale dossierMedical) {
    // ÉTAPE 1 : Récupération de l'objet existant
    DossierMedicale existingDossier = getDossierMedicalById(dossierId);
    // Si l'ID n'existe pas, getDossierMedicalById lance une exception
    
    // ÉTAPE 2 : Validation
    validateDossierMedical(dossierMedical);
    
    // ÉTAPE 3 : Vérification des dépendances (si modifiées)
    if (dossierMedical.getPatient() != null) {
        Patient patient = patientRepository.findById(...);
        if (patient == null) {
            throw new ValidationException("Le patient n'existe pas");
        }
    }
    
    // ÉTAPE 4 : Mise à jour sélective (seulement les champs fournis)
    if (dossierMedical.getDateDeCreation() != null) {
        existingDossier.setDateDeCreation(dossierMedical.getDateDeCreation());
    }
    if (dossierMedical.getPatient() != null) {
        existingDossier.setPatient(dossierMedical.getPatient());
    }
    // Pattern : Ne met à jour que ce qui est fourni (null = pas de changement)
    
    // ÉTAPE 5 : Audit
    existingDossier.setModifiePar("system");
    // Enregistre qui a modifié (à remplacer par l'utilisateur connecté)
    
    // ÉTAPE 6 : Persistance
    dossierMedicalRepository.update(existingDossier);
    return existingDossier;
}
```

**Points à expliquer** :
1. **Récupération d'abord** : On récupère l'objet existant pour préserver les données non modifiées
2. **Mise à jour sélective** : On ne modifie que les champs fournis (pattern null = pas de changement)
3. **Validation** : On vérifie les nouvelles données avant de les appliquer
4. **Audit** : On enregistre qui a modifié
5. **Persistance** : On sauvegarde via le repository

---

## 8. Résumé pour Présentation

### Points Clés à Mentionner

1. **Architecture en 3 couches** : Test → Service → Repository → Base de données
2. **Séparation des responsabilités** : Chaque couche a un rôle précis
3. **Pattern Interface/Implémentation** : Découplage et testabilité
4. **Injection de dépendances** : Via constructeur
5. **Optimisation** : Requêtes SQL optimisées, calculs en SQL
6. **Sécurité** : PreparedStatement, validation des entrées
7. **Règles métier** : Validations et contrôles dans les services
8. **Tests complets** : CRUD, recherche, calculs, statistiques

### Démonstration Suggérée

1. Montrer la structure des dossiers (api/impl)
2. Expliquer une méthode de repository (ex: `findByPatientId`)
3. Expliquer une méthode de service (ex: `createDossierMedical`)
4. Montrer un test en action
5. Expliquer les règles métier importantes

---

## 9. Conclusion

Le module Dossier Médical suit les meilleures pratiques :
- ✅ Architecture claire et modulaire
- ✅ Séparation des responsabilités
- ✅ Code maintenable et testable
- ✅ Performance optimisée
- ✅ Sécurité renforcée
- ✅ Règles métier respectées

Ce design permet une évolution facile et une maintenance simplifiée.
