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
    private final String firstName;
    private final String lastName;
    private final String deactivated;
    private final boolean hidden;
    private final Sex sex;
    private final State state;
    private final String screenName;
    private final String photo50;
    private final String photo100;
    private final Integer online;
    @NonNull
    private final List<Integer> groups;

    public enum Sex {
        MALE,
        FEMALE,
        UNKNOWN
    }

    public enum State {
        NOT_CONFIRMED,
        INVITED,
        WAITING_FOR_ACCEPT,
        CONFIRMED
    }

    public static class ContactBuilder {
        private List<Integer> groups = Collections.emptyList();

        public ContactBuilder groups(List<Integer> value) {
            groups = Collections.unmodifiableList(new ArrayList<>(value));
            return this;
        }
    }
}
