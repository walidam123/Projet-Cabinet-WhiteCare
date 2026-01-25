package ma.whitecare.service.modules.facture.impl;

import ma.whitecare.common.exceptions.ConsultationNotFoundException;
import ma.whitecare.common.exceptions.DossierMedicalNotFoundException;
import ma.whitecare.common.util.PDFGenerator;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.medical.DossierMedicale;
import ma.whitecare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.whitecare.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.whitecare.repository.modules.facture.FactureRepository;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.service.modules.facture.api.FactureService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository interventionRepository;

    public FactureServiceImpl(FactureRepository factureRepository,
            ConsultationRepository consultationRepository,
            DossierMedicalRepository dossierMedicalRepository,
            PatientRepository patientRepository,
            ma.whitecare.repository.modules.dossierMedical.api.InterventionRepository interventionRepository) {
        this.factureRepository = factureRepository;
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.patientRepository = patientRepository;
        this.interventionRepository = interventionRepository;
    }

    // ========== CRUD FACTURES ==========

    @Override
    public Facture createFacture(Facture facture) {
        if (facture == null) {
            throw new IllegalArgumentException("La facture ne peut pas être null");
        }
        factureRepository.create(facture);
        return facture;
    }

    @Override
    public Facture getFactureById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la facture est obligatoire");
        }
        Facture facture = factureRepository.findById(id);
        if (facture == null) {
            throw new RuntimeException("Facture non trouvée avec l'ID: " + id);
        }
        return facture;
    }

    @Override
    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    @Override
    public Facture updateFacture(Long id, Facture facture) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la facture est obligatoire");
        }
        if (facture == null) {
            throw new IllegalArgumentException("La facture ne peut pas être null");
        }
        facture.setIdFature(id);
        factureRepository.update(facture);
        return facture;
    }

    @Override
    public void deleteFacture(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la facture est obligatoire");
        }
        factureRepository.deleteById(id);
    }

    // ========== RECHERCHES ==========

    @Override
    public boolean existsById(Long id) {
        return factureRepository.existsById(id);
    }

    @Override
    public List<Facture> findBySituationFinanciereId(Long situationFinanciereId) {
        if (situationFinanciereId == null) {
            throw new IllegalArgumentException("L'ID de la situation financière est obligatoire");
        }
        return factureRepository.findBySituationFinanciereId(situationFinanciereId);
    }

    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            throw new IllegalArgumentException("L'ID de la consultation est obligatoire");
        }
        return factureRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return factureRepository.findByStatut(statut);
    }

    @Override
    public List<Facture> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début ne peut pas être après la date de fin");
        }
        return factureRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<Facture> findBySituationFinanciereIdAndStatut(Long situationFinanciereId, StatutFacture statut) {
        if (situationFinanciereId == null) {
            throw new IllegalArgumentException("L'ID de la situation financière est obligatoire");
        }
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return factureRepository.findBySituationFinanciereIdAndStatut(situationFinanciereId, statut);
    }

    // ========== STATISTIQUES ==========

    @Override
    public long countAll() {
        return factureRepository.countAll();
    }

    @Override
    public long countByStatut(StatutFacture statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        return factureRepository.countByStatut(statut);
    }

    @Override
    public long countBySituationFinanciereId(Long situationFinanciereId) {
        if (situationFinanciereId == null) {
            throw new IllegalArgumentException("L'ID de la situation financière est obligatoire");
        }
        return factureRepository.countBySituationFinanciereId(situationFinanciereId);
    }

    // ========== GESTION DES STATUTS ==========

    @Override
    public void updateStatut(Long id, StatutFacture statut) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la facture est obligatoire");
        }
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
        factureRepository.updateStatut(id, statut);
    }

    // ========== GÉNÉRATION PDF ==========

    @Override
    public byte[] generatePDF(Long factureId) throws java.io.IOException {
        // Récupérer la facture
        Facture facture = getFactureById(factureId);

        // Récupérer le dossier médical via la consultation ou la situation financière
        DossierMedicale dossier = null;
        if (facture.getConsultation() != null && facture.getConsultation().getIdConsultation() != null) {
            Consultation consultation = consultationRepository.findById(
                    facture.getConsultation().getIdConsultation());
            if (consultation != null && consultation.getDossierMedicale() != null &&
                    consultation.getDossierMedicale().getIdDM() != null) {
                dossier = dossierMedicalRepository.findById(consultation.getDossierMedicale().getIdDM());
            }
        } else if (facture.getSituationFinanciere() != null &&
                facture.getSituationFinanciere().getDossierMedicale() != null &&
                facture.getSituationFinanciere().getDossierMedicale().getIdDM() != null) {
            dossier = dossierMedicalRepository.findById(
                    facture.getSituationFinanciere().getDossierMedicale().getIdDM());
        }

        if (dossier == null || dossier.getPatient() == null) {
            throw new IllegalArgumentException("Impossible de générer le PDF : informations du patient manquantes");
        }

        // Récupérer le patient complet
        ma.whitecare.entities.patient.Patient patient = patientRepository.findById(
                dossier.getPatient().getId_Patient());
        if (patient == null) {
            throw new IllegalArgumentException("Patient non trouvé");
        }

        // Préparer les informations
        String factureIdStr = facture.getIdFature() != null ? facture.getIdFature().toString() : "N/A";
        String dateFacture = facture.getDateFacture() != null
                ? facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "Non spécifiée";
        String statut = facture.getStatut() != null ? facture.getStatut().name() : "N/A";

        // Récupérer les interventions si la facture est liée à une consultation
        java.util.List<PDFGenerator.InterventionInfo> interventions = new java.util.ArrayList<>();
        if (facture.getConsultation() != null && facture.getConsultation().getIdConsultation() != null) {
            java.util.List<ma.whitecare.entities.medical.InterventionMedecin> interventionsList = interventionRepository
                    .findByConsultationId(facture.getConsultation().getIdConsultation());

            for (ma.whitecare.entities.medical.InterventionMedecin intervention : interventionsList) {
                String acteNom = intervention.getActe() != null && intervention.getActe().getLibelle() != null
                        ? intervention.getActe().getLibelle()
                        : "Acte non spécifié";
                String dent = intervention.getNumDent() != null ? intervention.getNumDent().toString() : null;

                // Use prixDePatient which is the actual price charged for this intervention
                Double prix = 0.0;
                if (intervention.getPrixDePatient() != null) {
                    prix = intervention.getPrixDePatient();
                } else if (intervention.getActe() != null && intervention.getActe().getPrixDeBase() != null) {
                    // Fallback to base price if patient price not set
                    prix = intervention.getActe().getPrixDeBase();
                }

                interventions.add(new PDFGenerator.InterventionInfo(acteNom, dent, prix));
            }
        }

        // Générer le PDF
        return PDFGenerator.generateFacturePDF(
                patient.getNom() != null ? patient.getNom() : "",
                patient.getPrenom() != null ? patient.getPrenom() : "",
                patient.getAdresse() != null ? patient.getAdresse() : "",
                patient.getTelephone() != null ? patient.getTelephone() : "",
                factureIdStr,
                dateFacture,
                interventions,
                facture.getTotaleFacture(),
                facture.getTotalePayé(),
                facture.getReste(),
                statut);
    }
}
