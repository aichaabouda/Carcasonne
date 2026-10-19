package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

/**ICON
 * Cette classe abstraite fournit une interface pour créer les icônes des joueurs
 * en fonction de la couleur du joueur et du type d'occupant.
 * Les icônes sont créées en utilisant des formes SVG et en appliquant des couleurs
 * en fonction de la couleur du joueur spécifié.
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */
public final class Icon {
    private Icon(){} // Le constructeur est privé pour empêcher l'instanciation de la classe

    /**
     * Crée une nouvelle icône pour le joueur avec la couleur spécifiée et le type d'occupant donné.
     *
     * @param color La couleur du joueur
     * @param occupant Le type d'occupant (PAWN ou HUT)
     * @return Un objet Node représentant l'icône du joueur
     */
    public static Node newFor(PlayerColor color, Occupant.Kind occupant) {
        SVGPath playerIcon = new SVGPath();
        switch (occupant) {
            case PAWN:
                playerIcon.setContent( //retour à la ligne pour ne pas dépasser la ligne grise
                        "M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6L 6 -10 L 0"+
                                "-10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z");
                break;
            case HUT:
                playerIcon.setContent("M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z");
                break;
        }

        playerIcon.setFill(ColorMap.fillColor(color));
        playerIcon.setStroke(ColorMap.strokeColor(color));

        return playerIcon;


    }
}
