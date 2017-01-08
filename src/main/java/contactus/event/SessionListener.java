package contactus.event;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.objects.updates.AddMessage;
import contactus.core.Session;
import contactus.core.Updater;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;
import contactus.repository.inmem.InMemoryRepositoryFactory;
import contactus.view.LoginController;
import contactus.view.MainController;
import contactus.view.View;
import javafx.stage.Stage;
import org.picocontainer.PicoContainer;

import java.io.Closeable;

public class SessionListener implements EventListener<Session> {
    private final Stage stage;
    private final PicoContainer container;
    private final EventDispatcher eventDispatcher;
    private final View view;

    private MessageRepository messageRepository;
    private ContactRepository contactRepository;
    private Updater updater;
    private ContactGroupListener contactGroupListener;
    private ContactListener contactListener;
    private MessageListener messageListener;
    private AddMessageListener addMessageListener;

    private Session session;

    public SessionListener(Stage stage, PicoContainer container) {
        this.stage = stage;
        this.container = container;
        this.view = container.getComponent(View.class);
        this.eventDispatcher = container.getComponent(EventDispatcher.class);
    }

    @Override
    public void onEvent(Session session) {
        Session oldSession = this.session;
        this.session = session;

        if ((oldSession == null || !oldSession.isAuthorized()) && session.isAuthorized()) {
            onLogin(session);
        } else if ((oldSession == null || this.session.isAuthorized()) && !session.isAuthorized()) {
            onLogout();
        }
    }


    private synchronized void onLogin(Session session) {
        MainController controller = view.forController(MainController.class)
                .onStage(stage)
                .withTitle("Contactus")
                .show();

        RepositoryFactory factory = new InMemoryRepositoryFactory(session.getUserId());
        messageRepository = factory.openMessageRepository();
        contactRepository = factory.openUserRepository();

        VkApiClient client = container.getComponent(VkApiClient.class);
        updater = Updater.builder()
                .actor(session.getActor())
                .client(client)
                .maxSeenMessageId(messageRepository.maxId())
                .eventDispatcher(eventDispatcher)
                .build();

        Thread thread = new Thread(updater);
        thread.setDaemon(true);
        thread.setName("Updater " + session.getUserId());
        thread.start();

        contactGroupListener = new ContactGroupListener(contactRepository);
        contactListener = new ContactListener(contactRepository);
        messageListener = new MessageListener(messageRepository);
        addMessageListener = new AddMessageListener(messageRepository);

        eventDispatcher.addListener(ContactGroup.class, contactGroupListener);
        eventDispatcher.addListener(Contact.class, contactListener);
        eventDispatcher.addListener(Message.class, messageListener);
        eventDispatcher.addListener(AddMessage.class, addMessageListener);
    }

    private synchronized void onLogout() {
        LoginController controller = view.forController(LoginController.class)
                .onStage(stage)
                .withTitle("Contactus Login")
                .show();

        if (updater != null) {
            updater.stop();
        }

        eventDispatcher.removeListener(ContactGroup.class, contactGroupListener);
        eventDispatcher.removeListener(Contact.class, contactListener);
        eventDispatcher.removeListener(Message.class, messageListener);
        eventDispatcher.removeListener(AddMessage.class, addMessageListener);

        closeSilently(messageRepository);
        closeSilently(contactRepository);
    }

    private static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {

        }
    }
}
