package contactus.view.contact;

import contactus.data.ContactBinding;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

public class ContactListCell extends ListCell<ContactBinding> {

    public ContactListCell() {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(ContactBinding item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
            return;
        }

        ContactRenderer renderer = (ContactRenderer) getGraphic();
        if (renderer == null) {
            renderer = ContactRenderer.load();
            setGraphic(renderer);
        }
        renderer.setContact(item);
    }
}
