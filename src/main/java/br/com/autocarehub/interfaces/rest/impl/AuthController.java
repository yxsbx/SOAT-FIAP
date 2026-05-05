package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.application.usecase.auth.LoginUseCase;
import br.com.autocarehub.interfaces.rest.generated.api.AuthApi;
import br.com.autocarehub.interfaces.rest.generated.model.LoginRequest;
import br.com.autocarehub.interfaces.rest.generated.model.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        try {
            LoginUseCase.Output output = loginUseCase.execute(new LoginUseCase.Command(loginRequest.getUsername(), loginRequest.getPassword()));
            return ResponseEntity.ok(new LoginResponse(output.accessToken(), output.tokenType(), output.expiresIn()));
        } catch (UnsupportedOperationException exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
    }
}
