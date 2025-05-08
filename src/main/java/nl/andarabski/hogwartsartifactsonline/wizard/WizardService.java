package nl.andarabski.hogwartsartifactsonline.wizard;

import jakarta.transaction.Transactional;
import nl.andarabski.hogwartsartifactsonline.artifact.utils.IdWorker;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;
    private final IdWorker idWorker;

    public WizardService(WizardRepository wizardRepository, IdWorker idWorker) {
        this.wizardRepository = wizardRepository;
        this.idWorker = idWorker;
    }

    public Wizard findById(Integer wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("Wizard", wizardId));
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard) {
       // newWizard.setId((int) idWorker.nextId());
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard wizard) {
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard -> {
                    oldWizard.setName(wizard.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(() -> new ObjectNotFoundException(null, wizardId));
    }

    public void delete(Integer wizardId) {
        Wizard wizardToBeDeleted =  this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException(null, wizardId));

        // Before deletion, we will unassign this wizard's owned artifacts.
       wizardToBeDeleted.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }
}