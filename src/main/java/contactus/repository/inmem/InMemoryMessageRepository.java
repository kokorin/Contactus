package contactus.repository.inmem;

import contactus.model.Message;
import contactus.repository.MessageRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class InMemoryMessageRepository extends InMemoryRepository<Message> implements MessageRepository {
    @Override
    public int maxId() {
        return getData().keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    @Override
    protected int getId(Message item) {
        return item.getId();
    }

    @Override
    public List<Message> loadAll(Integer fromId) {
        List<Message> result = getData().values().stream()
                .filter(m -> m.getFromId().equals(fromId))
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }
}
