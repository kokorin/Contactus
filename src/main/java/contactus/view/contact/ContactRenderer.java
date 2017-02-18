package contactus.view.contact;

import contactus.data.ContactBinding;
import contactus.model.Contact;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;
import org.fxmisc.easybind.EasyBind;

import java.net.URL;
import java.util.Objects;

public class ContactRenderer extends HBox {
    @FXML
    protected ImageView avatarView;
    @FXML
    protected Label nameLabel;
    @FXML
    protected Label unreadCountLabel;

    private final ObjectProperty<ContactBinding> contact = new SimpleObjectProperty<>();

    private ContactRenderer() {
    }

    @FXML
    protected void initialize() {
        Objects.requireNonNull(avatarView);
        Objects.requireNonNull(nameLabel);
        Objects.requireNonNull(unreadCountLabel);

        avatarView.imageProperty().bind(EasyBind.monadic(contact)
                .flatMap(ContactBinding::contactProperty)
                .map(c -> c.getAvatarUrl())
                .map(url -> new Image(url, true))
        );
        nameLabel.textProperty().bind(EasyBind.monadic(contact)
                .flatMap(ContactBinding::contactProperty)
                .map(ContactRenderer::getName)
        );
        unreadCountLabel.textProperty().bind(EasyBind.monadic(contact)
                .flatMap(ContactBinding::unreadCountProperty)
                .map(ContactRenderer::getUnreadLabel)
        );
    }

    public void setContact(ContactBinding contact) {
        this.contact.set(contact);
    }

    @SneakyThrows
    public static ContactRenderer load() {
        ContactRenderer result = new ContactRenderer();

        String fxml = ContactRenderer.class.getSimpleName() + ".fxml";
        URL location =  ContactRenderer.class.getResource(fxml);
        FXMLLoader loader = new FXMLLoader(location);
        loader.setController(result);
        loader.setRoot(result);

        loader.load();

        return result;
    }

    private static String getName(Contact contact) {
        if (contact == null) {
            return "";
        }

        return "[" + contact.getId() + "] \n" +
                contact.getName() + " " + contact.getSurname() + " \n" +
                "\"" + contact.getNick() + "\"";
    }

    private static String getUnreadLabel(Number count) {
        if (count == null || count.intValue() == 0) {
            return "";
        }

        return count.toString();
    }

}
