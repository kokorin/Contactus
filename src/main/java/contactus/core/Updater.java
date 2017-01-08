package contactus.core;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
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
import contactus.event.EventDispatcher;

import java.util.Collections;
import java.util.Optional;

public class Updater implements Runnable {
    private final UserActor actor;
    private final VkApiClient client;
    private final int maxSeenMessageId;
    private final EventDispatcher eventDispatcher;

    private volatile boolean stopped = false;

    private Updater(UserActor actor, VkApiClient client, int maxSeenMessageId, EventDispatcher eventDispatcher) {
        this.actor = actor;
        this.client = client;
        this.maxSeenMessageId = maxSeenMessageId;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void run() {
        try {
            GetListsResponse listsResponse = client.friends().getLists(actor).execute();
            for (FriendsList friendsList : listsResponse.getItems()) {
                eventDispatcher.dispatchEvent(Converter.convertFriendsList(friendsList));
            }

            GetFieldsResponse friendsResponse = client.friends().get(actor, UserField.NICKNAME, UserField.PHOTO_50).execute();
            for (UserXtrLists user : friendsResponse.getItems()) {
                eventDispatcher.dispatchEvent(Converter.convertUserXtrLists(user));
            }

            LongpollParams params = client.messages().getLongPollServer(actor)
                    .needPts(true)
                    .execute();

            int pts = 1;
            while (params.getPts() > pts && !stopped) {
                GetLongPollHistoryResponse historyResponse = client.messages().getLongPollHistory(actor)
                        .pts(pts)
                        .execute();
                pts = historyResponse.getNewPts();

                Optional.ofNullable(historyResponse)
                        .map(GetLongPollHistoryResponse::getMessages)
                        .map(LongpollMessages::getMessages)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(Converter::convertMessage)
                        .forEach(eventDispatcher::dispatchEvent);

                for (User user : historyResponse.getProfiles()) {
                    eventDispatcher.dispatchEvent(Converter.convertUser(user));
                }
            }

            int ts = params.getTs();
            while (!stopped) {
                LongPollingUpdatesResponse updatesResponse = client.messages().longPollingUpdates()
                        .server(params.getServer())
                        .key(params.getKey())
                        .waitTime(5)
                        .ts(ts)
                        .execute();
                ts = updatesResponse.getTs();

                for (Update update : updatesResponse.getUpdates()) {
                    eventDispatcher.dispatchEvent(update);
                }
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopped = true;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserActor actor;
        private VkApiClient client;
        private int maxSeenMessageId;
        private EventDispatcher eventDispatcher;

        private Builder() {}

        public Builder actor(UserActor actor) {
            this.actor = actor;
            return this;
        }

        public Builder client(VkApiClient client) {
            this.client = client;
            return this;
        }

        public Builder maxSeenMessageId(int maxSeenMessageId) {
            this.maxSeenMessageId = maxSeenMessageId;
            return this;
        }

        public Builder eventDispatcher(EventDispatcher eventDispatcher) {
            this.eventDispatcher = eventDispatcher;
            return this;
        }

        public Updater build() {
            return new Updater(actor, client, maxSeenMessageId, eventDispatcher);
        }
    }
}
