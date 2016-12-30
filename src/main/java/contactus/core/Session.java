package contactus.core;

import com.vk.api.sdk.client.actors.UserActor;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.Instant;

public class Session {
    private final ReadOnlyObjectWrapper<UserActor> actor = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Instant> expiration = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyBooleanWrapper isAuthorized = new ReadOnlyBooleanWrapper();

    public Session() {
        isAuthorized.bind(actor.isNotNull().and(expiration.isNotNull()));
    }

    public void update(UserActor actor, Instant expiration) {
        this.actor.setValue(actor);
        this.expiration.setValue(expiration);
    }

    public UserActor getActor() {
        return actor.get();
    }

    public ReadOnlyObjectProperty<UserActor> actorProperty() {
        return actor.getReadOnlyProperty();
    }

    public Instant getExpiration() {
        return expiration.get();
    }

    public ReadOnlyObjectProperty<Instant> expirationProperty() {
        return expiration.getReadOnlyProperty();
    }

    public boolean isIsAuthorized() {
        return isAuthorized.get();
    }

    public ReadOnlyBooleanProperty isAuthorizedProperty() {
        return isAuthorized.getReadOnlyProperty();
    }
}
