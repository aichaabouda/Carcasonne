package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

import java.util.Objects;

/**COLORMAP
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public abstract class ColorMap {

    private ColorMap(){} // Le constructeur est privé pour empêcher l'instanciation de la classe

    /**
     * Renvoie la couleur de remplissage correspondant à la couleur du joueur spécifié.
     *
     * @param player La couleur du joueur
     * @return La couleur de remplissage associée à la couleur du joueur
     */

    public static javafx.scene.paint.Color fillColor(PlayerColor player){
        Objects.requireNonNull(player);
        return switch(player){
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case YELLOW -> Color.YELLOW;
            case PURPLE -> Color.PURPLE;
        };
    }

    /**
     * Renvoie la couleur de trait correspondant à la couleur du joueur spécifié : blanc
     * avec une version moins lumineuse pour les joueurs jaune et vert.
     *
     * @param player La couleur du joueur
     * @return La couleur de trait associée à la couleur du joueur
     */
    public static javafx.scene.paint.Color strokeColor(PlayerColor player){
        return (player == PlayerColor.YELLOW || player == PlayerColor.GREEN) ?
                Color.WHITE.deriveColor(0, 1, 0.6, 1)
                : Color.WHITE;
    }


}

