package com.jetbrains.services;

import com.jetbrains.exceptions.AlreadyRegisteredException;
import com.jetbrains.forms.UserForm;
import com.jetbrains.models.Auth;
import com.jetbrains.models.Update;
import com.jetbrains.models.User;
import com.jetbrains.repositories.AuthRepository;
import com.jetbrains.repositories.UpdateRepository;
import com.jetbrains.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jetbrains.models.Action.*;

@Service
public class MainService {

    @Autowired
    private UpdateRepository updateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Auth createProfile(UserForm userForm) throws AlreadyRegisteredException {
        LocalDateTime dateTime = LocalDateTime.now();
        User user = User.builder()
                .hashPassword(encoder.encode(userForm.getPassword()))
                .login(userForm.getLogin())
                .lastUpdateTime(dateTime)
                .build();
        userRepository.save(user);
        Update update = Update.builder()
                .login(userForm.getLogin())
                .time(dateTime)
                .action(PROFILE_CREATED)
                .build();
        updateRepository.save(update);
        Auth auth = Auth.builder()
                .login(userForm.getLogin())
                .token(UUID.randomUUID().toString())
                .build();
        authRepository.save(auth);
        return auth;
    }

    public List<Update> getUpdates(String login){
        return updateRepository.read(login);
    }

    public void changePassword(UserForm userForm){
        LocalDateTime dateTime = LocalDateTime.now();
        User user = User.builder()
                .login(userForm.getLogin())
                .hashPassword(encoder.encode(userForm.getPassword()))
                .lastUpdateTime(dateTime)
                .build();
        userRepository.updatePassword(user);
        Update update = Update.builder()
                .login(userForm.getLogin())
                .time(dateTime)
                .action(CHANGED_PASSWORD)
                .build();
        updateRepository.save(update);
    }

    public Optional<Auth> login(UserForm userForm){
        Optional<User> userCandidate = userRepository.read(userForm.getLogin());
        if(userCandidate.isPresent()){
            if(encoder.matches(userForm.getPassword(), userCandidate.get().getHashPassword())){
                Auth auth = Auth.builder()
                        .login(userForm.getLogin())
                        .token(UUID.randomUUID().toString())
                        .build();
                authRepository.save(auth);
                LocalDateTime now = LocalDateTime.now();
                Update update = Update.builder()
                        .login(userForm.getLogin())
                        .action(LOGIN)
                        .time(now)
                        .build();
                updateRepository.save(update);
                userRepository.updateLastUpdateTime(now, userForm.getLogin());
                return Optional.of(auth);
            }
        }
        return Optional.empty();
    }

    public void logout(Auth auth){

        authRepository.delete(auth);
    }


}
