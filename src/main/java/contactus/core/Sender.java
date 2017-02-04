package contactus.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vk.api.sdk.client.VkApiClient;
import contactus.event.MessageEvent;
import contactus.model.Message;
import lombok.AllArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@AllArgsConstructor
public class Sender {
    private final EventBus eventBus;
    private final Session session;
    private final VkApiClient client;

    @PostConstruct
    public void postConstruct() {
        eventBus.register(this);
    }

    @PreDestroy
    public void preDestroy() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() != MessageEvent.Type.SENDING) {
            return;
        }

        Message message = event.getMessage();
        try {
            client.messages().send(session.getActor())
                    .userId(message.getContactId())
                    .message(message.getBody())
                    .randomId(message.getRandomId())
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
