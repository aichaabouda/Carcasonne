package ch.epfl.chacun;

/*TileSide
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.List;

/* Interface sellée représentant un côté de tuile.*/
public sealed interface TileSide {

    /*Retourne les zones qui touchent ce côté de la tuile.
     * @return La liste des zones touchant ce côté.
     */
    List<Zone> zones();

    /*Vérifie si ce côté (this) est de la même catégorie que le côté donné.
     * @param that Le côté à comparer avec this.
     * @return true si les deux côtés sont du même type, sinon false.
     */
    boolean isSameKindAs(TileSide that);


    /*Enregistrement représentant un côté de tuile de forêt.*/
    record Forest(Zone.Forest forest) implements TileSide {

        /*Redefinition de la méthode abstraite au dessus.
        * @return la liste des zones touchant ce côté de la tuile.
        */
        @Override
        public List<Zone> zones() {
            return List.of(forest);
        }

        /*Redefinition de la méthode abstraite au dessus.
         * @param that Le côté à comparer avec this.
         * @return true si les deux côtés sont du même type, sinon false.
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Forest;
        }
    }

    /*Enregistrement représentant un côté de tuile de pré.*/
    record Meadow(Zone.Meadow meadow) implements TileSide {

        /*Redefinition de la méthode abstraite au dessus.
         * @return la liste des zones touchant ce côté de la tuile.
         */
        @Override
        public List<Zone> zones() {
            return List.of(meadow);
        }

        /*Vérifie si ce côté est du même type que le côté donné.
         * @param that Le côté à comparer avec this.
         * @return true si les deux côtés sont du même type, sinon false.
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Meadow;
        }
    }



    /*Enregistrement représentant un côté de tuile de rivière.*/
    record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {
        /*Retourne la liste des zones touchant ce côté de la tuile.
         * @return La liste contenant les zones de pré et de rivière.
         */
        @Override
        public List<Zone> zones() {
            return List.of(meadow1, river, meadow2);
        }

        /*Vérifie si ce côté est du même type que le côté donné.
         * @param that Le côté à comparer avec this.
         * @return true si les deux côtés sont du même type, sinon false.
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof River;
        }
    }
}
