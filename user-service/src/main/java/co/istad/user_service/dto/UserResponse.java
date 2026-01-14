package co.istad.user_service.dto;

import lombok.Builder;

@Builder
public record UserResponse (
        Integer id,
        String username,
        String email,
        String password
){
}
