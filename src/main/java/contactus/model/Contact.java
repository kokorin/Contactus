package contactus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Contact {
    private Integer id;
    private String firstName;
    private String lastName;
    private String deactivated;
    private boolean hidden;
    private Sex sex;
    private String screenName;
    private String photo50;
    private String photo100;
    private Integer online;
    private List<Integer> groups;


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
}
