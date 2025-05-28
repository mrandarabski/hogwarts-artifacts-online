package nl.andarabski.hogwartsartifactsonline.wizard.dto;

import jakarta.validation.constraints.NotEmpty;

public record WizardDto(Integer id,
                        @NotEmpty(message = "username is required.")
                        String name,
                        Integer numberOfArtifacts) {
}
