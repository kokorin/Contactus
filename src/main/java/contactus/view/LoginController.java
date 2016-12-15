package contactus.view;

import contactus.core.UserActor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController {
    @FXML
    protected Pane rootPane;
    @FXML
    protected WebView webView;

    private final UserActor userActor;

    private static final String API_VERSION = "5.45";
    private static final Pattern AUTH_URL_PATTERN = Pattern.compile("^[^#]+#access_token=(.+)&expires_in=(.*)&user_id=(.*)$");

    public LoginController(UserActor userActor) {
        this.userActor = userActor;
    }

    @FXML
    protected void initialize() {
        Objects.requireNonNull(rootPane, "View root tag must have fx:id=\"rootPane\"!");
        Objects.requireNonNull(webView, "No WebView found!");

        webView.getEngine().locationProperty().addListener(new UrlChangeListener());
        webView.getEngine().load(getLoginUrl());
    }

    private static String getLoginUrl() {
        //https://oauth.vk.com/authorize?client_id=5311400&scope=friends,messages&redirect_uri=https://oauth.vk.com/blank.html&display=wap&v=5.45&response_type=token
        return "https://oauth.vk.com/authorize?" +
                "client_id=5311400&" +
                "scope=friends,messages&" +
                "redirect_uri=https://oauth.vk.com/blank.html&" +
                "display=wap&" +
                "v=" + API_VERSION + "&" +
                "response_type=token";
    }


    private class UrlChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldUrl, String url) {
            if (url == null) {
                return;
            }

            Matcher matcher = AUTH_URL_PATTERN.matcher(url);
            if (!matcher.find()) {
                return;
            }

            String token = null;
            int userId = 0;
            Instant expiration = null;

            try {
                long expiresAfterSec = Long.parseLong(matcher.group(2));
                expiration = Instant.now().plusSeconds(expiresAfterSec);
                userId = Integer.parseInt(matcher.group(3));
                token = matcher.group(1);
            } catch (NumberFormatException e) {
                System.out.println("Wrong URL format: " + url);
            }

            userActor.update(userId, token, expiration);
        }
    }

}
