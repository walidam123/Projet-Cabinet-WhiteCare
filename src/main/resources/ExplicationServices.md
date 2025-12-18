# Guide Complet : Module Dossier Médical

Ce document fournit une explication complète du module Dossier Médical pour présentation au professeur. Il couvre l'architecture, les DTOs, les services, les implémentations et les tests.

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

## 2. Les DTOs (Data Transfer Objects)

### 2.1 Qu'est-ce qu'un DTO ?

Un **DTO (Data Transfer Object)** est un objet simple qui transporte des données entre les couches de l'application. Il ne contient pas de logique métier, seulement des données.

**Pourquoi utiliser des DTOs ?**
- **Séparation des couches** : Les entités métier (Entities) restent internes aux services
- **Sécurité** : On contrôle exactement quelles données sont exposées
- **Performance** : On peut limiter les données transférées (pas besoin de charger toutes les relations)
- **Flexibilité** : On peut modifier les entités sans affecter l'API

### 2.2 Types de DTOs Utilisés

Pour chaque entité, nous avons **3 types de DTOs** :

#### A. CreateDTO (Pour la Création)

**Rôle** : Contient les données nécessaires pour créer une nouvelle entité.

**Caractéristiques** :
- Contient les **IDs** des entités liées (pas les objets complets)
- Champs obligatoires annotés avec `@NotNull`
- Utilise le pattern Builder de Lombok

**Exemple : `CreateConsultationDTO`**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultationDTO {
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long dossierMedicalId;  // Seulement l'ID, pas l'objet DossierMedicale
    
    private LocalDate date;  // Optionnel (sera défini à aujourd'hui si null)
    private StatutConsultation statut;  // Optionnel (sera EN_ATTENTE si null)
    private String observationMedecin;  // Optionnel
}
```

**Utilisation dans les tests** :
```java
CreateConsultationDTO createDTO = CreateConsultationDTO.builder()
    .dossierMedicalId(dossier.getIdDM())  // Seulement l'ID
    .date(LocalDate.now())
    .statut(StatutConsultation.EN_ATTENTE)
    .observationMedecin("Consultation de test")
    .build();

ConsultationDTO created = service.createConsultation(createDTO);
```

#### B. UpdateDTO (Pour la Mise à Jour)

**Rôle** : Contient les données à modifier (mise à jour sélective).

**Caractéristiques** :
- **Tous les champs sont optionnels** (null = pas de modification)
- Pattern : Seulement les champs non-null sont mis à jour
- Permet de modifier partiellement une entité

**Exemple : `UpdateConsultationDTO`**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConsultationDTO {
    private Long dossierMedicalId;  // Optionnel
    private LocalDate date;  // Optionnel
    private StatutConsultation statut;  // Optionnel
    private String observationMedecin;  // Optionnel
    // Tous les champs sont null par défaut = pas de modification
}
```

**Utilisation dans les tests** :
```java
UpdateConsultationDTO updateDTO = UpdateConsultationDTO.builder()
    .observationMedecin("Observation mise à jour")  // Seulement ce champ
    .build();  // Les autres restent null = pas de modification

ConsultationDTO updated = service.updateConsultation(id, updateDTO);
// Seule l'observation est modifiée, le reste reste inchangé
```

#### C. DTO (Pour la Lecture)

**Rôle** : Contient les données à afficher après création/lecture.

**Caractéristiques** :
- Contient l'**ID généré** de l'entité créée
- Contient les **IDs** des entités liées (pas les objets complets)
- Contient les **champs d'audit** (dateCreation, createdBy, etc.)
- Ne contient que les données nécessaires à l'affichage

**Exemple : `ConsultationDTO`**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {
    private Long idConsultation;  // ID généré après création
    private LocalDate date;
    private StatutConsultation statut;
    private String observationMedecin;
    private Long dossierMedicalId;  // Seulement l'ID, pas l'objet DossierMedicale
    private LocalDate dateCreation;  // Champ d'audit
    private LocalDate dateDerniereModification;  // Champ d'audit
    private String createdBy;  // Champ d'audit
    private String updatedBy;  // Champ d'audit
}
```

**Utilisation dans les tests** :
```java
ConsultationDTO consultation = service.getConsultationById(id);
System.out.println("ID: " + consultation.getIdConsultation());
System.out.println("Dossier ID: " + consultation.getDossierMedicalId());
// On utilise seulement les données du DTO, pas les objets complets
```

### 2.3 DTOs du Module Dossier Médical

#### A. Dossier Médical

- **CreateDossierMedicalDTO** : `patientId`, `medecinId`, `dateDeCreation` (optionnel)
- **UpdateDossierMedicalDTO** : Tous les champs optionnels pour mise à jour sélective
- **DossierMedicalDTO** : `idDM`, `dateDeCreation`, `patientId`, `medecinId`, champs d'audit

#### B. Consultation

- **CreateConsultationDTO** : `dossierMedicalId`, `date` (optionnel), `statut` (optionnel), `observationMedecin` (optionnel)
- **UpdateConsultationDTO** : Tous les champs optionnels
- **ConsultationDTO** : `idConsultation`, `date`, `statut`, `observationMedecin`, `dossierMedicalId`, champs d'audit
- **ConsultationCompleteDTO** : ConsultationDTO + `List<InterventionDTO>` + `List<PrescriptionDTO>` (pour affichage complet)

#### C. Intervention

- **CreateInterventionDTO** : `consultationId`, `acteId`, `prixDePatient`, `numDent` (optionnel)
- **UpdateInterventionDTO** : Tous les champs optionnels
- **InterventionDTO** : `idIM`, `prixDePatient`, `numDent`, `acteId`, `consultationId`, champs d'audit

#### D. Prescription

- **CreatePrescriptionDTO** : `ordonnanceId`, `medicamentId`, `quantité`, `fréquence`, `duréeEnJours`
- **UpdatePrescriptionDTO** : Tous les champs optionnels
- **PrescriptionDTO** : `idPr`, `quantité`, `fréquence`, `duréeEnJours`, `medicamentId`, `ordonnanceId`, champs d'audit

### 2.4 Avantages des DTOs

**1. Sécurité** :
```java
// ❌ SANS DTO : Expose toute l'entité avec toutes ses relations
Consultation consultation = service.getConsultationById(id);
// Contient : DossierMedicale (avec Patient, Medecin), 
//            List<InterventionMedecin>, etc.
// → Trop de données exposées

// ✅ AVEC DTO : Expose seulement ce qui est nécessaire
ConsultationDTO consultation = service.getConsultationById(id);
// Contient seulement : idConsultation, date, statut, dossierMedicalId
// → Contrôle total sur les données exposées
```

**2. Performance** :
```java
// Le DTO ne contient que l'ID du dossier, pas l'objet complet
// → Pas besoin de charger toutes les relations
// → Moins de données transférées
// → Performance optimale
```

**3. Flexibilité** :
```java
// On peut modifier l'entité Consultation sans affecter le DTO
// → L'API reste stable même si l'entité change
// → Évolution facile du code métier
```

### 2.5 Pattern Builder avec Lombok

Tous les DTOs utilisent le pattern Builder grâce à Lombok :

```java
// Création avec Builder (fluide et lisible)
CreateConsultationDTO dto = CreateConsultationDTO.builder()
    .dossierMedicalId(123L)
    .date(LocalDate.now())
    .statut(StatutConsultation.EN_ATTENTE)
    .observationMedecin("Consultation de test")
    .build();

// Équivalent à :
CreateConsultationDTO dto = new CreateConsultationDTO();
dto.setDossierMedicalId(123L);
dto.setDate(LocalDate.now());
// ... mais beaucoup plus lisible avec Builder
```

**Avantages du Builder** :
- Code plus lisible
- Permet de créer des objets partiels (certains champs peuvent être omis)
- Facilite les tests

---

## 3. Couche Service (Logique Métier)

### 3.1 Rôle des Services

Les services contiennent :
- **Logique métier** : Règles de gestion spécifiques au domaine
- **Validation** : Vérification de la cohérence des données
- **Orchestration** : Coordination entre plusieurs repositories
- **Gestion des erreurs** : Messages d'erreur métier

### 3.2 Structure des Services avec Pattern DTO

**Architecture DTO (Data Transfer Object)** : Les services utilisent des DTOs pour séparer la couche présentation de la couche métier.

```java
// Interface - Contrat avec DTOs
public interface DossierMedicalService {
    DossierMedicalDTO createDossierMedical(CreateDossierMedicalDTO dto);
    DossierMedicalDTO updateDossierMedical(Long dossierId, UpdateDossierMedicalDTO dto);
    DossierMedicalDTO getDossierMedicalById(Long dossierId);
    List<DossierMedicalDTO> getAllDossiersMedicaux();
    // ...
}

// Implémentation - Logique avec conversion DTO ↔ Entity
public class DossierMedicalServiceImpl implements DossierMedicalService {
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    
    // Injection par constructeur
    public DossierMedicalServiceImpl(...) {
        this.dossierMedicalRepository = dossierMedicalRepository;
        // ...
    }
    
    // Conversion DTO → Entity (pour la persistance)
    private DossierMedicale convertToEntity(CreateDossierMedicalDTO dto) {
        // Charge les entités liées depuis les IDs
        Patient patient = patientRepository.findById(dto.getPatientId());
        Medecin medecin = medecinRepository.findById(dto.getMedecinId());
        
        // Construit l'entité avec Builder pattern
        return DossierMedicale.builder()
            .dateDeCreation(dto.getDateDeCreation())
            .patient(patient)
            .medecin(medecin)
            .build();
    }
    
    // Conversion Entity → DTO (pour le retour)
    private DossierMedicalDTO convertToDTO(DossierMedicale entity) {
        return DossierMedicalDTO.builder()
            .idDM(entity.getIdDM())
            .dateDeCreation(entity.getDateDeCreation())
            .patientId(entity.getPatient().getId_Patient())
            .medecinId(entity.getMedecin().getIdUser())
            .build();
    }
}
```

**Pourquoi utiliser des DTOs ?**
- **Séparation des couches** : Les entités métier ne sont pas exposées à la couche présentation
- **Sécurité** : On contrôle exactement quelles données sont exposées
- **Flexibilité** : On peut modifier les entités sans affecter l'API
- **Performance** : On peut limiter les données transférées (pas besoin de charger toutes les relations)

**Types de DTOs utilisés** :
- `Create*DTO` : Pour la création (contient les IDs des entités liées)
- `Update*DTO` : Pour la mise à jour (champs optionnels, seulement ceux à modifier)
- `*DTO` : Pour la lecture (contient les données à afficher)

**Injection de Dépendances** : Les repositories sont passés au constructeur. Avantages :
- Facilite les tests (on peut injecter des mocks)
- Découple le service des implémentations concrètes
- Respecte le principe d'inversion de dépendances

### 3.3 Services du Module

#### A. DossierMedicalService

**Rôle** : Gère le cycle de vie du dossier médical (point d'entrée principal)

**Méthode clé : `createDossierMedical`**

```java
public DossierMedicalDTO createDossierMedical(CreateDossierMedicalDTO dto) {
    // ÉTAPE 1 : Conversion DTO → Entity
    DossierMedicale dossierMedical = convertToEntity(dto);
    // Cette méthode charge le Patient et le Medecin depuis leurs IDs
    // et lance une exception si l'un d'eux n'existe pas
    
    // ÉTAPE 2 : VALIDATION
    validateDossierMedical(dossierMedical);
    
    // ÉTAPE 3 : RÈGLE MÉTIER : Un patient = Un seul dossier
    if (existsByPatientId(dto.getPatientId())) {
        throw new IllegalArgumentException("Un dossier médical existe déjà pour ce patient");
    }
    
    // ÉTAPE 4 : VALEURS PAR DÉFAUT
    if (dossierMedical.getDateDeCreation() == null) {
        dossierMedical.setDateDeCreation(LocalDate.now());
    }
    
    // ÉTAPE 5 : AUDIT
    dossierMedical.setCreePar("system"); // À remplacer par l'utilisateur connecté
    dossierMedical.setModifiePar("system");
    
    // ÉTAPE 6 : PERSISTANCE (avec Entity)
    dossierMedicalRepository.create(dossierMedical);
    
    // ÉTAPE 7 : Conversion Entity → DTO pour le retour
    return convertToDTO(dossierMedical);
}
```

**Flux de données** :
```
CreateDossierMedicalDTO (IDs seulement)
    ↓ convertToEntity()
DossierMedicale (Entity complète avec objets liés)
    ↓ repository.create()
Base de données
    ↓ convertToDTO()
DossierMedicalDTO (données à afficher)
```

**Règles métier importantes** :
1. **Unicité** : Un patient ne peut avoir qu'un seul dossier médical
2. **Intégrité référentielle** : Le patient et le médecin doivent exister
3. **Audit** : Enregistrement de qui a créé/modifié et quand

**Méthodes de recherche** :
- `findByPatientId` : Retourne `List<DossierMedicalDTO>` pour un patient donné
- `findByMedecinId` : Retourne `List<DossierMedicalDTO>` pour un médecin donné
- `findByDateCreationBetween` : Retourne `List<DossierMedicalDTO>` pour une période
- Les services délèguent au repository pour les requêtes SQL, puis convertissent les résultats en DTOs

#### B. ConsultationService

**Rôle** : Gère les visites médicales (rendez-vous)

**Méthode clé : `createConsultation`**

```java
public ConsultationDTO createConsultation(CreateConsultationDTO dto) {
    // ÉTAPE 1 : Conversion DTO → Entity
    Consultation consultation = convertToEntity(dto);
    // Charge le DossierMedicale depuis dto.getDossierMedicalId()
    // Lance une exception si le dossier n'existe pas
    
    // ÉTAPE 2 : VALIDATION
    validateConsultation(consultation);
    
    // ÉTAPE 3 : VALEURS PAR DÉFAUT
    if (consultation.getDate() == null) {
        consultation.setDate(LocalDate.now());
    }
    if (consultation.getStatut() == null) {
        consultation.setStatut(StatutConsultation.EN_ATTENTE);
    }
    
    // ÉTAPE 4 : AUDIT
    consultation.setCreePar("system");
    consultation.setModifiePar("system");
    
    // ÉTAPE 5 : PERSISTANCE
    consultationRepository.create(consultation);
    
    // ÉTAPE 6 : Conversion Entity → DTO
    return convertToDTO(consultation);
}
```

**Méthode de conversion** :
```java
private Consultation convertToEntity(CreateConsultationDTO dto) {
    // Charge le dossier médical depuis l'ID fourni dans le DTO
    DossierMedicale dossier = dossierMedicalRepository.findById(dto.getDossierMedicalId());
    if (dossier == null) {
        throw new IllegalArgumentException("Le dossier médical n'existe pas");
    }
    
    // Construit l'entité avec Builder pattern
    return Consultation.builder()
        .dossierMedicale(dossier)
        .Date(dto.getDate() != null ? dto.getDate() : LocalDate.now())
        .statut(dto.getStatut() != null ? dto.getStatut() : StatutConsultation.EN_ATTENTE)
        .observationMedecin(dto.getObservationMedecin())
        .build();
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
public InterventionDTO createIntervention(CreateInterventionDTO dto) {
    // ÉTAPE 1 : Conversion DTO → Entity
    InterventionMedecin intervention = convertToEntity(dto);
    // Cette méthode charge la Consultation et l'Acte depuis leurs IDs
    // et lance une exception si l'un d'eux n'existe pas
    
    // ÉTAPE 2 : VALIDATION
    validateIntervention(intervention);
    
    // ÉTAPE 3 : VALIDATION MÉTIER : Prix et numéro de dent
    if (intervention.getPrixDePatient() == null || 
        intervention.getPrixDePatient() < 0) {
        throw new IllegalArgumentException("Le prix de l'intervention doit être positif ou nul");
    }
    if (intervention.getNumDent() != null && 
        (intervention.getNumDent() < 1 || intervention.getNumDent() > 32)) {
        throw new IllegalArgumentException("Le numéro de dent doit être entre 1 et 32");
    }
    
    // ÉTAPE 4 : AUDIT
    intervention.setCreePar("system");
    intervention.setModifiePar("system");
    
    // ÉTAPE 5 : PERSISTANCE
    interventionRepository.create(intervention);
    
    // ÉTAPE 6 : Conversion Entity → DTO
    return convertToDTO(intervention);
}
```

**Méthode clé : `calculateTotalByConsultation`**

```java
public Double calculateTotalByConsultation(Long consultationId) {
    // Délègue au repository qui fait le calcul en SQL (SUM)
    return interventionRepository.calculateTotalByConsultation(consultationId);
}
```

**Pourquoi déléguer au repository ?**
- Le calcul se fait en SQL avec `SUM()` (performance optimale)
- Le service reste simple et lisible
- Séparation des responsabilités : Repository = accès aux données, Service = logique métier

#### D. PrescriptionService

**Rôle** : Gère les prescriptions de médicaments

**Méthode clé : `createPrescription`**

```java
public PrescriptionDTO createPrescription(CreatePrescriptionDTO dto) {
    // ÉTAPE 1 : Conversion DTO → Entity
    Prescription prescription = convertToEntity(dto);
    // Cette méthode charge l'Ordonnance et le Medicament depuis leurs IDs
    // et lance une exception si l'un d'eux n'existe pas
    
    // ÉTAPE 2 : VALIDATION
    validatePrescription(prescription);
    
    // ÉTAPE 3 : VALIDATION MÉTIER : Quantité, durée et fréquence
    if (prescription.getQuantité() <= 0) {
        throw new IllegalArgumentException("La quantité doit être positive");
    }
    if (prescription.getDuréeEnJours() <= 0) {
        throw new IllegalArgumentException("La durée en jours doit être positive");
    }
    if (prescription.getFréquence() == null || 
        prescription.getFréquence().trim().isEmpty()) {
        throw new IllegalArgumentException("La fréquence est requise");
    }
    
    // ÉTAPE 4 : AUDIT
    prescription.setCreePar("system");
    prescription.setModifiePar("system");
    
    // ÉTAPE 5 : PERSISTANCE
    prescriptionRepository.create(prescription);
    
    // ÉTAPE 6 : Conversion Entity → DTO
    return convertToDTO(prescription);
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
- Le repository fait un JOIN entre `prescription` et `medicament` pour obtenir le prix
- Calcul SQL : `SUM(quantité × prix_unitaire)` pour toutes les prescriptions
- Le service retourne simplement le résultat calculé par le repository

### 3.4 Patterns Importants dans les Services

#### Pattern : Conversion DTO ↔ Entity
```java
// Conversion DTO → Entity (pour la persistance)
private Consultation convertToEntity(CreateConsultationDTO dto) {
    // Charge les entités liées depuis les IDs du DTO
    DossierMedicale dossier = dossierMedicalRepository.findById(dto.getDossierMedicalId());
    if (dossier == null) {
        throw new IllegalArgumentException("Le dossier médical n'existe pas");
    }
    
    // Construit l'entité avec Builder pattern
    return Consultation.builder()
        .dossierMedicale(dossier)
        .Date(dto.getDate())
        .statut(dto.getStatut())
        .observationMedecin(dto.getObservationMedecin())
        .build();
}

// Conversion Entity → DTO (pour le retour)
private ConsultationDTO convertToDTO(Consultation entity) {
    return ConsultationDTO.builder()
        .idConsultation(entity.getIdConsultation())
        .date(entity.getDate())
        .statut(entity.getStatut())
        .observationMedecin(entity.getObservationMedecin())
        .dossierMedicalId(entity.getDossierMedicale().getIdDM())
        .build();
}
```

**Avantages** :
- **Séparation** : Les entités métier restent internes au service
- **Sécurité** : On contrôle quelles données sont exposées
- **Flexibilité** : On peut modifier les entités sans casser l'API

#### Pattern : Validation en Cascade
```java
// 1. Conversion DTO → Entity (inclut validation des dépendances)
Consultation consultation = convertToEntity(dto);
// Si le dossier n'existe pas, convertToEntity lance une exception

// 2. Validation de l'entité
validateConsultation(consultation);

// 3. Validation des règles métier
if (consultation.getDate() != null && consultation.getDate().isAfter(LocalDate.now())) {
    throw new IllegalArgumentException("La date ne peut pas être dans le futur");
}
```

#### Pattern : Valeurs par Défaut
```java
// Dans convertToEntity ou après conversion
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

#### Pattern : Délégation au Repository avec Conversion
```java
// Service : Validation + Délégation au Repository + Conversion Entity → DTO
public List<ConsultationDTO> findByStatut(StatutConsultation statut) {
    if (statut == null) {
        throw new IllegalArgumentException("Le statut ne peut pas être null");
    }
    // ÉTAPE 1 : Délègue au repository (retourne des Entities)
    // Le repository exécute la requête SQL optimisée
    List<Consultation> consultations = consultationRepository.findByStatut(statut);
    
    // ÉTAPE 2 : Convertit toutes les Entities en DTOs
    return consultations.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
}
```

**Note sur les Repositories** :
- Les repositories sont des interfaces (`*Repository`) avec implémentations (`*RepositoryImpl`)
- Ils gèrent l'accès aux données : requêtes SQL, mapping ResultSet → Entity
- Les services les utilisent pour persister les entités et récupérer les données
- Les services convertissent ensuite les Entities en DTOs pour les retourner

---

## 4. Couche Test (Tests d'Intégration)

### 4.1 Structure des Tests avec DTOs

Les tests sont situés dans `src/main/java/ma/whitecare/service/test/` et utilisent les services avec DTOs.

Chaque test suit cette structure :

```java
public class TestDossierMedicalService {
    public static void main(String[] args) {
        // 1. INITIALISATION DES REPOSITORIES ET SERVICES
        DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
        PatientRepository patientRepo = new PatientRepositoryImpl();
        MedecinRepository medecinRepo = new MedecinRepositoryImpl();
        ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
        
        // Injection des dépendances dans le service
        DossierMedicalServiceImpl service = new DossierMedicalServiceImpl(
            dossierRepo, patientRepo, medecinRepo, consultationRepo);
        
        // 2. PRÉPARATION DES DONNÉES DE TEST
        // Création d'un patient (utilise directement le repository pour la préparation)
        Patient testPatient = new Patient();
        testPatient.setNom("Martin");
        testPatient.setPrenom("Sophie");
        testPatient.setEmail("sophie.martin.test@email.com");
        // ... autres champs
        patientRepo.create(testPatient);
        
        // Récupération d'un médecin existant
        List<Medecin> medecins = medecinRepo.findAll();
        Medecin testMedecin = medecins.get(0);
        
        // 3. TESTS AVEC DTOS
        // Test 1: Création avec CreateDTO
        CreateDossierMedicalDTO createDTO = CreateDossierMedicalDTO.builder()
            .patientId(testPatient.getId_Patient())
            .medecinId(testMedecin.getIdUser())
            .dateDeCreation(LocalDate.now())
            .build();
        
        DossierMedicalDTO created = service.createDossierMedical(createDTO);
        System.out.println("✓ Dossier créé avec ID: " + created.getIdDM());
        
        // Pause de 7 secondes pour montrer la création dans la base de données
        Thread.sleep(7000);
        
        // Test 2: Lecture (retourne un DTO)
        DossierMedicalDTO found = service.getDossierMedicalById(created.getIdDM());
        
        // Test 3: Recherche (retourne une liste de DTOs)
        List<DossierMedicalDTO> byPatient = service.findByPatientId(testPatient.getId_Patient());
        
        // Test 4: Mise à jour avec UpdateDTO
        UpdateDossierMedicalDTO updateDTO = UpdateDossierMedicalDTO.builder()
            .dateDeCreation(LocalDate.now().minusDays(1))
            .build();
        
        Thread.sleep(7000); // Pause avant mise à jour
        
        DossierMedicalDTO updated = service.updateDossierMedical(created.getIdDM(), updateDTO);
        
        // Test 5: Suppression
        Thread.sleep(7000); // Pause avant suppression
        service.deleteDossierMedical(created.getIdDM());
        
        // 4. VÉRIFICATION
        // Vérifie que l'objet n'existe plus après suppression
        try {
            service.getDossierMedicalById(created.getIdDM());
            System.out.println("✗ ERREUR: Devrait être supprimé");
        } catch (Exception e) {
            System.out.println("✓ Suppression confirmée");
        }
    }
}
```

### 4.2 Utilisation des DTOs dans les Tests

**Création avec CreateDTO** :
```java
CreateConsultationDTO createDTO = CreateConsultationDTO.builder()
    .dossierMedicalId(dossier.getIdDM())  // Seulement l'ID, pas l'objet complet
    .date(LocalDate.now())
    .statut(StatutConsultation.EN_ATTENTE)
    .observationMedecin("Consultation de test")
    .build();

ConsultationDTO created = consultationService.createConsultation(createDTO);
// Le service charge automatiquement le DossierMedicale depuis l'ID
```

**Mise à jour avec UpdateDTO** :
```java
UpdateConsultationDTO updateDTO = UpdateConsultationDTO.builder()
    .observationMedecin("Observation mise à jour")  // Seulement les champs à modifier
    .build();  // Les autres champs restent null = pas de modification

ConsultationDTO updated = consultationService.updateConsultation(id, updateDTO);
// Pattern : Mise à jour sélective (seulement les champs non-null)
```

**Lecture avec DTO** :
```java
ConsultationDTO consultation = consultationService.getConsultationById(id);
// Retourne un DTO avec les données à afficher
// Ne contient que les IDs des entités liées, pas les objets complets
```

### 4.3 Types de Tests Effectués

#### Tests CRUD Complets avec DTOs
1. **Create** : Création avec `CreateDTO` et vérification du `DTO` retourné avec ID généré
2. **Read** : Récupération par ID et vérification des données dans le `DTO`
3. **Update** : Mise à jour avec `UpdateDTO` et vérification des changements dans le `DTO`
4. **Delete** : Suppression et vérification de l'inexistence

#### Tests de Recherche
- Recherche par critères simples (ID, date, statut) → Retourne `List<DTO>`
- Recherche par critères combinés (dossier + date) → Retourne `List<DTO>`
- Recherche par période (date entre X et Y) → Retourne `List<DTO>`

#### Tests de Calcul
- Calcul du total d'une consultation (somme des interventions) → Retourne `Double`
- Calcul du coût d'une ordonnance (somme des prescriptions) → Retourne `Double`
- Calcul du coût total patient (toutes les interventions) → Retourne `Double`

#### Tests de Statistiques
- Comptage total → Retourne `long`
- Comptage par critères (par statut, par dossier, etc.) → Retourne `long`

#### Tests de Validation
- Vérification des règles métier (ex: un patient = un dossier)
- Vérification des contraintes (ex: prix positif, numéro de dent 1-32)
- Vérification des dépendances (ex: dossier médical doit exister)

#### Tests Métier Avancés
- `getConsultationComplete()` : Récupère consultation + interventions + prescriptions
- `getHistoriqueDentaire()` : Groupe les interventions par numéro de dent
- `getPrescriptionsActives()` : Filtre les prescriptions encore valides
- `getDerniereConsultation()` : Trouve la consultation la plus récente

### 4.4 Points Importants des Tests

**Utilisation des Repositories pour la Préparation** :
```java
// Les repositories sont utilisés pour créer les données de test nécessaires
Patient testPatient = new Patient();
// ... configuration de l'entité
patientRepo.create(testPatient);  // Utilise directement le repository

DossierMedicale dossier = new DossierMedicale();
dossier.setPatient(testPatient);
dossierRepo.create(dossier);  // Préparation des données de test
// C'est acceptable car c'est de la préparation, pas du test réel
```

**Utilisation des Services avec DTOs pour les Tests** :
```java
// Les services sont utilisés pour les tests réels avec DTOs
CreateDossierMedicalDTO createDTO = CreateDossierMedicalDTO.builder()
    .patientId(testPatient.getId_Patient())  // Seulement l'ID
    .medecinId(testMedecin.getIdUser())
    .build();

DossierMedicalDTO created = service.createDossierMedical(createDTO);
// Teste la logique métier complète : validation, conversion, persistance
```

**Pauses pour Démonstration** :
```java
// Pause de 7 secondes après création pour montrer la base de données
Thread.sleep(7000);

// Pause avant mise à jour pour montrer l'état avant modification
Thread.sleep(7000);

// Pause avant suppression pour montrer l'état avant suppression
Thread.sleep(7000);
```
**Pourquoi ?** Permet au professeur de vérifier les changements dans la base de données à chaque étape.

**Gestion des Erreurs** :
```java
try {
    service.getDossierMedicalById(deletedId);
    System.out.println("✗ ERREUR: Devrait être supprimé");
} catch (IllegalArgumentException e) {
    System.out.println("✓ Suppression confirmée (exception attendue)");
}
```

### 4.5 Fichiers de Test

Les tests sont organisés dans `src/main/java/ma/whitecare/service/test/` :

1. **TestConsultationService.java** : Tests complets du service de consultation
   - 14 scénarios de test (CRUD, recherche, statistiques, méthodes métier)
   - Utilise `CreateConsultationDTO`, `UpdateConsultationDTO`, `ConsultationDTO`
   - Teste `getConsultationComplete()` qui charge interventions et prescriptions

2. **TestDossierMedicalService.java** : Tests complets du service de dossier médical
   - 13 scénarios de test
   - Utilise `CreateDossierMedicalDTO`, `UpdateDossierMedicalDTO`, `DossierMedicalDTO`
   - Teste `getDerniereConsultation()` et `getHistoriqueCompletPatient()`

3. **TestInterventionService.java** : Tests complets du service d'intervention
   - 14 scénarios de test
   - Utilise `CreateInterventionDTO`, `UpdateInterventionDTO`, `InterventionDTO`
   - Teste `getHistoriqueDentaire()` et `getCoutTotalPatient()`

4. **TestPrescriptionService.java** : Tests complets du service de prescription
   - 14 scénarios de test
   - Utilise `CreatePrescriptionDTO`, `UpdatePrescriptionDTO`, `PrescriptionDTO`
   - Teste `getPrescriptionsActives()` et `getHistoriquePrescriptions()`

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
- **DTOs** : Objets de transfert de données pour séparer les couches
  - `Create*DTO` : Pour la création (contient les IDs des entités liées)
  - `Update*DTO` : Pour la mise à jour (champs optionnels)
  - `*DTO` : Pour la lecture (données à afficher)
- **Conversion DTO ↔ Entity** : Dans les services
  - `convertToEntity()` : DTO → Entity (pour la persistance)
  - `convertToDTO()` : Entity → DTO (pour le retour)
- **RowMappers** : Conversion automatique ResultSet → Entity
- Évite la duplication de code
- Facilite la maintenance
- Sécurise l'exposition des données

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
   // Dans convertToEntity(), on charge le Patient depuis l'ID du DTO
   Patient patient = patientRepository.findById(dto.getPatientId());
   if (patient == null) {
       throw new IllegalArgumentException("Le patient n'existe pas");
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

### Exemple : `updateDossierMedical` avec DTOs

Si le professeur demande d'expliquer cette méthode :

```java
public DossierMedicalDTO updateDossierMedical(Long dossierId, 
                                             UpdateDossierMedicalDTO dto) {
    // ÉTAPE 1 : Récupération de l'entité existante
    DossierMedicale existingDossier = dossierMedicalRepository.findById(dossierId);
    if (existingDossier == null) {
        throw new IllegalArgumentException("Dossier médical non trouvé avec l'ID: " + dossierId);
    }
    
    // ÉTAPE 2 : Mise à jour sélective depuis le DTO
    updateEntityFromDTO(existingDossier, dto);
    // Cette méthode met à jour seulement les champs non-null du DTO
    // Si dto.getDateDeCreation() != null → met à jour
    // Si dto.getPatientId() != null → charge le Patient et met à jour
    
    // ÉTAPE 3 : Validation de l'entité mise à jour
    validateDossierMedical(existingDossier);
    
    // ÉTAPE 4 : Audit
    existingDossier.setModifiePar("system");
    // Enregistre qui a modifié (à remplacer par l'utilisateur connecté)
    
    // ÉTAPE 5 : Persistance (avec Entity)
    dossierMedicalRepository.update(existingDossier);
    
    // ÉTAPE 6 : Conversion Entity → DTO pour le retour
    return convertToDTO(existingDossier);
}
```

**Méthode `updateEntityFromDTO`** :
```java
private void updateEntityFromDTO(DossierMedicale entity, UpdateDossierMedicalDTO dto) {
    // Mise à jour sélective : seulement les champs non-null
    if (dto.getDateDeCreation() != null) {
        entity.setDateDeCreation(dto.getDateDeCreation());
    }
    
    if (dto.getPatientId() != null) {
        Patient patient = patientRepository.findById(dto.getPatientId());
        if (patient == null) {
            throw new IllegalArgumentException("Le patient n'existe pas");
        }
        entity.setPatient(patient);
    }
    
    if (dto.getMedecinId() != null) {
        Medecin medecin = medecinRepository.findById(dto.getMedecinId());
        if (medecin == null) {
            throw new IllegalArgumentException("Le médecin n'existe pas");
        }
        entity.setMedecin(medecin);
    }
}
```

**Points à expliquer** :
1. **Récupération d'abord** : On récupère l'entité existante pour préserver les données non modifiées
2. **Mise à jour sélective** : On ne modifie que les champs non-null du DTO (pattern null = pas de changement)
3. **Chargement des dépendances** : Si un ID est fourni, on charge l'entité correspondante
4. **Validation** : On vérifie les nouvelles données avant de les appliquer
5. **Audit** : On enregistre qui a modifié
6. **Persistance** : On sauvegarde via le repository (avec Entity)
7. **Conversion** : On retourne un DTO pour la couche présentation

---

## 8. Résumé pour Présentation

### Points Clés à Mentionner

1. **Architecture en 3 couches** : Test → Service → Repository → Base de données
2. **Pattern DTO** : Séparation entre couche présentation (DTOs) et couche métier (Entities)
3. **Séparation des responsabilités** : Chaque couche a un rôle précis
4. **Pattern Interface/Implémentation** : Découplage et testabilité
5. **Injection de dépendances** : Via constructeur
6. **Conversion DTO ↔ Entity** : Dans les services pour isoler les entités métier
7. **Optimisation** : Requêtes SQL optimisées, calculs en SQL
8. **Sécurité** : PreparedStatement, validation des entrées
9. **Règles métier** : Validations et contrôles dans les services
10. **Tests complets** : CRUD avec DTOs, recherche, calculs, statistiques, méthodes métier avancées
11. **Pauses dans les tests** : Thread.sleep(7000) pour démonstration des changements en base

### Démonstration Suggérée

1. **Montrer la structure des dossiers** :
   - `api/` : Interfaces des services (contrats)
   - `impl/` : Implémentations avec logique métier
   - `dto/` : Objets de transfert de données

2. **Expliquer le flux DTO → Entity → Base → DTO** :
   - Montrer `CreateConsultationDTO` (contient seulement l'ID du dossier)
   - Expliquer `convertToEntity()` qui charge le DossierMedicale
   - Montrer la persistance avec l'Entity
   - Expliquer `convertToDTO()` qui retourne les données à afficher

3. **Expliquer une méthode de service** (ex: `createConsultation`) :
   - Conversion DTO → Entity
   - Validation et règles métier
   - Persistance via repository
   - Conversion Entity → DTO

5. **Montrer un test en action** :
   - Création avec CreateDTO
   - Pause de 7 secondes pour vérifier en base
   - Mise à jour avec UpdateDTO
   - Pause avant suppression
   - Vérification de la suppression

5. **Expliquer les règles métier importantes** :
   - Unicité (un patient = un dossier)
   - Transitions de statut contrôlées
   - Validation des dépendances

---

## 9. Architecture DTO : Avantages et Flux

### 9.1 Pourquoi Utiliser des DTOs ?

**Sans DTOs (ancienne approche)** :
```java
// Le service expose directement les entités métier
public Consultation createConsultation(Consultation consultation) {
    // Problème : L'entité Consultation contient toutes les relations
    // (DossierMedicale, Interventions, Prescriptions, etc.)
    // → Risque de charger trop de données
    // → Couplage fort entre couches
}
```

**Avec DTOs (nouvelle approche)** :
```java
// Le service utilise des DTOs légers
public ConsultationDTO createConsultation(CreateConsultationDTO dto) {
    // Avantage : Le DTO contient seulement l'ID du dossier
    // → Pas de chargement inutile de données
    // → Découplage entre couches
    // → Contrôle total sur les données exposées
}
```

### 9.2 Flux Complet : Création d'une Consultation

```
1. TEST → Crée CreateConsultationDTO
   CreateConsultationDTO {
     dossierMedicalId: 123  // Seulement l'ID
     date: 2024-01-15
     statut: EN_ATTENTE
   }
   
2. SERVICE → convertToEntity()
   - Charge DossierMedicale depuis l'ID 123
   - Construit Consultation avec Builder
   Consultation {
     idConsultation: null
     date: 2024-01-15
     statut: EN_ATTENTE
     dossierMedicale: <objet complet chargé>
   }
   
3. SERVICE → Validation
   - Vérifie que le dossier existe
   - Vérifie les règles métier
   - Définit valeurs par défaut si nécessaire
   
4. SERVICE → repository.create(consultation)
   - Persiste l'entité en base
   - L'ID est généré automatiquement
   
5. SERVICE → convertToDTO()
   ConsultationDTO {
     idConsultation: 456  // ID généré
     date: 2024-01-15
     statut: EN_ATTENTE
     dossierMedicalId: 123  // Seulement l'ID, pas l'objet complet
   }
   
6. TEST → Reçoit ConsultationDTO
   - Affiche les résultats
   - Utilise le DTO pour les vérifications
```

### 9.3 Composants Clés des Services

**1. Conversion DTO → Entity** :
- Charge les entités liées depuis les IDs
- Valide l'existence des dépendances
- Construit l'entité avec Builder pattern

**2. Validation** :
- Vérifie les règles métier
- Vérifie les contraintes (prix positif, dates valides, etc.)
- Lance des exceptions explicites

**3. Persistance** :
- Utilise le repository pour sauvegarder
- Traite les entités métier (pas les DTOs)

**4. Conversion Entity → DTO** :
- Extrait les données à afficher
- Ne retourne que les IDs des entités liées
- Contrôle ce qui est exposé

### 9.4 Structure des Tests

**Organisation** :
- **Location** : `src/main/java/ma/whitecare/service/test/`
- **4 fichiers de test** : Un pour chaque service
- **Tests d'intégration** : Testent le flux complet avec vraie base de données

**Structure typique** :
1. Initialisation des repositories et services
2. Préparation des données (création patient, dossier, etc.)
3. Tests CRUD avec DTOs
4. Tests de recherche
5. Tests de calculs et statistiques
6. Tests de méthodes métier avancées
7. Pauses de 7 secondes pour démonstration

**Exemple de test** :
```java
// Création avec CreateDTO
CreateConsultationDTO createDTO = CreateConsultationDTO.builder()
    .dossierMedicalId(dossier.getIdDM())
    .date(LocalDate.now())
    .statut(StatutConsultation.EN_ATTENTE)
    .build();

ConsultationDTO created = service.createConsultation(createDTO);
// Pause pour montrer la création
Thread.sleep(7000);

// Mise à jour avec UpdateDTO
UpdateConsultationDTO updateDTO = UpdateConsultationDTO.builder()
    .observationMedecin("Observation mise à jour")
    .build();

Thread.sleep(7000); // Pause avant mise à jour
ConsultationDTO updated = service.updateConsultation(created.getIdConsultation(), updateDTO);
```

---

## 10. Conclusion

Le module Dossier Médical suit les meilleures pratiques :
- ✅ Architecture claire et modulaire avec séparation en 3 couches
- ✅ Pattern DTO pour séparer présentation et métier
- ✅ Séparation des responsabilités (Repository, Service, Test)
- ✅ Code maintenable et testable avec injection de dépendances
- ✅ Performance optimisée (requêtes SQL, calculs en SQL)
- ✅ Sécurité renforcée (PreparedStatement, validation)
- ✅ Règles métier respectées et validées
- ✅ Tests complets avec DTOs et pauses pour démonstration

**Architecture finale** :
```
Tests (DTOs) 
    ↓ utilise
Services (DTO ↔ Entity) 
    ↓ utilise
Repositories (Entities, requêtes SQL) 
    ↓ utilise
Base de données
```

**Flux de données** :
- **Entrée** : Tests créent des `CreateDTO` avec seulement les IDs
- **Service** : Convertit DTO → Entity, valide, persiste via repository
- **Repository** : Exécute requêtes SQL, retourne Entities
- **Service** : Convertit Entity → DTO pour le retour
- **Sortie** : Tests reçoivent des `DTO` avec les données à afficher

Ce design permet :
- Une évolution facile (modifier les entités sans affecter l'API)
- Une maintenance simplifiée (séparation claire des responsabilités)
- Une meilleure sécurité (contrôle des données exposées)
- Des tests complets et démonstratifs
