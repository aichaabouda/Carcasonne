package ch.epfl.chacun;

/*ZONE
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */


import java.util.List;

/*Interface sellée représentant toutes les zones possibles*/
public sealed interface Zone {

    enum SpecialPower { //Énumération représentant les pouvoirs spéciaux qu'une zone peut avoir
        SHAMAN, // Le chaman
        LOGBOAT, // La pirogue
        HUNTING_TRAP, // La fosse à pieux
        PIT_TRAP, // La grande fosse à pieux
        WILD_FIRE, // Le feu
        RAFT // Le radeau
    }

    /* Retourne l'identifiant de la tuile contenant la zone spécifiée.
     * @param zoneId L'identifiant de la zone.
     * @return L'identifiant de la tuile.
     */
    static int tileId(int zoneId) {
        return zoneId / 10;
    }


    /* Retourne l'identifiant local de la zone spécifiée
     * @param zoneId L'identifiant de la zone
     * @return L'identifiant local de la zone
     */
    static int localId(int zoneId) {
        return zoneId % 10;
    }

    /*Méthode abstraite qui retourne l'identifiant de la zone.
     * @return L'identifiant de la zone
     */
    int id();

    /*Méthode par défaut qui retourne l'identifiant de la tuile contenant cette zone
     * @return L'identifiant de la tuile
     */
    default int tileId() {
        return tileId(id());
    }

    /*Méthode par défaut qui retourne l'identifiant local de cette zone.
     * @return L'identifiant local de la zone
     */
    default int localId() {
        return localId(id());
    }

    /*Méthode par défaut qui retourne le pouvoir spécial de cette zone.
     * @return Le pouvoir spécial de la zone, ou null si elle n'en possède aucun (valeur retournée par défaut)
     */
    default SpecialPower specialPower() {
        return null;
    }



    record Forest(int id, Kind kind) implements Zone { //Représente une zone de type forêt.

        public enum Kind { //Énumération représentant les différents types de forêts
            PLAIN, // Une forêt vide
            WITH_MENHIR, // Une forêt contenant au moins un menhir
            WITH_MUSHROOMS // Une forêt contenant un groupe de champignons
        }
    }


    record Meadow(int id, List<Animal> animals, SpecialPower specialPower) implements Zone { //Représente une zone de type pré.

        /*Constructeur de Meadow.
         * @param animals La liste des animaux dans le pré.
         * @param specialPower Le pouvoir spécial de la zone.
         */
        public Meadow {
            animals = List.copyOf(animals);
        }
    }


    sealed interface Water extends Zone { //Interface pour les réseaux hydrographiques - zones aquatiques.

        /*Méthode abstraite qui retourne le nombre de poissons dans la zone.
         * @return Le nombre de poissons.
         */
        int fishCount();
    }


    record Lake(int id, int fishCount, SpecialPower specialPower) implements Water {} //Représente une zone de type lac.

    record River(int id, int fishCount, Lake lake) implements Water { //Représente une zone de type rivière.

        /*Indique si la rivière est associée à un lac
         * @return True si la rivière est associée à un lac, sinon False
         */
        public boolean hasLake() {
            return lake != null;
        }
    }
}
