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

import static ch.epfl.chacun.Tiles.TILES;

public final class BoardUITest2 extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


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
        var menhirTiles = tilesByKind.get(Tile.Kind.MENHIR);
        var z0 = new Zone.Meadow(88_0, List.of(), Zone.SpecialPower.SHAMAN);
        var z1 = new Zone.River(88_1, 1, null);
        var z2 = new Zone.Meadow(88_2, List.of(), null);
        var z3 = new Zone.Forest(88_3, Zone.Forest.Kind.PLAIN);
        var sN = new TileSide.River(z0, z1, z2);
        var sE = new TileSide.River(z2, z1, z0);
        var sS = new TileSide.Forest(z3);
        var sW = new TileSide.Meadow(z0);
        menhirTiles.add(0, new Tile(88, Tile.Kind.MENHIR, sN, sE, sS, sW));

        // Tile 92
        var z = new Zone.Forest(92_0, Zone.Forest.Kind.PLAIN);
        var z11 = new Zone.Meadow(92_1, List.of(), Zone.SpecialPower.PIT_TRAP);
        var nn = new TileSide.Forest(z);
        var ee = new TileSide.Forest(z);
        var ss = new TileSide.Meadow(z11);
        var ww = new TileSide.Meadow(z11);
        menhirTiles.add(new Tile(92, Tile.Kind.MENHIR, nn, ee, ss, ww));

        // Tile 94
        var zz = new Zone.Forest(94_0, Zone.Forest.Kind.PLAIN);
        var z111 = new Zone.Meadow(94_1, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        var nnn = new TileSide.Forest(zz);
        var eee = new TileSide.Meadow(z111);
        var sss = new TileSide.Meadow(z111);
        var www = new TileSide.Meadow(z111);
        menhirTiles.add(new Tile(94, Tile.Kind.MENHIR, nnn, eee, sss, www));

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
                .create(12, gameStateO, tileToPlaceRotationP, visibleOccupantsP, highlightedTilesP,
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