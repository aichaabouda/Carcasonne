package ch.epfl.chacun.gui;

import ch.epfl.chacun.gui.ActionUI;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class ActionsUITest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ObservableValue<List<String>> observableActions = new SimpleObjectProperty<>(
                Arrays.asList("AU", "C", "AM", "B")); // List of last four actions in Base32

        Consumer<String> actionConsumer = new Consumer<String>() {
            @Override
            public void accept(String action) {
                System.out.println("Action entered: " + action);
            }
        };

        var actionsNode = ActionUI.create(observableActions, actionConsumer);

        var rootNode = new BorderPane(actionsNode);
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Actions UI Test");
        primaryStage.show();
    }
}