package ch.epfl.chacun.gui;

import ch.epfl.chacun.Animal;
import ch.epfl.chacun.MessageBoard;
import ch.epfl.chacun.PlayerColor;
import ch.epfl.chacun.TextMakerFr;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageBoardUITest extends Application {


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        var scorers1 = Set.of(PlayerColor.BLUE, PlayerColor.GREEN);
        var scorers2 = Set.of(PlayerColor.RED, PlayerColor.YELLOW);

        var messageListO = new SimpleObjectProperty<List<MessageBoard.Message>>(List.of());
        var tileSet0 = new SimpleObjectProperty<Set<Integer>>(Set.of());

        var messageBoardNode = MessageBoardUI.create(messageListO, tileSet0);


        var playerNames = Map.of(
                PlayerColor.GREEN, "Rose",
                PlayerColor.BLUE, "Bernard"
        );

        var textMaker = new
                TextMakerFr(playerNames);

        var message1 = new MessageBoard.Message(
                textMaker.playersScoredForest(scorers1, 69, 3, 4),
                69,
                scorers1,
                Set.of(7, 9)
        );
        var message2 = new MessageBoard.Message(textMaker
                .playerScoredHuntingTrap(PlayerColor.BLUE, 96, Map.of(Animal.Kind.MAMMOTH
                        , 2, Animal.Kind.DEER,1)), 96, scorers2, Set.of(4, 6));

        var tileSet = Set.of(7, 9, 4, 6);

        var messageList = List.of(message1, message2);
        messageListO.setValue(messageList);
        tileSet0.setValue(tileSet);

        var rootPane = new VBox(messageBoardNode);

        var scrollPane = new ScrollPane(rootPane);

        primaryStage.setScene(new Scene(scrollPane));

        primaryStage.setTitle("ChaCuN messageBoardTest");

        primaryStage.show();
    }
}
