package contactus.event;

import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message.Direction;

import java.time.Instant;

public class Events {
    private Events(){}

    public static ContactGroupEvent convertFriendsList(FriendsList friendsList) {
        ContactGroup result = new ContactGroup();
        result.setId(friendsList.getId());
        result.setName(friendsList.getName());

        return new ContactGroupEvent(ContactGroupEvent.Type.ADD, result);
    }


    public static ContactEvent convertUser(User user) {
        Contact result = toContact(user);

        return new ContactEvent(ContactEvent.Type.ADD, result);
    }

    public static ContactEvent convertUserXtrLists(UserXtrLists user) {
        Contact result = toContact(user);

        return new ContactEvent(ContactEvent.Type.ADD, result);
    }

    private static Contact toContact(User user) {
        Contact result = new Contact();

        result.setId(user.getId());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setScreenName(user.getScreenName());

        return result;
    }

    public static MessageEvent convertMessage(Message message) {
        contactus.model.Message result = new contactus.model.Message();

        result.setId(message.getId());
        result.setTitle(message.getTitle());
        result.setBody(message.getBody());
        result.setUserId(message.getUserId());
        result.setDate(Instant.ofEpochMilli(message.getDate()));
        result.setDirection(message.isOut() ? Direction.OUTPUT : Direction.INPUT);

        return new MessageEvent(MessageEvent.Type.RECEIVE, result);
    }
}
