package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;


/**
 * BOARD UI
 * contient le code de création de la partie de l'interface graphique qui affiche
 * le plateau du jeu en cours.
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public final class BoardUI {

    private BoardUI(){} // Constructeur privé pour une classe non instanciable

    // Génère un identifiant unique pour un occupant en fonction de son type et de sa zone
    private static String occupantId(Occupant o){
        return STR."\{o.kind().name().toLowerCase()}_\{o.zoneId()}";
    }

    // Génère un identifiant unique pour un animal en fonction de son identifiant
    private static String animalId(Animal a ){
        return STR."marker_\{a.id()}";
    }


    /**
     * Crée un composant d'interface graphique représentant le plateau de jeu.
     * Ce composant est encapsulé dans un objet ScrollPane pour permettre le défilement
     * lorsque le plateau est trop grand pour tenir dans la fenêtre.
     *
     * @param reach la portée du plateau de jeu
     * @param observableGameState une propriété observable contenant l'état actuel du jeu
     * @param observableRotation une propriété observable contenant la rotation actuelle de la vue du plateau
     * @param observableOccupants une propriété observable contenant l'ensemble des occupants du plateau
     * @param highlightedTiles une propriété observable contenant l'ensemble des tuiles mises sur le plateau
     * @param rotationConsumer un consommateur pour les événements de rotation de la vue du plateau
     * @param posConsumer un consommateur pour les événements de sélection de position sur le plateau
     * @param occupantConsumer un consommateur pour les événements de sélection d'occupant sur le plateau
     * @return un nœud d'interface graphique représentant le plateau de jeu
     */
    public static Node create(int reach, ObservableValue<GameState> observableGameState ,
                              ObservableValue<Rotation> observableRotation ,
                              ObservableValue<Set<Occupant>> observableOccupants,
                              ObservableValue<Set<Integer>> highlightedTiles ,
                              Consumer<Rotation> rotationConsumer , Consumer<Pos> posConsumer ,
                              Consumer<Occupant> occupantConsumer){
        GridPane gridPane = new GridPane();
        gridPane.setId("board-grid");

        for (int x =-reach ; x <= reach ; x ++){
            for (int y =-reach ; y <= reach ; y ++) {
                Pos thisPos = new Pos(x, y);

                ImageView thisTileView = new ImageView();
                thisTileView.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                thisTileView.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);

                Group group = new Group(thisTileView);

                ObservableValue<Board> observableBoard = observableGameState.map(GameState::board);
                ObservableValue<Set<Pos>> observableFringe = observableBoard.map(Board::insertionPositions);
                ObservableValue<Set<Animal>> observableCancelledAnimals = observableGameState.map(game ->
                        game.board().cancelledAnimals());
                ObservableValue<PlacedTile> observablePlacedTile = observableGameState.map(game ->
                        game.board().tileAt(thisPos));
                BooleanBinding dependencyBooleanBinding =Bindings.createBooleanBinding( ()->
                                observableGameState.getValue().tileToPlace() != null &&
                                        observableFringe.getValue().contains(thisPos),
                        observableGameState, observableFringe);
                ObservableValue<CellData> cellData = Bindings
                        .createObjectBinding(() -> {
                                    GameState currentGameState = observableGameState.getValue();
                                    PlacedTile placedTile = observablePlacedTile.getValue();
                                    if (placedTile != null) {
                                        Set<Integer> hlIds = highlightedTiles.getValue();
                                        Color veilColor = hlIds.isEmpty() || hlIds.contains(placedTile.id())
                                                ? null : Color.BLACK;
                                        return new CellData(placedTile, veilColor);
                                    } else if (dependencyBooleanBinding.get()) {
                                        if (group.hoverProperty().get()) {
                                            PlacedTile nextTile = new PlacedTile(
                                                    currentGameState.tileToPlace(),
                                                    currentGameState.currentPlayer(),
                                                    observableRotation.getValue(),
                                                    thisPos);
                                            Color veilColor = observableBoard.getValue().canAddTile(nextTile)
                                                    ? null : Color.WHITE;
                                            return new CellData(nextTile, veilColor);
                                        } else {
                                            PlayerColor currentPlayer = currentGameState.currentPlayer();
                                            assert currentPlayer != null;
                                            return new CellData(ColorMap.fillColor(currentPlayer));
                                        }
                                    } else return new CellData(null);
                                }, observablePlacedTile, observableGameState, highlightedTiles,
                                dependencyBooleanBinding, group.hoverProperty(), observableRotation, observableBoard);


                thisTileView.imageProperty().bind(cellData.map(CellData::background));
                group.rotateProperty().bind(cellData.map(CellData::rotation));

                group.effectProperty().bind(cellData.map(CellData::veil).map(veil -> {
                    ColorInput c = new ColorInput(0, 0, ImageLoader.NORMAL_TILE_FIT_SIZE,
                            ImageLoader.NORMAL_TILE_FIT_SIZE, veil);
                    Blend b = new Blend(BlendMode.SRC_OVER);
                    b.setOpacity(0.5);
                    b.setTopInput(c);
                    return b;
                })); //pour recouvrir les cases d'un voile

                group.setOnMouseClicked(entry-> {
                    if(entry.isStillSincePress() && dependencyBooleanBinding.get()){
                        switch(entry.getButton()){
                            case MouseButton.PRIMARY -> posConsumer.accept(thisPos);
                            case MouseButton.SECONDARY ->
                                rotationConsumer.accept(entry.isAltDown() ?
                                        Rotation.RIGHT
                                        : Rotation.LEFT);
                        }
                    }});


                observablePlacedTile.addListener((_, oldTile, newTile) -> {
                    if(oldTile == null){
                        for(Occupant potOcc : newTile.potentialOccupants()){
                            Node tileOccupantsO = Icon.newFor(newTile.placer(), potOcc.kind());
                            tileOccupantsO.setId(occupantId(potOcc));
                            tileOccupantsO.visibleProperty().bind(observableOccupants.map(visibleOcc ->
                                    visibleOcc.contains(potOcc)));
                            //rotation sur les occupants pour apparaître toujours verticaux à l'écran
                            tileOccupantsO.setRotate(- newTile.rotation().degreesCW());
                            tileOccupantsO.setOnMouseClicked(s->{
                                if(s.isStillSincePress()) occupantConsumer.accept(potOcc);
                            });
                            group.getChildren().add(tileOccupantsO);
                        }

                        newTile.meadowZones().forEach(m->m.animals().forEach(animal->{
                            ImageView tileCancelledAnimalsO = new ImageView();
                            tileCancelledAnimalsO.setId(animalId(animal));
                            tileCancelledAnimalsO.getStyleClass().add("marker");
                            tileCancelledAnimalsO.visibleProperty().bind(observableCancelledAnimals
                                    .map(a->a.contains(animal)));
                            tileCancelledAnimalsO.setFitHeight(ImageLoader.MARKER_FIT_SIZE);
                            tileCancelledAnimalsO.setFitWidth(ImageLoader.MARKER_FIT_SIZE);
                            group.getChildren().add(tileCancelledAnimalsO);
                        }));
                    }
                });
                gridPane.add(group , x  + reach , y + reach );
            }
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setId("board-scroll-pane");
        scrollPane.getStylesheets().add("board.css");
        scrollPane.setVvalue(0.5); scrollPane.setHvalue(0.5);
        return scrollPane;
    }

    /**
     * Enregistrement privé qui contient toute les infromations liées à une case à savoir l'image en fond,
     * le voile et la rotation qui y est appliquée
     * @param background l'image de fond
     * @param rotation
     * @param veil la couleur du voile qui recouvre la case
     */
    private record CellData(Image background, int rotation, Color veil) {
        static final Map<Integer, Image> cache = new HashMap<>();
        static WritableImage emptyTile() {
            //créer une image de couleur uniforme utilisée dans toutes les cases vides
            WritableImage emptyTileImage = new WritableImage(1, 1);
            emptyTileImage.getPixelWriter().setColor(0, 0, Color.gray(0.98));
            return emptyTileImage;
        }
        static Image getImage(int id){
            cache.computeIfAbsent(id, ImageLoader::normalImageForTile);
            return cache.get(id);
        }

        CellData(PlacedTile placedTile, Color color){
            this(getImage(placedTile.id()), placedTile.rotation().degreesCW(), color);
        }

        CellData(Color color){
            this(emptyTile(), 0, color);
        }
    }
}