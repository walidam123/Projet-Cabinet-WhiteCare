package ma.whitecare.repository.modules.dossierMedical.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {
    
    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM consultation ORDER BY id_consultation";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de toutes les consultations", e);
        }
        return out;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM consultation WHERE id_consultation = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapConsultation(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la consultation par ID: " + id, e);
        }
    }

    @Override
    public void create(Consultation consultation) {
        String sql = """
            INSERT INTO consultation(date, statut, observation_medecin, dossier_medicale_id,
                                    creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (consultation.getDate() != null) {
                ps.setDate(1, Date.valueOf(consultation.getDate()));
            } else {
                ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            }

            if (consultation.getStatut() != null) {
                ps.setString(2, consultation.getStatut().name());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setString(3, consultation.getObservationMedecin());

            if (consultation.getDossierMedicale() != null && consultation.getDossierMedicale().getIdDM() != null) {
                ps.setLong(4, consultation.getDossierMedicale().getIdDM());
            } else {
                throw new RuntimeException("Le dossier médical est requis pour créer une consultation");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);
            ps.setString(7, consultation.getCreePar() != null ? consultation.getCreePar() : "system");
            ps.setString(8, consultation.getModifiePar() != null ? consultation.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    consultation.setIdConsultation(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la consultation", e);
        }
    }

    @Override
    public void update(Consultation consultation) {
        String sql = """
            UPDATE consultation 
            SET date=?, statut=?, observation_medecin=?, dossier_medicale_id=?,
                last_modification_date=?, updated_by=?
            WHERE id_consultation=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (consultation.getDate() != null) {
                ps.setDate(1, Date.valueOf(consultation.getDate()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            if (consultation.getStatut() != null) {
                ps.setString(2, consultation.getStatut().name());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setString(3, consultation.getObservationMedecin());

            if (consultation.getDossierMedicale() != null && consultation.getDossierMedicale().getIdDM() != null) {
                ps.setLong(4, consultation.getDossierMedicale().getIdDM());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setString(6, consultation.getModifiePar() != null ? consultation.getModifiePar() : "system");
            ps.setLong(7, consultation.getIdConsultation());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la consultation ID: " + consultation.getIdConsultation(), e);
        }
    }

    @Override
    public void delete(Consultation consultation) {
        if (consultation != null) {
            deleteById(consultation.getIdConsultation());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM consultation WHERE id_consultation = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la consultation ID: " + id, e);
        }
    }
}
