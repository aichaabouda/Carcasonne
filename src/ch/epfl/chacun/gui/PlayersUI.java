package ch.epfl.chacun.gui;

import ch.epfl.chacun.GameState;
import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import ch.epfl.chacun.TextMaker;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;

import static ch.epfl.chacun.Occupant.Kind.HUT;
import static ch.epfl.chacun.Occupant.Kind.PAWN;
import static ch.epfl.chacun.Occupant.occupantsCount;
import static ch.epfl.chacun.gui.ColorMap.fillColor;

/**
 * PLAYERSUI
 * La classe PlayersUI contient le code de création de la partie de l'interface graphique qui affiche
 * les informations sur les joueurs.
 * Elle fournit une méthode statique pour créer un composant d'interface graphique représentant
 * les informations des joueurs, telles que leur nom, leurs points et leurs occupants.
 * Elle utilise JavaFX pour générer et manipuler les éléments graphiques.
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public final class PlayersUI {

    private PlayersUI() {} // Le constructeur est privé pour empêcher l'instanciation de la classe

    /**
     * Crée un composant d'interface graphique représentant les informations des joueurs.
     *
     * @param observableGameState une propriété observable contenant l'état de jeu
     * @param text un objet TextMaker pour la génération de texte
     * @return un composant d'interface graphique représentant les informations des joueurs
     */

    public static Node create(ObservableValue<GameState> observableGameState, TextMaker text) {
        VBox vbox = new VBox();
        vbox.getStylesheets().add("players.css");
        vbox.setId("players");

        // Mappe l'état de jeu au joueur courant
        ObservableValue<PlayerColor> observableCurrentPlayer =
                observableGameState.map(GameState::currentPlayer);
        // Valeur observable contenant les points des joueurs
        ObservableValue<Map<PlayerColor, Integer>> pointsO =
                observableGameState.map(gameState -> gameState.messageBoard().points());

        for (PlayerColor color : observableGameState.getValue().players()) {
            Text pointsText = new Text();
            pointsText.textProperty().bind(pointsO.map(point -> {
                        int points = point.getOrDefault(color, 0);
                        return STR." \{text.playerName(color)} : \{text.points(points)} \n";
                    })
            );
            TextFlow playerInfo = new TextFlow(new Circle(5, fillColor(color)), pointsText);
            playerInfo.getStyleClass().add("player");

            for (Occupant.Kind k : List.of(HUT,PAWN)) {
                for (int i = 0; i < occupantsCount(k); ++i) {
                    int counter =i;
                    Node icon = Icon.newFor(color, k);
                    icon.opacityProperty().bind(observableGameState.map(
                            state-> state.freeOccupantsCount(color,k)> counter ? 1.0: 0.1 ));
                    playerInfo.getChildren().add(icon);
                }
                if(k==HUT){ playerInfo.getChildren().add(new Text("   ")); }
            }
            observableCurrentPlayer.addListener((_, _, newColor) -> {
                if (newColor == color) {
                    playerInfo.getStyleClass().add("current");
                } else {
                    playerInfo.getStyleClass().remove("current");
                }
            });
            vbox.getChildren().add(playerInfo);
        }
        return vbox;
    }
}
