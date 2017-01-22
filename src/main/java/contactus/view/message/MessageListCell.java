package contactus.view.message;

import contactus.model.Message;
import javafx.scene.control.ListCell;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MessageListCell extends ListCell<Message>{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    private static final String DEFAULT_STYLE_CLASS = "message-list-cell";
    private static final String UNREAD_STYLE_CLASS = "unread";

    public MessageListCell() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);

        String text = null;
        boolean isUnread = false;

        /*if (item != null && !empty) {
            text = new StringJoiner(" ")
                    .add(item.getFlags().contains(MessageFlag.OUTBOX) ? "OUT" : "IN")
                    .add(item.getDateTime().format(FORMATTER))
                    .add("\n\t")
                    .add(item.getBody())
                    .toString();

            boolean unread = item.getFlags().contains(MessageFlag.UNREAD);
            boolean incoming = !item.getFlags().contains(MessageFlag.OUTBOX);
            isUnread = unread && incoming;
        }

        setText(text);
        if (isUnread && !getStyleClass().contains(UNREAD_STYLE_CLASS)) {
            getStyleClass().add(UNREAD_STYLE_CLASS);
        } else if (!isUnread && getStyleClass().contains(UNREAD_STYLE_CLASS)) {
            getStyleClass().remove(UNREAD_STYLE_CLASS);
        }*/
    }
}
