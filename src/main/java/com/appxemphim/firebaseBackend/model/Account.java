package com.appxemphim.firebaseBackend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Account {
    private String email;
    private String id;
    private Role role;
}
