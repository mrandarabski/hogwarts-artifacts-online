package nl.andarabski.hogwartsartifactsonline.artifact;

import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import nl.andarabski.hogwartsartifactsonline.artifact.utils.IdWorker;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("Artifact", artifactId));
    }

    @Timed("findAllArtifactsService.time")
    public List<Artifact> findAll() {

        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update) {
       return this.artifactRepository.findById(artifactId)
                        .map(oldArtifact -> {
                            oldArtifact.setName(update.getName());
                            oldArtifact.setDescription(update.getDescription());
                            oldArtifact.setImageUrl(update.getImageUrl());
                            return this.artifactRepository.save(oldArtifact);
                        })
               .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));

    }

    public void delete(String artifactId) {
        this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));
        this.artifactRepository.deleteById(artifactId);

    }
}
