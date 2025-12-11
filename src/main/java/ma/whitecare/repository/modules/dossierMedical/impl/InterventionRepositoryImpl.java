package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterventionRepositoryImpl implements InterventionRepository {
    
    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM intervention_medecin ORDER BY id_im";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de toutes les interventions", e);
        }
        return out;
    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM intervention_medecin WHERE id_im = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapInterventionMedecin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'intervention par ID: " + id, e);
        }
    }

    @Override
    public void create(InterventionMedecin intervention) {
        String sql = """
            INSERT INTO intervention_medecin(prix_de_patient, num_dent, consultation_id, acte_id,
                                             creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (intervention.getPrixDePatient() != null) {
                ps.setDouble(1, intervention.getPrixDePatient());
            } else {
                throw new RuntimeException("Le prix de l'intervention est requis");
            }

            if (intervention.getNumDent() != null) {
                ps.setInt(2, intervention.getNumDent());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (intervention.getConsultation() != null && intervention.getConsultation().getIdConsultation() != null) {
                ps.setLong(3, intervention.getConsultation().getIdConsultation());
            } else {
                throw new RuntimeException("La consultation est requise pour créer une intervention");
            }

            if (intervention.getActe() != null && intervention.getActe().getIdActe() != null) {
                ps.setLong(4, intervention.getActe().getIdActe());
            } else {
                throw new RuntimeException("L'acte est requis pour créer une intervention");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);
            ps.setString(7, intervention.getCreePar() != null ? intervention.getCreePar() : "system");
            ps.setString(8, intervention.getModifiePar() != null ? intervention.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    intervention.setIdIM(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'intervention", e);
        }
    }

    @Override
    public void update(InterventionMedecin intervention) {
        String sql = """
            UPDATE intervention_medecin 
            SET prix_de_patient=?, num_dent=?, consultation_id=?, acte_id=?,
                last_modification_date=?, updated_by=?
            WHERE id_im=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (intervention.getPrixDePatient() != null) {
                ps.setDouble(1, intervention.getPrixDePatient());
            } else {
                ps.setNull(1, Types.DOUBLE);
            }

            if (intervention.getNumDent() != null) {
                ps.setInt(2, intervention.getNumDent());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (intervention.getConsultation() != null && intervention.getConsultation().getIdConsultation() != null) {
                ps.setLong(3, intervention.getConsultation().getIdConsultation());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            if (intervention.getActe() != null && intervention.getActe().getIdActe() != null) {
                ps.setLong(4, intervention.getActe().getIdActe());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setString(6, intervention.getModifiePar() != null ? intervention.getModifiePar() : "system");
            ps.setLong(7, intervention.getIdIM());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'intervention ID: " + intervention.getIdIM(), e);
        }
    }

    @Override
    public void delete(InterventionMedecin intervention) {
        if (intervention != null) {
            deleteById(intervention.getIdIM());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM intervention_medecin WHERE id_im = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'intervention ID: " + id, e);
        }
    }
}
