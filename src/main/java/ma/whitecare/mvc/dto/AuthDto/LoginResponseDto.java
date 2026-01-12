package ma.whitecare.mvc.dto.AuthDto;

import ma.whitecare.mvc.dto.UserDto.UserDTO;

public class LoginResponseDto {
    private String token; // Token de session ou JWT (pour l'instant, ID de session)
    private UserDTO user;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
