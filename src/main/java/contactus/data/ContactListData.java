package contactus.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import contactus.event.ContactEvent;
import contactus.event.ContactGroupEvent;
import contactus.event.MessageEvent;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message;
import contactus.repository.ContactGroupRepository;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class ContactListData {
    private final EventBus eventBus;
    private final ContactRepository contactRepository;
    private final ContactGroupRepository contactGroupRepository;
    private final MessageRepository messageRepository;

    private final Map<Integer, ContactBinding> bindingMap = new HashMap<>();
    private final ObservableList<ContactBinding> contactList = FXCollections.observableArrayList();
    private final ObservableList<ContactGroup> groupList = FXCollections.observableArrayList();

    public ObservableList<ContactBinding> getContacts() {
        return FXCollections.unmodifiableObservableList(contactList);
    }


    public ObservableList<ContactGroup> getContactGroups() {
        return FXCollections.unmodifiableObservableList(groupList);
    }

    @PostConstruct
    public void postConstruct() {
        eventBus.register(this);

        //TODO do we need here to use Platform.runLater
        for (Contact contact : contactRepository.loadAll()) {
            ContactBinding binding = getContactBinding(contact.getId());
            Platform.runLater(() -> binding.setContact(contact));
        }

        Set<ContactGroup> loadedGroups = contactGroupRepository.loadAll();
        Platform.runLater(() -> groupList.setAll(loadedGroups));

        for (Message message : messageRepository.loadLast()) {
            ContactBinding binding = getContactBinding(message.getContactId());
            Platform.runLater(() -> binding.setLastMessage(message));
        }

        for (Map.Entry<Integer, Set<Integer>> entry : messageRepository.loadUnreadIds().entrySet()) {
            Integer contactId = entry.getKey();
            Set<Integer> messageIds = entry.getValue();
            ContactBinding binding = getContactBinding(contactId);
            Platform.runLater(() -> binding.setUnreadMessageIds(messageIds));
        }
    }

    @PreDestroy
    public void preDestroy() {
        eventBus.unregister(this);
        bindingMap.clear();
        Platform.runLater(contactList::clear);
        Platform.runLater(groupList::clear);
    }

    @Subscribe
    public void onContactEvent(ContactEvent contactEvent) {
        Contact contact = contactEvent.getContact();
        ContactBinding binding = getContactBinding(contact.getId());
        Platform.runLater(() -> binding.setContact(contact));
    }

    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        Message message = messageEvent.getMessage();
        ContactBinding binding = getContactBinding(message.getContactId());
        Message prevMessage = binding.getLastMessage();

        Platform.runLater(() -> {
            if (prevMessage == null || prevMessage.getDate().isBefore(message.getDate())) {
                binding.setLastMessage(message);
            }
            if (message.isUnread()) {
                binding.getUnreadMessageIds().add(message.getId());
            } else {
                binding.getUnreadMessageIds().remove(message.getId());
            }
        });
    }

    @Subscribe
    public void onContactGroupEvent(ContactGroupEvent contactGroupEvent) {
        ContactGroup group = contactGroupEvent.getContactGroup();
        Platform.runLater(() -> {
            int index = groupList.indexOf(group);
            if (index != -1) {
                groupList.set(index, group);
            } else {
                groupList.add(group);
            }
        });
    }

    private ContactBinding getContactBinding(Integer id) {
        ContactBinding result = bindingMap.get(id);
        if (result == null) {
            ContactBinding binding = new ContactBinding();
            bindingMap.put(id, binding);
            Platform.runLater(() -> contactList.add(binding));
            result = binding;
        }

        return result;
    }
}
