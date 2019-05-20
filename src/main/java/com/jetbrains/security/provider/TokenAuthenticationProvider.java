package com.jetbrains.security.provider;

import com.jetbrains.models.Auth;
import com.jetbrains.repositories.AuthRepository;
import com.jetbrains.security.details.UserDetailsImpl;
import com.jetbrains.security.token.TokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider{

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;

        Optional<Auth> authCandidate = authRepository.readOne((tokenAuthentication.getAuth()));

        if(authCandidate.isPresent()){
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(authCandidate.get().getLogin());
            userDetails.setAuth(authCandidate.get());
            tokenAuthentication.setUserDetails(userDetails);
            tokenAuthentication.setAuthenticated(true);
            return tokenAuthentication;
        } //else throw new IllegalArgumentException("Bad token");

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthentication.class.equals(authentication);
    }
}
