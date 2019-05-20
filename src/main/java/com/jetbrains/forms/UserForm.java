package com.jetbrains.forms;

import com.jetbrains.models.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserForm {
    String login;
    String password;

    public static UserForm from(Optional<User> user){
        if (user.isEmpty()) return null;
        return UserForm.builder()
                .login(user.get().getLogin())
                .password(user.get().getHashPassword())
                .build();
    }

    public boolean equals(PasswordEncoder encoder, Optional<User> user){
        if (user.isEmpty()) return false;
        return user.get().getLogin().equals(login) && encoder.matches(password, user.get().getHashPassword());

    }
}
