package nl.andarabski.hogwartsartifactsonline.user;

import jakarta.transaction.Transactional;
import nl.andarabski.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll() {
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Integer userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }


    public HogwartsUser save(HogwartsUser hogwartsUser) {
        return this.userRepository.save(hogwartsUser);
    }

    public HogwartsUser update(Integer userId, HogwartsUser updateUser) {
        HogwartsUser oldHogwartsUser = this.userRepository.findById(userId)
                       .orElseThrow(() -> new ObjectNotFoundException("User", userId));
        oldHogwartsUser.setUsername(updateUser.getUsername());
        oldHogwartsUser.setEnabled(updateUser.isEnabled());
        oldHogwartsUser.setRoles(updateUser.getRoles());
        return this.userRepository.save(oldHogwartsUser);

    }

    public void delete(Integer userId) {
      this.userRepository.findById(userId)
               .orElseThrow(() -> new ObjectNotFoundException("user", userId));
       this.userRepository.deleteById(userId);
    }
}
