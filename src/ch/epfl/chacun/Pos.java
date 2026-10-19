package ch.epfl.chacun;
/*POS
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

/**
 * Représente une position dans un système de coordonnées cartésiennes.
 */

public record Pos(int x,int y) {

    /** La position d'origine avec les coordonnées (0, 0). */
    public final static Pos ORIGIN = new Pos(0, 0);

    /**
     * Retourne une nouvelle position décalée selon les déplacements spécifiés.
     *
     * @param dX le déplacement en abscisse
     * @param dY le déplacement en ordonnée
     * @return la nouvelle position décalée
     */

    public Pos translated(int dX, int dY) {
        return new Pos(this.x + dX, this.y + dY);
    }

    /**
     * Retourne la position voisine du récepteur dans la direction spécifiée.
     *
     * @param direction la direction donnée
     * @return la position voisine
     */

    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> this.translated(0,-1);
            case S ->  this.translated(0,1);
            case E -> this.translated(1,0);
            case  W ->  this.translated(-1,0);

        };


    }
}
