# Guide de Lecture du Code - Module Dossier Médical

## 📚 Ordre Logique de Lecture pour Comprendre le Code

Ce guide vous explique dans quel ordre lire les fichiers pour comprendre le fonctionnement du module Dossier Médical.

---

## 🎯 Ordre Recommandé de Lecture

### **ÉTAPE 1 : Comprendre les Entités (Entities)**
**Pourquoi commencer ici ?**
- Les entités représentent les objets métier réels
- Elles définissent la structure des données
- Elles sont utilisées partout dans le code

**Fichiers à lire :**
```
1. src/main/java/ma/whitecare/entities/medical/DossierMedicale.java
   → Structure d'un dossier médical (patient, médecin, date)

2. src/main/java/ma/whitecare/entities/medical/Consultation.java
   → Structure d'une consultation (date, statut, observations)

3. src/main/java/ma/whitecare/entities/medical/InterventionMedecin.java
   → Structure d'une intervention (acte, prix, numéro de dent)

4. src/main/java/ma/whitecare/entities/medical/Prescription.java
   → Structure d'une prescription (médicament, quantité, fréquence)

5. src/main/java/ma/whitecare/entities/medical/Ordonnance.java
   → Structure d'une ordonnance (consultation, dossier médical)
```

**Ce que vous apprendrez :**
- Les champs de chaque entité
- Les relations entre entités (DossierMedicale → Consultation → Intervention/Prescription)
- Les types de données utilisés

---

### **ÉTAPE 2 : Comprendre les DTOs (Data Transfer Objects)**
**Pourquoi maintenant ?**
- Les DTOs sont la couche de communication entre les services et les tests
- Ils montrent comment les données sont transmises
- Ils séparent la structure interne (Entity) de l'API externe

**Fichiers à lire (dans cet ordre pour chaque entité) :**
```
Pour Dossier Médical :
1. src/main/java/ma/whitecare/mvc/dto/dossierMedical/CreateDossierMedicalDTO.java
   → Ce qui est nécessaire pour CRÉER un dossier
   → Contient seulement les IDs (patientId, medecinId)

2. src/main/java/ma/whitecare/mvc/dto/dossierMedical/UpdateDossierMedicalDTO.java
   → Ce qui peut être MODIFIÉ dans un dossier
   → Tous les champs sont optionnels (null = pas de modification)

3. src/main/java/ma/whitecare/mvc/dto/dossierMedical/DossierMedicalDTO.java
   → Ce qui est RETOURNÉ après création/lecture
   → Contient l'ID généré et les données à afficher

Répéter pour :
- Consultation : CreateConsultationDTO, UpdateConsultationDTO, ConsultationDTO
- Intervention : CreateInterventionDTO, UpdateInterventionDTO, InterventionDTO
- Prescription : CreatePrescriptionDTO, UpdatePrescriptionDTO, PrescriptionDTO
```

**Ce que vous apprendrez :**
- Pattern DTO : CreateDTO (création), UpdateDTO (mise à jour), DTO (lecture)
- Les DTOs contiennent seulement les IDs des entités liées (pas les objets complets)
- Séparation entre couche présentation (DTO) et couche métier (Entity)

---

### **ÉTAPE 3 : Comprendre les Repositories (Accès aux Données)**
**Pourquoi maintenant ?**
- Les repositories gèrent la persistance (SQL)
- Les services les utilisent pour sauvegarder/récupérer les données
- Comprendre les repositories aide à comprendre comment les services fonctionnent

**Fichiers à lire (pour chaque entité) :**

#### A. Interface Repository (API)
```
1. src/main/java/ma/whitecare/repository/modules/dossierMedical/api/DossierMedicalRepository.java
   → Contrat : quelles méthodes sont disponibles
   → Hérite de CrudRepository (findAll, findById, create, update, delete)

2. src/main/java/ma/whitecare/repository/modules/dossierMedical/api/ConsultationRepository.java
3. src/main/java/ma/whitecare/repository/modules/dossierMedical/api/InterventionRepository.java
4. src/main/java/ma/whitecare/repository/modules/dossierMedical/api/PrescriptionRepository.java
```

**Ce que vous apprendrez :**
- Les méthodes disponibles pour chaque entité
- Les méthodes personnalisées (findByPatientId, findByStatut, etc.)

#### B. Implémentation Repository (SQL)
```
1. src/main/java/ma/whitecare/repository/modules/dossierMedical/impl/DossierMedicalRepositoryImpl.java
   → Comment les requêtes SQL sont exécutées
   → Comment les ResultSet sont convertis en Entities

2. src/main/java/ma/whitecare/repository/modules/dossierMedical/impl/ConsultationRepositoryImpl.java
3. src/main/java/ma/whitecare/repository/modules/dossierMedical/impl/InterventionRepositoryImpl.java
4. src/main/java/ma/whitecare/repository/modules/dossierMedical/impl/PrescriptionRepositoryImpl.java
```

**Points importants à observer :**
- Utilisation de `PreparedStatement` pour éviter les injections SQL
- `Statement.RETURN_GENERATED_KEYS` pour récupérer les IDs générés
- `RowMappers.mapXXX(rs)` pour convertir ResultSet → Entity
- `try-with-resources` pour la gestion automatique des connexions

**Fichier utilitaire important :**
```
src/main/java/ma/whitecare/repository/common/RowMappers.java
→ Contient toutes les méthodes de mapping ResultSet → Entity
→ Regardez mapDossierMedicale(), mapConsultation(), mapInterventionMedecin(), mapPrescription()
```

---

### **ÉTAPE 4 : Comprendre les Services (Logique Métier)**
**Pourquoi maintenant ?**
- Les services orchestrent tout : DTOs → Entities → Repositories
- Ils contiennent la logique métier et les validations
- C'est la couche la plus importante à comprendre

**Fichiers à lire (dans cet ordre) :**

#### A. Interface Service (API)
```
1. src/main/java/ma/whitecare/service/modules/dossierMedical/api/DossierMedicalService.java
   → Contrat : quelles opérations sont disponibles avec DTOs
   → Exemple : createDossierMedical(CreateDossierMedicalDTO) → DossierMedicalDTO

2. src/main/java/ma/whitecare/service/modules/dossierMedical/api/ConsultationService.java
3. src/main/java/ma/whitecare/service/modules/dossierMedical/api/InterventionService.java
4. src/main/java/ma/whitecare/service/modules/dossierMedical/api/PrescriptionService.java
```

**Ce que vous apprendrez :**
- Les méthodes publiques disponibles
- Les types de DTOs utilisés en entrée et sortie

#### B. Implémentation Service (Logique)
```
1. src/main/java/ma/whitecare/service/modules/dossierMedical/impl/DossierMedicalServiceImpl.java
   → Logique complète : validation, conversion DTO ↔ Entity, règles métier

2. src/main/java/ma/whitecare/service/modules/dossierMedical/impl/ConsultationServiceImpl.java
3. src/main/java/ma/whitecare/service/modules/dossierMedical/impl/InterventionServiceImpl.java
4. src/main/java/ma/whitecare/service/modules/dossierMedical/impl/PrescriptionServiceImpl.java
```

**Points importants à observer dans chaque service :**

**1. Injection de Dépendances (Constructeur)**
```java
private final DossierMedicalRepository dossierMedicalRepository;
private final PatientRepository patientRepository;
// ...

public DossierMedicalServiceImpl(...) {
    this.dossierMedicalRepository = dossierMedicalRepository;
    // ...
}
```

**2. Méthode de Création (exemple)**
```java
public DossierMedicalDTO createDossierMedical(CreateDossierMedicalDTO dto) {
    // ÉTAPE 1 : Conversion DTO → Entity
    DossierMedicale dossier = convertToEntity(dto);
    
    // ÉTAPE 2 : Validation
    validateDossierMedical(dossier);
    
    // ÉTAPE 3 : Règles métier
    if (existsByPatientId(dto.getPatientId())) {
        throw new IllegalArgumentException("Un dossier existe déjà");
    }
    
    // ÉTAPE 4 : Valeurs par défaut
    if (dossier.getDateDeCreation() == null) {
        dossier.setDateDeCreation(LocalDate.now());
    }
    
    // ÉTAPE 5 : Persistance (via Repository)
    dossierMedicalRepository.create(dossier);
    
    // ÉTAPE 6 : Conversion Entity → DTO pour retour
    return convertToDTO(dossier);
}
```

**3. Méthodes de Conversion**
```java
// DTO → Entity (pour la persistance)
private DossierMedicale convertToEntity(CreateDossierMedicalDTO dto) {
    // Charge les entités liées depuis les IDs
    Patient patient = patientRepository.findById(dto.getPatientId());
    Medecin medecin = medecinRepository.findById(dto.getMedecinId());
    
    // Construit l'entité
    return DossierMedicale.builder()
        .patient(patient)
        .medecin(medecin)
        .dateDeCreation(dto.getDateDeCreation())
        .build();
}

// Entity → DTO (pour le retour)
private DossierMedicalDTO convertToDTO(DossierMedicale entity) {
    return DossierMedicalDTO.builder()
        .idDM(entity.getIdDM())
        .patientId(entity.getPatient().getId_Patient())
        .medecinId(entity.getMedecin().getIdUser())
        .dateDeCreation(entity.getDateDeCreation())
        .build();
}
```

**4. Méthodes de Validation**
```java
private void validateDossierMedical(DossierMedicale dossier) {
    if (dossier.getPatient() == null) {
        throw new IllegalArgumentException("Le patient est requis");
    }
    // ... autres validations
}
```

---

### **ÉTAPE 5 : Comprendre les Tests (Exemples d'Utilisation)**
**Pourquoi en dernier ?**
- Les tests montrent comment tout fonctionne ensemble
- Ils donnent des exemples concrets d'utilisation
- Ils montrent le flux complet de bout en bout

**Fichiers à lire (dans l'ordre logique des dépendances) :**
```
1. src/main/java/ma/whitecare/service/test/TestDossierMedicalService.java
   → Teste la création d'un Dossier Médical avec DTOs
   → Montre le flux : CreateDTO → Service → Repository → Base de données

2. src/main/java/ma/whitecare/service/test/TestConsultationService.java
   → Teste la création de Consultations
   → Montre comment utiliser le Dossier Médical créé précédemment

3. src/main/java/ma/whitecare/service/test/TestInterventionService.java
   → Teste la création d'Interventions
   → Montre comment utiliser une Consultation existante

4. src/main/java/ma/whitecare/service/test/TestPrescriptionService.java
   → Teste la création de Prescriptions
   → Montre comment utiliser une Ordonnance existante
```

**Points importants à observer dans les tests :**
- Création des données de test (Patient, Medecin, etc.)
- Utilisation des DTOs pour créer les entités
- Vérification des résultats avec les DTOs retournés
- Pauses (`Thread.sleep(7000)`) pour démonstration

---

## 🔄 Flux Complet de Données

### Exemple : Création d'une Consultation

```
1. TEST crée CreateConsultationDTO
   CreateConsultationDTO {
     dossierMedicalId: 123  // Seulement l'ID
     date: 2024-01-15
     statut: EN_ATTENTE
   }
   
2. SERVICE reçoit le DTO
   ConsultationServiceImpl.createConsultation(createDTO)
   
3. SERVICE convertit DTO → Entity
   convertToEntity(dto) {
     - Charge DossierMedicale depuis l'ID 123 (via repository)
     - Construit Consultation avec Builder
   }
   
4. SERVICE valide et applique règles métier
   - Vérifie que le dossier existe
   - Définit valeurs par défaut si nécessaire
   
5. SERVICE persiste via Repository
   consultationRepository.create(consultation)
   → Repository exécute SQL INSERT
   → Récupère l'ID généré
   
6. SERVICE convertit Entity → DTO
   convertToDTO(consultation) {
     - Extrait les données à afficher
     - Retourne ConsultationDTO avec ID généré
   }
   
7. TEST reçoit ConsultationDTO
   ConsultationDTO {
     idConsultation: 456  // ID généré
     dossierMedicalId: 123
     date: 2024-01-15
     statut: EN_ATTENTE
   }
```

---

## 📋 Checklist de Lecture

### Niveau 1 : Fondations
- [ ] Lire les entités (DossierMedicale, Consultation, Intervention, Prescription)
- [ ] Comprendre les relations entre entités
- [ ] Lire les DTOs (Create, Update, DTO pour chaque entité)

### Niveau 2 : Accès aux Données
- [ ] Lire les interfaces Repository (API)
- [ ] Lire les implémentations Repository (SQL)
- [ ] Comprendre RowMappers (mapping ResultSet → Entity)
- [ ] Observer les patterns : PreparedStatement, try-with-resources, RETURN_GENERATED_KEYS

### Niveau 3 : Logique Métier
- [ ] Lire les interfaces Service (API)
- [ ] Lire les implémentations Service (logique)
- [ ] Comprendre les méthodes de conversion (DTO ↔ Entity)
- [ ] Observer les validations et règles métier
- [ ] Comprendre l'injection de dépendances

### Niveau 4 : Utilisation
- [ ] Lire les tests dans l'ordre logique
- [ ] Observer le flux complet DTO → Service → Repository → Base
- [ ] Comprendre comment les DTOs sont utilisés dans les tests

---

## 🎓 Conseils pour la Lecture

### 1. Commencez Simple
- Lisez d'abord une seule entité complète (ex: Consultation)
- Suivez le flux : Entity → DTOs → Repository → Service → Test
- Une fois compris, répétez pour les autres entités

### 2. Utilisez les Commentaires
- Les méthodes importantes ont souvent des commentaires
- Les noms de méthodes sont explicites (convertToEntity, validateXXX)

### 3. Suivez les Dépendances
- Dossier Médical → Consultation → Intervention/Prescription
- Lisez dans cet ordre pour comprendre les dépendances

### 4. Observez les Patterns
- Pattern DTO : CreateDTO, UpdateDTO, DTO
- Pattern Repository : Interface + Implémentation
- Pattern Service : Conversion, Validation, Délégation

### 5. Testez en Ligne
- Après avoir lu un service, exécutez son test
- Observez le comportement réel
- Comparez avec ce que vous avez compris

---

## 📁 Structure des Dossiers

```
src/main/java/ma/whitecare/
├── entities/medical/              ← ÉTAPE 1 : Entités
│   ├── DossierMedicale.java
│   ├── Consultation.java
│   ├── InterventionMedecin.java
│   └── Prescription.java
│
├── mvc/dto/dossierMedical/         ← ÉTAPE 2 : DTOs
│   ├── CreateDossierMedicalDTO.java
│   ├── UpdateDossierMedicalDTO.java
│   ├── DossierMedicalDTO.java
│   └── ... (même structure pour Consultation, Intervention, Prescription)
│
├── repository/
│   ├── modules/dossierMedical/
│   │   ├── api/                   ← ÉTAPE 3A : Interfaces Repository
│   │   │   ├── DossierMedicalRepository.java
│   │   │   ├── ConsultationRepository.java
│   │   │   └── ...
│   │   └── impl/                  ← ÉTAPE 3B : Implémentations Repository
│   │       ├── DossierMedicalRepositoryImpl.java
│   │       ├── ConsultationRepositoryImpl.java
│   │       └── ...
│   └── common/
│       └── RowMappers.java        ← ÉTAPE 3 : Mapping SQL → Entity
│
└── service/
    └── modules/dossierMedical/
        ├── api/                   ← ÉTAPE 4A : Interfaces Service
        │   ├── DossierMedicalService.java
        │   ├── ConsultationService.java
        │   └── ...
        └── impl/                  ← ÉTAPE 4B : Implémentations Service
            ├── DossierMedicalServiceImpl.java
            ├── ConsultationServiceImpl.java
            └── ...
        └── test/                  ← ÉTAPE 5 : Tests
            ├── TestDossierMedicalService.java
            ├── TestConsultationService.java
            └── ...
```

---

## 🎯 Résumé : Ordre de Lecture Recommandé

1. **Entities** (5 fichiers) → Structure des données
2. **DTOs** (12 fichiers) → Communication entre couches
3. **Repository Interfaces** (4 fichiers) → Contrats d'accès aux données
4. **Repository Implementations** (4 fichiers) → Code SQL réel
5. **RowMappers** (1 fichier) → Conversion SQL → Entity
6. **Service Interfaces** (4 fichiers) → Contrats de logique métier
7. **Service Implementations** (4 fichiers) → Logique métier complète
8. **Tests** (4 fichiers) → Exemples d'utilisation

**Temps estimé :**
- Niveau 1-2 : 30-45 minutes (fondations)
- Niveau 3 : 45-60 minutes (repositories)
- Niveau 4 : 60-90 minutes (services)
- Niveau 5 : 30-45 minutes (tests)

**Total : 3-4 heures pour une compréhension complète**

---

**Bon courage dans votre lecture du code ! 🚀**
