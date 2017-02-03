package contactus.event;

import contactus.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContactEvent {
    private final Type type;
    private final Contact contact;

    public enum Type {
        ADD, UPDATE
    }
}
