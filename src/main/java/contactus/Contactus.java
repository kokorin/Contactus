package contactus;

import com.sun.javafx.css.StyleManager;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import contactus.core.Session;
import contactus.core.Updater;
import contactus.event.EventDispatcher;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;
import contactus.repository.UserRepository;
import contactus.repository.inmem.InMemoryRepositoryFactory;
import contactus.view.LoginController;
import contactus.view.MainController;
import contactus.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import org.picocontainer.DefaultPicoContainer;

import java.io.Closeable;

public class Contactus extends Application {
    private View view;
    private Stage stage;
    private Session session;
    private EventDispatcher eventDispatcher;
    private VkApiClient client;

    @Override
    public void init() throws Exception {
        super.init();

        session = new Session();
        client = new VkApiClient(new HttpTransportClient());
        eventDispatcher = new EventDispatcher();

        DefaultPicoContainer container = new DefaultPicoContainer();
        container.addComponent(container)
                .addComponent(View.class)
                .addComponent(client)
                .addComponent(session)
                .addComponent(eventDispatcher)
                .addComponent(LoginController.class)
                .addComponent(MainController.class);

        view = container.getComponent(View.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.runLater(() -> StyleManager.getInstance().addUserAgentStylesheet("main.css"));
        stage = primaryStage;

        session.isAuthorizedProperty().addListener(new AuthorizationChangeListener());
        showLogin();
    }

    private void showLogin() {
        LoginController controller = view.forController(LoginController.class)
                .onStage(stage)
                .withTitle("Contactus")
                .show();
    }

    private void showMain() {
        MainController controller = view.forController(MainController.class)
                .onStage(stage)
                .withTitle("Contactus")
                .show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class AuthorizationChangeListener implements ChangeListener<Boolean> {
        MessageRepository messageRepository;
        UserRepository userRepository;

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isAuthorized) {
            if (isAuthorized) {
                RepositoryFactory factory = new InMemoryRepositoryFactory(session.getActor().getId());
                messageRepository = factory.openMessageRepository();
                userRepository = factory.openUserRepository();

                showMain();

                Updater updater = Updater.builder()
                        .actor(session.getActor())
                        .client(client)
                        .maxSeenMessageId(messageRepository.maxId())
                        .eventDispatcher(eventDispatcher)
                        .build();

                Thread thread = new Thread(updater);
                thread.setDaemon(true);
                thread.setName("Updater " + session.getActor().getId());
                thread.start();
            } else {
                closeSilently(messageRepository);
                closeSilently(userRepository);
                showLogin();
            }
        }

    }

    private static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {

        }
    }
}
