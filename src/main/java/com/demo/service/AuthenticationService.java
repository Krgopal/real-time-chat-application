package com.demo.service;

import com.demo.model.AuthenticationRequest;
import com.demo.model.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse generateAuthenticationToken(AuthenticationRequest authenticationRequest);
}
