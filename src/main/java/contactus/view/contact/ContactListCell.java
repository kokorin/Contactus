package contactus.view.contact;

import contactus.data.ContactBinding;
import contactus.model.Contact;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

public class ContactListCell extends ListCell<ContactBinding> {
    private static final String DEFAULT_STYLE_CLASS = "contact-list-cell";
    private static final String UNREAD_STYLE_CLASS = "unread";

    private Subscription unreadSubscription = null;

    public ContactListCell() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected void updateItem(ContactBinding item, boolean empty) {
        super.updateItem(item, empty);

        if (unreadSubscription != null) {
            unreadSubscription.unsubscribe();
        }
        if (item != null && !empty) {
            ObservableValue<String> name = EasyBind.monadic(item.contactProperty()).map(ContactListCell::getName);
            textProperty().bind(name);
            EasyBind.includeWhen(getStyleClass(), UNREAD_STYLE_CLASS, item.unreadCountProperty().isNotEqualTo(0));
        } else {
            textProperty().unbind();
            setText("");
        }
    }

    private static String getName(Contact contact) {
        if (contact == null) {
            return "";
        }

        return "[" + contact.getId() + "] " +
                contact.getFirstName() + " " +
                "\"" + contact.getScreenName() + "\" " +
                contact.getLastName();
    }
}
