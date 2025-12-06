# Services du Module Dossier Médical

Ce document décrit les services du module dossier médical de l'application WhiteCare. Ces services encapsulent la logique métier et assurent la validation des données, la vérification des dépendances et l'application des règles de gestion.

## 📋 Vue d'ensemble

Le module dossier médical comprend quatre services principaux qui gèrent les différentes entités liées aux dossiers médicaux des patients :

1. **DossierMedicalService** - Gestion des dossiers médicaux
2. **ConsultationService** - Gestion des consultations médicales
3. **InterventionService** - Gestion des interventions réalisées
4. **PrescriptionService** - Gestion des prescriptions de médicaments

---

## 🗂️ DossierMedicalService

**Responsabilité principale :** Gère la création, modification, consultation et suppression des dossiers médicaux avec validation complète des données et vérification des dépendances.

**Fonctionnalités :**
- Création de nouveaux dossiers médicaux avec vérification de l'unicité (un seul dossier par patient)
- Validation de l'existence du patient et du médecin avant création
- Recherche de dossiers par patient ou par médecin
- Vérification de l'existence d'un dossier pour un patient donné
- Validation des dates (la date de création ne peut pas être dans le futur)
- Gestion des mises à jour avec validation des entités liées

**Règles métier :**
- Un patient ne peut avoir qu'un seul dossier médical actif
- Le patient et le médecin sont obligatoires pour créer un dossier
- La date de création est automatiquement définie à la date actuelle si non fournie

---

## 🩺 ConsultationService

**Responsabilité principale :** Gère la création, modification et suivi des consultations médicales avec validation des statuts et vérification des dépendances.

**Fonctionnalités :**
- Création de consultations avec validation du dossier médical associé
- Gestion des statuts de consultation (EN_ATTENTE, EN_COURS, TERMINEE, ANNULEE, URGENCE)
- Recherche de consultations par dossier médical, par statut ou par période
- Changement de statut avec validation des transitions autorisées
- Marquage d'une consultation comme terminée avec observations médicales
- Filtrage des consultations urgentes et en cours
- Comptage des consultations par statut

**Règles métier :**
- Une consultation terminée ne peut pas changer de statut
- Une consultation annulée ne peut pas changer de statut
- Une consultation annulée ne peut pas être terminée
- Le statut par défaut est EN_ATTENTE si non spécifié
- La date de consultation ne peut pas être dans le futur

---

## 🔧 InterventionService

**Responsabilité principale :** Gère la création, modification et consultation des interventions médicales réalisées lors des consultations avec validation des prix et vérification des actes.

**Fonctionnalités :**
- Création d'interventions avec validation de la consultation et de l'acte
- Validation du prix (doit être positif)
- Validation du numéro de dent (entre 1 et 32 pour les dents permanentes)
- Recherche d'interventions par consultation, par acte ou par numéro de dent
- Calcul du total des interventions pour une consultation donnée
- Comptage des interventions par acte

**Règles métier :**
- La consultation et l'acte sont obligatoires pour créer une intervention
- Le prix de l'intervention doit être positif ou nul
- Le numéro de dent doit être entre 1 et 32 (dentition permanente)
- Le prix est utilisé pour le calcul des factures

---

## 💊 PrescriptionService

**Responsabilité principale :** Gère la création, modification et consultation des prescriptions de médicaments avec validation des quantités, fréquences et durées de traitement.

**Fonctionnalités :**
- Création de prescriptions avec validation de l'ordonnance et du médicament
- Validation de la quantité (doit être positive)
- Validation de la durée en jours (doit être positive)
- Validation de la fréquence (obligatoire et non vide)
- Recherche de prescriptions par ordonnance, par médicament ou par durée
- Calcul du coût total d'une ordonnance (somme des prix des médicaments × quantités)
- Comptage des prescriptions par médicament

**Règles métier :**
- L'ordonnance et le médicament sont obligatoires pour créer une prescription
- La quantité, la durée et la fréquence doivent être positives
- La fréquence est obligatoire (ex: "2 fois par jour", "matin et soir")
- Le coût total est calculé en multipliant le prix unitaire du médicament par la quantité prescrite

---

## 🔗 Relations entre les services

Les services sont interdépendants et respectent la hiérarchie suivante :

```
DossierMedicalService (niveau 1)
    └── ConsultationService (niveau 2)
            ├── InterventionService (niveau 3)
            └── PrescriptionService (niveau 3, via Ordonnance)
```

**Flux typique :**
1. Création d'un **Dossier Médical** pour un patient
2. Création d'une **Consultation** liée au dossier
3. Ajout d'**Interventions** réalisées lors de la consultation
4. Création d'une **Ordonnance** avec **Prescriptions** de médicaments

---

## ✅ Validation et Sécurité

Tous les services implémentent :
- **Validation des entrées** : Vérification que les données obligatoires sont présentes
- **Vérification d'existence** : S'assurer que les entités référencées existent avant création/modification
- **Validation des règles métier** : Application des contraintes spécifiques au domaine médical
- **Gestion des erreurs** : Messages d'erreur clairs et explicites pour faciliter le débogage

---

## 📝 Notes d'utilisation

- Tous les services utilisent le pattern Repository pour l'accès aux données
- Les services sont thread-safe si les repositories sous-jacents le sont
- Les exceptions lancées sont de type `IllegalArgumentException` pour les erreurs de validation
- Les méthodes de recherche retournent des listes vides si aucun résultat n'est trouvé (sauf si une exception est attendue)

---

**Dernière mise à jour :** 2025-12-06

