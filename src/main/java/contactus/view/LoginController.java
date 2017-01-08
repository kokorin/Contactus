package contactus.view;

import contactus.core.Session;
import contactus.event.EventDispatcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.util.Objects;

public class LoginController {
    private final EventDispatcher eventDispatcher;
    @FXML
    protected Pane rootPane;
    @FXML
    protected WebView webView;

    private static final String API_VERSION = "5.45";

    public LoginController(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
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
            Session session = Session.parseUrl(url);
            eventDispatcher.dispatchEvent(session);
        }
    }

}
