package contactus.data;

import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class Data {
    private final Map<Integer, ContactBinding> map = new HashMap<>();
    private final ObservableList<ContactBinding> dataList = FXCollections.observableArrayList();
    private final ObservableList<ContactGroup> groups = FXCollections.observableArrayList();

    public ObservableList<ContactBinding> getContactBindings() {
        return FXCollections.unmodifiableObservableList(dataList);
    }

    public ObservableList<ContactGroup> getContactGroups() {
        return FXCollections.unmodifiableObservableList(groups);
    }

    public void updateContact(Contact contact) {
        getContactBinding(contact.getId()).setContact(contact);
    }

    public void updateUnreadCount(Integer id, Integer value) {
        getContactBinding(id).setUnreadCount(value);
    }

    public void updateLastMessage(Integer id, Message message) {
        getContactBinding(id).setLastMessage(message);
    }

    public void updateGroup(ContactGroup group) {
        int index = groups.indexOf(group);
        if (index != -1) {
            groups.set(index, group);
        } else  {
            groups.add(group);
        }
    }

    private ContactBinding getContactBinding(Integer id) {
        ContactBinding result = map.get(id);
        if (result == null) {
            result = new ContactBinding();
            map.put(id, result);
            dataList.add(result);
        }

        return result;
    }
}
