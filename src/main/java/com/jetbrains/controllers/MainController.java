package com.jetbrains.controllers;

import com.jetbrains.dto.ExceptionDto;
import com.jetbrains.exceptions.AlreadyRegisteredException;
import com.jetbrains.forms.UserForm;
import com.jetbrains.models.Auth;
import com.jetbrains.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @PostMapping("/signIn")
    public ResponseEntity<Object> login(@RequestParam("login") String login, @RequestParam("password") String password){
        UserForm userForm = UserForm.builder()
                .login(login)
                .password(password)
                .build();
        Optional<Auth> authCandidate = mainService.login(userForm);
        if(authCandidate.isPresent())
            return ResponseEntity.ok(authCandidate.get());
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signOut")
    public ResponseEntity<Object> logout(@RequestParam("login") String login, @RequestParam("token") String token){
        Auth auth = Auth.builder()
                .login(login)
                .token(token)
                .build();
        mainService.logout(auth);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/updates")
    public ResponseEntity<Object> getUpdates(@RequestParam("login") String login){
        return ResponseEntity.ok(mainService.getUpdates(login));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestParam("password") String password, @RequestParam("login") String login){
        UserForm userForm = UserForm.builder()
                .login(login)
                .password(password)
                .build();
        mainService.changePassword(userForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/createProfile")
    public ResponseEntity<Object> createProfile(@RequestParam("login") String login, @RequestParam("password") String password){
        UserForm userForm = UserForm.builder()
                .login(login)
                .password(password)
                .build();
        try {
            return ResponseEntity.ok(mainService.createProfile(userForm));
        } catch (AlreadyRegisteredException e){
            ExceptionDto dto = ExceptionDto.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .error("Bad Request")
                    .status(400)
                    .message("This user has already registered")
                    .path("/createProfile")
                    .build();
            return ResponseEntity.badRequest().body(dto);
        }

    }
}
