package com.lingo_leap.dto;

import com.lingo_leap.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {
    private Long Id;
    private String userName;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private String message;


}
