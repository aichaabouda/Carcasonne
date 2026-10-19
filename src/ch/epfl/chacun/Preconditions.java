package ch.epfl.chacun;
/* PRECONDITIONS
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

/**
 * Classe utilitaire fournissant des méthodes pour vérifier les préconditions.
 */
public final class Preconditions {

    // Constructeur privé pour empêcher l'instanciation de la classe
    private Preconditions() {}

    /**
     * Vérifie qu'une condition spécifiée est vraie, sinon lance une IllegalArgumentException.
     *
     * @param shouldBeTrue La condition à vérifier.
     * @throws IllegalArgumentException Si la condition n'est pas vraie.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
