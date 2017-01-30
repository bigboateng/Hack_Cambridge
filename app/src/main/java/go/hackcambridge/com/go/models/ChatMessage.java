package go.hackcambridge.com.go.models;

/**
 * Created by boatengyeboah on 29/01/2017.
 */

public class ChatMessage {

    private String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
