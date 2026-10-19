package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

import static ch.epfl.chacun.gui.ImageLoader.*;

/**
 * DECKSUI
 * contient le code de création de la partie de l'interface graphique qui affiche
 * les tas de tuiles ainsi que la tuile à poser.
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */
public final class DecksUI {

    private DecksUI() {} // Le constructeur est privé pour empêcher l'instanciation de la classe

    /**Crée un composant d'interface graphique représentant
     * un tas de tuiles du type donné en fonction du nombre de tuiles restantes de ce type
     *
     * @param observableNbRemainingKindTile nombre de tuiles restantes du type donné
     * @param kind le type en question
     * @return Un nœud StackPane représentant le paquet de tuiles.
     */
    private static Node tileDeck(ObservableValue<Integer> observableNbRemainingKindTile, String kind){
        ImageView tileView= new ImageView();
        tileView.setId(kind);
        tileView.setFitWidth(NORMAL_TILE_FIT_SIZE);
        tileView.setFitHeight(tileView.getFitWidth());
        Text normalText= new Text();
        normalText.textProperty().bind(observableNbRemainingKindTile.map(String::valueOf));
        return new StackPane(tileView,normalText);
    }

    public static Node create(ObservableValue<Tile> observableTileToPlace,
                              ObservableValue<Integer> observableNbRemainingNormalTile,
                              ObservableValue<Integer> observableNbRemainingMenhirTile,
                              ObservableValue<String> observableText,
                              Consumer<Occupant> occupantConsumer){

        ObservableValue<Boolean> observableIsVisibleText = observableText.map(string -> !string.isEmpty());

        HBox hbox= new HBox(tileDeck(observableNbRemainingNormalTile, Tile.Kind.NORMAL.name()),
                tileDeck(observableNbRemainingMenhirTile, Tile.Kind.MENHIR.name()));
        hbox.setId("decks");

        ImageView nextTileView= new ImageView();
        nextTileView.setFitWidth(LARGE_TILE_FIT_SIZE);
        nextTileView.setFitHeight(nextTileView.getFitWidth());
        nextTileView.imageProperty().bind(observableTileToPlace.map(newTile -> largeImageForTile(newTile.id())));

        Text nextTileText= new Text();
        nextTileText.setWrappingWidth(0.8*nextTileView.getFitWidth());
        nextTileText.textProperty().bind(observableText);
        nextTileText.visibleProperty().bind(observableIsVisibleText);

        StackPane nextTileStackPane= new StackPane(nextTileView,nextTileText);
        nextTileStackPane.setId("next-tile");
        nextTileStackPane.setOnMouseClicked(_-> {
            if (observableIsVisibleText.getValue())
            {occupantConsumer.accept(null);}
        });

        VBox vbox = new VBox(hbox,nextTileStackPane);
        vbox.getStylesheets().add("decks.css");
        return vbox;
    }


}
