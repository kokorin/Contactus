package contactus.model;

import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Contact {
    @NonNull
    private final Integer id;
    private final String name;
    private final String surname;
    private final String nick;
    private final boolean deactivated;
    private final boolean hidden;
    private final Sex sex;
    private final State state;
    private final String avatarUrl;
    private final String photo100;
    private final boolean online;
    @NonNull
    private final List<Integer> groups;

    public enum Sex {
        MALE,
        FEMALE,
        UNKNOWN;

        public static Sex parse(String value) {
            for (Sex sex : Sex.values()) {
                if (sex.name().equals(value)) {
                    return sex;
                }
            }
            return null;
        }
    }

    public enum State {
        NOT_CONFIRMED,
        INVITED,
        WAITING_FOR_ACCEPT,
        CONFIRMED;

        public static State parse(String value) {
            for (State state : State.values()) {
                if (state.name().equals(value)) {
                    return state;
                }
            }
            return null;
        }
    }

    public static class ContactBuilder {
        private List<Integer> groups = Collections.emptyList();

        public ContactBuilder groups(List<Integer> value) {
            groups = Collections.unmodifiableList(new ArrayList<>(value));
            return this;
        }
    }
}
