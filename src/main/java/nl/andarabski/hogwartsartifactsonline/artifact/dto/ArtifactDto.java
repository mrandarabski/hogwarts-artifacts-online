package nl.andarabski.hogwartsartifactsonline.artifact.dto;

import jakarta.validation.constraints.NotEmpty;
import nl.andarabski.hogwartsartifactsonline.wizard.dto.WizardDto;

public record ArtifactDto(String id,
                          @NotEmpty(message = "name is required")
                          String name,
                          @NotEmpty(message = "description is required")
                          String description,
                          @NotEmpty(message = "imageUrl is required")
                          String imageUrl,
                          WizardDto owner) {
}
