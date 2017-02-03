package contactus.event;

import contactus.model.ContactGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContactGroupEvent {
    private final Type type;
    private final ContactGroup contactGroup;

    public enum Type {
        ADD, UPDATE
    }
}
