package nl.andarabski.hogwartsartifactsonline.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.andarabski.hogwartsartifactsonline.system.StatusCode;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import nl.andarabski.hogwartsartifactsonline.user.dto.UserDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Turns off Spring Security
@ActiveProfiles(value = "dev")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private List<HogwartsUser> hogwartsUsers;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    private final String userObject = "User";

    @BeforeEach
    void setUp() {
        hogwartsUsers = new ArrayList<>();

        HogwartsUser u1 = new HogwartsUser(1, "john", "123456", true, "admin user");
        HogwartsUser u2 = new HogwartsUser(2, "eric", "654321", true, "user");
        HogwartsUser u3 = new HogwartsUser(3, "tom", "qwerty", false, "user");

        hogwartsUsers.add(u1);
        hogwartsUsers.add(u2);
        hogwartsUsers.add(u3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllUsersSuccess() throws Exception {
        given(this.userService.findAll()).willReturn(hogwartsUsers);

        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[1].username").value("eric"));
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        given(this.userService.findById(1)).willReturn(hogwartsUsers.get(0));

        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
               // .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("john"));
    }

    @Test
    void testFindUserByIdNotFound() throws Exception {
        given(this.userService.findById(1)).willThrow(new ObjectNotFoundException(userObject, 1));

        this.mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + userObject + " with Id: 1 :("))
                // .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddUserSuccess() throws Exception {
        HogwartsUser newUser = new HogwartsUser(null, "newuser", "test123", true, "user");
        HogwartsUser savedUser = new HogwartsUser(4, "newuser", "test123", true, "user");

        given(this.userService.save(any(HogwartsUser.class))).willReturn(savedUser);

        this.mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        UserDto updateDto = new UserDto(1, "johan_updated", true, "admin user");

        HogwartsUser updatedUser = new HogwartsUser(1, "john_updated", "123456", true, "admin user");

        given(this.userService.update(eq(1), any(HogwartsUser.class))).willReturn(updatedUser);

        this.mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("john_updated"));
    }

    @Test
    void testUpdateUserErrorWithNonExistentId() throws Exception {
        // Given
        given(this.userService.update(eq(4), Mockito.any(HogwartsUser.class))).willThrow(new ObjectNotFoundException(userObject, 4));
        UserDto updateDto = new UserDto(4, "johan_updated", true, "admin user");
        String json = objectMapper.writeValueAsString(updateDto);

        // When and Than
        this.mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + userObject + " with Id: 4 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(this.userService).delete(1);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
    }

    @Test
    void testDeleteUserErrorWithNonExistentId() throws Exception {
       doThrow(new ObjectNotFoundException(userObject, 4)).when(this.userService).delete(4);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/4"))

                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find " + userObject + " with Id: 4 :("))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
