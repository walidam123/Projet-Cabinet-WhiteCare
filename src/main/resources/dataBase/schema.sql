
-- FULL SCHEMA (users/staff/medecin/secretaire + roles ...
CREATE TABLE IF NOT EXISTS utilisateur (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,

  nom VARCHAR(120) NOT NULL,
  prenom VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
    adresse VARCHAR(255),
    cin VARCHAR(32) UNIQUE,
    tel VARCHAR(40),
    sexe ENUM('HOMME','FEMME'),

  login VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(120) NOT NULL,
  last_login_date TIMESTAMP NULL,
  date_naissance DATE NULL,
  actif BOOLEAN NOT NULL DEFAULT TRUE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64)
);
CREATE TABLE IF NOT EXISTS staff (
  id BIGINT PRIMARY KEY,
  salaire DECIMAL(12,2) DEFAULT 0,
  prime DECIMAL(12,2) DEFAULT 0,
  date_recrutement DATE,
  solde_conge INT DEFAULT 0,
  cabinet_medicale_id BIGINT,
  CONSTRAINT fk_staff_user FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE
FOREIGN KEY fk_ur_cabinet_medicale(cabinet_medicale_id) REFERENCES cabinet_medicale(id) ON DELETE CASCADE,

);
CREATE TABLE IF NOT EXISTS medecin (
  id BIGINT PRIMARY KEY,
  specialite VARCHAR(120),
  agenda_medecin TEXT,
  CONSTRAINT fk_med_staff FOREIGN KEY (id) REFERENCES staff(id) ON DELETE CASCADE

);
CREATE TABLE IF NOT EXISTS secretaire (
  id BIGINT PRIMARY KEY,
  num_cnss VARCHAR(64),
  commission DECIMAL(12,2) DEFAULT 0,
  CONSTRAINT fk_sec_staff FOREIGN KEY (id) REFERENCES staff(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  libelle VARCHAR(80) NOT NULL UNIQUE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64)
);
CREATE TABLE IF NOT EXISTS utilisateur_role (
  utilisateur_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (utilisateur_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);
CREATE TABLE if not exists notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre ENUM() NOT NULL,-- a Remplir
    message VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    type ENUM() NOT NULL,-- a Remplir
    priorite ENUM() NOT NULL,-- a Remplir
    lue BOOLEAN DEFAULT FALSE,

    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      last_modification_date TIMESTAMP NULL,
      created_by VARCHAR(64),
      updated_by VARCHAR(64),
)
-- Table de liaison: utilisateur_notification (ManyToMany)
create table if not exist utilisateur_notification(
 utilisateur_id BIGINT NOT NULL,
  notification_id BIGINT NOT NULL,
  PRIMARY KEY (utilisateur_id, notification_id),
  CONSTRAINT fk_ur_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
  CONSTRAINT fk_ur_notification FOREIGN KEY (notification_id) REFERENCES notification(id) ON DELETE CASCADE
)

create table if not exists revenues(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    description Varchar(100),
    montant DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    cabinet_medicale_id BIGINT,


    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    FOREIGN KEY fk_ur_cabinet_medicale(cabinet_medicale_id) REFERENCES cabinet_medicale(id) ON DELETE CASCADE,

)

create table if not exists charges(

id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    description Varchar(100),
    montant DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    cabinet_medicale_id BIGINT,


    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    FOREIGN KEY fk_ur_cabinet_medicale(cabinet_medicale_id) REFERENCES cabinet_medicale(id) ON DELETE CASCADE,


)
 create table if not exists cabinet_medicale(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
nom VARCHAR(30) NOT NULL ,
email VARCHAR(30) NOT NULL UNIQUE,
logo VARCHAR(100) NOT NUll,
adresse VARCHAR(30) NOT NULL,
cin VARCHAR(15) NOT NULL,
tel1 VARCHAR(15) NOT NULL,
tel2 VARCHAR(15) NOT NULL,
siteweb VARCHAR(25) NOT NULL,
instagram VARCHAR(15) NOT NULL,
facebook VARCHAR(15) NOT NULL,
description VARCHAR(100) NOT NULL,

 creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64)
 )


create table if not exists statistiques(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
nom VARCHAR(30) NOT NULL ,
categorie ENUM() NOT NULL,-- a Remplir
chiffre DECIMAL(12,2) DEFAULT 0,
dateCalcul DATE,
    cabinet_medicale_id BIGINT,


creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64),

    FOREIGN KEY fk_ur_cabinet_medicale(cabinet_medicale_id) REFERENCES cabinet_medicale(id) ON DELETE CASCADE,

)
create table if not exists agenda_mensuel(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mois ENUM('JANVIER', 'FEVRIER', 'MARS', 'AVRIL', 'MAI', 'JUIN', 'JUILLET', 'AOUT', 'SEPTEMBRE', 'OCTOBRE', 'NOVEMBRE', 'DECEMBRE') NOT NULL,
    annee INT NOT NULL,
    medecin_id BIGINT NOT NULL,


creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64),
    FOREIGN KEY fk_ur_medecin(medecin_id) REFERENCES mdecin(id) ON DELETE CASCADE,


)



create table if not exists patient(
idPatient BIGINT AUTO_INCREMENT PRIMARY KEY,
 nom VARCHAR(20),
 dateDeNaissance DATE,
sexe ENUM{'HOMME','FEMME'} not null,
adresse VARCHAR(20),
telephone VARCHAR(15),
assurance ENUM('CNSS', 'CNOPS','PRIVEE','AUCUNE' )DEFAULT 'AUCUNE',

creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64)


)

create table if not exists antecedents(
id_antecedent BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    categorie VARCHAR(100),
    niveau_de_risque ENUM('FAIBLE', 'MOYEN', 'ELEVE', 'CRITIQUE') NOT NULL,

creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64),
)

CREATE TABLE IF NOT EXISTS patient_antecedents (
                                                   patient_id BIGINT NOT NULL,
                                                   antecedents_id BIGINT NOT NULL,
                                                   PRIMARY KEY (patient_id, antecedents_id),
    CONSTRAINT fk_pa_patient FOREIGN KEY (patient_id) REFERENCES patient(idPatient) ON DELETE CASCADE,
    CONSTRAINT fk_pa_antecedents FOREIGN KEY (antecedents_id) REFERENCES antecedents(id_antecedent) ON DELETE CASCADE
    );


create table if not exists acte(
idActe BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(25) NOT NULL,
    categorie VARCHAR(25),
    prixDeBase  DECIMAL(12,2) DEFAULT 0,

creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64),
)


create table if not exists medicament(
idMct BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(25) NOT NULL,
    laboratoire VARCHAR(25),
     type VARCHAR(25),
     forme ENUM('COMPRIME','GELULE','SIROP','SOLUTION_BUVABLE','INJECTABLE','POMMADE','GEL','CREME','SPRAY','GOUTTES') NOT NULL,
    remboursable  BOOLEAN,
 prixUnitaire DECIMAL(12,2) DEFAULT 0,
 description VARCHAR(100),


creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64)
);


create table if not exists prescription(
    idPr BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantite INTEGER,
    frequence VARCHAR(25),
    dureeEnjours INTEGER,

    medicament_id BIGINT NOT NULL,
    ordonnance_id BIGINT NOT NULL,

creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64),

       CONSTRAINT fk_pm_medicament FOREIGN KEY (medicament_id) REFERENCES medicament(idMct) ON DELETE CASCADE,
       CONSTRAINT fk_pm_ordonnance FOREIGN KEY (ordonnance_id) REFERENCES ordonnance(idOrd) ON DELETE CASCADE

);


create table if not exists ordonnance(
    idPr BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,

    consultation_id BIGINT NOT NULL,
    dossierMedicale_id BIGINT NOT NULL,

creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     last_modification_date TIMESTAMP NULL,
     created_by VARCHAR(64),
     updated_by VARCHAR(64),

CONSTRAINT fk_co_consultation FOREIGN KEY (consultation_id) REFERENCES consultation(idConsultation) ON DELETE CASCADE,
       CONSTRAINT fk_do_dossierMedicale FOREIGN KEY (dossierMedicale_id) REFERENCES dossierMedicale(idDM) ON DELETE CASCADE

);


create table if not exists dossierMedicale(
idDM BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateDecreation DATE NOT NULL,

    patient_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,
creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
last_modification_date TIMESTAMP NULL,
created_by VARCHAR(64),
    updated_by VARCHAR(64),
    CONSTRAINT fk_dp_medecin FOREIGN KEY (medecin_id) REFERENCES medecin(id) ON DELETE CASCADE,
    CONSTRAINT fk_dp_patient FOREIGN KEY (patient_id) REFERENCES patient(idPatient) ON DELETE CASCADE

)

CREATE TABLE if not exists certificat (
                                          id_certif BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          date_debut DATE NOT NULL,
                                          date_fin DATE NOT NULL,
                                          duree INT,
                                          note_medecin TEXT,
                                          dossier_medicale_id BIGINT NOT NULL,
                                          consulation_id BIGINT NOT NULL,

    -- Champs hérités de BaseEntity
                                          creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          last_modification_date TIMESTAMP NULL,
                                          created_by VARCHAR(64),
    updated_by VARCHAR(64),
    CONSTRAINT fk_dc_dossierMedicale FOREIGN KEY (dossier_medicale_id) REFERENCES dossiermedicale(idDm) ON DELETE CASCADE,
    CONSTRAINT fk_cc_consultation FOREIGN KEY (consulation_id) REFERENCES consultation(id_consultation) ON DELETE CASCADE
    )

CREATE TABLE if not exists rdv (
                     id_rdv BIGINT AUTO_INCREMENT PRIMARY KEY,
                     date DATE NOT NULL,
                     heure TIME NOT NULL,
                     motif VARCHAR(100),
                     statut ENUM('PLANIFIE', 'CONFIRME', 'EN_SALLE','EN_CONSULTATION', 'TERMINE', 'ANNULE', 'ABSENT') default 'PLANIFIE',
                     note_medecin VARCHAR(200),

                     consultation_id BIGINT NOT NULL,
                     dossier_medicale_id BIGINT,
    -- Champs hérités de BaseEntity
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),

    CONSTRAINT fk_cr_consultation FOREIGN KEY (consultation_id) REFERENCES consultation(id_consultation) ON DELETE CASCADE,
    CONSTRAINT fk_dr_dossierMedicale FOREIGN KEY (dossier_medicale_id) REFERENCES dossierMedicale(id_Dm) ON DELETE CASCADE
)

CREATE TABLE if not exists intervention_medecin (
                                      id_im BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      prix_de_patient DOUBLE NOT NULL,
                                      num_dent INT,
                                      consultation_id BIGINT NOT NULL,
                                      acte_id BIGINT NOT NULL,
    -- Champs hérités de BaseEntity
                                      creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      last_modification_date TIMESTAMP NULL,
                                      created_by VARCHAR(64),
                                      updated_by VARCHAR(64),
    CONSTRAINT fk_ci_consultation FOREIGN KEY (consultation_id) REFERENCES consultation(id_consultation) ON DELETE CASCADE,
    CONSTRAINT fk_ca_acte FOREIGN KEY (acte_id) REFERENCES acte(idActe) ON DELETE CASCADE
)


CREATE TABLE if not exists facture (
                         id_facture BIGINT AUTO_INCREMENT PRIMARY KEY,
                         totale_facture DOUBLE NOT NULL,
                         totale_paye DOUBLE DEFAULT 0,
                         reste DOUBLE,
                         statut ENUM('BROUILLON','REGLEE','PARTIELLE','ANNULEE','IMPAYEE') DEFAULT 'BROUILLON',
                         date_facture DATETIME NOT NULL,

                         situation_financiere_id BIGINT,
                         consultation_id BIGINT,

    -- Champs hérités de BaseEntity
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),


    CONSTRAINT fk_fs_situationfinanciere  FOREIGN KEY (situation_financiere_id) REFERENCES situation_financiere(idSf) ON DELETE CASCADE,
    CONSTRAINT fk_fc_consultation  FOREIGN KEY (consultation_id) REFERENCES consultation(idConsultation) ON DELETE CASCADE
)

CREATE TABLE IF NOT EXISTS situation_financiere (
                                      idSf BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      totale_des_actes DOUBLE,
                                      totale_paye DOUBLE,
                                      credit DOUBLE,
                                      statut ENUM('SOLDE','EN_RETARD','IMPAYE','PARTIEL'),
                                      en_promo ENUM('OUI', 'NON'),
    dossier_medicale_id BIGINT,
    -- Champs hérités de BaseEntity
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modification_date TIMESTAMP NULL,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    CONSTRAINT fk_ds_dossierMedicale FOREIGN KEY (dossier_medicale_id) REFERENCES dossierMedicale(idDm) ON DELETE CASCADE
)

CREATE TABLE consultation (
                              id_consultation BIGINT AUTO_INCREMENT PRIMARY KEY,
                              date DATE NOT NULL,
                              statut ENUM('EN_ATTENTE', 'EN_COURS', 'TERMINEE', 'ANNULEE','URGENCE') NOT NULL,
                              observation_medecin TEXT,
                              dossier_medicale_id BIGINT NOT NULL,

    -- Champs hérités de BaseEntity
                              creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              last_modification_date TIMESTAMP NULL,
                              created_by VARCHAR(64),
                              updated_by VARCHAR(64),
                            constraint fk_cd_dossierMedicale FOREIGN KEY (dossier_medicale_id) REFERENCES dossier_medicale(idDm) ON DELETE CASCADE

)