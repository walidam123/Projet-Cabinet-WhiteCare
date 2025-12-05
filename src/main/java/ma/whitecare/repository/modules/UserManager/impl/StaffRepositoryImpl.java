package ma.whitecare.repository.modules.UserManager.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.entities.user.Secretaire;
import ma.whitecare.entities.user.Staff;
import ma.whitecare.entities.user.Utilisateur;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.UserManager.api.StaffRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {


    private final UtilisateurRepository utilisateurRepository=new UtilisateurRepositoryImpl();



    @Override
    public List<Staff> findByCabinetMedicaleId(Long cabinetId) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            LEFT JOIN medecin m ON u.id = m.id
            LEFT JOIN secretaire sec ON u.id = sec.id
            WHERE s.cabinet_medicale_id = ?
            ORDER BY u.nom, u.prenom
            """;

        List<Staff> staffList = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utilisateur utilisateur = RowMappers.mapStaffGenerique(rs);
                    if (utilisateur instanceof Staff) {
                        staffList.add((Staff) utilisateur);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du staff par cabinet: " + cabinetId, e);
        }
        return staffList;
    }

    @Override
    public List<Staff> findByCabinetAndActif(Long cabinetId, boolean actif) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            LEFT JOIN medecin m ON u.id = m.id
            LEFT JOIN secretaire sec ON u.id = sec.id
            WHERE s.cabinet_medicale_id = ? AND u.actif = ?
            ORDER BY u.nom, u.prenom
            """;

        List<Staff> staffList = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setBoolean(2, actif);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utilisateur utilisateur = RowMappers.mapStaffGenerique(rs);
                    if (utilisateur instanceof Staff) {
                        staffList.add((Staff) utilisateur);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du staff par cabinet et statut", e);
        }
        return staffList;
    }

    @Override
    public List<Staff> findPageByCabinet(Long cabinetId, int limit, int offset) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite, sec.num_cnss, sec.commission
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            LEFT JOIN medecin m ON u.id = m.id
            LEFT JOIN secretaire sec ON u.id = sec.id
            WHERE s.cabinet_medicale_id = ?
            ORDER BY u.nom, u.prenom
            LIMIT ? OFFSET ?
            """;

        List<Staff> staffList = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utilisateur utilisateur = RowMappers.mapStaffGenerique(rs);
                    if (utilisateur instanceof Staff) {
                        staffList.add((Staff) utilisateur);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la pagination du staff par cabinet", e);
        }
        return staffList;
    }

    @Override
    public List<Medecin> findMedecinsByCabinet(Long cabinetId) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            WHERE s.cabinet_medicale_id = ?
            ORDER BY u.nom, u.prenom
            """;

        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medecin medecin = RowMappers.mapMedecinComplet(rs);
                    medecins.add(medecin);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médecins par cabinet: " + cabinetId, e);
        }
        return medecins;

    }

    @Override
    public Medecin findMedecinById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            WHERE u.id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapMedecinComplet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du médecin par ID: " + id, e);
        }
    }

    @Override
    public List<Secretaire> findSecretairesByCabinet(Long cabinetId) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   sec.num_cnss, sec.commission
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
                    Secretaire secretaire = RowMappers.mapSecretaireComplet(rs);
                    secretaires.add(secretaire);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des secrétaires par cabinet: " + cabinetId, e);
        }
        return secretaires;
    }

    @Override
    public Secretaire findSecretaireById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   sec.num_cnss, sec.commission
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
            throw new RuntimeException("Erreur lors de la recherche de la secrétaire par ID: " + id, e);
        }
    }

    @Override
    public void updateSalaire(Long staffId, Double nouveauSalaire) {

            String sql = "UPDATE staff SET salaire = ? WHERE id = ?";
            try (Connection c = SessionFactory.getInstance().getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setObject(1, nouveauSalaire, Types.DOUBLE);
                ps.setLong(2, staffId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la mise à jour du salaire: " + staffId, e);
            }

    }

    @Override
    public void updatePrime(Long staffId, Double nouvellePrime) {
        String sql = "UPDATE staff SET prime = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, nouvellePrime, Types.DOUBLE);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la prime: " + staffId, e);
        }
    }

    @Override
    public void updateSoldeConge(Long staffId, Integer nouveauSolde) {
        String sql = "UPDATE staff SET solde_conge = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, nouveauSolde, Types.INTEGER);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du solde congé: " + staffId, e);
        }
    }

    @Override
    public void assignToCabinet(Long staffId, Long cabinetId) {
        String sql = "UPDATE staff SET cabinet_medicale_id = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, cabinetId, Types.BIGINT);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'affectation au cabinet: " + staffId, e);
        }
    }

    @Override
    public Long countStaffByCabinet(Long cabinetId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE cabinet_medicale_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage du staff par cabinet: " + cabinetId, e);
        }
    }

    @Override
    public List<Staff> findAll() {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite, sec.num_cnss, sec.commission
            FROM utilisateur u
            LEFT JOIN staff s ON u.id = s.id
            LEFT JOIN medecin m ON u.id = m.id
            LEFT JOIN secretaire sec ON u.id = sec.id
            WHERE u.id IN (SELECT id FROM staff)
            ORDER BY u.nom, u.prenom
            """;

        List<Staff> staffList = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utilisateur utilisateur = RowMappers.mapStaffGenerique(rs);
                if (utilisateur instanceof Staff) {
                    staffList.add((Staff) utilisateur);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de tous les staff", e);
        }
        return staffList;
    }


    @Override
    public Staff findById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_medicale_id,
                   m.specialite, sec.num_cnss, sec.commission
            FROM utilisateur u
            LEFT JOIN staff s ON u.id = s.id
            LEFT JOIN medecin m ON u.id = m.id
            LEFT JOIN secretaire sec ON u.id = sec.id
            WHERE u.id = ? AND u.id IN (SELECT id FROM staff)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur utilisateur = RowMappers.mapStaffGenerique(rs);
                    if (utilisateur instanceof Staff) {
                        return (Staff) utilisateur;
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du staff par ID: " + id, e);
        }    }

    @Override
    public void create(Staff staff) {
        // 1. D'abord créer l'utilisateur via UtilisateurRepository
        utilisateurRepository.create(staff); // staff hérite de Utilisateur, ça fonctionne

        // 2. Puis créer dans la table staff
        createStaffTable(staff);

    }

    private void createStaffTable(Staff newElement) {


        String sql = """
            INSERT INTO staff(id, salaire, prime, date_recrutement, solde_conge, cabinet_medicale_id)
            VALUES(?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, newElement.getIdUser());
            ps.setDouble(2, newElement.getSalaire());
            ps.setDouble(3, newElement.getPrime());
            ps.setDate(4, newElement.getDateRecrutement() != null ?
                    Date.valueOf(newElement.getDateRecrutement()) : null);
            ps.setInt(5, newElement.getSoldeConge());
            ps.setLong(6, newElement.getCabinetMedicaleId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création dans la table staff", e);
        }
    }


    @Override
    public void update(Staff newValuesElement) {
        String sql = """
            UPDATE staff SET salaire=?, prime=?, date_recrutement=?, 
                   solde_conge=?, cabinet_medicale_id=? WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, newValuesElement.getSalaire());
            ps.setDouble(2, newValuesElement.getPrime());
            ps.setDate(3, newValuesElement.getDateRecrutement() != null ?
                    Date.valueOf(newValuesElement.getDateRecrutement()) : null);
            ps.setInt(4, newValuesElement.getSoldeConge());
            ps.setLong(5, newValuesElement.getCabinetMedicaleId());
            ps.setLong(6, newValuesElement.getIdUser());

            ps.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du staff", e);
        }
    }

    @Override
    public void delete(Staff oldElement) {
        if (oldElement != null) {
            deleteById(oldElement.getIdUser());
        }
    }


    @Override
    public void deleteById(Long id) {
        System.out.println("🗑️ Suppression staff ID: " + id);

        try {


            // 1. Supprimer de la table staff
            deleteFromStaffTable(id);

            // 3. Supprimer l'utilisateur via UtilisateurRepository
            utilisateurRepository.deleteById(id);

            System.out.println("✅ Staff et utilisateur supprimés - ID: " + id);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException("Échec suppression staff ID " + id, e);
        }
    }

    public void deleteFromStaffTable(Long id) {

        // Puis supprimer de staff
        String sql = "DELETE FROM staff WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du staff: " + id, e);
        }
    }
}
