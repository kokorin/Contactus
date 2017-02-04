package contactus.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.updates.AddMessage;
import com.vk.api.sdk.objects.updates.Update;
import com.vk.api.sdk.objects.users.User;
import contactus.event.ContactEvent;
import contactus.event.ContactGroupEvent;
import contactus.event.MessageEvent;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message.Direction;
import contactus.repository.ContactRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;

public class EventConverter {
    private final EventBus eventBus;
    private final ContactRepository contactRepository;

    public EventConverter(EventBus eventBus, ContactRepository contactRepository) {
        this.eventBus = eventBus;
        this.contactRepository = contactRepository;
    }

    @PostConstruct
    public void postConstruct() {
        eventBus.register(this);
    }

    @PreDestroy
    public void preDestroy() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void convertFriendsList(FriendsList friendsList) {
        ContactGroup result = new ContactGroup();
        result.setId(friendsList.getId());
        result.setName(friendsList.getName());

        ContactGroupEvent event = new ContactGroupEvent(ContactGroupEvent.Type.ADD, result);
        eventBus.post(event);
    }

    @Subscribe
    public void convertUser(User user) {
        if (user.getClass() != User.class) {
            return;
        }

        Contact.ContactBuilder builder = toContact(user);

        Contact stored = contactRepository.load(user.getId());
        if (stored != null) {
            builder.groups(stored.getGroups());
        }
        ContactEvent event = new ContactEvent(ContactEvent.Type.ADD, builder.build());
        eventBus.post(event);
    }

    @Subscribe
    public void convertUserXtrLists(UserXtrLists user) {
        Contact result = toContact(user)
                .groups(user.getLists())
                .build();

        ContactEvent event = new ContactEvent(ContactEvent.Type.ADD, result);
        eventBus.post(event);
    }

    private Contact.ContactBuilder toContact(User user) {
        return Contact.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .screenName(user.getScreenName());
    }

    @Subscribe
    public void convertMessage(Message message) {
        contactus.model.Message result = contactus.model.Message.builder()
                .id(message.getId())
                .title(message.getTitle())
                .body(message.getBody())
                .contactId(message.getUserId())
                .date(Instant.ofEpochMilli(message.getDate()))
                .direction(message.isOut() ? Direction.OUTPUT : Direction.INPUT)
                .build();

        MessageEvent.Type type = result.getDirection() == Direction.INPUT ?
                MessageEvent.Type.RECEIVE : MessageEvent.Type.SENT;
        MessageEvent event = new MessageEvent(type, result);
        eventBus.post(event);
    }

    @Subscribe
    public void convertUpdate(Update update) {
        if (update instanceof AddMessage) {
            convertMessage(((AddMessage) update).getMessage());
        }
    }
}
