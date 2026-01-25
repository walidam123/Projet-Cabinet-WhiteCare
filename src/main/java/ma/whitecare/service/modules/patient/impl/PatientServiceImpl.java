package ma.whitecare.service.modules.patient.impl;

import ma.whitecare.common.exceptions.PatientNotFoundException;
import ma.whitecare.common.validators.PatientValidator;
import ma.whitecare.entities.patient.Patient;
import ma.whitecare.entities.patient.Antecedents;
import ma.whitecare.mvc.dto.PatientAntecedentDto.PatientDto;
import ma.whitecare.repository.modules.patient.api.PatientRepository;
import ma.whitecare.service.modules.patient.api.PatientService;

import java.time.LocalDate;
import java.util.List;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findPage(Integer.MAX_VALUE, 0);
    }

    @Override
    public Patient getPatientById(Long id) {
        if (id == null || !patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        Patient p = patientRepository.findById(id);
        if (p != null) {
            p.setAntecedents(patientRepository.getAntecedentsOfPatient(id));
        }
        return p;
    }

    @Override
    public void createPatient(Patient patient) {
        PatientDto dto = convertToDto(patient);
        PatientValidator.validate(dto);
        patientRepository.create(patient);
        if (patient.getAntecedents() != null && !patient.getAntecedents().isEmpty()) {
            for (Antecedents a : patient.getAntecedents()) {
                patientRepository.addAntecedentToPatient(patient.getId_Patient(), a.getId_Antecedent());
            }
        }
    }

    @Override
    public void updatePatient(Patient updatedPatient) {
        PatientDto dto = convertToDto(updatedPatient);
        PatientValidator.validate(dto);
        patientRepository.update(updatedPatient);
        // Manage Antecedents: Clear and Re-add
        patientRepository.removeAllAntecedentsFromPatient(updatedPatient.getId_Patient());
        if (updatedPatient.getAntecedents() != null && !updatedPatient.getAntecedents().isEmpty()) {
            for (Antecedents a : updatedPatient.getAntecedents()) {
                patientRepository.addAntecedentToPatient(updatedPatient.getId_Patient(), a.getId_Antecedent());
            }
        }
    }

    @Override
    public void deletePatient(Patient patient) {
        patientRepository.delete(patient);
    }

    @Override
    public void deletePatientById(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public List<Patient> findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    public List<Patient> findByTelephone(String telephone) {
        return patientRepository.findByTelephone(telephone);
    }

    @Override
    public List<Patient> findByAssurance(String assurance) {
        return patientRepository.findByAssurance(assurance);
    }

    @Override
    public List<Patient> findBySexe(String sexe) {
        return patientRepository.findBySexe(sexe);
    }

    @Override
    public List<Patient> findByDateNaissanceBetween(LocalDate start, LocalDate end) {
        return patientRepository.findByDateNaissanceBetween(start, end);
    }

    // ✅ Méthode utilitaire pour convertir un Patient en DTO
    private PatientDto convertToDto(Patient p) {
        PatientDto dto = new PatientDto();
        dto.setNom(p.getNom());
        dto.setPrenom(p.getPrenom());
        dto.setDateNaissance(p.getDateNaissance());
        dto.setSexe(p.getSexe());
        dto.setAdresse(p.getAdresse());
        dto.setTelephone(p.getTelephone());
        dto.setEmail(p.getEmail());
        dto.setAssurance(p.getAssurance());
        dto.setCreePar(p.getCreePar());
        dto.setModifiePar(p.getModifiePar());
        return dto;
    }
}