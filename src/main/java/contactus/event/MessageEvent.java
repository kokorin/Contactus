package contactus.event;

import contactus.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MessageEvent {
    private final Type type;
    private final Message message;

    public enum Type {
        SEND,
        RECEIVE,
        UPDATE,
        REMOVE
    }
}
