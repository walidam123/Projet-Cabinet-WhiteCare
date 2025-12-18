note-- Insertion des antécédents médicaux les plus courants
INSERT INTO antecedents (nom, categorie, niveau_de_risque, created_by, updated_by) VALUES
('Hypertension artérielle', 'Cardiovasculaire', 'MOYEN', 'system', 'system'),
('Diabète de type 2', 'Métabolique', 'ELEVE', 'system', 'system'),
('Asthme', 'Respiratoire', 'MOYEN', 'system', 'system'),
('Allergie aux pénicillines', 'Allergique', 'FAIBLE', 'system', 'system'),
('Hypercholestérolémie', 'Métabolique', 'MOYEN', 'system', 'system'),
('Arthrose', 'Rhumatologique', 'FAIBLE', 'system', 'system'),
('Reflux gastro-oesophagien', 'Digestif', 'FAIBLE', 'system', 'system'),
('Dépression', 'Psychiatrique', 'MOYEN', 'system', 'system'),
('Migraines', 'Neurologique', 'FAIBLE', 'system', 'system'),
('Tabagisme actif', 'Comportemental', 'ELEVE', 'system', 'system');

INSERT INTO role (libelle,creation_date, last_modification_date, created_by, updated_by)
VALUES ('ADMIN',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system_test', 'system_test');

INSERT INTO role (libelle,creation_date, last_modification_date, created_by, updated_by)
VALUES ('MEDECIN',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system_test', 'system_test');

INSERT INTO role (libelle,creation_date, last_modification_date, created_by, updated_by)
VALUES ('SECRETAIRE',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');
