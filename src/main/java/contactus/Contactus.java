package contactus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.javafx.css.StyleManager;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import contactus.core.Session;
import contactus.core.Updater;
import contactus.data.ContactListData;
import contactus.data.MessageListData;
import contactus.event.SessionEvent;
import contactus.picocontainer.ContainerAdapter;
import contactus.repository.RepositoryFactory;
import contactus.repository.RepositorySaver;
import contactus.repository.inmem.InMemoryRepositoryFactory;
import contactus.view.View;
import contactus.view.contact.ContactListController;
import contactus.view.login.LoginController;
import contactus.view.main.MainController;
import contactus.view.message.MessagingController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import java.time.Instant;

import static contactus.event.SessionEvent.Type;

public class Contactus extends Application {
    private DefaultPicoContainer mainContainer;
    private MutablePicoContainer authContainer;
    private Stage primaryStage;

    private static final Logger LOGGER = LogManager.getLogger(Contactus.class);

    @Override
    public void init() throws Exception {
        super.init();
        Gson gson = new GsonBuilder().create();
        EventBus eventBus = new EventBus();

        mainContainer = new DefaultPicoContainer();

        mainContainer.addAdapter(new ContainerAdapter())
                .addComponent(View.class)
                .addComponent(new VkApiClient(new HttpTransportClient()))
                .addComponent(LoginController.class)
                .addComponent(gson)
                .addComponent(eventBus);

        mainContainer.start();
        eventBus.register(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Platform.runLater(() -> StyleManager.getInstance().addUserAgentStylesheet("main.css"));

        Session session = mainContainer.getComponent(Gson.class).fromJson(System.getProperty("SESSION"), Session.class);
        Type type = Type.LOGIN;
        if (session == null || Instant.now().isAfter(session.getExpiration())) {
            session = Session.EMPTY;
            type = Type.LOGOUT;
        }
        SessionEvent event = new SessionEvent(type, session);
        mainContainer.getComponent(EventBus.class).post(event);
    }

    @Subscribe
    public void onSessionEvent(SessionEvent event) {
        if (event.getType() == Type.LOGOUT) {
            showLogin();
        } else if (event.getType() == Type.LOGIN) {
            showMain(event.getSession());
        }
    }

    @Subscribe
    public void onDeadEvent(DeadEvent event) {
        LOGGER.warn("Dead event: " + event.getEvent() + ", source " + event.getSource());
    }

    protected void showLogin() {
        if (authContainer != null) {
            authContainer.stop();
            authContainer = null;
        }

        LoginController controller = mainContainer.getComponent(View.class)
                .forController(LoginController.class)
                .onStage(primaryStage)
                .withTitle("Contactus Login")
                .show();
    }

    private void showMain(Session session) {
        authContainer = new PicoBuilder(mainContainer)
                .withJavaEE5Lifecycle()
                .withConstructorInjection()
                .withConsoleMonitor()
                .withCaching()
                .build();

        RepositoryFactory repositoryFactory = new InMemoryRepositoryFactory();

        authContainer
                .addComponent(session)
                .addComponent(repositoryFactory.openContactRepository())
                .addComponent(repositoryFactory.openMessageRepository())
                .addComponent(View.class)
                .addAdapter(new ContainerAdapter())
                .addComponent(ContactListData.class)
                .addComponent(MessageListData.class)
                .addComponent(Updater.class)
                .addComponent(RepositorySaver.class)
                .addComponent(MainController.class)
                .addComponent(ContactListController.class)
                .addComponent(MessagingController.class)
                .start();

        //TODO is it possible to enable autocreation of some components?
        authContainer.getComponent(Updater.class);
        authContainer.getComponent(RepositorySaver.class);

        authContainer.getComponent(View.class)
                .forController(MainController.class)
                .onStage(primaryStage)
                .show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
