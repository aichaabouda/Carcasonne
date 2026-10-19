package ch.epfl.chacun;

/* OCCUPANT
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */
import java.util.Objects;
import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Représente un occupant (pion ou hutte) d'une zone.
 */
public record Occupant(Kind kind , int zoneId) {
    /**
     * Le type énuméré Kind représente les différentes sortes d'occupants.
     */
    public enum Kind {
        PAWN, // Représente un pion
        HUT // Représente une hutte
    }

    /**
     * Constructeur compact de l'Occupant.
     *
     * @param kind   la sorte d'occupant
     * @param zoneId l'identifiant de la zone dans laquelle se trouve l'occupant
     * @throws NullPointerException     si kind est null
     * @throws IllegalArgumentException si zoneId est strictement négatif
     */

    public Occupant {
        Objects.requireNonNull(kind);
        checkArgument(zoneId >= 0);
    }

    /**
     * Retourne le nombre d'occupants de la sorte donnée que possède un joueur.
     *
     * @param kind la sorte d'occupant
     * @return le nombre d'occupants de cette sorte
     */

    public static int occupantsCount(Kind kind) {
        return switch (kind) {
            case PAWN -> 5;
            case HUT -> 3;
        };
    }

}
