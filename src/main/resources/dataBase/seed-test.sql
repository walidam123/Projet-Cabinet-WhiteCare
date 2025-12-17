-- =====================================================
-- SEED DATA FOR SERVICE TESTS
-- =====================================================
-- This file contains test data required to run service tests
-- 
-- IMPORT ORDER:
-- 1. schema.sql (creates database and tables)
-- 2. seed.sql (basic data: roles, antecedents)
-- 3. seed-test.sql (this file - test data for services)
--
-- This file creates:
-- - A test cabinet_medicale
-- - A test doctor (utilisateur -> staff -> medecin)
-- - Test medicaments (for prescription tests)
-- - Test actes (for intervention tests)
-- =====================================================

USE WhiteCare;

-- =====================================================
-- 1. CABINET MEDICALE (Required for staff)
-- =====================================================
INSERT INTO cabinet_medicale (
    nom, email, logo, adresse, cin, tel1, tel2, 
    siteweb, instagram, facebook, description,
    creation_date, created_by, updated_by
) VALUES (
    'Cabinet Test WhiteCare',
    'test@whitecare.ma',
    'logo_test.png',
    '123 Rue Test, Rabat',
    'TEST123456',
    '0522123456',
    '0522123457',
    'www.whitecare-test.ma',
    '@whitecare_test',
    'WhiteCareTest',
    'Cabinet médical de test pour les services',
    CURRENT_TIMESTAMP,
    'system',
    'system'
) ON DUPLICATE KEY UPDATE nom = nom;

-- =====================================================
-- 2. UTILISATEUR (Doctor user)
-- =====================================================
INSERT INTO utilisateur (
    nom, prenom, email, adresse, cin, tel, sexe,
    login, password_hash, date_naissance, actif,
    creation_date, created_by, updated_by
) VALUES (
    'Dupont',
    'Jean',
    'jean.dupont@whitecare.ma',
    '456 Avenue Test, Rabat',
    'TEST789012',
    '0612345678',
    'HOMME',
    'dr.dupont',
    '$2a$10$dummy.hash.for.testing.purposes.only',
    '1980-05-15',
    TRUE,
    CURRENT_TIMESTAMP,
    'system',
    'system'
) ON DUPLICATE KEY UPDATE nom = nom;

-- =====================================================
-- 3. STAFF (Link user to cabinet)
-- =====================================================
INSERT INTO staff (
    id, salaire, prime, date_recrutement, solde_conge, cabinet_medicale_id
) 
SELECT 
    u.id,
    15000.00,
    2000.00,
    '2020-01-15',
    25,
    (SELECT id FROM cabinet_medicale WHERE email = 'test@whitecare.ma' LIMIT 1)
FROM utilisateur u
WHERE u.login = 'dr.dupont'
ON DUPLICATE KEY UPDATE salaire = salaire;

-- =====================================================
-- 4. MEDECIN (Specialize staff as doctor)
-- =====================================================
INSERT INTO medecin (
    id, specialite
)
SELECT 
    u.id,
    'Médecine générale'
FROM utilisateur u
WHERE u.login = 'dr.dupont'
ON DUPLICATE KEY UPDATE specialite = specialite;

-- =====================================================
-- 5. ROLE ASSIGNMENT (Assign MEDECIN role to doctor)
-- =====================================================
INSERT INTO utilisateur_role (
    utilisateur_id, role_id
)
SELECT 
    u.id,
    r.id
FROM utilisateur u
CROSS JOIN role r
WHERE u.login = 'dr.dupont' AND r.libelle = 'MEDECIN'
ON DUPLICATE KEY UPDATE utilisateur_id = utilisateur_id;

-- =====================================================
-- 6. MEDICAMENTS (For prescription tests)
-- =====================================================
INSERT INTO medicament (
    nom, laboratoire, type, forme, remboursable, prixUnitaire, description,
    creation_date, created_by, updated_by
) VALUES
    ('Paracétamol', 'PharmaLab', 'Antalgique', 'COMPRIME', TRUE, 15.50, 'Antalgique et antipyrétique', CURRENT_TIMESTAMP, 'system', 'system'),
    ('Ibuprofène', 'MediCorp', 'Anti-inflammatoire', 'COMPRIME', TRUE, 22.00, 'Anti-inflammatoire non stéroïdien', CURRENT_TIMESTAMP, 'system', 'system'),
    ('Amoxicilline', 'BioPharm', 'Antibiotique', 'GELULE', TRUE, 45.75, 'Antibiotique à large spectre', CURRENT_TIMESTAMP, 'system', 'system'),
    ('Vitamine D', 'HealthPlus', 'Vitamine', 'GELULE', FALSE, 30.00, 'Complément alimentaire', CURRENT_TIMESTAMP, 'system', 'system'),
    ('Sirop Toux', 'PharmaCare', 'Antitussif', 'SIROP', TRUE, 28.50, 'Sirop contre la toux sèche', CURRENT_TIMESTAMP, 'system', 'system')
ON DUPLICATE KEY UPDATE nom = nom;

-- =====================================================
-- 7. ACTES (For intervention tests)
-- =====================================================
INSERT INTO acte (
    libelle, categorie, prixDeBase,
    creation_date, created_by, updated_by
) VALUES
    ('Détartrage', 'Soin préventif', 300.00, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Extraction dentaire', 'Chirurgie', 500.00, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Soin carie', 'Soin curatif', 400.00, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Blanchiment dentaire', 'Esthétique', 800.00, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Consultation générale', 'Consultation', 200.00, CURRENT_TIMESTAMP, 'system', 'system')
ON DUPLICATE KEY UPDATE libelle = libelle;

-- =====================================================
-- VERIFICATION QUERIES (Optional - for testing)
-- =====================================================
-- Uncomment to verify data was inserted correctly:
-- SELECT 'Médecins:' as Info, COUNT(*) as Count FROM medecin;
-- SELECT 'Médicaments:' as Info, COUNT(*) as Count FROM medicament;
-- SELECT 'Actes:' as Info, COUNT(*) as Count FROM acte;
-- SELECT 'Cabinet:' as Info, COUNT(*) as Count FROM cabinet_medicale;

-- =====================================================
-- END OF TEST SEED DATA
-- =====================================================

