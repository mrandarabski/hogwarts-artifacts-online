package nl.andarabski.hogwartsartifactsonline.user.converter;

import nl.andarabski.hogwartsartifactsonline.user.HogwartsUser;
import nl.andarabski.hogwartsartifactsonline.user.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {

    @Override
    public UserDto convert(HogwartsUser source) {

        // We are not setting password in Dto
        final UserDto userDto = new UserDto(source.getId(),
                                            source.getUsername(),
                                            source.isEnabled(),
                                            source.getRoles());
        return userDto;
    }
}
