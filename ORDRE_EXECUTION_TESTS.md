# Ordre d'Exécution des Tests - Guide pour la Démonstration

## 📋 Ordre Logique d'Exécution

Les tests doivent être exécutés dans cet ordre pour respecter la hiérarchie des dépendances :

```
1. TestDossierMedicalService     (Niveau 1 - Base)
    ↓
2. TestConsultationService       (Niveau 2 - Dépend du Dossier Médical)
    ↓
3. TestInterventionService       (Niveau 3 - Dépend de la Consultation)
    ↓
4. TestPrescriptionService       (Niveau 3 - Dépend de la Consultation et Ordonnance)
```

---

## 🔄 Flux Logique Complet

### **ÉTAPE 1 : TestDossierMedicalService** 
**Fichier** : `src/main/java/ma/whitecare/service/test/TestDossierMedicalService.java`

**Pourquoi en premier ?**
- C'est la **base** de toute la hiérarchie
- Crée le **Dossier Médical** qui sera utilisé par les autres services
- Dépend seulement de **Patient** et **Medecin** (données de base)

**Ce que ce test démontre :**
- ✅ Création d'un Dossier Médical avec DTO (`CreateDossierMedicalDTO`)
- ✅ Conversion DTO → Entity dans le service
- ✅ Validation : Un patient = Un seul dossier médical
- ✅ Lecture avec DTO (`DossierMedicalDTO`)
- ✅ Mise à jour avec DTO (`UpdateDossierMedicalDTO`)
- ✅ Recherche par Patient ID et Médecin ID
- ✅ Suppression

**Points à expliquer au professeur :**
- Le DTO contient seulement les IDs (`patientId`, `medecinId`), pas les objets complets
- Le service charge les entités liées depuis les IDs
- Pause de 7 secondes après création pour montrer la base de données

---

### **ÉTAPE 2 : TestConsultationService**
**Fichier** : `src/main/java/ma/whitecare/service/test/TestConsultationService.java`

**Pourquoi en deuxième ?**
- Dépend du **Dossier Médical** créé précédemment
- Une Consultation doit être liée à un Dossier Médical existant
- C'est le **niveau 2** de la hiérarchie

**Ce que ce test démontre :**
- ✅ Création d'une Consultation avec DTO (`CreateConsultationDTO`)
- ✅ Le DTO contient seulement `dossierMedicalId` (pas l'objet complet)
- ✅ Gestion des statuts (EN_ATTENTE, EN_COURS, TERMINEE, etc.)
- ✅ Changement de statut avec validation des transitions
- ✅ Recherche par statut, par date, par dossier médical
- ✅ Méthode `getConsultationComplete()` qui charge interventions + prescriptions
- ✅ Mise à jour et suppression

**Points à expliquer au professeur :**
- Le DTO ne contient que l'ID du dossier médical
- Le service charge automatiquement le DossierMedicale depuis l'ID
- Les règles métier : une consultation terminée ne peut pas changer de statut
- Pause de 7 secondes pour montrer la consultation créée

---

### **ÉTAPE 3 : TestInterventionService**
**Fichier** : `src/main/java/ma/whitecare/service/test/TestInterventionService.java`

**Pourquoi en troisième ?**
- Dépend d'une **Consultation** existante
- Une Intervention est un acte médical réalisé **pendant** une consultation
- C'est le **niveau 3** de la hiérarchie

**Ce que ce test démontre :**
- ✅ Création d'une Intervention avec DTO (`CreateInterventionDTO`)
- ✅ Le DTO contient `consultationId` et `acteId` (IDs seulement)
- ✅ Validation : Prix positif, numéro de dent entre 1-32
- ✅ Calcul du total d'une consultation (`calculateTotalByConsultation`)
- ✅ Recherche par consultation, par acte, par numéro de dent
- ✅ Méthode `getHistoriqueDentaire()` qui groupe par numéro de dent
- ✅ Calcul du coût total patient

**Points à expliquer au professeur :**
- Les interventions sont liées à une consultation spécifique
- Le calcul du total se fait en SQL (SUM) pour performance
- Le service délègue au repository pour les calculs SQL
- Pause de 7 secondes pour montrer l'intervention créée

---

### **ÉTAPE 4 : TestPrescriptionService**
**Fichier** : `src/main/java/ma/whitecare/service/test/TestPrescriptionService.java`

**Pourquoi en dernier ?**
- Dépend d'une **Ordonnance** (qui dépend d'une Consultation)
- Une Prescription est un médicament prescrit **dans** une ordonnance
- C'est le **niveau 3** (parallèle aux Interventions)

**Ce que ce test démontre :**
- ✅ Création d'une Prescription avec DTO (`CreatePrescriptionDTO`)
- ✅ Le DTO contient `ordonnanceId` et `medicamentId` (IDs seulement)
- ✅ Validation : Quantité positive, durée positive, fréquence requise
- ✅ Calcul du coût total d'une ordonnance (`calculateCoutTotalOrdonnance`)
- ✅ Recherche par ordonnance, par médicament, par durée
- ✅ Méthode `getPrescriptionsActives()` qui filtre les prescriptions valides
- ✅ Historique des prescriptions

**Points à expliquer au professeur :**
- Les prescriptions sont liées à une ordonnance
- Le calcul du coût utilise un JOIN SQL (prescription × médicament)
- Le service délègue au repository pour les calculs complexes
- Pause de 7 secondes pour montrer la prescription créée

---

## 🎯 Points Clés à Mentionner Pendant la Démonstration

### 1. **Architecture DTO**
- Chaque test utilise des **DTOs** (CreateDTO, UpdateDTO, DTO)
- Les DTOs contiennent seulement les **IDs** des entités liées
- Le service convertit DTO → Entity pour la persistance
- Le service convertit Entity → DTO pour le retour

### 2. **Hiérarchie des Dépendances**
```
Dossier Médical (niveau 1)
    └── Consultation (niveau 2)
            ├── Intervention (niveau 3)
            └── Prescription (niveau 3, via Ordonnance)
```

### 3. **Pauses pour Démonstration**
- Chaque test a des pauses de **7 secondes** (`Thread.sleep(7000)`)
- Permet de vérifier les changements dans la base de données
- Montre l'état avant et après chaque opération

### 4. **Validation et Règles Métier**
- Chaque service valide les données avant persistance
- Vérifie l'existence des entités liées
- Applique les règles métier (unicité, transitions de statut, etc.)

### 5. **Séparation des Responsabilités**
- **Tests** : Créent les DTOs et appellent les services
- **Services** : Validation, conversion DTO ↔ Entity, règles métier
- **Repositories** : Requêtes SQL, persistance (utilisés par les services)

---

## 📝 Commandes d'Exécution

### Option 1 : Exécution Manuelle (Recommandée pour Démonstration)

```bash
# 1. Test Dossier Médical
cd /home/rajae/Desktop/Projet-Cabinet-WhiteCare
java -cp "target/classes:target/dependency/*" ma.whitecare.service.test.TestDossierMedicalService

# Attendre la fin complète, puis :

# 2. Test Consultation
java -cp "target/classes:target/dependency/*" ma.whitecare.service.test.TestConsultationService

# Attendre la fin complète, puis :

# 3. Test Intervention
java -cp "target/classes:target/dependency/*" ma.whitecare.service.test.TestInterventionService

# Attendre la fin complète, puis :

# 4. Test Prescription
java -cp "target/classes:target/dependency/*" ma.whitecare.service.test.TestPrescriptionService
```

### Option 2 : Exécution via IDE

1. Ouvrir `TestDossierMedicalService.java`
2. Clic droit → Run `main()`
3. Attendre la fin complète
4. Répéter pour les autres tests dans l'ordre

---

## ⚠️ Notes Importantes

1. **Chaque test est indépendant** : Chaque test crée ses propres données de test (Patient, Dossier, etc.)
2. **Base de données** : Assurez-vous que MySQL est démarré et que la base `WhiteCare` existe
3. **Médecins** : Les tests nécessitent au moins un médecin dans la base (exécuter `seed.sql` si nécessaire)
4. **Actes et Médicaments** : Les tests Intervention et Prescription nécessitent des actes et médicaments existants
5. **Pauses** : Les pauses de 7 secondes permettent de vérifier la base de données entre les opérations

---

## 🎓 Script de Présentation Suggéré

### Introduction
"Je vais vous montrer les tests des services du module Dossier Médical. Les tests suivent une hiérarchie logique basée sur les dépendances entre les entités."

### Test 1 : Dossier Médical
"Commençons par le test du Dossier Médical, qui est la base de toute la hiérarchie. Je vais créer un Dossier Médical en utilisant un CreateDTO qui contient seulement les IDs du patient et du médecin..."

### Test 2 : Consultation
"Maintenant, je vais tester le service de Consultation. Une Consultation doit être liée à un Dossier Médical existant. Regardez comment le DTO contient seulement l'ID du dossier..."

### Test 3 : Intervention
"Ensuite, je teste le service d'Intervention. Les Interventions sont des actes médicaux réalisés pendant une Consultation. Je vais montrer le calcul du total en SQL..."

### Test 4 : Prescription
"Enfin, je teste le service de Prescription. Les Prescriptions sont des médicaments prescrits dans une Ordonnance. Je vais montrer le calcul du coût total avec JOIN SQL..."

### Conclusion
"Comme vous pouvez le voir, chaque service utilise des DTOs pour séparer les couches, valide les données, et délègue la persistance aux repositories. Les pauses de 7 secondes permettent de vérifier les changements dans la base de données."

---

## ✅ Checklist Avant la Démonstration

- [ ] MySQL est démarré
- [ ] La base de données `WhiteCare` existe
- [ ] Les tables sont créées (exécuter `schema.sql`)
- [ ] Des données de base existent (exécuter `seed.sql` pour médecins, actes, médicaments)
- [ ] Les tests compilent sans erreur
- [ ] Les fichiers de test sont accessibles
- [ ] Vous avez préparé les explications pour chaque test

---

**Bon courage pour votre présentation ! 🎉**
