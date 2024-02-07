package com.demo.service.impl;

import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.AuthenticationRequest;
import com.demo.model.AuthenticationResponse;
import com.demo.service.AuthenticationService;
import com.demo.service.JwtService;
import com.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    public AuthenticationResponse generateAuthenticationToken(AuthenticationRequest authenticationRequest) {
        if (authenticationRequest == null || StringUtils.isEmpty(authenticationRequest.getUsername()) || StringUtils.isEmpty(authenticationRequest.getPassword())) {
            throw CommonExceptionType.BAD_REQUEST.getException("username or password is empty.");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        try {
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            final String jwt = jwtService.generateToken(usernamePasswordAuthenticationToken.getName());
            return new AuthenticationResponse(jwt);
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            LOG.error("User validation failed.");
            if (e.getCause() instanceof CustomException) {
                throw (CustomException) e.getCause();
            }
            throw CommonExceptionType.AUTH_FAILED.getException("username and password does not match");
        } catch (CustomException ce) {
            LOG.error("Failed to generate auth token for username:" + authenticationRequest.getUsername());
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to authenticate user: " + authenticationRequest.getUsername() + " with Exception message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("user authentication failed.");
        }
    }
}
