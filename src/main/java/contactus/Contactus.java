package contactus;

import com.sun.javafx.css.StyleManager;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import contactus.core.UserActor;
import contactus.view.LoginController;
import contactus.view.MainController;
import contactus.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import org.picocontainer.DefaultPicoContainer;

public class Contactus extends Application {
    private View view;
    private Stage stage;
    private UserActor actor;

    @Override
    public void init() throws Exception {
        super.init();

        actor = new UserActor();

        DefaultPicoContainer container = new DefaultPicoContainer();
        container.addComponent(container)
                .addComponent(View.class)
                .addComponent(HttpTransportClient.class)
                .addComponent(VkApiClient.class)
                .addComponent(actor)
                .addComponent(LoginController.class)
                .addComponent(MainController.class);

        view = container.getComponent(View.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.runLater(() -> StyleManager.getInstance().addUserAgentStylesheet("main.css"));
        stage = primaryStage;

        actor.isAuthorizedProperty().addListener(new AuthorizationChangeListener());
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

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isAuthorized) {
            if (isAuthorized) {
                showMain();
            } else {
                showLogin();
            }
        }
    }
}
