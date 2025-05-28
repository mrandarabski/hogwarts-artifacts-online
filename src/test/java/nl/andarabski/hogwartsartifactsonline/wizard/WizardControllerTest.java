package nl.andarabski.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.andarabski.hogwartsartifactsonline.artifact.Artifact;
import nl.andarabski.hogwartsartifactsonline.system.StatusCode;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import nl.andarabski.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Turns off Spring Security
public class WizardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WizardService wizardService;

    private List<Wizard> wizards;

    private final String objectWizard = "Wizard";
    private final String objectArtifact = "Artifact";

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setup() {
        wizards = new ArrayList<>();

        Artifact a1 = new Artifact("1250808601744904191", "Deluminator", "A Deluminator is a device invented by Albus Dumbledore...", "imageUrl");
        Artifact a2 = new Artifact("1250808601744904192", "Invisibility Cloak", "A invisibility cloak is used to make the wearer invisible.", "imageUrl");
        Artifact a3 = new Artifact("1250808601744904193", "Elder Wand", "The Elder Wand... extremely powerful wand...", "imageUrl");
        Artifact a4 = new Artifact("1250808601744904194", "The Marauder's Map", "A magical map of Hogwarts...", "imageUrl");
        Artifact a5 = new Artifact("1250808601744904195", "The Sword of Gryffindor", "A goblin-made sword...", "imageUrl");
        Artifact a6 = new Artifact("1250808601744904196", "Resurrection Stone", "The Resurrection Stone allows the holder to bring back...", "imageUrl");

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");
        w1.addArtifact(a1);
        w1.addArtifact(a3);
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a2);
        w2.addArtifact(a4);
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");
        w3.addArtifact(a5);
        this.wizards.add(w3);
    }

    @Test
    void testFindAllWizard() throws Exception {
        given(this.wizardService.findAll()).willReturn(wizards);

        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(wizards.size())))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Harry Potter"));
    }

    @Test
    void testFindWizardByIdSuccess() throws Exception {
        given(wizardService.findById(1)).willReturn(wizards.get(0));

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));
    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        given(wizardService.findById(1)).willThrow(new ObjectNotFoundException(objectWizard, 1));

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + objectWizard + " with Id: 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Albus Dumbledore", 0);
        String json = objectMapper.writeValueAsString(wizardDto);

        Wizard savedWizard = new Wizard();
        savedWizard.setId(4);
        savedWizard.setName("Mr Andarabski");

        given(wizardService.save(Mockito.any(Wizard.class))).willReturn(savedWizard);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/wizards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.name").value("Mr Andarabski"));
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(2, "Harry Potter", 0);
        String json = objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(5);
        updatedWizard.setName("Mr Andarabski");

        given(wizardService.update(eq(5), Mockito.any(Wizard.class))).willReturn(updatedWizard);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.name").value("Mr Andarabski"));
    }

    @Test
    void testUpdateWizardErrorWithNonExistentId() throws Exception {
        given(wizardService.update(eq(5), Mockito.any(Wizard.class))).willThrow(new ObjectNotFoundException(objectWizard, 5));
        WizardDto wizardDto = new WizardDto(5, "Harry Potter", 0);
        String json = objectMapper.writeValueAsString(wizardDto);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + objectWizard + " with Id: 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardSuccess() throws Exception {
        doNothing().when(wizardService).delete(1);

        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testDeleteWizardErrorWithNonExistentId() throws Exception {
        doThrow(new ObjectNotFoundException(objectWizard, 2)).when(wizardService).delete(2);

        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + objectWizard + " with Id: 2 :("))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testAssignArtifactSuccess() throws Exception {
        // Given.
       doNothing().when(this.wizardService).assignArtifact(2, "1250808601744904192");
        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/2/artifacts/1250808601744904192").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception {
        // Given.
        doThrow(new ObjectNotFoundException("Wizard", 5)).when(this.wizardService).assignArtifact(5, "1250808601744904192");
        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/5/artifacts/1250808601744904192").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + objectWizard + " with Id: 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception {
        // Given.
        doThrow(new ObjectNotFoundException("Artifact", "1250808601744904199")).when(this.wizardService).assignArtifact(2, "1250808601744904199");
        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/2/artifacts/1250808601744904199").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + objectArtifact + " with Id: 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
