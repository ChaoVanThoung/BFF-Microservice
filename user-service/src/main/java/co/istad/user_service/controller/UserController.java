package co.istad.user_service.controller;

import co.istad.user_service.dto.UserRequest;
import co.istad.user_service.dto.UserResponse;
import co.istad.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // find all
    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    // create new
    @PostMapping
    public UserResponse create(@RequestBody UserRequest userRequest) {
        return userService.create(userRequest);
    }

    // find by id
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

}
