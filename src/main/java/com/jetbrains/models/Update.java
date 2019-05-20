package com.jetbrains.models;

import lombok.*;

import java.time.LocalDateTime;

import static com.jetbrains.models.Action.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Update {
    String login;
    LocalDateTime time;
    Action action;

    public static Action from(String action){
        if(CHANGED_PASSWORD.toString().equals(action))
            return CHANGED_PASSWORD;
        if(LOGIN.toString().equals(action))
            return LOGIN;
        if(PROFILE_CREATED.toString().equals(action))
            return PROFILE_CREATED;
        return null;
    }

}
