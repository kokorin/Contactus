package contactus.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.picocontainer.PicoContainer;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

public class View {
    private final PicoContainer container;

    public View(PicoContainer container) {
        this.container = container;
    }

    public <T> ControllerBuilder<T> forController(Class<T> controllerClass) {
        return new ControllerBuilder<T>(controllerClass);
    }

    public class ControllerBuilder<T> {
        private final Class<T> controllerClass;

        private FXMLLoader fxmlLoader;
        private Stage stage;
        private String title;
        private Window owner;

        public ControllerBuilder(Class<T> controllerClass) {
            this.controllerClass = controllerClass;
        }

        public FXMLLoader getFxmlLoader() {
            if (fxmlLoader == null) {
                String fxml = controllerClass.getSimpleName().replace("Controller", "View.fxml");
                URL location = controllerClass.getResource(fxml);
                Objects.requireNonNull(location, "FXML not found: " + fxml);

                fxmlLoader = new FXMLLoader(location);
                fxmlLoader.setControllerFactory(clazz -> {
                    if (!controllerClass.equals(clazz)) {
                        throw new RuntimeException("Expecting controller " + controllerClass + " but FXML specifies " + clazz);
                    }

                    @SuppressWarnings("unchecked")
                    T controller = container.getComponent(controllerClass);

                    if (controller == null) {
                        throw new RuntimeException("Controller " + controllerClass + " not found in context " + container);
                    }

                    return controller;
                });

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException("Error loading " + controllerClass, e);
                }
            }

            return fxmlLoader;
        }

        public T getController() {
            return getFxmlLoader().getController();
        }

        public Parent getRoot() {
            return getFxmlLoader().getRoot();
        }

        public ControllerBuilder<T> onStage(Stage stage) {
            this.stage = stage;
            return this;
        }

        public ControllerBuilder<T> withTitle(String title) {
            this.title = title;
            return this;
        }

        public ControllerBuilder<T> withModality(Window owner) {
            this.owner = owner;
            return this;
        }

        public ControllerBuilder<T> beforeShow(Consumer<T> consumer) {
            consumer.accept(getController());
            return this;
        }

        public T show() {
            prepare();
            stage.show();
            return getController();
        }

        public T showAndWait() {
            prepare();
            stage.showAndWait();
            return getController();
        }

        private void prepare() {
            if (stage == null) {
                stage = new Stage();
            }
            if (title != null) {
                stage.setTitle(title);
            }

            if (owner != null) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(owner);
            }

            stage.setScene(new Scene(getRoot()));
            stage.sizeToScene();
        }
    }
}
