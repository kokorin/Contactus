package contactus.repository;

import contactus.model.Message;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public interface MessageRepository extends Repository<Message>{
    Set<Message> loadAll(Integer contactId);
    Set<Message> loadAll(Integer contactId, Instant since);
    Set<Message> loadAllUnread(Integer contactId, Message.Direction direction);
    Set<Message> loadLast();
    Map<Integer, Set<Integer>> loadUnreadIds();

    void saveLastPts(int value);
    int loadLastPts();
}
