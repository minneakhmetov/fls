package com.jetbrains.models;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Auth {
    String login;
    String token;

    public boolean isNotNull(){
        return login != null && token != null;
    }
}
