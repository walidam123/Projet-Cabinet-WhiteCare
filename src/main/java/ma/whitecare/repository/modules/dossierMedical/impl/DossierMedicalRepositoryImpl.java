package ma.whitecare.repository.modules.dossierMedical.impl;



import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {

    @Override
    public List<DossierMedicale> findAll() {
        String sql = "SELECT * FROM dossierMedicale ORDER BY idDM";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapDossierMedicale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de tous les dossiers médicaux", e);
        }
        return out;
    }

    @Override
    public DossierMedicale findById(Long id) {
        String sql = "SELECT * FROM dossierMedicale WHERE idDM = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapDossierMedicale(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du dossier médical par ID: " + id, e);
        }
    }

    @Override
    public void create(DossierMedicale dossier) {
        String sql = """
            INSERT INTO dossierMedicale(dateDecreation, patient_id, medecin_id,
                                        creation_date, last_modification_date, created_by, updated_by)
            VALUES(?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (dossier.getDateDeCreation() != null) {
                ps.setDate(1, Date.valueOf(dossier.getDateDeCreation()));
            } else {
                ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            }

            if (dossier.getPatient() != null && dossier.getPatient().getId_Patient() != null) {
                ps.setLong(2, dossier.getPatient().getId_Patient());
            } else {
                throw new RuntimeException("Le patient est requis pour créer un dossier médical");
            }

            if (dossier.getMedecin() != null && dossier.getMedecin().getIdUser() != null) {
                ps.setLong(3, dossier.getMedecin().getIdUser());
            } else {
                throw new RuntimeException("Le médecin est requis pour créer un dossier médical");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            ps.setString(6, dossier.getCreePar() != null ? dossier.getCreePar() : "system");
            ps.setString(7, dossier.getModifiePar() != null ? dossier.getModifiePar() : "system");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    dossier.setIdDM(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du dossier médical", e);
        }
    }

    @Override
    public void update(DossierMedicale dossier) {
        String sql = """
            UPDATE dossierMedicale 
            SET dateDecreation=?, patient_id=?, medecin_id=?,
                last_modification_date=?, updated_by=?
            WHERE idDM=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (dossier.getDateDeCreation() != null) {
                ps.setDate(1, Date.valueOf(dossier.getDateDeCreation()));
            } else {
                ps.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            }

            if (dossier.getPatient() != null && dossier.getPatient().getId_Patient() != null) {
                ps.setLong(2, dossier.getPatient().getId_Patient());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            if (dossier.getMedecin() != null && dossier.getMedecin().getIdUser() != null) {
                ps.setLong(3, dossier.getMedecin().getIdUser());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, dossier.getModifiePar() != null ? dossier.getModifiePar() : "system");
            ps.setLong(6, dossier.getIdDM());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du dossier médical ID: " + dossier.getIdDM(), e);
        }
    }

    @Override
    public void delete(DossierMedicale dossier) {
        if (dossier != null) {
            deleteById(dossier.getIdDM());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM dossierMedicale WHERE idDM = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du dossier médical ID: " + id, e);
        }
    }

    @Override
    public List<DossierMedicale> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM dossierMedicale WHERE patient_id = ? ORDER BY dateDecreation DESC, idDM";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapDossierMedicale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par patient ID: " + patientId, e);
        }
        return out;
    }

    @Override
    public List<DossierMedicale> findByMedecinId(Long medecinId) {
        String sql = "SELECT * FROM dossierMedicale WHERE medecin_id = ? ORDER BY dateDecreation DESC, idDM";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapDossierMedicale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par médecin ID: " + medecinId, e);
        }
        return out;
    }

    @Override
    public List<DossierMedicale> findByDateCreation(LocalDate date) {
        String sql = "SELECT * FROM dossierMedicale WHERE dateDecreation = ? ORDER BY idDM";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapDossierMedicale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par date de création: " + date, e);
        }
        return out;
    }

    @Override
    public List<DossierMedicale> findByDateCreationBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM dossierMedicale WHERE dateDecreation >= ? AND dateDecreation <= ? ORDER BY dateDecreation DESC, idDM";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapDossierMedicale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche entre les dates: " + startDate + " et " + endDate, e);
        }
        return out;
    }

    @Override
    public boolean existsByPatientId(Long patientId) {
        String sql = "SELECT 1 FROM dossierMedicale WHERE patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par patient ID: " + patientId, e);
        }
    }

    @Override
    public boolean existsById(Long dossierId) {
        String sql = "SELECT 1 FROM dossierMedicale WHERE idDM = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence par ID: " + dossierId, e);
        }
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM dossierMedicale";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage de tous les dossiers médicaux", e);
        }
    }

    @Override
    public long countByPatientId(Long patientId) {
        String sql = "SELECT COUNT(*) FROM dossierMedicale WHERE patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par patient ID: " + patientId, e);
        }
    }

    @Override
    public long countByMedecinId(Long medecinId) {
        String sql = "SELECT COUNT(*) FROM dossierMedicale WHERE medecin_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage par médecin ID: " + medecinId, e);
        }
    }
}