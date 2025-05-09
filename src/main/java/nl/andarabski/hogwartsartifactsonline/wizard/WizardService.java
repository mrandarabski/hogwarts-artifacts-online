package nl.andarabski.hogwartsartifactsonline.wizard;

import jakarta.transaction.Transactional;
import nl.andarabski.hogwartsartifactsonline.artifact.Artifact;
import nl.andarabski.hogwartsartifactsonline.artifact.ArtifactRepository;
import nl.andarabski.hogwartsartifactsonline.artifact.utils.IdWorker;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;
    //private final IdWorker idWorker;

    public WizardService(WizardRepository wizardRepository,ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
        //this.idWorker = idWorker;
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

    public void assignArtifact(Integer wizardId, String artifactId) {
    // Find this artifact by id from DB
      Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("Artifact", artifactId));
    // Find this wizard by id from DB.
      Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("Wizard", wizardId));

    // Artifact assignment
    // We need to see if the artifact is already owned by some wizard.
      if(artifactToBeAssigned != null) {
          artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
      }
      wizard.addArtifact(artifactToBeAssigned);

    }
}