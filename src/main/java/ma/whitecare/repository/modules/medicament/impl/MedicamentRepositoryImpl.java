package ma.whitecare.repository.modules.medicament.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.enums.FormeMedicament;
import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.medicament.api.MedicamentRepository;

import java.sql.*;
import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    @Override
    public List<Medicament> findAll() {
        return List.of();
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE idMct = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapmedicament(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du médicament par ID: " + id, e);
        }
    }

    @Override
    public void create(Medicament medicament) {
        String sql = """
            INSERT INTO medicament(nom, laboratoire, type, forme, remboursable, prixUnitaire, description,
                                creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, medicament.getNom());
            ps.setString(2, medicament.getLaboratoire());
            ps.setString(3, medicament.getType());

            if (medicament.getForme() != null) {
                ps.setString(4, medicament.getForme().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (medicament.getRemboursable() != null) {
                ps.setBoolean(5, medicament.getRemboursable());
            } else {
                ps.setNull(5, Types.BOOLEAN);
            }

            if (medicament.getPrixUnitaire() != null) {
                ps.setDouble(6, medicament.getPrixUnitaire());
            } else {
                ps.setDouble(6, 0.0);
            }

            ps.setString(7, medicament.getDescription());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(8, now);
            ps.setTimestamp(9, now);
            ps.setString(10, medicament.getCreePar() != null ? medicament.getCreePar() : "system");
            ps.setString(11, medicament.getModifiePar() != null ? medicament.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    medicament.setIdMct(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du médicament", e);
        }
    }

    @Override
    public void update(Medicament newValuesElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void delete(Medicament oldElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void deleteById(Long aLong) {
        // TODO: Implement JDBC logic
    }

    @Override
    public List<Medicament> findByNom(String nom) {
        return List.of();
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        return List.of();
    }

    @Override
    public List<Medicament> findByType(String type) {
        return List.of();
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        return List.of();
    }

    @Override
    public void updatePrix(Long medicamentId, Double nouveauPrix) {
        // TODO: Implement JDBC logic
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }
}

