package ma.whitecare.repository.modules.ordonnance.impl;

import ma.whitecare.conf.SessionFactory;
import ma.whitecare.entities.medical.Ordonnance;
import ma.whitecare.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.whitecare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {
    
    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM ordonnance";
        List<Ordonnance> ordonnances = new ArrayList<>();
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ordonnances.add(mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les ordonnances", e);
        }
        
        return ordonnances;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE idOrd = ?";
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrdonnance(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'ordonnance ID: " + id, e);
        }
        
        return null;
    }

    @Override
    public void create(Ordonnance ordonnance) {
        String sql = """
            INSERT INTO ordonnance (date, consultation_id, dossierMedicale_id,
                                 creation_date, last_modification_date, created_by, updated_by)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (ordonnance.getDate() != null) {
                ps.setDate(1, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
            }
            
            ps.setLong(2, ordonnance.getConsultation().getIdConsultation());
            ps.setLong(3, ordonnance.getDossierMedicale().getIdDM());
            
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
    public void update(Ordonnance ordonnance) {
        String sql = """
            UPDATE ordonnance 
            SET date = ?, consultation_id = ?, dossierMedicale_id = ?,
                last_modification_date = ?, updated_by = ?
            WHERE idOrd = ?
            """;
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            if (ordonnance.getDate() != null) {
                ps.setDate(1, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
            }
            
            ps.setLong(2, ordonnance.getConsultation().getIdConsultation());
            ps.setLong(3, ordonnance.getDossierMedicale().getIdDM());
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, now);
            ps.setString(5, ordonnance.getModifiePar() != null ? ordonnance.getModifiePar() : "system");
            ps.setLong(6, ordonnance.getIdOrd());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'ordonnance ID: " + ordonnance.getIdOrd(), e);
        }
    }

    @Override
    public void delete(Ordonnance ordonnance) {
        deleteById(ordonnance.getIdOrd());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE idOrd = ?";
        
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'ordonnance ID: " + id, e);
        }
    }
    
    private Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setIdOrd(rs.getLong("idOrd"));
        
        Date date = rs.getDate("date");
        if (date != null) {
            ordonnance.setDate(date.toLocalDate());
        }
        
        // Note: consultation and dossierMedicale are not loaded here to avoid circular dependencies
        // They should be loaded separately if needed
        
        Timestamp creationDate = rs.getTimestamp("creation_date");
        if (creationDate != null) {
            ordonnance.setDateCreation(creationDate.toLocalDateTime().toLocalDate());
        }
        
        Timestamp lastModDate = rs.getTimestamp("last_modification_date");
        if (lastModDate != null) {
            ordonnance.setDateDerniereModification(lastModDate.toLocalDateTime().toLocalDate());
        }
        
        ordonnance.setCreePar(rs.getString("created_by"));
        ordonnance.setModifiePar(rs.getString("updated_by"));
        
        return ordonnance;
    }
}

