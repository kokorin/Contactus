package contactus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Message {
    private Integer id;
    private Instant date;
    private Direction direction;
    private Integer userId;
    private Integer fromId;
    private Integer randomId;
    private boolean important;
    private boolean deleted;
    //private List<Message> fwdMessages;
    private boolean readState;
    private String title;
    private String body;
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
        OUTPUT
    }
}
