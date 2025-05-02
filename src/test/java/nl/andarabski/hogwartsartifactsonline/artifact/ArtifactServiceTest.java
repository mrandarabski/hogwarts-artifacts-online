package nl.andarabski.hogwartsartifactsonline.artifact;

import nl.andarabski.hogwartsartifactsonline.artifact.utils.IdWorker;
import nl.andarabski.hogwartsartifactsonline.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    private ArtifactRepository artifactRepository;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;
    @Mock
    IdWorker idWorker;



    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("imageUrl");

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("A invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("imageUrl");

        artifacts = new ArrayList<>();
        this.artifacts.add(a1);
        this.artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        //given. Arrange inputs and targets. Define the behavior of Mock ojbect ArticatRespository

        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        artifact.setOwner(w);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact)); // Defines the behovior of the mock object.

        //when. Act on the target behavior. Whet the steps should cover the method to be tested.

        Artifact returnArtifact = artifactService.findById("1250808601744904192");

        //then. Assert expected outcomes.
        assertThat(returnArtifact.getId()).isEqualTo(artifact.getId());
        assertThat(returnArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());
        verify(artifactRepository, times(1)).findById("1250808601744904192");

       // verify(artifactRepository, times(wantedNumberOfInvocations:1)).findById("1250808601744904192");

    }

    @Test
    void testFindByIdNotFound() {
        // Given.
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> {
            Artifact returnArtifact =  artifactService.findById("1250808601744904192");
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with Id: 1250808601744904192 :(");
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindAllSuccess() {
        Artifact artifact = new Artifact();
        // Given.
        given(artifactRepository.findAll()).willReturn(this.artifacts);

        // When.
        List<Artifact> actualArtifacts = artifactService.findAll();
        // Then.
        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }


    @Test
    void testSaveSuccess() {
        // Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description...");
        newArtifact.setImageUrl("ImageUrl...");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        // When.
        Artifact saveArtifact = artifactService.save(newArtifact);

        // Then
        assertThat(saveArtifact.getId()).isEqualTo("123456");
        assertThat(saveArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(saveArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(saveArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        verify(artifactRepository, times(1)).save(newArtifact);

    }

    @Test
    void testUpdateSuccess() {
        // Given.
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        oldArtifact.setImageUrl("ImageUrl");

        Artifact update = new Artifact();
        update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("A new description...");
        update.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        // When.
        Artifact updedArtifact = artifactService.update("1250808601744904192", update);

        // Then.
        assertThat(updedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updedArtifact.getDescription()).isEqualTo(update.getDescription());
        verify(artifactRepository, times(1)).findById("1250808601744904192");
        verify(artifactRepository, times(1)).save(oldArtifact);

    }

    @Test
    void testUpdateNotFound() {
        // Given.
        Artifact update = new Artifact();
        update.setName("Invisibility Cloak");
        update.setDescription("A new description...");
        update.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        // When.
        assertThrows(ArtifactNotFoundException.class, () -> {
            artifactService.update("1250808601744904192", update);
        });

        // Then.
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testDeleteSuccess() {
        // Given.
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("ImageUrl");

        given(artifactRepository.findById(Mockito.anyString())).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("1250808601744904192");

        // When.
        artifactService.delete(artifact.getId());
        // Then.
        verify(artifactRepository, times(1)).deleteById(artifact.getId());
    }

    @Test
    void testDeleteNotFound() {
        // Given.

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        // When.
        assertThrows(ArtifactNotFoundException.class, () -> {
            artifactService.delete("1250808601744904192");
        });

        // Then.
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }
}