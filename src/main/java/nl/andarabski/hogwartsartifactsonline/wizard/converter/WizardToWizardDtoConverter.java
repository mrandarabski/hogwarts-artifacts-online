package nl.andarabski.hogwartsartifactsonline.wizard.converter;

import nl.andarabski.hogwartsartifactsonline.artifact.Artifact;
import nl.andarabski.hogwartsartifactsonline.wizard.Wizard;
import nl.andarabski.hogwartsartifactsonline.wizard.WizardRepository;
import nl.andarabski.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {

    private final WizardRepository wizardRepository;

    public WizardToWizardDtoConverter(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
    }

    @Override
    public WizardDto convert(Wizard source) {
        WizardDto wizardDto = new WizardDto(source.getId(),
                                            source.getName(),
                                            source.getNumberOfArtifacts());
        return wizardDto;
    }
}
