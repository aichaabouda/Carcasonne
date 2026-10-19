package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class DecksUITest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var playerNames = Map.of(PlayerColor.RED, "Rose",
                PlayerColor.BLUE, "Bernard");
        var playerColors = playerNames.keySet().stream()
                .sorted()
                .toList();

        var tilesByKind = Tiles.TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks = new TileDecks(tilesByKind.get(Tile.Kind.START),
                tilesByKind.get(Tile.Kind.NORMAL),
                tilesByKind.get(Tile.Kind.MENHIR));

        ObservableValue<Tile> observableTileToPlace = new SimpleObjectProperty<>(tileDecks.normalTiles().get(0));
        ObservableValue<Integer> normalTilesLeft = new SimpleObjectProperty<>(tileDecks.deckSize(Tile.Kind.NORMAL));
        ObservableValue<Integer> menhirTilesLeft = new SimpleObjectProperty<>(tileDecks.deckSize(Tile.Kind.MENHIR));
        ObservableValue<String> observableText = new SimpleObjectProperty<>("Cv ou quoi");
        Consumer<Occupant> eventManager = new Consumer<Occupant>() {
            @Override
            public void accept(Occupant occupant) {
            }
        };

        var decksNode = DecksUI.create(observableTileToPlace, normalTilesLeft, menhirTilesLeft, observableText, eventManager);

        var rootNode = new BorderPane(decksNode);
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("ChaCuN test decks");
        primaryStage.show();
    }
}