package contactus.core;

import com.vk.api.sdk.client.actors.Actor;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.time.Instant;

public class UserActor implements Actor {
    private int userId;
    private String accessToken;
    private Instant expiration;

    private final ReadOnlyBooleanWrapper isAuthorized = new ReadOnlyBooleanWrapper();

    public void update(int userId, String accessToken, Instant expiration) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expiration = expiration;

        isAuthorized.set(userId > 0 && accessToken != null && expiration != null);
    }

    @Override
    public Integer getId() {
        return userId;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public boolean isIsAuthorized() {
        return isAuthorized.get();
    }

    public ReadOnlyBooleanProperty isAuthorizedProperty() {
        return isAuthorized.getReadOnlyProperty();
    }
}
