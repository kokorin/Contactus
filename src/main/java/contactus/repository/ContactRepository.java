package contactus.repository;

import contactus.model.Contact;
import contactus.model.ContactGroup;

import java.util.List;

public interface ContactRepository extends Repository<Contact> {
    List<Contact> loadAll();

    void saveContactGroup(ContactGroup contactGroup);
    ContactGroup loadGroup(int id);
    List<ContactGroup> loadGroups();
}
