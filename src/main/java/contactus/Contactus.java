package contactus;

import com.sun.javafx.css.StyleManager;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import contactus.core.Session;
import contactus.core.SessionListener;
import contactus.event.EventDispatcher;
import contactus.picocontainer.ContainerAdapter;
import contactus.view.View;
import contactus.view.login.LoginController;
import contactus.view.main.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.picocontainer.DefaultPicoContainer;

public class Contactus extends Application {
    private DefaultPicoContainer container;

    @Override
    public void init() throws Exception {
        super.init();

        container = new DefaultPicoContainer();

        container.addAdapter(new ContainerAdapter())
                .addComponent(View.class)
                .addComponent(new VkApiClient(new HttpTransportClient()))
                .addComponent(new EventDispatcher())
                .addComponent(LoginController.class)
                //.addComponent(SessionListener.class)
                .addComponent(MainController.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.runLater(() -> StyleManager.getInstance().addUserAgentStylesheet("main.css"));

        SessionListener sessionListener = new SessionListener(primaryStage, container);
        container.getComponent(EventDispatcher.class).addListener(Session.class, sessionListener);

        Session session = Session.parseUrl(System.getProperty("URL"));
        sessionListener.onEvent(session);
    }


    public static void main(String[] args) {
        launch(args);
    }

}
