package ma.whitecare.repository.modules.actes.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.actes.api.ActeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActeRepositoryImpl implements ActeRepository {
    @Override
    public List<Acte> findAll() {
        String sql = """
            SELECT a.idActe, a.libelle, a.categorie, a.prixDeBase,
                   a.creation_date, a.last_modification_date,
                   a.created_by, a.updated_by
            FROM acte a
            ORDER BY a.libelle
            """;

        List<Acte> actes = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                actes.add(RowMappers.mapActeComplet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de tous les actes", e);
        }

        return actes;
    }

    @Override
    public Acte findById(Long id) {
        String sql = """
            SELECT a.idActe, a.libelle, a.categorie, a.prixDeBase,
                   a.creation_date, a.last_modification_date,
                   a.created_by, a.updated_by
            FROM acte a
            WHERE a.idActe = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapActeComplet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'acte par ID: " + id, e);
        }
    }

    @Override
    public void create(Acte newElement) {
        String sql = """
            INSERT INTO acte (libelle, categorie, prixDeBase,creation_date,last_modification_date ,created_by, updated_by)
            VALUES (?, ?, ?, ?, ?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getLibelle());
            ps.setString(2, newElement.getCategorie());
            ps.setDouble(3, newElement.getPrixDeBase() != null ? newElement.getPrixDeBase() : 0.0);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setString(6, newElement.getCreePar() != null ? newElement.getCreePar() : "system");
            ps.setString(7, newElement.getModifiePar() != null ? newElement.getModifiePar() : "system");

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setIdActe(generatedKeys.getLong(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'acte: " + newElement.getLibelle(), e);
        }
    }

    @Override
    public void update(Acte newValuesElement) {
        String sql = """
            UPDATE acte 
            SET libelle = ?, categorie = ?, prixDeBase = ?, 
                last_modification_date = CURRENT_TIMESTAMP, 
                updated_by = ?
            WHERE idActe = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getLibelle());
            ps.setString(2, newValuesElement.getCategorie());
            ps.setDouble(3, newValuesElement.getPrixDeBase() != null ? newValuesElement.getPrixDeBase() : 0.0);
            ps.setString(4, newValuesElement.getModifiePar());
            ps.setLong(5, newValuesElement.getIdActe());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'acte ID: " + newValuesElement.getIdActe(), e);
        }
    }

    @Override
    public void delete(Acte oldElement) {
        if (oldElement != null) {
            deleteById(oldElement.getIdActe());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM acte WHERE idActe = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'acte ID: " + id, e);
        }
    }

    @Override
    public List<Acte> findByLibelle(String libelle) {
        String sql = """
            SELECT a.idActe, a.libelle, a.categorie, a.prixDeBase,
                   a.creation_date, a.last_modification_date,
                   a.created_by, a.updated_by
            FROM acte a
            WHERE a.libelle LIKE ?
            ORDER BY a.libelle
            """;

        List<Acte> actes = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + libelle + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actes.add(RowMappers.mapActeComplet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par libellé: " + libelle, e);
        }

        return actes;
    }

    @Override
    public List<Acte> findByCategorie(String categorie) {
        String sql = """
            SELECT a.idActe, a.libelle, a.categorie, a.prixDeBase,
                   a.creation_date, a.last_modification_date,
                   a.created_by, a.updated_by
            FROM acte a
            WHERE a.categorie = ?
            ORDER BY a.libelle
            """;

        List<Acte> actes = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actes.add(RowMappers.mapActeComplet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par catégorie: " + categorie, e);
        }

        return actes;
    }

    @Override
    public void updatePrix(Long acteId, Double nouveauPrix) {
        String sql = """
            UPDATE acte 
            SET prixDeBase = ?, 
                last_modification_date = CURRENT_TIMESTAMP
            WHERE idActe = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, nouveauPrix != null ? nouveauPrix : 0.0);
            ps.setLong(2, acteId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Aucun acte trouvé avec l'ID: " + acteId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du prix de l'acte ID: " + acteId, e);
        }
    }

    @Override
    public void updateCategorie(Long acteId, String nouvelleCategorie) {
        String sql = """
            UPDATE acte 
            SET categorie = ?, 
                last_modification_date = CURRENT_TIMESTAMP
            WHERE idActe = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nouvelleCategorie);
            ps.setLong(2, acteId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Aucun acte trouvé avec l'ID: " + acteId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la catégorie de l'acte ID: " + acteId, e);
        }

    }

    @Override
    public void updateLibelle(Long acteId, String nouveauLibelle) {
        String sql = """
            UPDATE acte 
            SET libelle = ?, 
                last_modification_date = CURRENT_TIMESTAMP
            WHERE idActe = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nouveauLibelle);
            ps.setLong(2, acteId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Aucun acte trouvé avec l'ID: " + acteId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du libellé de l'acte ID: " + acteId, e);
        }
    }

    @Override
    public Long countByCategorie(String categorie) {
        String sql = "SELECT COUNT(*) FROM acte WHERE categorie = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des actes par catégorie: " + categorie, e);
        }
    }
}
