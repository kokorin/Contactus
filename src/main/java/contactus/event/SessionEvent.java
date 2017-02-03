package contactus.event;

import contactus.core.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SessionEvent {
    private final Type type;
    private final Session session;

    public enum  Type {
        LOGIN,
        UPDATE,
        LOGOUT
    }
}
