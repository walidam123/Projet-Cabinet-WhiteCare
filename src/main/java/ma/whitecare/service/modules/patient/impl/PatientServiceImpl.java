package ma.whitecare.service.modules.patient.impl;

import ma.whitecare.entities.patient.Patient;
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
            throw new RuntimeException("Patient non trouvé");
        }
        return patientRepository.findById(id);
    }

    @Override
    public void createPatient(Patient patient) {
        patientRepository.create(patient);
    }

    @Override
    public void updatePatient(Patient updatedPatient) {
        patientRepository.update(updatedPatient);
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
}
