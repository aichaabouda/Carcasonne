package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BoardUITest3 extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        var TILES = Tiles.TILES;

        var playerNames = Map.of(PlayerColor.RED, "Rose",
                PlayerColor.BLUE, "Bernard");
        var playerColors = playerNames.keySet().stream()
                .sorted()
                .toList();

        Text text = new Text("Press R to reset");

        var tilesByKind = TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var startTiles = tilesByKind.get(Tile.Kind.START);
        var normalTiles = tilesByKind.get(Tile.Kind.NORMAL);
        var l1 = new Zone.Lake(80_8, 1, null);
        var z0 = new Zone.Meadow(80_0, List.of(), null);
        var z1 = new Zone.River(80_1, 0, l1);
        var a2_0 = new Animal(80_2_0, Animal.Kind.MAMMOTH);
        var a2_1 = new Animal(80_2_1, Animal.Kind.AUROCHS);
        var z2 = new Zone.Meadow(80_2, List.of(a2_0, a2_1), null);
        var z3 = new Zone.River(80_3, 0, l1);
        var sN = new TileSide.River(z0, z1, z2);
        var sE = new TileSide.Meadow(z2);
        var sS = new TileSide.Meadow(z2);
        var sW = new TileSide.River(z2, z3, z0);
        var menhirTiles = tilesByKind.get(Tile.Kind.MENHIR);
        menhirTiles.add(0, new Tile(80, Tile.Kind.MENHIR, sN, sE, sS, sW));
        Collections.shuffle(normalTiles);

        var tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        var tileDecks1 = new TileDecks(tilesByKind.get(Tile.Kind.START),
                List.of(TILES.get(36),TILES.get(37),TILES.get(22),TILES.get(58),TILES.get(13)),
                //tilesByKind.get(Tile.Kind.NORMAL),
                //tilesByKind.get(Tile.Kind.MENHIR));
                List.of(TILES.get(88),TILES.get(94))
        );

        var textMaker = new TextMakerFr(playerNames);

        var gameState = GameState.initial(playerColors, tileDecks, textMaker);
        var gameStateO = new SimpleObjectProperty<>(gameState);
        var messageList = gameStateO.map(state -> state.messageBoard().messages());
        var highlightedTilesP = new SimpleObjectProperty<>(Set.<Integer>of());
        var tileToPlaceO = gameStateO.map(GameState::tileToPlace);
        var normalTileCount = gameStateO.map(state -> state.tileDecks().normalTiles().size());
        var menhirTileCount = gameStateO.map(state -> state.tileDecks().menhirTiles().size());
        var textProperty = gameStateO.map(state ->
                switch (state.nextAction()) {
                    case OCCUPY_TILE -> textMaker.clickToOccupy();
                    case RETAKE_PAWN -> textMaker.clickToUnoccupy();
                    default -> "";
                });
        Consumer<Occupant> occupantHandler = occupant -> {
            GameState state = gameStateO.get();
            if (state.nextAction() == GameState.Action.OCCUPY_TILE)
                gameStateO.set(state.withNewOccupant(occupant));
            else gameStateO.set(state.withOccupantRemoved(occupant));
        };

        Rotation inititalRotation = Rotation.NONE;
        var tileToPlaceRotationP = new SimpleObjectProperty<>(inititalRotation);
        var visibleOccupantsP = gameStateO.map(state -> {
            Set<Occupant> allOccupants = new HashSet<>(state.board().occupants());
            if (state.nextAction() == GameState.Action.OCCUPY_TILE) {
                allOccupants.addAll(state.lastTilePotentialOccupants());
            }
            return allOccupants;
        });
        var playerNode = PlayersUI.create(gameStateO, textMaker);
        var messageBoardNode = MessageBoardUI.create(messageList, highlightedTilesP);
        var decksNode = DecksUI.create(tileToPlaceO, normalTileCount, menhirTileCount, textProperty, occupantHandler);
        var boardNode = BoardUI
                .create(1, gameStateO, tileToPlaceRotationP, visibleOccupantsP, highlightedTilesP,
                        r -> tileToPlaceRotationP.set(tileToPlaceRotationP.get().add(r)),
                        t -> {
                            GameState state = gameStateO.get();
                            gameStateO.set(state.withPlacedTile(new PlacedTile(
                                    state.tileToPlace(), state.currentPlayer(), tileToPlaceRotationP.get(), t)));
                        },
                        occupantHandler);

        gameStateO.set(gameStateO.get().withStartingTilePlaced());
        var firstUI = new VBox(playerNode, messageBoardNode, decksNode, text);
        var completeUI = new HBox(boardNode, firstUI);

        Scene scene = new Scene(completeUI);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                try {
                    start(primaryStage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }});

        primaryStage.setScene(scene);
        primaryStage.setTitle("ChaCuN");
        primaryStage.show();
    }
}