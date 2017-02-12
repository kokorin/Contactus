package contactus.model;

import lombok.*;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Message {
    private final Integer id;
    @NonNull
    private final Integer contactId;
    private final Integer randomId;
    @NonNull
    private final Instant date;
    @NonNull
    private final Direction direction;
    private final boolean important;
    private final boolean deleted;
    //private final List<Message> fwdMessages;
    private final boolean unread;
    private final String title;
    private final String body;
    //private List<MessageAttachment> attachments;
    //private Integer chatId;
    //private List<Integer> chatActive;
    //private Integer usersCount;
    //private Integer adminId;
    //private String photo50;
    //private String photo100;
    //private String photo200;
    //private Geo geo;

    public enum Direction {
        INPUT,
        OUTPUT;

        public static Direction parse(String value) {
            for (Direction direction : Direction.values()) {
                if (direction.name().equals(value)) {
                    return direction;
                }
            }
            return null;
        }
    }
}
