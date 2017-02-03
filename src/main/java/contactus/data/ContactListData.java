package contactus.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import contactus.event.ContactEvent;
import contactus.event.ContactGroupEvent;
import contactus.event.MessageEvent;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

public class ContactListData {
    private final EventBus eventBus;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;

    private final Map<Integer, ContactBinding> bindingMap = new HashMap<>();
    private final ObservableList<ContactBinding> contactList = FXCollections.observableArrayList();
    private final ObservableList<ContactGroup> groupList = FXCollections.observableArrayList();

    public ContactListData(EventBus eventBus, ContactRepository contactRepository, MessageRepository messageRepository) {
        this.eventBus = eventBus;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    public ObservableList<ContactBinding> getContactBindings() {
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
        for (Message message : messageRepository.loadLast()) {
            ContactBinding binding = getContactBinding(message.getFromId());
            Platform.runLater(() -> binding.setLastMessage(message));
        }
        for (Map.Entry<Integer, Integer> entry : messageRepository.loadUnreadCount().entrySet()) {
            ContactBinding binding = getContactBinding(entry.getKey());
            Platform.runLater(() -> binding.setUnreadCount(entry.getValue()));
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
        ContactBinding binding = getContactBinding(message.getFromId());
        Platform.runLater(() -> binding.setLastMessage(message));
        //TODO update unread count
    }

    @Subscribe
    public void onContactGroupEvent(ContactGroupEvent contactGroupEvent) {
        ContactGroup group = contactGroupEvent.getContactGroup();
        int index = groupList.indexOf(group);
        Platform.runLater(() -> {
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
