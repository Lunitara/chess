package service;

public class UserService {
    public record RegisterResult(String username, String authToken) {
    }
    public record RegisterRequest(String username, String password, String email) {
    }
    public record LoginResult(String username, String AuthToken) {
    }
    public record LoginRequest(String username, String password) {
    }
    public record LogoutRequest() {
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        return null;
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {}
    boolean checkPassword(String password) {


        return false;
    }
}
