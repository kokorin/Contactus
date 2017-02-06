package contactus.core;

import com.google.common.eventbus.EventBus;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiAuthException;
import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.friends.responses.GetListsResponse;
import com.vk.api.sdk.objects.messages.LongpollMessages;
import com.vk.api.sdk.objects.messages.LongpollParams;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse;
import com.vk.api.sdk.objects.updates.Update;
import com.vk.api.sdk.objects.updates.responses.LongPollingUpdatesResponse;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.queries.users.UserField;
import contactus.event.SessionEvent;
import contactus.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.Optional;

import static com.vk.api.sdk.queries.messages.MessagesLongPolliingUpdatesMode.*;

@RequiredArgsConstructor
public class Updater implements Runnable {
    public final EventBus eventBus;
    public final Session session;
    public final VkApiClient client;
    public final MessageRepository messageRepository;

    private volatile boolean stopped = false;

    @Override
    public void run() {
        boolean friendListsLoaded = false;
        boolean friendsLoaded = false;
        int pts = messageRepository.loadLastPts();

        //TODO pass multiple FriendLists, Users and Messages to EventBus at a time
        while (!stopped) {
            try {
                if (!friendListsLoaded && !stopped) {
                    GetListsResponse listsResponse = client.friends().getLists(session.getActor()).execute();
                    for (FriendsList friendsList : listsResponse.getItems()) {
                        eventBus.post(friendsList);
                    }
                    friendListsLoaded = true;
                }

                if (!friendsLoaded && !stopped) {
                    GetFieldsResponse friendsResponse = client.friends().get(session.getActor(), UserField.NICKNAME, UserField.PHOTO_50).execute();
                    for (UserXtrLists user : friendsResponse.getItems()) {
                        eventBus.post(user);
                    }
                    friendsLoaded = true;
                }

                LongpollParams params = client.messages().getLongPollServer(session.getActor())
                        .needPts(true)
                        .execute();

                while (params.getPts() > pts && !stopped) {
                    GetLongPollHistoryResponse historyResponse = client.messages().getLongPollHistory(session.getActor())
                            .pts(pts)
                            .execute();

                    //TODO We have to publish Users before messages for DB consistency
                    // but it's not reliable in case of async EventBus
                    for (User user : historyResponse.getProfiles()) {
                        eventBus.post(user);
                    }

                    Optional.ofNullable(historyResponse)
                            .map(GetLongPollHistoryResponse::getMessages)
                            .map(LongpollMessages::getMessages)
                            .orElse(Collections.<Message>emptyList())
                            .stream()
                            .forEach(eventBus::post);

                    pts = historyResponse.getNewPts();
                }

                int ts = params.getTs();
                while (!stopped) {
                    LongPollingUpdatesResponse updatesResponse = client.messages().longPollingUpdates()
                            .server(params.getServer())
                            .key(params.getKey())
                            .waitTime(25)
                            .ts(ts)
                            .modes(RANDOM_ID, NEED_PTS, ATTACHMENTS, EXTENDED_EVENTS)
                            .execute();
                    ts = updatesResponse.getTs();

                    for (Update update : updatesResponse.getUpdates()) {
                        eventBus.post(update);
                    }
                    pts = updatesResponse.getPts();
                    messageRepository.saveLastPts(pts);
                }
            } catch (ApiAuthException e) {
                stop();
                eventBus.post(new SessionEvent(SessionEvent.Type.LOGOUT, Session.EMPTY));
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
