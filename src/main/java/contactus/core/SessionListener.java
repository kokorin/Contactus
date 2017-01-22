package contactus.core;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.objects.updates.AddMessage;
import contactus.data.Data;
import contactus.data.listener.ContactDataListener;
import contactus.data.listener.ContactGroupDataListener;
import contactus.event.EventDispatcher;
import contactus.event.EventListener;
import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.model.Message;
import contactus.picocontainer.ContainerAdapter;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;
import contactus.repository.inmem.InMemoryRepositoryFactory;
import contactus.repository.listener.AddMessageRepositoryListener;
import contactus.repository.listener.ContactGroupRepositoryListener;
import contactus.repository.listener.ContactRepositoryListener;
import contactus.repository.listener.MessageRepositoryListener;
import contactus.view.View;
import contactus.view.contact.ContactListController;
import contactus.view.login.LoginController;
import contactus.view.main.MainController;
import contactus.view.message.MessagingController;
import javafx.stage.Stage;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

import java.io.Closeable;

public class SessionListener implements EventListener<Session> {
    private final Stage stage;
    private final PicoContainer parentContainer;
    private final EventDispatcher eventDispatcher;

    private MessageRepository messageRepository;
    private ContactRepository contactRepository;
    private Updater updater;
    private ContactGroupRepositoryListener contactGroupListener;
    private ContactRepositoryListener contactListener;
    private MessageRepositoryListener messageListener;
    private AddMessageRepositoryListener addMessageListener;
    private ContactDataListener contactDataListener;
    private ContactGroupDataListener contactGroupDataListener;

    private Session session;

    public SessionListener(Stage stage, PicoContainer parentContainer) {
        this.stage = stage;
        this.parentContainer = parentContainer;
        this.eventDispatcher = parentContainer.getComponent(EventDispatcher.class);
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

        RepositoryFactory factory = new InMemoryRepositoryFactory(session.getUserId());
        messageRepository = factory.openMessageRepository();
        contactRepository = factory.openUserRepository();

        VkApiClient client = parentContainer.getComponent(VkApiClient.class);
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

        Data data = new Data();
        contactDataListener = new ContactDataListener(data);
        contactGroupDataListener = new ContactGroupDataListener(data);

        contactGroupListener = new ContactGroupRepositoryListener(contactRepository);
        contactListener = new ContactRepositoryListener(contactRepository);
        messageListener = new MessageRepositoryListener(messageRepository);
        addMessageListener = new AddMessageRepositoryListener(messageRepository);

        eventDispatcher.addListener(ContactGroup.class, contactGroupListener);
        eventDispatcher.addListener(Contact.class, contactListener);
        eventDispatcher.addListener(Message.class, messageListener);
        eventDispatcher.addListener(AddMessage.class, addMessageListener);

        eventDispatcher.addListener(Contact.class, contactDataListener);
        eventDispatcher.addListener(ContactGroup.class, contactGroupDataListener);

        PicoContainer authContainer = new DefaultPicoContainer(parentContainer)
                .addComponent(View.class)
                .addAdapter(new ContainerAdapter())
                .addComponent(MainController.class)
                .addComponent(ContactListController.class)
                .addComponent(MessagingController.class)
                .addComponent(messageRepository)
                .addComponent(contactRepository)
                .addComponent(data);

        MainController controller = authContainer.getComponent(View.class)
                .forController(MainController.class)
                .onStage(stage)
                .withTitle("Contactus")
                .show();
    }

    private synchronized void onLogout() {
        LoginController controller = parentContainer.getComponent(View.class)
                .forController(LoginController.class)
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
        eventDispatcher.removeListener(Contact.class, contactDataListener);
        eventDispatcher.removeListener(ContactGroup.class, contactGroupDataListener);

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
