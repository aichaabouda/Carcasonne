package ch.epfl.chacun;
/*Tile
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Représente une tuile dans le jeu.
 * Une tuile est composée de quatre côtés (nord, est, sud, ouest) et appartient à un type spécifique (START, NORMAL, MENHIR).
 */
public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {

    public enum Kind {
        START, NORMAL, MENHIR //Énumération représentant les différents types de tuiles.
    }

    /*Retourne les côtés de la tuile dans l'ordre : nord, est, sud, ouest.
     * @return Une liste contenant les côtés de la tuile dans l'ordre spécifié.
     */
    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }

    /*Retourne l'ensemble des zones présentes sur les côtés de la tuile.
     * @return Un ensemble contenant toutes les zones présentes sur les côtés de la tuile.
     */
    public Set<Zone> sideZones() {
        return this.sides().stream()
                .flatMap(side -> side.zones().stream())
                .collect(Collectors.toSet());
    }

    /*Retourne l'ensemble de toutes les zones présentes sur la tuile, y compris les zones qui couvrent plusieurs côtés.
     * Si la zone est une rivière ayant un lac, la zone du lac est également incluse.
     * @return Un ensemble contenant toutes les zones présentes sur la tuile.
     */
    public Set<Zone> zones() {
        Set<Zone> zones = new HashSet<>();
        sideZones()
                .forEach(zone -> {
                    zones.add(zone);
                    if (zone instanceof Zone.River river && river.hasLake()) {
                        zones.add(river.lake());
                    }
                });
        return zones;
    }

}
