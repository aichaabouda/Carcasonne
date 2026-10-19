package ch.epfl.chacun;
/** DIRECTION
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.List;

/**
 * Enumération des directions correspondant aux points cardinaux.
 * Les directions disponibles sont : N (nord), E (est), S (sud) et W (ouest).
 */

public enum Direction {
    N, //(haut de l'écran)
    E, //(droite de l'écran)
    S, //(bas de l'écran)
    W; //(gauche de l'écran)

    public static final List<ch.epfl.chacun.Direction> ALL = List.of(values());
    public static final int COUNT = ALL.size();
    /**
     * Retourne la direction après l'application d'une rotation au recepteur.
     *
     * @param rotation la rotation à appliquer
     * @return la direction résultante
     */
    public Direction rotated(Rotation rotation) {
        return ALL.get((this.ordinal() + rotation.quarterTurnsCW()) % COUNT);
    }

    /**
     * Retourne la direction opposée à celle du récepteur.
     *
     * @return la direction opposée
     */
    public Direction opposite(){
        return switch(this){
            case N -> S;
            case S ->N;
            case E ->W;
            case W ->E;

        };
    }

}