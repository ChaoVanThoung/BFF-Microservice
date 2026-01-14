package co.istad.user_service.service;

import co.istad.user_service.domain.User;
import co.istad.user_service.dto.UserRequest;
import co.istad.user_service.dto.UserResponse;
import co.istad.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(
                user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword()).build()
        ).toList();
    }

    @Override
    public UserResponse create(UserRequest userRequest) {

        User user = new User();
        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());

        User saveUser = userRepository.save(user);
        return UserResponse.builder()
                .id(saveUser.getId())
                .username(saveUser.getUsername())
                .email(saveUser.getEmail())
                .password(saveUser.getPassword())
                .build();
    }

    @Override
    public UserResponse findById(Integer id) {

        User user = userRepository.findById(id).orElse(null);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
