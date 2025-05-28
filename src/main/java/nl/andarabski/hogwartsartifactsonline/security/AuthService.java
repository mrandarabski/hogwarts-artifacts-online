package nl.andarabski.hogwartsartifactsonline.security;

import nl.andarabski.hogwartsartifactsonline.user.HogwartsUser;
import nl.andarabski.hogwartsartifactsonline.user.MyUserPrincipal;
import nl.andarabski.hogwartsartifactsonline.user.converter.UserToUserDtoConverter;
import nl.andarabski.hogwartsartifactsonline.user.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JWTProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JWTProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user info.
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = userToUserDtoConverter.convert(hogwartsUser);

        // Create a JWT
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
