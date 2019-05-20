package com.jetbrains.forms;

import com.jetbrains.models.Action;
import com.jetbrains.models.Update;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateForm {
    String login;
    Action action;

    public static UpdateForm fromLastUpdate(List<Update> updates) {
        if (updates.size() == 0) return null;
        return UpdateForm.builder()
                .login(updates.get(updates.size() - 1).getLogin())
                .action(updates.get(updates.size() - 1).getAction())
                .build();
    }
}
