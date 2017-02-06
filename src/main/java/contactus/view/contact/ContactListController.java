package contactus.view.contact;

import contactus.data.ContactBinding;
import contactus.data.ContactListData;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import java.util.Objects;


public class ContactListController {
    @FXML
    protected ComboBox<ContactGroup> groupSelector;
    @FXML
    protected ListView<ContactBinding> contactListView;

    private final ContactListData contactListData;
    private final ReadOnlyObjectWrapper<Contact> selectedContact = new ReadOnlyObjectWrapper<>();

    private static final ContactGroup EVERYONE = ContactGroup.builder()
            .name("Everyone")
            .build();

    public ContactListController(ContactListData contactListData) {
        this.contactListData = contactListData;
    }

    @FXML
    protected void initialize() {
        Objects.requireNonNull(groupSelector);
        Objects.requireNonNull(contactListView);

        //TODO a little bit of hack here to concat ObservableList
        ObservableList<ContactGroup> groups = FXCollections.observableArrayList();
        groups.add(EVERYONE);
        ObservableList<ContactGroup> realGroups = contactListData.getContactGroups();
        Bindings.bindContent(groups.subList(1, 1), realGroups);


        groupSelector.setItems(groups);
        groupSelector.getSelectionModel().select(EVERYONE);
        groupSelector.setConverter(new ContactGroupConverter());

        contactListView.setCellFactory(view -> new ContactListCell());

        groupSelector.valueProperty().addListener((observable, oldValue, newValue) -> updateContacts());
        updateContacts();

        MonadicBinding<Contact> contactBinding = EasyBind.monadic(contactListView.getSelectionModel().selectedItemProperty()).flatMap(ContactBinding::contactProperty);
        selectedContact.bind(contactBinding);
    }

    private void updateContacts() {
        ContactGroup group = groupSelector.getValue();
        ObservableList<ContactBinding> contacts = contactListData.getContacts();

        if (group != null && group != EVERYONE) {
            contacts = contacts.filtered(contactData -> contactData.getContact().getGroups().contains(group.getId()));
        }

        contactListView.setItems(contacts);
    }

    public Contact getSelectedContact() {
        return selectedContact.get();
    }

    public ReadOnlyObjectProperty<Contact> selectedContactProperty() {
        return selectedContact.getReadOnlyProperty();
    }

    private static class ContactGroupConverter extends StringConverter<ContactGroup> {
        @Override
        public String toString(ContactGroup object) {
            return object.getName();
        }

        @Override
        public ContactGroup fromString(String string) {
            return null;
        }
    }
}
