package com.jetbrains.models;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    String login;
    String hashPassword;
    LocalDateTime lastUpdateTime;
}
