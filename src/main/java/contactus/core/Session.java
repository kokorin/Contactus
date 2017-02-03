package contactus.core;

import com.google.gson.annotations.JsonAdapter;
import com.vk.api.sdk.client.actors.UserActor;
import contactus.gson.SessionJsonAdapter;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonAdapter(SessionJsonAdapter.class)
public class Session {
    private final Integer userId;
    private final String accessToken;
    private final Instant expiration;

    public static final Session EMPTY = new Session(null, null, null);
    private static final Pattern AUTH_URL_PATTERN = Pattern.compile("^[^#]+#access_token=(.+)&expires_in=(.*)&user_id=(.*)$");

    public Session(Integer userId, String accessToken, Instant expiration) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expiration = expiration;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public UserActor getActor() {
        return new UserActor(userId, accessToken);
    }

    public boolean isAuthorized() {
        return userId != null && accessToken != null && expiration != null;
    }

    public static Session parseUrl(String url) {
        if (url == null) {
            return EMPTY;
        }

        Matcher matcher = AUTH_URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            return EMPTY;
        }

        String token = null;
        Integer userId = null;
        Instant expiration = null;

        try {
            long expiresAfterSec = Long.parseLong(matcher.group(2));
            expiration = Instant.now().plusSeconds(expiresAfterSec);
            userId = Integer.valueOf(matcher.group(3));
            token = matcher.group(1);
        } catch (NumberFormatException e) {
            System.out.println("Wrong URL format: " + url);
        }

        return  new Session(userId, token, expiration);
    }
}
