package contactus.data.listener;

import contactus.data.Data;
import contactus.event.EventListener;
import contactus.model.ContactGroup;

public class ContactGroupDataListener implements EventListener<ContactGroup>{
    private final Data data;

    public ContactGroupDataListener(Data data) {
        this.data = data;
    }

    @Override
    public void onEvent(ContactGroup group) {
        data.updateGroup(group);
    }
}
