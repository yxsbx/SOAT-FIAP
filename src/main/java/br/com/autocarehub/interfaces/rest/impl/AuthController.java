package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.interfaces.rest.generated.api.AuthApi;
import br.com.autocarehub.interfaces.rest.generated.model.LoginRequest;
import br.com.autocarehub.interfaces.rest.generated.model.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
