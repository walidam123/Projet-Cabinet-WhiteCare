package ma.whitecare.repository.modules.Medicament;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ma.whitecare.repository.common.RowMappers.mapResultSetToMedicament;

public class MedicamentRepositoryImpl implements MedicamentRepository {
    @Override
    public List<Medicament> findAll() {
        String sql = "SELECT * FROM medicament ORDER BY nom";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(mapResultSetToMedicament(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }


    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE idMct = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapResultSetToMedicament(rs);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Medicament newElement) {
        String sql = "INSERT INTO medicament (nom, laboratoire, type, forme, remboursable, " +
                "prixUnitaire, description,created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getLaboratoire());
            ps.setString(3, newElement.getType());
            ps.setString(4, newElement.getForme() != null ? newElement.getForme().name() : null);
            ps.setBoolean(5, Boolean.TRUE.equals(newElement.getRemboursable()));
            ps.setDouble(6, newElement.getPrixUnitaire());
            ps.setString(7, newElement.getDescription());
            ps.setString(8, newElement.getCreePar());
            ps.setString(9, newElement.getModifiePar());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newElement.setIdMct(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Medicament newValuesElement) {
        String sql = "UPDATE medicament SET nom = ?, laboratoire = ?, type = ?, forme = ?, " +
                "remboursable = ?, prixUnitaire = ?, description = ?, updated_by = ?, " +
                "last_modification_date = CURRENT_TIMESTAMP WHERE idMct = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getLaboratoire());
            ps.setString(3, newValuesElement.getType());
            ps.setString(4, newValuesElement.getForme() != null ? newValuesElement.getForme().name() : null);
            ps.setBoolean(5, Boolean.TRUE.equals(newValuesElement.getRemboursable()));
            ps.setDouble(6, newValuesElement.getPrixUnitaire());
            ps.setString(7, newValuesElement.getDescription());
            ps.setString(8, newValuesElement.getModifiePar());
            ps.setLong(9, newValuesElement.getIdMct());

             ps.executeUpdate();



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Medicament oldElement) {
if (oldElement!=null)deleteById(oldElement.getIdMct());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM medicament WHERE idMct = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du médicament", e);
        }
    }

    @Override
    public Optional<Medicament> findByNomExact(String nom) {
        String sql = "SELECT * FROM medicament WHERE LOWER(nom) = LOWER(?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedicament(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par nom exact", e);
        }
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        String sql = "SELECT * FROM medicament WHERE LOWER(laboratoire) = LOWER(?) ORDER BY nom";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, laboratoire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapResultSetToMedicament(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par laboratoire", e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByType(String type) {
        String sql = "SELECT * FROM medicament WHERE LOWER(type) = LOWER(?) ORDER BY nom";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapResultSetToMedicament(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par type", e);
        }
        return out;
    }
}
