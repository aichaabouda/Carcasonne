package ch.epfl.chacun;
/* PLAYERCOLOR
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */


import java.util.List;
/**
 * Enumération des couleurs associées aux joueurs.
 * Les couleurs disponibles sont : ROUGE, BLEU, VERT, JAUNE et VIOLET.
 */

public enum PlayerColor {

    RED, BLUE, GREEN, YELLOW, PURPLE;

    /**
     * Liste immuable contenant toutes les valeurs du type énuméré, dans leur ordre de définition.
     */

    public static final List<PlayerColor> ALL= List.of(values());

}