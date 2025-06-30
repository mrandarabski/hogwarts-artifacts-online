package nl.andarabski.hogwartsartifactsonline.wizard;

import nl.andarabski.hogwartsartifactsonline.artifact.Artifact;
import nl.andarabski.hogwartsartifactsonline.artifact.ArtifactRepository;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "dev")
public class WizardServiceTest {

    @Mock
    private WizardRepository wizardRepository;
    @Mock
    private ArtifactRepository artifactRepository;

    @InjectMocks
    private WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp() {
        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");

        this.wizards = new ArrayList<>();
        this.wizards.add(w1);
        this.wizards.add(w2);
        this.wizards.add(w3);
    }

    @AfterEach
    void tearDown() {}

    @Test
    void testFindAllWizards() {
        // Gevin. Arrange input and targets. Define the behavor of Mock object
        given(this.wizardRepository.findAll()).willReturn(this.wizards);

        // When. Act on the target behavior. Act steps should cover the method
        List<Wizard> actualWizards = this.wizardService.findAll();

        // Then. Assert expected outcomes.
        assertThat(actualWizards.size()).isEqualTo(this.wizards.size());

        // Verify wizardRepository.findAll() is called exactly once.
        verify(this.wizardRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdSuccess() {

        // Given. Arrange input and targets. Define the behavior of Mock object
        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(w1));

        // When. Act on the target behavior. Act steps should cover the method
        Wizard returnedWizard = this.wizardService.findById(1);

        // Then. Assert expect outcomes.
        assertThat(returnedWizard.getId()).isEqualTo(w1.getId());
        assertThat(returnedWizard.getName()).isEqualTo(w1.getName());
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(this.wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        // When.
        Throwable thrown = catchThrowable(() -> this.wizardService.findById(1));

        // Then.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Wizard with Id: 1 :(");
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testSaveSuccess() {
        //Given
        Wizard newWizard = new Wizard();
        newWizard.setName("Hermione Granger");
        given(this.wizardRepository.save(newWizard)).willReturn(newWizard);

        // When.
        Wizard returnedWizard = this.wizardService.save(newWizard);

        // Then.
        assertThat(returnedWizard.getName()).isEqualTo(newWizard.getName());
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        // Given.
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1);
        oldWizard.setName("Albus Dumbledore");
        given(this.wizardRepository.findById(1)).willReturn(Optional.of(oldWizard));

        Wizard update = new Wizard();
        update.setName("Harry Potter - update");

        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);
        // When.
        Wizard updatedWizard = this.wizardService.update(1, update);

        // Then.
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound() {
        // Given.
        Wizard update = new Wizard();
        update.setName("Albus Dumbledore - update");
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> this.wizardService.update(1, update));

        // Then
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteSuccess() {
        // Given.
        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");
        given(this.wizardRepository.findById(1)).willReturn(Optional.of(w1));
        doNothing().when(this.wizardRepository).deleteById(1);

        // When.
        this.wizardService.delete(1);

        // Then.
        verify(this.wizardRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound() {
        // Given.
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());
        // When.
        assertThrows(ObjectNotFoundException.class, () -> this.wizardService.delete(1));

        // Then.
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testAssignArtifactSuccess() {
        // Given.
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("A invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("imageUrl");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.of(w3));

        // When.
        this.wizardService.assignArtifact(3, "1250808601744904192");

        // Then.
        assertThat(a.getOwner().getId()).isEqualTo(3);
        assertThat(w3.getArtifacts()).contains(a);

    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception {
        // Given.
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("A invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("imageUrl");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a);

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

        // When.
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
           // this.wizardService.assignArtifact(3, "125008601744904192");
            this.wizardService.assignArtifact(3, "1250808601744904192");  // matches stubbed one

        });

        // Then.
        assertThat(thrown)
        .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Wizard with Id: 3 :(");
        assertThat(a.getOwner().getId()).isEqualTo(2);


    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() {
        // Given.
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
       // given(this.wizardRepository.findById(3)).willReturn(Optional.of(w3));

        // When.
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            // this.wizardService.assignArtifact(3, "125008601744904192");
            this.wizardService.assignArtifact(3, "1250808601744904192");  // matches stubbed one

        });

        // Then.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Artifact with Id: 1250808601744904192 :(");


    }
}
