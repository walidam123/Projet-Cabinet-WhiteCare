package ma.whitecare.service.modules.dossierMedical.impl;

import ma.whitecare.entities.medical.InterventionMedecin;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.Acte;
import ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.actes.api.ActeRepository;
import ma.whitecare.service.modules.dossierMedical.api.InterventionService;

import javax.validation.ValidationException;
import java.util.List;

public class InterventionServiceImpl implements InterventionService {

    private final InterventionRepository interventionRepository;
    private final ConsultationRepository consultationRepository;
    private final ActeRepository acteRepository;

    public InterventionServiceImpl(InterventionRepository interventionRepository,
                                    ConsultationRepository consultationRepository,
                                    ActeRepository acteRepository) {
        this.interventionRepository = interventionRepository;
        this.consultationRepository = consultationRepository;
        this.acteRepository = acteRepository;
    }

    @Override
    public InterventionMedecin createIntervention(InterventionMedecin intervention) {
        // Validation
        validateIntervention(intervention);

        // Vérifier que la consultation existe
        if (intervention.getConsultation() == null || intervention.getConsultation().getIdConsultation() == null) {
            throw new ValidationException("La consultation est requise pour créer une intervention");
        }
        Consultation consultation = consultationRepository.findById(intervention.getConsultation().getIdConsultation());
        if (consultation == null) {
            throw new ValidationException("La consultation avec l'ID " + intervention.getConsultation().getIdConsultation() + " n'existe pas");
        }

        // Vérifier que l'acte existe
        if (intervention.getActe() == null || intervention.getActe().getIdActe() == null) {
            throw new ValidationException("L'acte est requis pour créer une intervention");
        }
        Acte acte = acteRepository.findById(intervention.getActe().getIdActe());
        if (acte == null) {
            throw new ValidationException("L'acte avec l'ID " + intervention.getActe().getIdActe() + " n'existe pas");
        }

        // Valider le prix
        if (intervention.getPrixDePatient() == null || intervention.getPrixDePatient() < 0) {
            throw new ValidationException("Le prix de l'intervention doit être positif ou nul");
        }

        // Valider le numéro de dent si fourni
        if (intervention.getNumDent() != null && (intervention.getNumDent() < 1 || intervention.getNumDent() > 32)) {
            throw new ValidationException("Le numéro de dent doit être entre 1 et 32");
        }

        // Set audit fields
        intervention.setCreePar("system"); // À remplacer par l'utilisateur connecté
        intervention.setModifiePar("system");

        // Créer l'intervention
        interventionRepository.create(intervention);
        return intervention;
    }

    @Override
    public InterventionMedecin updateIntervention(Long interventionId, InterventionMedecin intervention) {
        InterventionMedecin existingIntervention = getInterventionById(interventionId);

        // Validation
        validateIntervention(intervention);

        // Vérifier que la consultation existe si modifiée
        if (intervention.getConsultation() != null && intervention.getConsultation().getIdConsultation() != null) {
            Consultation consultation = consultationRepository.findById(intervention.getConsultation().getIdConsultation());
            if (consultation == null) {
                throw new ValidationException("La consultation avec l'ID " + intervention.getConsultation().getIdConsultation() + " n'existe pas");
            }
        }

        // Vérifier que l'acte existe si modifié
        if (intervention.getActe() != null && intervention.getActe().getIdActe() != null) {
            Acte acte = acteRepository.findById(intervention.getActe().getIdActe());
            if (acte == null) {
                throw new ValidationException("L'acte avec l'ID " + intervention.getActe().getIdActe() + " n'existe pas");
            }
        }

        // Valider le prix si modifié
        if (intervention.getPrixDePatient() != null && intervention.getPrixDePatient() < 0) {
            throw new ValidationException("Le prix de l'intervention doit être positif ou nul");
        }

        // Valider le numéro de dent si modifié
        if (intervention.getNumDent() != null && (intervention.getNumDent() < 1 || intervention.getNumDent() > 32)) {
            throw new ValidationException("Le numéro de dent doit être entre 1 et 32");
        }

        // Mettre à jour les champs
        if (intervention.getPrixDePatient() != null) {
            existingIntervention.setPrixDePatient(intervention.getPrixDePatient());
        }
        if (intervention.getNumDent() != null) {
            existingIntervention.setNumDent(intervention.getNumDent());
        }
        if (intervention.getActe() != null) {
            existingIntervention.setActe(intervention.getActe());
        }
        if (intervention.getConsultation() != null) {
            existingIntervention.setConsultation(intervention.getConsultation());
        }

        // Mettre à jour les champs d'audit
        existingIntervention.setModifiePar("system"); // À remplacer par l'utilisateur connecté

        interventionRepository.update(existingIntervention);
        return existingIntervention;
    }

    @Override
    public void deleteIntervention(Long interventionId) {
        if (!existsById(interventionId)) {
            throw new ValidationException("Intervention non trouvée avec l'ID: " + interventionId);
        }
        interventionRepository.deleteById(interventionId);
    }

    @Override
    public InterventionMedecin getInterventionById(Long interventionId) {
        InterventionMedecin intervention = interventionRepository.findById(interventionId);
        if (intervention == null) {
            throw new ValidationException("Intervention non trouvée avec l'ID: " + interventionId);
        }
        return intervention;
    }

    @Override
    public List<InterventionMedecin> getAllInterventions() {
        return interventionRepository.findAll();
    }

    @Override
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation ne peut pas être null");
        }
        List<InterventionMedecin> allInterventions = getAllInterventions();
        return allInterventions.stream()
                .filter(i -> i.getConsultation() != null && consultationId.equals(i.getConsultation().getIdConsultation()))
                .toList();
    }

    @Override
    public List<InterventionMedecin> findByActeId(Long acteId) {
        if (acteId == null) {
            throw new ValidationException("L'ID de l'acte ne peut pas être null");
        }
        List<InterventionMedecin> allInterventions = getAllInterventions();
        return allInterventions.stream()
                .filter(i -> i.getActe() != null && acteId.equals(i.getActe().getIdActe()))
                .toList();
    }

    @Override
    public List<InterventionMedecin> findByNumDent(Integer numDent) {
        if (numDent == null) {
            throw new ValidationException("Le numéro de dent ne peut pas être null");
        }
        if (numDent < 1 || numDent > 32) {
            throw new ValidationException("Le numéro de dent doit être entre 1 et 32");
        }
        List<InterventionMedecin> allInterventions = getAllInterventions();
        return allInterventions.stream()
                .filter(i -> numDent.equals(i.getNumDent()))
                .toList();
    }

    @Override
    public List<InterventionMedecin> findByConsultationAndActe(Long consultationId, Long acteId) {
        if (consultationId == null || acteId == null) {
            throw new ValidationException("L'ID de la consultation et l'ID de l'acte sont requis");
        }
        List<InterventionMedecin> allInterventions = getAllInterventions();
        return allInterventions.stream()
                .filter(i -> i.getConsultation() != null &&
                        consultationId.equals(i.getConsultation().getIdConsultation()) &&
                        i.getActe() != null &&
                        acteId.equals(i.getActe().getIdActe()))
                .toList();
    }

    @Override
    public Double calculateTotalByConsultation(Long consultationId) {
        if (consultationId == null) {
            throw new ValidationException("L'ID de la consultation ne peut pas être null");
        }
        List<InterventionMedecin> interventions = findByConsultationId(consultationId);
        return interventions.stream()
                .filter(i -> i.getPrixDePatient() != null)
                .mapToDouble(InterventionMedecin::getPrixDePatient)
                .sum();
    }

    @Override
    public boolean existsById(Long interventionId) {
        if (interventionId == null) {
            return false;
        }
        return interventionRepository.findById(interventionId) != null;
    }

    @Override
    public long countAllInterventions() {
        return getAllInterventions().size();
    }

    @Override
    public long countByConsultationId(Long consultationId) {
        return findByConsultationId(consultationId).size();
    }

    @Override
    public long countByActeId(Long acteId) {
        return findByActeId(acteId).size();
    }

    // ========== VALIDATION PRIVÉE ==========
    private void validateIntervention(InterventionMedecin intervention) {
        if (intervention == null) {
            throw new ValidationException("L'intervention ne peut pas être null");
        }
    }
}

