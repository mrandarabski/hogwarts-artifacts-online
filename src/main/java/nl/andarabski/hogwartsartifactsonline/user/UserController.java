package nl.andarabski.hogwartsartifactsonline.user;

import jakarta.validation.Valid;
import nl.andarabski.hogwartsartifactsonline.system.Result;
import nl.andarabski.hogwartsartifactsonline.system.StatusCode;
import nl.andarabski.hogwartsartifactsonline.user.converter.UserDtoToUserConverter;
import nl.andarabski.hogwartsartifactsonline.user.converter.UserToUserDtoConverter;
import nl.andarabski.hogwartsartifactsonline.user.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;
    private final UserDtoToUserConverter userDtoToUserConverter;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public UserController(UserService userService, UserDtoToUserConverter userDtoToUserConverter, UserToUserDtoConverter userToUserDtoConverter) {
        this.userService = userService;
        this.userDtoToUserConverter = userDtoToUserConverter;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    // Convert foundUser to a list of UserDatos.
    @GetMapping
    public Result findAllUsers() {
        List<HogwartsUser> foundHogartsUsers = this.userService.findAll();
        List<UserDto> userDtos = foundHogartsUsers.stream()
                .map(this.userToUserDtoConverter::convert)
                .collect(Collectors.toList());
        // Note that userDato does not contain a password field.
        return new Result(true, StatusCode.SUCCESS, "Find All Success", userDtos);
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Integer userId) {
        HogwartsUser foundUser =  this.userService.findById(userId);
        UserDto userDto = this.userToUserDtoConverter.convert(foundUser);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
    }

    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser newUser) {
        HogwartsUser savedUser = this.userService.save(newUser);
        UserDto userDto = this.userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", userDto);
    }

    // We are not using this to update password, we need another changePassword method in this controller class create to change password
    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDto) {
        HogwartsUser update =  this.userDtoToUserConverter.convert(userDto);
        HogwartsUser updatedHogwartsUser = this.userService.update(userId, update);
        UserDto updatedUserDto = this.userToUserDtoConverter.convert(updatedHogwartsUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {
        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

    @PatchMapping("/{userId}/password")
    public Result changePassword(@PathVariable Integer userId, @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        String confirmNewPassword = passwordMap.get("confirmNewPassword");
        this.userService.changePassword(userId, oldPassword, newPassword, confirmNewPassword);
        return new Result(true, StatusCode.SUCCESS, "Change Password Success", null);
    }
}
