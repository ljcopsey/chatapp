import java.time.Instant;

public class Message {

    enum Type {
        LOGIN,
        PRIVATE,
        BROADCAST,
        QUIT
    }
    public Message.Type messageType;
    public String senderUsername;
    public String data;
    public String recipientUsername;
    public String tag;

    public Message(Message.Type messageType, String senderUsername, String data) {
        this.messageType = messageType;
        this.senderUsername = senderUsername;
        this.data = data;
    }

    public Message() {
        //Empty constructor for Jackson objects
    }
}
