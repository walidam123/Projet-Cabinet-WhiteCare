package ma.whitecare.repository.modules.UserManager.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.UserManager.api.SecretaireRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    private final StaffRepositoryImpl staffRepository=new StaffRepositoryImpl();
    private final UtilisateurRepository utilisateurRepository=new UtilisateurRepositoryImpl();
    @Override
    public List<Secretaire> findByNumeroCRSS(String numeroCRSS) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE sec.num_cnss = ?
            ORDER BY u.nom, u.prenom
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numeroCRSS);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    secretaires.add(RowMappers.mapSecretaireComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par numéro CRSS: " + numeroCRSS, e);
        }
        return secretaires;
    }

    @Override
    public Optional<Secretaire> findByEmail(String email) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE u.email = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapSecretaireComplet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par email: " + email, e);
        }
    }

    @Override
    public List<Secretaire> findByNomPrenom(String nom, String prenom) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE u.nom LIKE ? AND u.prenom LIKE ?
            ORDER BY u.nom, u.prenom
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + prenom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    secretaires.add(RowMappers.mapSecretaireComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par nom/prénom", e);
        }
        return secretaires;
    }

    @Override
    public List<Secretaire> findByCabinetId(Long cabinetId) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE s.cabinet_medicale_id = ?
            ORDER BY u.nom, u.prenom
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    secretaires.add(RowMappers.mapSecretaireComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par cabinet: " + cabinetId, e);
        }
        return secretaires;
    }

    @Override
    public void affecterAuCabinet(Long secretaireId, Long cabinetId) {
        String sql = "UPDATE staff SET cabinet_medicale_id = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, secretaireId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur affectation au cabinet", e);
        }
    }

    @Override
    public void retirerDuCabinet(Long secretaireId) {
        String sql = "UPDATE staff SET cabinet_medicale_id = NULL WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, secretaireId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur retrait du cabinet", e);
        }
    }

    @Override
    public List<Secretaire> findSecretairesDisponiblesByCabinet(Long cabinetId) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE s.cabinet_medicale_id = ? AND u.actif = true
            ORDER BY u.nom, u.prenom
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    secretaires.add(RowMappers.mapSecretaireComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche secrétaires disponibles", e);
        }
        return secretaires;
    }

    @Override
    public void updateCommission(Long secretaireId, Double nouvelleCommission) {
        String sql = "UPDATE secretaire SET commission = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, nouvelleCommission);
            ps.setLong(2, secretaireId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour commission", e);
        }
    }

    @Override
    public void updateStatutDisponibilite(Long secretaireId, boolean disponible) {
        String sql = "UPDATE utilisateur SET actif = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setLong(2, secretaireId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour statut disponibilité", e);
        }
    }

    @Override
    public List<Secretaire> findPageByCabinet(Long cabinetId, int limit, int offset) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE s.cabinet_medicale_id = ?
            ORDER BY u.nom, u.prenom
            LIMIT ? OFFSET ?
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    secretaires.add(RowMappers.mapSecretaireComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur pagination par cabinet", e);
        }
        return secretaires;
    }

    @Override
    public List<Secretaire> getSecretairesAvecRDVEnCours() {
        String sql = """
            SELECT DISTINCT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            INNER JOIN rendez_vous rv ON sec.id = rv.secretaire_id
            WHERE rv.statut = 'EN_COURS'
            ORDER BY u.nom, u.prenom
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                secretaires.add(RowMappers.mapSecretaireComplet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche secrétaires avec RDV en cours", e);
        }
        return secretaires;
    }



    @Override
    public void updateSalaire(Long secretaireId, Double nouveauSalaire) {
        staffRepository.updateSalaire(secretaireId, nouveauSalaire);
    }

    @Override
    public void updatePrime(Long secretaireId, Double nouvellePrime) {
        staffRepository.updatePrime(secretaireId, nouvellePrime);
    }

    @Override
    public void updateSoldeConge(Long secretaireId, Integer nouveauSolde) {
        staffRepository.updateSoldeConge(secretaireId, nouveauSolde);
    }



    @Override
    public List<Secretaire> findAll() {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            ORDER BY u.nom, u.prenom
            """;

        List<Secretaire> secretaires = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                secretaires.add(RowMappers.mapSecretaireComplet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche toutes les secrétaires", e);
        }
        return secretaires;
    }

    @Override
    public Secretaire findById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN secretaire sec ON u.id = sec.id
            WHERE u.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapSecretaireComplet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche secrétaire ID: " + id, e);
        }
    }

    @Override
    public void create(Secretaire newElement) {
        staffRepository.create(newElement);
        String sql = """
            INSERT INTO secretaire(id, num_cnss, commission)
            VALUES(?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, newElement.getIdUser());
            ps.setString(2, newElement.getNumCNSS());
            ps.setDouble(3, newElement.getCommission());


            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création dans la table Secretaire", e);
        }
    }

    @Override
    public void update(Secretaire newValuesElement) {

            // 1. Mettre à jour l'utilisateur
            utilisateurRepository.update(newValuesElement);

            // 2. Mettre à jour le staff
            staffRepository.update(newValuesElement);



        String sql = "UPDATE secretaire SET num_cnss = ?, commission = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newValuesElement.getNumCNSS());
            ps.setDouble(2, newValuesElement.getCommission());
            ps.setLong(3, newValuesElement.getIdUser());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour infos secrétaire", e);
        }
    }



    @Override
    public void delete(Secretaire oldElement) {
        if (oldElement!=null)deleteById(oldElement.getIdUser());
    }

    @Override
    public void deleteById(Long id) {
        System.out.println("🗑️ Suppression Secretaire ID: " + id);

        try {


            // 1. Supprimer de la table Secretaire
            deleteFromSecretaireTable(id);

            // 3. Supprimer l'utilisateur via staffRepository
            staffRepository.deleteById(id);

            System.out.println("✅ Staff et utilisateur et secretaire supprimés - ID: " + id);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException("Échec suppression staff ID " + id, e);
        }
    }


    public void deleteFromSecretaireTable(Long id) {

        // Puis supprimer de Secretaire
        String sql = "DELETE FROM secretaire WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du Secretaire: " + id, e);
        }
    }


}



