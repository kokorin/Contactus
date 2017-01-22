package contactus.view.contact;

import contactus.data.ContactBinding;
import contactus.data.Data;
import contactus.model.Contact;
import contactus.model.ContactGroup;
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

    private final Data contactDataHolder;
    private final ReadOnlyObjectWrapper<Contact> selectedContact = new ReadOnlyObjectWrapper<>();

    private static final ContactGroup EVERYONE;
    static  {
        EVERYONE = new ContactGroup();
        EVERYONE.setName("Everyone");
    }

    public ContactListController(Data contactDataHolder) {
        this.contactDataHolder = contactDataHolder;
    }

    @FXML
    protected void initialize() {
        Objects.requireNonNull(groupSelector);
        Objects.requireNonNull(contactListView);

        ObservableList<ContactGroup> groups = FXCollections.concat(
                FXCollections.singletonObservableList(EVERYONE),
                contactDataHolder.getContactGroups()
        );

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
        ObservableList<ContactBinding> contacts = contactDataHolder.getContactBindings();

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
