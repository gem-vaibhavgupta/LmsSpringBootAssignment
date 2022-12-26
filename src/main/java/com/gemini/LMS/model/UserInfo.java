package com.gemini.LMS.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String token;
    private String role;
}
