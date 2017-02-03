package contactus.core;

import com.google.common.eventbus.EventBus;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.friends.responses.GetListsResponse;
import com.vk.api.sdk.objects.messages.LongpollMessages;
import com.vk.api.sdk.objects.messages.LongpollParams;
import com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse;
import com.vk.api.sdk.objects.updates.Update;
import com.vk.api.sdk.objects.updates.responses.LongPollingUpdatesResponse;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.queries.users.UserField;
import contactus.event.Events;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.Optional;

@Setter
public class Updater implements Runnable {
    public final EventBus eventBus;
    public final Session session;
    public final VkApiClient client;

    private volatile boolean stopped = false;

    public Updater(EventBus eventBus, Session session, VkApiClient client) {
        this.eventBus = eventBus;
        this.session = session;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            GetListsResponse listsResponse = client.friends().getLists(session.getActor()).execute();
            for (FriendsList friendsList : listsResponse.getItems()) {
                eventBus.post(Events.convertFriendsList(friendsList));
            }

            GetFieldsResponse friendsResponse = client.friends().get(session.getActor(), UserField.NICKNAME, UserField.PHOTO_50).execute();
            for (UserXtrLists user : friendsResponse.getItems()) {
                eventBus.post(Events.convertUserXtrLists(user));
            }

            LongpollParams params = client.messages().getLongPollServer(session.getActor())
                    .needPts(true)
                    .execute();

            //TODO store PTS in repository
            int pts = 1;
            while (params.getPts() > pts && !stopped) {
                GetLongPollHistoryResponse historyResponse = client.messages().getLongPollHistory(session.getActor())
                        .pts(pts)
                        .execute();
                pts = historyResponse.getNewPts();

                Optional.ofNullable(historyResponse)
                        .map(GetLongPollHistoryResponse::getMessages)
                        .map(LongpollMessages::getMessages)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(Events::convertMessage)
                        .forEach(eventBus::post);

                for (User user : historyResponse.getProfiles()) {
                    eventBus.post(Events.convertUser(user));
                }
            }

            int ts = params.getTs();
            while (!stopped) {
                LongPollingUpdatesResponse updatesResponse = client.messages().longPollingUpdates()
                        .server(params.getServer())
                        .key(params.getKey())
                        .waitTime(25)
                        .ts(ts)
                        .execute();
                ts = updatesResponse.getTs();

                for (Update update : updatesResponse.getUpdates()) {
                    eventBus.post(update);
                }
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start() {
        Thread thread = new Thread(this);
        thread.setName("Updater");
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    public void stop() {
        stopped = true;
    }
}
