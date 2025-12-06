package ma.whitecare.service.modules.dossierMedical;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.actes.api.ActeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des interventions médicales.
 * Gère la création, modification et consultation des interventions réalisées
 * lors des consultations avec validation des prix et vérification des actes.
 */
public class InterventionService {

    private final InterventionRepository interventionRepository;
    private final ConsultationRepository consultationRepository;
    private final ActeRepository acteRepository;

    public InterventionService(InterventionRepository interventionRepository,
                               ConsultationRepository consultationRepository,
                               ActeRepository acteRepository) {
        this.interventionRepository = interventionRepository;
        this.consultationRepository = consultationRepository;
        this.acteRepository = acteRepository;
    }

    /**
     * Récupère toutes les interventions.
     */
    public List<InterventionMedecin> findAll() {
        return interventionRepository.findAll();
    }

    /**
     * Récupère une intervention par son ID avec vérification d'existence.
     */
    public InterventionMedecin findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'intervention doit être valide");
        }
        InterventionMedecin intervention = interventionRepository.findById(id);
        if (intervention == null) {
            throw new IllegalArgumentException("Intervention introuvable avec l'ID: " + id);
        }
        return intervention;
    }

    /**
     * Crée une nouvelle intervention avec validation complète.
     */
    public InterventionMedecin create(InterventionMedecin intervention) {
        validateIntervention(intervention);
        
        // Vérifier que la consultation existe
        if (intervention.getConsultation() == null || intervention.getConsultation().getIdConsultation() == null) {
            throw new IllegalArgumentException("La consultation est obligatoire pour créer une intervention");
        }
        Consultation consultation = consultationRepository.findById(intervention.getConsultation().getIdConsultation());
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation introuvable avec l'ID: " + intervention.getConsultation().getIdConsultation());
        }

        // Vérifier que l'acte existe
        if (intervention.getActe() == null || intervention.getActe().getIdActe() == null) {
            throw new IllegalArgumentException("L'acte est obligatoire pour créer une intervention");
        }
        Acte acte = acteRepository.findById(intervention.getActe().getIdActe());
        if (acte == null) {
            throw new IllegalArgumentException("Acte introuvable avec l'ID: " + intervention.getActe().getIdActe());
        }

        // Valider le prix
        if (intervention.getPrixDePatient() == null || intervention.getPrixDePatient() < 0) {
            throw new IllegalArgumentException("Le prix de l'intervention doit être positif");
        }

        // Valider le numéro de dent si fourni (doit être entre 1 et 32 pour les dents permanentes)
        if (intervention.getNumDent() != null) {
            if (intervention.getNumDent() < 1 || intervention.getNumDent() > 32) {
                throw new IllegalArgumentException("Le numéro de dent doit être entre 1 et 32");
            }
        }

        interventionRepository.create(intervention);
        return intervention;
    }

    /**
     * Met à jour une intervention existante avec validation.
     */
    public InterventionMedecin update(InterventionMedecin intervention) {
        if (intervention == null || intervention.getIdIM() == null) {
            throw new IllegalArgumentException("L'intervention et son ID sont obligatoires");
        }

        // Vérifier que l'intervention existe
        InterventionMedecin existing = interventionRepository.findById(intervention.getIdIM());
        if (existing == null) {
            throw new IllegalArgumentException("Intervention introuvable avec l'ID: " + intervention.getIdIM());
        }

        validateIntervention(intervention);
        
        // Vérifier la consultation si modifiée
        if (intervention.getConsultation() != null && intervention.getConsultation().getIdConsultation() != null) {
            Consultation consultation = consultationRepository.findById(intervention.getConsultation().getIdConsultation());
            if (consultation == null) {
                throw new IllegalArgumentException("Consultation introuvable avec l'ID: " + intervention.getConsultation().getIdConsultation());
            }
        }

        // Vérifier l'acte si modifié
        if (intervention.getActe() != null && intervention.getActe().getIdActe() != null) {
            Acte acte = acteRepository.findById(intervention.getActe().getIdActe());
            if (acte == null) {
                throw new IllegalArgumentException("Acte introuvable avec l'ID: " + intervention.getActe().getIdActe());
            }
        }

        interventionRepository.update(intervention);
        return intervention;
    }

    /**
     * Supprime une intervention avec vérification d'existence.
     */
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'intervention doit être valide");
        }
        
        InterventionMedecin intervention = interventionRepository.findById(id);
        if (intervention == null) {
            throw new IllegalArgumentException("Intervention introuvable avec l'ID: " + id);
        }

        interventionRepository.deleteById(id);
    }

    /**
     * Supprime une intervention.
     */
    public void delete(InterventionMedecin intervention) {
        if (intervention == null || intervention.getIdIM() == null) {
            throw new IllegalArgumentException("L'intervention est obligatoire");
        }
        delete(intervention.getIdIM());
    }

    /**
     * Trouve toutes les interventions d'une consultation.
     */
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        if (consultationId == null || consultationId <= 0) {
            throw new IllegalArgumentException("L'ID de la consultation doit être valide");
        }
        
        Consultation consultation = consultationRepository.findById(consultationId);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation introuvable avec l'ID: " + consultationId);
        }

        return interventionRepository.findAll().stream()
                .filter(i -> i.getConsultation() != null && consultationId.equals(i.getConsultation().getIdConsultation()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les interventions d'un acte.
     */
    public List<InterventionMedecin> findByActeId(Long acteId) {
        if (acteId == null || acteId <= 0) {
            throw new IllegalArgumentException("L'ID de l'acte doit être valide");
        }
        
        Acte acte = acteRepository.findById(acteId);
        if (acte == null) {
            throw new IllegalArgumentException("Acte introuvable avec l'ID: " + acteId);
        }

        return interventionRepository.findAll().stream()
                .filter(i -> i.getActe() != null && acteId.equals(i.getActe().getIdActe()))
                .collect(Collectors.toList());
    }

    /**
     * Trouve toutes les interventions pour une dent spécifique.
     */
    public List<InterventionMedecin> findByNumDent(Integer numDent) {
        if (numDent == null || numDent < 1 || numDent > 32) {
            throw new IllegalArgumentException("Le numéro de dent doit être entre 1 et 32");
        }

        return interventionRepository.findAll().stream()
                .filter(i -> numDent.equals(i.getNumDent()))
                .collect(Collectors.toList());
    }

    /**
     * Calcule le total des interventions d'une consultation.
     */
    public Double calculateTotalByConsultation(Long consultationId) {
        List<InterventionMedecin> interventions = findByConsultationId(consultationId);
        return interventions.stream()
                .filter(i -> i.getPrixDePatient() != null)
                .mapToDouble(InterventionMedecin::getPrixDePatient)
                .sum();
    }

    /**
     * Compte le nombre d'interventions.
     */
    public long count() {
        return interventionRepository.findAll().size();
    }

    /**
     * Compte le nombre d'interventions par acte.
     */
    public long countByActeId(Long acteId) {
        return findByActeId(acteId).size();
    }

    /**
     * Valide les données d'une intervention.
     */
    private void validateIntervention(InterventionMedecin intervention) {
        if (intervention == null) {
            throw new IllegalArgumentException("L'intervention ne peut pas être null");
        }
        
        if (intervention.getPrixDePatient() != null && intervention.getPrixDePatient() < 0) {
            throw new IllegalArgumentException("Le prix de l'intervention ne peut pas être négatif");
        }
    }
}

