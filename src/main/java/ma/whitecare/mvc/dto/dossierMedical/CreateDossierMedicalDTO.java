package ma.whitecare.mvc.dto.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDossierMedicalDTO {

    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;

    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;

    private LocalDate dateDeCreation;
}

