# Guide de Présentation : Services du Dossier Médical

Ce document est conçu pour t'aider à expliquer le code des services à ton professeur. Il détaille le rôle de chaque service, ses fonctions principales et la logique d'implémentation.

---

## 1. Concept Général (Architecture)

**À expliquer au début :**
*   **Couche Service** : C'est le "cerveau" de l'application. Elle contient la **logique métier** et les **règles de validation**.
*   **Séparation des responsabilités** : Le Service ne parle pas directement à la base de données. Il utilise des **Repositories** (DAO) pour ça.
*   **Pattern Interface/Implémentation** :
    *   `DossierMedicalService` (Interface) : Définit le "QUOI" (le contrat, les méthodes disponibles).
    *   `DossierMedicalServiceImpl` (Classe) : Définit le "COMMENT" (le code réel).
*   **Injection de Dépendances** : Les repositories sont passés au service via le constructeur (ex: `new DossierMedicalServiceImpl(repo)`). C'est plus propre et plus facile à tester.

---

## 2. Détail des Services

### A. DossierMedicalService
**Rôle :** Gère le dossier central du patient. C'est le point d'entrée.

**Fonctions Clés à Expliquer :**
*   **`createDossierMedical`** :
    *   *Ce qu'elle fait :* Crée un nouveau dossier.
    *   *Logique métier :*
        1.  Vérifie que le patient existe.
        2.  Vérifie que le médecin existe.
        3.  **Règle importante** : Vérifie qu'un dossier n'existe pas déjà pour ce patient (`existsByPatientId`) -> Un patient = Un seul dossier.
        4.  Initialise la date de création si elle est vide.
*   **`findByPatientId` / `findByMedecinId`** : Permet de filtrer les dossiers. Utile pour afficher "Mes patients" (pour un médecin) ou le dossier d'un patient spécifique.

### B. ConsultationService
**Rôle :** Gère les rendez-vous/visites médicales.

**Fonctions Clés à Expliquer :**
*   **`createConsultation`** :
    *   Vérifie que le Dossier Médical lié existe bien.
    *   Définit le statut par défaut à `EN_ATTENTE` si non précisé.
*   **`changeStatut(id, nouveauStatut)`** :
    *   *Ce qu'elle fait :* Fait avancer la consultation (EN_ATTENTE -> EN_COURS -> TERMINEE).
    *   *Règle métier :* On pourrait ajouter des contrôles ici (ex: impossible de passer de TERMINEE à EN_ATTENTE).
*   **`findByStatut`** : Utile pour le tableau de bord du médecin (voir qui est en salle d'attente).

### C. InterventionService
**Rôle :** Gère les actes médicaux (soins) faits pendant une consultation (ex: détartrage, extraction).

**Fonctions Clés à Expliquer :**
*   **`createIntervention`** :
    *   Lie l'intervention à une Consultation et à un Acte (type de soin).
    *   Enregistre le prix et potentiellement la dent concernée.
*   **`calculateTotalByConsultation(consultationId)`** :
    *   *Logique métier intéressante :* C'est une méthode de calcul.
    *   Elle récupère toutes les interventions d'une consultation.
    *   Elle additionne les prix (`sum`) pour savoir combien le patient doit payer pour cette visite.

### D. PrescriptionService
**Rôle :** Gère les médicaments prescrits.

**Fonctions Clés à Expliquer :**
*   **`createPrescription`** :
    *   Lie un Médicament à une Ordonnance.
    *   Définit la posologie (quantité, fréquence, durée).
    *   *Validation :* Vérifie que la quantité et la durée sont positives (> 0).
*   **`calculateCoutTotalOrdonnance(ordonnanceId)`** :
    *   Calcule le coût estimé des médicaments prescrits (Prix unitaire du médicament * Quantité prescrite).

---

## 3. Exemple de lecture de code (Code Walkthrough)

Si le prof te demande de lire une méthode (ex: `updateDossierMedical`), suis ces étapes :

1.  **Récupération** : "D'abord, je récupère l'objet existant via son ID (`getById`)."
2.  **Validation** : "Ensuite, je valide les données reçues (ex: est-ce que le nouveau patient existe ?)."
3.  **Mise à jour** : "Je mets à jour uniquement les champs qui ont changé (si `nouvelleDate != null`, alors je change la date)."
4.  **Audit** : "Je mets à jour le champ `modifiePar` pour savoir qui a touché à la donnée."
5.  **Persistance** : "Enfin, j'appelle le repository `update(objet)` pour sauvegarder dans la base de données."

## 4. Les Tests

Tu peux mentionner que tu as utilisé des classes `Test...Service` avec une méthode `main` pour simuler un scénario réel :
1.  Créer les données de base (Patient, Médecin).
2.  Appeler le service pour créer l'entité.
3.  Vérifier qu'elle a bien un ID (donc insérée en base).
4.  La relire, la modifier et la supprimer pour valider tout le cycle (CRUD).

