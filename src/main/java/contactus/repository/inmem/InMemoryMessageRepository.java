package contactus.repository.inmem;

import contactus.model.Message;
import contactus.repository.MessageRepository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    public List<Message> loadAll(Integer userId) {
        List<Message> result = getData().values().stream()
                .filter(m -> Objects.equals(m.getUserId(), userId))
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }

    @Override
    public List<Message> loadAll(Integer userId, Instant since) {
        List<Message> result = getData().values().stream()
                .filter(m -> Objects.equals(m.getUserId(), userId))
                .filter(m -> m.getDate().isAfter(since))
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }

    @Override
    public Message loadLast(Integer userId) {
        return getData().values().stream()
                .filter(m -> Objects.equals(m.getUserId(), userId))
                .reduce((l, r) -> l.getDate().isAfter(r.getDate()) ? l : r)
                .orElse(null);
    }
}
