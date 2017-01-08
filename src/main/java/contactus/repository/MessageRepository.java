package contactus.repository;

import contactus.model.Message;

import java.util.List;

public interface MessageRepository extends Repository<Message>{
    int maxId();
    List<Message> loadAll(Integer fromId);
}
