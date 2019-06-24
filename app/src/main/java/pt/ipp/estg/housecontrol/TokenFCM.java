package pt.ipp.estg.housecontrol;

public class TokenFCM {

    private String 	token, username, timestamp;

    public TokenFCM() {}

    public TokenFCM(String token, String username, String timestamp) {
        this.token = token;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
