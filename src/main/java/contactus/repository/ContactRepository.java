package contactus.repository;

import contactus.model.Contact;
import contactus.model.ContactGroup;

public interface ContactRepository extends Repository<Contact> {
    void saveContactGroup(ContactGroup contactGroup);
    ContactGroup loadContactGroup(int id);
}
