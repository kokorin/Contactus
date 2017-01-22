package contactus.data.listener;

import contactus.data.Data;
import contactus.event.EventListener;
import contactus.model.Contact;

public class ContactDataListener implements EventListener<Contact>{
    private final Data data;

    public ContactDataListener(Data data) {
        this.data = data;
    }

    @Override
    public void onEvent(Contact contact) {
        data.updateContact(contact);
    }
}
