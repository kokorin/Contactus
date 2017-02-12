package contactus.data;

import contactus.model.Contact;
import contactus.model.Message;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.Set;

public class ContactBinding {
    private final ObjectProperty<Contact> contact = new SimpleObjectProperty<>();
    private final ObjectProperty<Message> lastMessage = new SimpleObjectProperty<>();
    private final ObservableSet<Integer> unreadMessageIds = FXCollections.observableSet(new HashSet<Integer>());
    private final ReadOnlyIntegerWrapper unreadCount = new ReadOnlyIntegerWrapper();

    public ContactBinding() {
        unreadCount.bind(Bindings.size(unreadMessageIds));
    }

    public Contact getContact() {
        return contact.get();
    }

    public ObjectProperty<Contact> contactProperty() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact.set(contact);
    }

    public Message getLastMessage() {
        return lastMessage.get();
    }

    public ObjectProperty<Message> lastMessageProperty() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage.set(lastMessage);
    }

    public ObservableSet<Integer> getUnreadMessageIds() {
        return unreadMessageIds;
    }

    public void setUnreadMessageIds(Set<Integer> ids) {
        unreadMessageIds.clear();
        unreadMessageIds.addAll(ids);
    }

    public int getUnreadCount() {
        return unreadCount.get();
    }

    public ReadOnlyIntegerProperty unreadCountProperty() {
        return unreadCount.getReadOnlyProperty();
    }
}
