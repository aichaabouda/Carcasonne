package ch.epfl.chacun;

/*ZONE PARTITION
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*Représente une partition de zones.
 * @param <Z> le type générique de la zone
 */
public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas) {

    /*Constructeur de la partition de zones.
     * @param areas les aires de la partition
     */
    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    /*Constructeur par défaut d'une partition de zones vide.*/
    public ZonePartition() {
        this(Set.of());
    }

    /*Retourne l'aire contenant la zone spécifiée.
     * @param zone la zone à rechercher
     * @return l'aire contenant la zone spécifiée
     * @throws IllegalArgumentException si aucune aire ne contient la zone donnée
     */
    public Area<Z> areaContaining(Z zone) {
        return this.areas.stream()
                .filter(area -> area.zones().contains(zone))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune aire ne contient la zone donnée"));
    }

    /*Constructeur de la classe imbriquée statique Builder.
     * @param <Z> le type de la zone
     */
    public static final class Builder<Z extends Zone> {
        private final HashSet<Area<Z>> areas;

        /*Constructeur du Builder à partir d'une partition existante.
         * @param partition la partition existante à partir de laquelle construire
         */
        public Builder(ZonePartition<Z> partition) {
            this.areas = new HashSet<>(partition.areas());
        }

        /*Ajoute une aire individuelle composée de la zone et du nombre de connections donnés à la partition.
         * @param zone la zone contenue dans la nouvelle aire à ajouter
         * @param openConnections le nombre de connexions ouvertes de l'aire
         */
        public void addSingleton(Z zone, int openConnections) {
            this.areas.add(new Area<>(Set.of(zone), Collections.emptyList(), openConnections));
        }

        /*Ajoute un occupant initial à une aire existante dans la partition.
         * @param zone la zone de l'aire à laquelle on ajoite l'occupant
         * @param color la couleur de l'occupant initial
         * @throws IllegalArgumentException si la zone n'appartient pas à une aire de la partition
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {
            Area<Z> areaOfTheZone = findAreaForZone(zone);

            Area<Z> newArea = areaOfTheZone.withInitialOccupant(color);
            this.areas.remove(areaOfTheZone);
            this.areas.add(newArea);
        }


        /*Supprime un occupant spécifique d'une zone de la partition.
         * @param zone la zone de l'occupant à supprimer
         * @param color la couleur de l'occupant à supprimer
         * @throws IllegalArgumentException si la zone n'appartient pas à une aire de la partition
         */
        public void removeOccupant(Z zone, PlayerColor color) {
            Area<Z> targetArea = findAreaForZone(zone);

            Area<Z> newArea = targetArea.withoutOccupant(color);
            this.areas.remove(targetArea);
            this.areas.add(newArea);
        }


        /*Supprime tous les occupants de l'aire spécifiée dans la partition.
         * @param area l'aire dont on doit supprimer tous les occupants
         * @throws IllegalArgumentException si l'aire n'est pas présente dans la partition
         */
        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(this.areas.remove(area));
            Area<Z> newArea = area.withoutOccupants();
            this.areas.add(newArea);
        }


        /*Fusionne deux aires de la partition.
         * @param zone1 la première zone à fusionner
         * @param zone2 la deuxième zone à fusionner
         * @throws IllegalArgumentException si l'une des deux zones n'appartient pas à une aire de la partition
         */
        public void union(Z zone1, Z zone2) {
            Area<Z> area1 = findAreaForZone(zone1);
            Area<Z> area2 = findAreaForZone(zone2);

            Area<Z> newArea = area1.connectTo(area2);
            this.areas.remove(area1);
            if (!area1.equals(area2)) {
                this.areas.remove(area2);
            }
            this.areas.add(newArea);
        }

        /*Construit et retourne la partition de zones.
         * @return la partition de zones construite
         */
        public ZonePartition<Z> build() {
            return new ZonePartition<>(this.areas);
        }

        /*Méthode utilitaire privée pour cherher l'aire contenant la zone spécifiée.
         * @param zone la zone à rechercher
         * @return l'aire contenant la zone spécifiée
         * @throws IllegalArgumentException si aucune aire ne contient la zone donnée
         */
        private Area<Z> findAreaForZone(Z zone) {
            return this.areas.stream()
                    .filter(area -> area.zones().contains(zone))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
