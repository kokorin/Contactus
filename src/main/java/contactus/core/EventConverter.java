package contactus.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.updates.*;
import com.vk.api.sdk.objects.users.User;
import contactus.event.ContactEvent;
import contactus.event.ContactGroupEvent;
import contactus.event.MessageEvent;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message.Direction;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.Set;

public class EventConverter {
    private final EventBus eventBus;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;

    public EventConverter(EventBus eventBus, ContactRepository contactRepository, MessageRepository messageRepository) {
        this.eventBus = eventBus;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
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
        ContactGroup result = ContactGroup.builder()
                .id(friendsList.getId())
                .name(friendsList.getName())
                .build();

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
                .name(user.getFirstName())
                .surname(user.getLastName())
                .nick(user.getScreenName())
                .avatarUrl(user.getPhoto50());
    }

    @Subscribe
    public void convertMessage(Message message) {
        contactus.model.Message result = contactus.model.Message.builder()
                .id(message.getId())
                .randomId(message.getRandomId())
                .contactId(message.getUserId())
                .title(message.getTitle())
                .body(message.getBody())
                .date(Instant.ofEpochSecond(message.getDate()))
                .direction(message.isOut() ? Direction.OUTPUT : Direction.INPUT)
                .unread(!message.isReadState() && !message.isOut())
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
        } else if (update instanceof SetMessageFlags) {
            SetMessageFlags setMessageFlags = (SetMessageFlags) update;
            contactus.model.Message message = messageRepository.load(setMessageFlags.getMessageId());
            contactus.model.Message.MessageBuilder builder = message.toBuilder();

            if (!setMessageFlags.getFlags().contains(MessageFlag.UNREAD)) {
                builder.unread(false);
            }

            MessageEvent event = new MessageEvent(MessageEvent.Type.UPDATE, builder.build());
            eventBus.post(event);
        } else if (update instanceof RemoveMessageFlags) {
            RemoveMessageFlags removeMessageFlags = (RemoveMessageFlags) update;
            contactus.model.Message message = messageRepository.load(removeMessageFlags.getMessageId());
            contactus.model.Message.MessageBuilder builder = message.toBuilder();

            if (removeMessageFlags.getFlags().contains(MessageFlag.UNREAD)) {
                builder.unread(false);
            }

            MessageEvent event = new MessageEvent(MessageEvent.Type.UPDATE, builder.build());
            eventBus.post(event);
        } else if (update instanceof ReadAllIncomingMessages) {
            ReadAllIncomingMessages readAllIncomingMessages = (ReadAllIncomingMessages) update;
            Set<contactus.model.Message> unreads = messageRepository.loadAllUnread(readAllIncomingMessages.getPeerId(), Direction.INPUT);

            for (contactus.model.Message message : unreads) {
                if (message.getId() > readAllIncomingMessages.getMessageId()) {
                    continue;
                }

                message = message.toBuilder().unread(false).build();
                MessageEvent event = new MessageEvent(MessageEvent.Type.UPDATE, message);
                eventBus.post(event);
            }
        } else if (update instanceof ReadAllOutgoingMessages) {
            ReadAllOutgoingMessages readAllOutgoingMessages = (ReadAllOutgoingMessages) update;
            Set<contactus.model.Message> unreads = messageRepository.loadAllUnread(readAllOutgoingMessages.getPeerId(), Direction.OUTPUT);

            for (contactus.model.Message message : unreads) {
                if (message.getId() > readAllOutgoingMessages.getMessageId()) {
                    continue;
                }

                message = message.toBuilder().unread(false).build();
                MessageEvent event = new MessageEvent(MessageEvent.Type.UPDATE, message);
                eventBus.post(event);
            }
        }
    }
}
