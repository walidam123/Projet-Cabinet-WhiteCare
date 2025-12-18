package ma.whitecare.repository.modules.Ordonnance;

import ma.whitecare.conf.ApplicationContext;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.entities.medical.Prescription;
import ma.whitecare.repository.common.RowMappers;
import ma.whitecare.repository.modules.dossierMedical.api.PrescriptionRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {
    
    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM ordonnance ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                out.add(RowMappers.mapOrdonnance(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les ordonnances", e);
        }
        
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE idOrd = ?";
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapOrdonnance(rs);
                }
                return null;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ordonnance par ID: " + id, e);
        }
    }

    @Override
    public void create(Ordonnance newElement) {
        String sql = "INSERT INTO ordonnance (date, consultation_id, dossierMedicale_id, " +
                "created_by, updated_by) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDate(1, Date.valueOf(newElement.getDate()));
            ps.setLong(2, newElement.getConsultation() != null ? 
                    newElement.getConsultation().getIdConsultation() : null);
            ps.setLong(3, newElement.getDossierMedicale() != null ? 
                    newElement.getDossierMedicale().getIdDM() : null);
            ps.setString(4, newElement.getCreePar() != null ? newElement.getCreePar() : "system");
            ps.setString(5, newElement.getModifiePar() != null ? newElement.getModifiePar() : "system");
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setIdOrd(generatedKeys.getLong(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public void update(Ordonnance newValuesElement) {
        String sql = "UPDATE ordonnance SET date = ?, consultation_id = ?, dossierMedicale_id = ?, " +
                "last_modification_date = CURRENT_TIMESTAMP, updated_by = ? WHERE idOrd = ?";
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(newValuesElement.getDate()));
            ps.setObject(2, newValuesElement.getConsultation() != null ? 
                    newValuesElement.getConsultation().getIdConsultation() : null);
            ps.setObject(3, newValuesElement.getDossierMedicale() != null ? 
                    newValuesElement.getDossierMedicale().getIdDM() : null);
            ps.setString(4, newValuesElement.getModifiePar() != null ? 
                    newValuesElement.getModifiePar() : "system");
            ps.setLong(5, newValuesElement.getIdOrd());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'ordonnance", e);
        }
    }

    @Override
    public void delete(Ordonnance oldElement) {
        if (oldElement != null && oldElement.getIdOrd() != null) {
            deleteById(oldElement.getIdOrd());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE idOrd = ?";
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'ordonnance", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM ordonnance WHERE idOrd = ?";
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence de l'ordonnance", e);
        }
    }

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        String sql = "SELECT * FROM ordonnance WHERE dossierMedicale_id = ? ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapOrdonnance(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par dossier médical", e);
        }
        
        return out;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM ordonnance WHERE consultation_id = ? ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapOrdonnance(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par consultation", e);
        }
        
        return out;
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM ordonnance WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        
        try (Connection c = ApplicationContext.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapOrdonnance(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par période", e);
        }
        
        return out;
    }

    @Override
    public List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        // Utiliser PrescriptionRepository pour récupérer les prescriptions
        PrescriptionRepository prescriptionRepository = 
                ApplicationContext.getInstance().getBean(PrescriptionRepository.class);
        
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId);
    }
}
