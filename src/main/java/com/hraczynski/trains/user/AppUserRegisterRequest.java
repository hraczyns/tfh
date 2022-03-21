package com.hraczynski.trains.user;

import lombok.Data;

@Data
public class AppUserRegisterRequest {
    private String username;
    private String password;
    private String name;
    private String surname;
}
