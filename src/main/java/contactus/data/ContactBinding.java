package contactus.data;

import contactus.model.Contact;
import contactus.model.Message;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ContactBinding {
    private final ObjectProperty<Contact> contact = new SimpleObjectProperty<>();
    private final ObjectProperty<Message> lastMessage = new SimpleObjectProperty<>();
    private final IntegerProperty unreadCount = new SimpleIntegerProperty();

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

    public int getUnreadCount() {
        return unreadCount.get();
    }

    public IntegerProperty unreadCountProperty() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount.set(unreadCount);
    }
}
