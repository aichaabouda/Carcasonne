package ch.epfl.chacun;
/* ROTATION
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.List;

/* Enumération représentant les différentes rotations possibles. */
public enum Rotation {
    NONE,        // Aucune rotation
    RIGHT,       // Rotation d'un quart de tour vers la droite (sens horaire)
    HALF_TURN,   // Rotation d'un demi-tour
    LEFT;        // Rotation d'un quart de tour vers la gauche (sens antihoraire)

    public static final List<Rotation> ALL = List.of(values()); //Liste immuable de toutes les valeurs de l'énumération, dans l'ordre de définition.


    public static final int COUNT = ALL.size(); // Nombre d'éléments de l'énumération (Ici 4)

    /*Ajoute la rotation spécifiée à cette rotation et renvoie le résultat.
     * Cette méthode permet d'ajouter une rotation à une autre rotation. La méthode effectue
     * l'addition modulo du nombre total de rotations possibles (ici 4) , assurant que le résultat
     * reste dans l'ensemble des rotations disponibles.
     * Le module s'explique également par le fait que certaines rotations s'annulent mutuellement :
     * Par exemple : LEFT + RIGHT -> (3 + 1) % 4 = 0 -> NONE ce qui est bien à quoi on s'attend.
     * @param that la rotation à ajouter à cette rotation.
     * @return la rotation résultante après l'addition de cette rotation avec une autre.
     */
    public Rotation add(Rotation that) {
        return ALL.get((this.ordinal() + that.ordinal()) % COUNT);
    }

    /* Retourne la rotation négative du récepteur.
     * En l'occurrence, COUNT = 4.
     * Donc, selon la valeur de this :
     * NONE -> (4 - 0)% 4 = 0 -> NONE ce qui est trivial
     * RIGHT -> (4 - 1)% 4 = 3 -> LEFT pour annuler une rotation à droite ilf aut tourner à gauche
     * HALF -> (4 - 2)% 4 = 2 -> HALF pour annuler un demi tour il faut faire un autre demi tour pour arriver à la même place
     * LEFT -> (4 - 3)% 4 = 1 -> RIGHT
     * @return la rotation négative du récepteur.
     */
    public Rotation negated() {
        return ALL.get((COUNT - this.ordinal()) % COUNT);
    }

    /*Retourne le nombre de quarts de tours (dans le sens horaire) qu'il y a dans la rotation.
     * Cela correspond à l'ordinal de la rotation donnée(
     * aucune -> 0,
     * à droite -> 1 quart de tour dans le sens horaire,
     * demi tour -> 2 quarts de tour dans le sens horaire,
     * à gauche -> 1 quart de tour à gauche = 3 quarts de tour a droite).
     * @return Le nombre de quarts de tours.
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /*Retourne l'angle correspondant à la rotation dans le sens horaire, en degrés.
     * Un quart de tour = 90 degrés et on a vu que l'ordinal est le nombre de quarts de tour
     * (cf. la documentation de la méthode quarterTurnsCW )
     *  @return L'angle en degrés.
     */
    public int degreesCW() {
        return this.ordinal() * 90;
    }
}
