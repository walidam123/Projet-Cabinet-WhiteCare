package ma.whitecare.repository.modules.rdv.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.rdv.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVRepositoryImpl implements RDVRepository {

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM rdv ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les RDV", e);
        }
        return out;
    }

    @Override
    public RDV findById(Long id) {
        String sql = "SELECT * FROM rdv WHERE id_rdv = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRDV(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du RDV par ID: " + id, e);
        }
    }

    @Override
    public void create(RDV newElement) {
        String sql = "INSERT INTO rdv (date, heure, motif, statut, note_medecin, " +
                "consultation_id, dossier_medicale_id, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDate()));
            ps.setTime(2, Time.valueOf(newElement.getHeure()));
            ps.setString(3, newElement.getMotif());
            ps.setString(4, newElement.getStatut() != null ? newElement.getStatut().name() : StatutRendezVous.PLANIFIE.name());
            ps.setString(5, newElement.getNoteMedecin());
            ps.setLong(6, newElement.getConsultation().getIdConsultation());
            ps.setObject(7, newElement.getDossierMedicale() != null ?
                    newElement.getDossierMedicale().getIdDM() : null, Types.BIGINT);
            ps.setString(8, newElement.getCreePar() != null ? newElement.getCreePar() : "system");
            ps.setString(9, newElement.getModifiePar() != null ? newElement.getModifiePar() : "system");

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setIdRDV(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du RDV", e);
        }
    }

    @Override
    public void update(RDV newValuesElement) {
        String sql = "UPDATE rdv SET date = ?, heure = ?, motif = ?, statut = ?, " +
                "note_medecin = ?, consultation_id = ?, dossier_medicale_id = ?, " +
                "updated_by = ?, last_modification_date = CURRENT_TIMESTAMP WHERE id_rdv = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDate()));
            ps.setTime(2, Time.valueOf(newValuesElement.getHeure()));
            ps.setString(3, newValuesElement.getMotif());
            ps.setString(4, newValuesElement.getStatut().name());
            ps.setString(5, newValuesElement.getNoteMedecin());
            ps.setLong(6, newValuesElement.getConsultation().getIdConsultation());
            ps.setObject(7, newValuesElement.getDossierMedicale() != null ?
                    newValuesElement.getDossierMedicale().getIdDM() : null, Types.BIGINT);
            ps.setString(8, newValuesElement.getModifiePar() != null ? newValuesElement.getModifiePar() : "system");
            ps.setLong(9, newValuesElement.getIdRDV());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du RDV", e);
        }
    }

    @Override
    public void delete(RDV oldElement) {
        if (oldElement != null && oldElement.getIdRDV() != null) {
            deleteById(oldElement.getIdRDV());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rdv WHERE id_rdv = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du RDV", e);
        }
    }

    @Override
    public List<RDV> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM rdv WHERE consultation_id = ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par consultation ID: " + consultationId, e);
        }
        return out;
    }

    @Override
    public Optional<RDV> findFirstByConsultationId(Long consultationId) {
        List<RDV> rdvs = findByConsultationId(consultationId);
        return rdvs.isEmpty() ? Optional.empty() : Optional.of(rdvs.get(0));
    }

    @Override
    public List<RDV> findByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = "SELECT * FROM rdv WHERE dossier_medicale_id = ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical ID: " + dossierMedicaleId, e);
        }
        return out;
    }

    @Override
    public List<RDV> findByStatut(StatutRendezVous statut) {
        String sql = "SELECT * FROM rdv WHERE statut = ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par statut: " + statut, e);
        }
        return out;
    }

    @Override
    public List<RDV> findByStatutAndDate(StatutRendezVous statut, LocalDate date) {
        String sql = "SELECT * FROM rdv WHERE statut = ? AND date = ? ORDER BY heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par statut et date", e);
        }
        return out;
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        String sql = "SELECT * FROM rdv WHERE date = ? ORDER BY heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par date: " + date, e);
        }
        return out;
    }

    @Override
    public List<RDV> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM rdv WHERE date BETWEEN ? AND ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par plage de dates", e);
        }
        return out;
    }

    @Override
    public List<RDV> findByDateAndHeure(LocalDate date, LocalTime heure) {
        String sql = "SELECT * FROM rdv WHERE date = ? AND heure = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(heure));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par date et heure", e);
        }
        return out;
    }

    @Override
    public List<RDV> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutRendezVous statut) {
        String sql = "SELECT * FROM rdv WHERE dossier_medicale_id = ? AND statut = ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            ps.setString(2, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical et statut", e);
        }
        return out;
    }

    @Override
    public List<RDV> findByConsultationIdAndStatut(Long consultationId, StatutRendezVous statut) {
        String sql = "SELECT * FROM rdv WHERE consultation_id = ? AND statut = ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            ps.setString(2, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par consultation et statut", e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM rdv WHERE id_rdv = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par ID: " + id, e);
        }
        return false;
    }

    @Override
    public boolean existsByConsultationId(Long consultationId) {
        String sql = "SELECT COUNT(*) FROM rdv WHERE consultation_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par consultation ID: " + consultationId, e);
        }
        return false;
    }

    @Override
    public boolean existsByDateAndHeure(LocalDate date, LocalTime heure) {
        String sql = "SELECT COUNT(*) FROM rdv WHERE date = ? AND heure = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(heure));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par date et heure", e);
        }
        return false;
    }

    @Override
    public long countByStatut(StatutRendezVous statut) {
        String sql = "SELECT COUNT(*) FROM rdv WHERE statut = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par statut: " + statut, e);
        }
        return 0;
    }

    @Override
    public long countByDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM rdv WHERE date = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par date: " + date, e);
        }
        return 0;
    }
}
