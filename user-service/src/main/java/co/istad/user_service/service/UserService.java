package co.istad.user_service.service;

import co.istad.user_service.dto.UserRequest;
import co.istad.user_service.dto.UserResponse;

import java.util.List;

public interface UserService {

    // find all
    List<UserResponse> findAll();

    // create new
    UserResponse create(UserRequest userRequest);

    // find by id
    UserResponse findById(Integer id);

}
