package contactus.repository;

import contactus.model.Message;

import java.time.Instant;
import java.util.List;

public interface MessageRepository extends Repository<Message>{
    int maxId();
    List<Message> loadAll(Integer fromId);
    List<Message> loadAll(Integer fromId, Instant since);
    Message loadLast(Integer fromId);
}
