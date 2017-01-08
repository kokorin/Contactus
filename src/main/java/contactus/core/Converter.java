package contactus.core;

import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;
import contactus.model.Contact;
import contactus.model.ContactGroup;

import java.time.Instant;

public class Converter {
    private Converter(){}

    public static ContactGroup convertFriendsList(FriendsList friendsList) {
        ContactGroup result = new ContactGroup();

        result.setId(friendsList.getId());
        result.setName(friendsList.getName());

        return result;
    }


    public static Contact convertUser(User user) {
        Contact result = new Contact();

        result.setId(user.getId());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setScreenName(user.getScreenName());

        return result;
    }

    public static Contact convertUserXtrLists(UserXtrLists user) {
        Contact result = convertUser((User)user);

        return result;
    }

    public static contactus.model.Message convertMessage(Message message) {
        contactus.model.Message result = new contactus.model.Message();

        result.setId(message.getId());
        result.setTitle(message.getTitle());
        result.setBody(message.getBody());
        result.setUserId(message.getUserId());
        result.setDate(Instant.ofEpochMilli(message.getDate()));

        return result;
    }
}
