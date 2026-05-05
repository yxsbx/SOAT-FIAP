package br.com.autocarehub.application.usecase.auth;

public class LoginUseCase {

    public Output execute(Command command) {
        throw new UnsupportedOperationException("Authentication is not implemented");
    }

    public record Command(String username, String password) {
    }

    public record Output(String accessToken, String tokenType, long expiresIn) {
    }
}
