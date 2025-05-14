package nl.andarabski.hogwartsartifactsonline.user;

import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    List<HogwartsUser> hogwartsUsers;

    @BeforeEach
    void setUp() {
        hogwartsUsers = new ArrayList<>();
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("John");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(1);
        u2.setUsername("Eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("Tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        this.hogwartsUsers.add(u1);
        this.hogwartsUsers.add(u2);
        this.hogwartsUsers.add(u3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllUsersSuccess() {
        // Given.
        HogwartsUser hogwartsUser = new HogwartsUser();
        given(this.userRepository.findAll()).willReturn(this.hogwartsUsers);

        // When.
        List<HogwartsUser> actualUsers = this.userService.findAll();
        // Then.
        assertThat(actualUsers.size()).isEqualTo(hogwartsUsers.size());
    }


    @Test
    void testFindByIdSuccess() {
        // Given.
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setId(1);
        hogwartsUser.setUsername("John");
        hogwartsUser.setPassword("123456");
        hogwartsUser.setEnabled(true);
        hogwartsUser.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.of(hogwartsUser));
        // When.
        HogwartsUser actualUser = this.userService.findById(1);

        // Then.
        assertThat(actualUser.getId()).isEqualTo(hogwartsUser.getId());
        assertThat(actualUser.getUsername()).isEqualTo(hogwartsUser.getUsername());
        assertThat(actualUser.getPassword()).isEqualTo(hogwartsUser.getPassword());
        assertThat(actualUser.isEnabled()).isEqualTo(hogwartsUser.isEnabled());
        assertThat(actualUser.getRoles()).isEqualTo(hogwartsUser.getRoles());
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given.
        given(this.userRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());
        // When.
        Throwable thrown = catchThrowable(() -> {
            HogwartsUser returnedUser = this.userService.findById(1);
        });

        // Then.
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find User with Id: 1 :(");
        verify(this.userRepository, times(1)).findById(Mockito.any(Integer.class));
    }

    @Test
    void testSaveUserSuccess() {
        // Given.
        HogwartsUser newUser = new HogwartsUser();
        newUser.setId(1);
        newUser.setUsername("John");
        newUser.setPassword("123456");
        newUser.setEnabled(true);
        newUser.setRoles("user");

        given(this.userRepository.save(newUser)).willReturn(newUser);

        // When.
        HogwartsUser actualUser = this.userService.save(newUser);

        // Then.
        assertThat(actualUser.getId()).isEqualTo(newUser.getId());
        assertThat(actualUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(actualUser.getPassword()).isEqualTo(newUser.getPassword());
        assertThat(actualUser.isEnabled()).isEqualTo(newUser.isEnabled());
        assertThat(actualUser.getRoles()).isEqualTo(newUser.getRoles());
        verify(this.userRepository, times(1)).save(newUser);
    }

    @Test
    void testUpdateSuccess() {
        // Given.
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(1);
        oldUser.setUsername("John");
        oldUser.setPassword("123456");
        oldUser.setEnabled(true);
        oldUser.setRoles("user");

        HogwartsUser update = new HogwartsUser();
        update.setUsername("John Updated");
        update.setPassword("123456");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.of(oldUser));
        given(this.userRepository.save(oldUser)).willReturn(oldUser);

        // When.
        HogwartsUser updatedUser = this.userService.update(1, update);

        // Then.
        assertThat(updatedUser.getId()).isEqualTo(1);
        assertThat(updatedUser.getUsername()).isEqualTo(update.getUsername());
        verify(this.userRepository, times(1)).findById(1);
        verify(this.userRepository, times(1)).save(oldUser);

    }

    @Test
    void testUpdateErrorNotExists() {
        // Given.
        HogwartsUser update = new HogwartsUser();
        update.setUsername("John Updated");
        update.setPassword("123456");
        update.setEnabled(true);
        update.setRoles("user");

        given(this.userRepository.findById(1)).willReturn(Optional.empty());
        // When.
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.update(1, update);
        });

        // Then.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find User with Id: 1 :(");
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void tesDeleteSuccess() {
        // Given.
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setId(1);
        hogwartsUser.setUsername("John");
        hogwartsUser.setEnabled(true);
        hogwartsUser.setRoles("admin user");

        given(this.userRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.of(hogwartsUser));
        doNothing().when(this.userRepository).deleteById(1);
        // When.
        userService.delete(hogwartsUser.getId());

        // Then.
        verify(this.userRepository, times(1)).deleteById(hogwartsUser.getId());
    }

    @Test
    void tesDeleteNotFound() {
        // Given.
        given(this.userRepository.findById(1)).willReturn(Optional.empty());
        // When.
       Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.delete(1);
        });

        // Then.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id: 1 :(");
        verify(this.userRepository, times(1)).findById(1);
    }
}