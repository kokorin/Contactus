package contactus.repository;

import contactus.model.Message;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface MessageRepository extends Repository<Message>{
    int maxId();
    List<Message> loadAll(Integer fromId);
    List<Message> loadAll(Integer fromId, Instant since);
    Message loadLast(Integer fromId);
    List<Message> loadLast();
    Map<Integer, Integer> loadUnreadCount();
}
