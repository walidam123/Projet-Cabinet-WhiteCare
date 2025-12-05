package ma.whitecare.repository.modules.UserManager.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.user.Medecin;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.UserManager.api.MedecinRepository;
import ma.whitecare.repository.modules.UserManager.api.StaffRepository;
import ma.whitecare.repository.modules.UserManager.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {



    private final StaffRepository staffRepository=new StaffRepositoryImpl();
    private final UtilisateurRepository utilisateurRepository=new UtilisateurRepositoryImpl();
    @Override
    public List<Medecin> findByNomPrenom(String nom, String prenom) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            WHERE u.nom LIKE ? AND u.prenom LIKE ?
            ORDER BY u.nom, u.prenom
            """;

        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + prenom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medecins.add(RowMappers.mapMedecinComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par nom/prénom", e);
        }
        return medecins;
    }

    @Override
    public List<Medecin> findBySpecialite(String specialite) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            WHERE m.specialite = ?
            ORDER BY u.nom, u.prenom
            """;

        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, specialite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medecins.add(RowMappers.mapMedecinComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par spécialité: " + specialite, e);
        }
        return medecins;
    }

    @Override
    public List<Medecin> findByCabinetId(Long cabinetId) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
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
                    medecins.add(RowMappers.mapMedecinComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par cabinet: " + cabinetId, e);
        }
        return medecins;
    }

    @Override
    public void affecterAuCabinet(Long medecinId, Long cabinetId) {
        String sql = "UPDATE staff SET cabinet_medicale_id = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, cabinetId, Types.BIGINT);
            ps.setLong(2, medecinId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur affectation au cabinet", e);
        }
    }

    @Override
    public void retirerDuCabinet(Long medecinId) {
        String sql = "UPDATE staff SET cabinet_medicale_id = NULL WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur retrait du cabinet", e);
        }
    }



    @Override
    public List<Medecin> findAvailableMedecins() {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            WHERE u.actif = true
            ORDER BY u.nom, u.prenom
            """;

        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                medecins.add(RowMappers.mapMedecinComplet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche médecins disponibles", e);
        }
        return medecins;
    }

    @Override
    public List<Medecin> findAvailableByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public void updateDisponibilite(Long medecinId, boolean disponible) {
        String sql = "UPDATE utilisateur SET actif = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setLong(2, medecinId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour disponibilité", e);
        }
    }




    @Override
    public List<Medecin> findPageByCabinet(Long cabinetId, int limit, int offset) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            WHERE s.cabinet_medicale_id = ?
            ORDER BY u.nom, u.prenom
            LIMIT ? OFFSET ?
            """;

        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medecins.add(RowMappers.mapMedecinComplet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur pagination par cabinet", e);
        }
        return medecins;
    }

    @Override
    public void updateSpecialite(Long medecinId, String nouvelleSpecialite) {
        String sql = "UPDATE medecin SET specialite = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nouvelleSpecialite);
            ps.setLong(2, medecinId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour spécialité", e);
        }
    }

    @Override
    public void updateNumeroOrdre(Long medecinId, String nouveauNumero) {
        String sql = "UPDATE medecin SET numero_ordre = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nouveauNumero);
            ps.setLong(2, medecinId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour numéro d'ordre", e);
        }
    }

    @Override
    public void updateSalaire(Long medecinId, Double nouveauSalaire) {
        // Utiliser StaffRepository qui a déjà cette méthode
        staffRepository.updateSalaire(medecinId, nouveauSalaire);
    }

    @Override
    public void updatePrime(Long medecinId, Double nouvellePrime) {
        // Utiliser StaffRepository qui a déjà cette méthode
        staffRepository.updatePrime(medecinId, nouvellePrime);
    }

    @Override
    public void updateSoldeConge(Long medecinId, Integer nouveauSolde) {
        // Utiliser StaffRepository qui a déjà cette méthode
        staffRepository.updateSoldeConge(medecinId, nouveauSolde);
    }

    @Override
    public List<Medecin> findAll() {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
            FROM utilisateur u
            INNER JOIN staff s ON u.id = s.id
            INNER JOIN medecin m ON u.id = m.id
            ORDER BY u.nom, u.prenom
            """;

        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                medecins.add(RowMappers.mapMedecinComplet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche tous les médecins", e);
        }
        return medecins;    }

    @Override
    public Medecin findById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, 
                   s.cabinet_medicale_id, m.specialite
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
            throw new RuntimeException("Erreur recherche médecin ID: " + id, e);
        }
    }

    @Override
    public void create(Medecin newElement) {
        staffRepository.create(newElement);
        String sql = """
            INSERT INTO medecin(id, specialite)
            VALUES(?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, newElement.getIdUser());
            ps.setString(2, newElement.getSpecialite());



            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création dans la table Secretaire", e);
        }
    }

    @Override
    public void update(Medecin newValuesElement) {
        // 1. Mettre à jour l'utilisateur
        utilisateurRepository.update(newValuesElement);

        // 2. Mettre à jour le staff
        staffRepository.update(newValuesElement);



        String sql = "UPDATE medecin SET specialite = ?WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newValuesElement.getSpecialite());
            ps.setLong(3, newValuesElement.getIdUser());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour infos secrétaire", e);
        }
    }

    @Override
    public void delete(Medecin oldElement) {
        if (oldElement!=null)deleteById(oldElement.getIdUser());

    }
    @Override
    public void deleteById(Long id) {
        System.out.println("🗑️ Suppression Medecin ID: " + id);

        try {


            // 1. Supprimer de la table Secretaire
            deleteFromMedecinTable(id);

            // 3. Supprimer l'utilisateur via staffRepository
            staffRepository.deleteById(id);

            System.out.println("✅ Staff et utilisateur et medecin supprimés - ID: " + id);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException("Échec suppression staff ID " + id, e);
        }
    }


    public void deleteFromMedecinTable(Long id) {

        // Puis supprimer de Secretaire
        String sql = "DELETE FROM medecin WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du Secretaire: " + id, e);
        }
    }


}

