package ma.whitecare.repository.modules.ordonnance.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    @Override
    public List<Ordonnance> findAll() {
        return List.of();
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE idOrd = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ordonnance ordonnance = RowMappers.mapOrdonnance(rs);
                    
                    // Charger les relations si nécessaire
                    Long consultationId = rs.getLong("consultation_id");
                    Long dossierId = rs.getLong("dossierMedicale_id");
                    
                    // Créer des objets minimaux pour les relations (si nécessaire)
                    // Pour l'instant, on laisse null car elles ne sont pas toujours nécessaires
                    // Si le service en a besoin, il les chargera séparément
                    
                    return ordonnance;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ordonnance par ID: " + id, e);
        }
    }

    @Override
    public void create(Ordonnance ordonnance) {
        String sql = """
            INSERT INTO ordonnance(date, consultation_id, dossierMedicale_id,
                                creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (ordonnance.getDate() != null) {
                ps.setDate(1, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            }

            if (ordonnance.getConsultation() != null && ordonnance.getConsultation().getIdConsultation() != null) {
                ps.setLong(2, ordonnance.getConsultation().getIdConsultation());
            } else {
                throw new RuntimeException("La consultation est requise pour créer une ordonnance");
            }

            if (ordonnance.getDossierMedicale() != null && ordonnance.getDossierMedicale().getIdDM() != null) {
                ps.setLong(3, ordonnance.getDossierMedicale().getIdDM());
            } else {
                throw new RuntimeException("Le dossier médical est requis pour créer une ordonnance");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            ps.setString(6, ordonnance.getCreePar() != null ? ordonnance.getCreePar() : "system");
            ps.setString(7, ordonnance.getModifiePar() != null ? ordonnance.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    ordonnance.setIdOrd(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public void update(Ordonnance newValuesElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void delete(Ordonnance oldElement) {
        // TODO: Implement JDBC logic
    }

    @Override
    public void deleteById(Long aLong) {
        // TODO: Implement JDBC logic
    }

    @Override
    public List<Ordonnance> findByDossierMedicalId(Long dossierId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return List.of();
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

