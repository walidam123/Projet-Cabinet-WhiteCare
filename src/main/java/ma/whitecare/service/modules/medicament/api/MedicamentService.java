package ma.whitecare.service.modules.medicament.api;


import ma.whitecare.entities.medical.Medicament;
import ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.MedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO;

import java.util.List;
import java.util.Optional;

public interface MedicamentService {

    // ========== CRUD MÉDICAMENTS ==========
    Medicament createMedicament(CreateMedicamentDTO medicamentDTO);
    Medicament getMedicamentById(Long id);
    List<Medicament> getAll();
    Medicament updateMedicament(Long id, UpdateMedicamentDTO updateDTO);
    void deleteMedicament(Long id);

    // ========== RECHERCHES ==========
    Optional<Medicament> getByExactName(String nom);
    List<Medicament> getByLaboratoire(String laboratoire);
    List<Medicament> getByType(String type);

    // ========== VALIDATION ==========
    boolean isNomAvailable(String nom);

    // ========== CONVERSIONS ==========
    MedicamentDTO convertToDTO(Medicament medicament);
    List<MedicamentDTO> convertToDTOList(List<Medicament> medicaments);
}

