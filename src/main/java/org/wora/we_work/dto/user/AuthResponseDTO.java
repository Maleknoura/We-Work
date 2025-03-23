package org.wora.we_work.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String userType;
    private String username;
    private Long userId;
    private Set<String> roles;
    private LocalDateTime tokenExpiration;

    }

