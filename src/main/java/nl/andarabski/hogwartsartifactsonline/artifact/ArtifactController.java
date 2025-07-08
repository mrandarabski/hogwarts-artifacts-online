package nl.andarabski.hogwartsartifactsonline.artifact;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import nl.andarabski.hogwartsartifactsonline.artifact.converter.ArtifactDtoToArtifactConverter;
import nl.andarabski.hogwartsartifactsonline.artifact.converter.ArtifactToArtifactDtoConverter;
import nl.andarabski.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import nl.andarabski.hogwartsartifactsonline.system.StatusCode;
import org.springframework.web.bind.annotation.*;

import nl.andarabski.hogwartsartifactsonline.system.Result;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;
    private final MeterRegistry meterRegistry;

    public ArtifactController(ArtifactService artifactService, ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter, ArtifactDtoToArtifactConverter artifactDtoToArtifact, MeterRegistry meterRegistry) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifact;
        this.meterRegistry = meterRegistry;
    }


    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId){
        Artifact foundArtifact = this.artifactService.findById(artifactId);
        this.meterRegistry.counter("artifact.id." + artifactId).increment();
        ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(foundArtifact);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
    }

    @GetMapping
    public Result findAllArtifacts(){

        List<Artifact> foundArtifacts = this.artifactService.findAll();
        // Convert foudArtifacts to a list of artifactsDtos
        List<ArtifactDto> artifactDtos = foundArtifacts.stream()
                .map(this.artifactToArtifactDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Success", artifactDtos);
    }

    @PostMapping
    public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
        // Convert artifactDto to artifact
        Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = this.artifactService.save(newArtifact);
        ArtifactDto savedArtifactDto = this.artifactToArtifactDtoConverter.convert(savedArtifact);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedArtifactDto);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(@PathVariable String artifactId, @Valid @RequestBody ArtifactDto artifactDto) {
       Artifact update = this.artifactDtoToArtifactConverter.convert(artifactDto);
       Artifact updateArtifact = this.artifactService.update(artifactId, update);
       ArtifactDto updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updateArtifact);

        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedArtifactDto);
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId) {
       this.artifactService.delete(artifactId);
       return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
