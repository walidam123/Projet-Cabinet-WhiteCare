-- Insertion des antécédents médicaux les plus courants
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

-- 1. Assurer que le rôle ADMIN existe
INSERT INTO role (libelle, creation_date, last_modification_date, created_by, updated_by)
SELECT 'ADMIN', NOW(), NOW(), 'system', 'system'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE libelle = 'ADMIN');

-- 2. Insérer l'utilisateur (Mot de passe : admin)
INSERT INTO utilisateur (nom, prenom, email, login, password_hash, cin, tel, sexe, actif, creation_date, last_modification_date, created_by, updated_by)
VALUES ('ADMIN', 'Admin', 'admin@whitecare.ma', 'admin', '$2a$10$7.P5wYV7XG8u0H7e8t8vX.W7YvGfJvK7M8vT7wZ9yX7WvU7T7yG7y', 'ADMIN01', '0600000000', 'HOMME', 1, NOW(), NOW(), 'system', 'system');

-- 3. Assigner le rôle à l'utilisateur
INSERT INTO utilisateur_role (utilisateur_id, role_id)
VALUES (LAST_INSERT_ID(), (SELECT id FROM role WHERE libelle = 'ADMIN'));
