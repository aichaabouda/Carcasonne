package ch.epfl.chacun;
/**
 * ANIMAL
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */


/**
 * Représente un animal situé dans un pré.
 */

public record Animal(int id,Kind kind) {

    /**
     * Le type énuméré Kind représente les différentes sortes d'animaux.
     */

    public enum Kind {
        MAMMOTH , // Représente un mammouth
        AUROCHS , // Représente un auroch
        DEER , // Représente un cerf
        TIGER  // Représente un smilodon
    }

    /**
     * Retourne l'identifiant de la tuile sur laquelle se trouve l'animal.
     *
     * @return l'identifiant de la tuile
     */

    public int tileId(){
        return Zone.tileId(this.id/10);

    }

}