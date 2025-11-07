package com.example.controller.request.auth;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AuthCreateRoleRequest {
    @Size(max = 3, message = "The user cannot have more than 3 roles")
    private List<String> roleListName;
}
