package ch.epfl.chacun;
/*TileDecks
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.List;
import java.util.function.Predicate;

/*Représente le tas de toutes les tuiles qu'on peut placer dans le jeu.
 * Cette classe gère trois types de paquets : les tuiles de départ, les tuiles normales et les tuiles de menhir.
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {

    /*Construit un tas de tuiles avec les listes fournies.
     * @param startTiles Liste des tuiles de départ.
     * @param normalTiles Liste des tuiles normales.
     * @param menhirTiles Liste des tuiles de menhir.
     */
    public TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {
        this.startTiles = List.copyOf(startTiles);
        this.normalTiles = List.copyOf(normalTiles);
        this.menhirTiles = List.copyOf(menhirTiles);
    }

    /*Récupère la taille d'un paquet spécifique en fonction du type de tuiles donné.
     * @param kind Le type de tuiles (START, NORMAL, MENHIR).
     * @return La taille du paquet correspondant au type spécifié.
     */
    public int deckSize(Tile.Kind kind) {
        return switchKind(kind).size();
    }

    /*Récupère la tuile supérieure d'un paquet spécifique en fonction du type de tuiles.
     * @param kind Le type de tuiles (START, NORMAL, MENHIR) dont on veut récuperer la première tuile (tuile du haut).
     * @return La tuile supérieure du type spécifié, ou null si le paquet est vide.
     */
    public Tile topTile(Tile.Kind kind) {
        List<Tile> kindTile = switchKind(kind);
        return !kindTile.isEmpty() ? kindTile.getFirst() : null;
    }

    /*Crée un nouvel objet TileDecks avec la tuile supérieure tirée d'un paquet spécifique (sans la tuile supérieure d'un paquet spécifique) .
     * @param kind Le type de tuiles (START, NORMAL, MENHIR) en question.
     * @return Un nouvel objet TileDecks identique à this avec la tuile supérieure tirée du paquet spécifié.
     * @throws IllegalArgumentException si le paquet spécifié est vide.
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        List<Tile> tileListOfKind = switchKind(kind);
        Preconditions.checkArgument(!tileListOfKind.isEmpty());
        List<Tile> tileListWithoutTopTile = tileListOfKind.subList(1, tileListOfKind.size());

        return switch (kind) {
            case START -> new TileDecks(tileListWithoutTopTile, normalTiles, menhirTiles);
            case NORMAL -> new TileDecks(startTiles, tileListWithoutTopTile, menhirTiles);
            case MENHIR -> new TileDecks(startTiles, normalTiles, tileListWithoutTopTile);
        };
    }

    /*Crée un nouvel objet TileDecks en tirant les tuiles du type spécifié jusqu'à ce qu'une tuile remplisse le prédicat donné.
     * @param kind Le type de tuiles (START, NORMAL, MENHIR).
     * @param predicate La condition à remplir par la tuile tirée.
     * @return Un nouvel objet TileDecks avec des tuiles tirées du paquet spécifié jusqu'à ce que la condition soit remplie.
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks newDeck = this;
        while (!newDeck.switchKind(kind).isEmpty() && !predicate.test(newDeck.topTile(kind))) {
            newDeck = newDeck.withTopTileDrawn(kind);
        }
        return newDeck;
    }



    /*Méthode utilitaire privée pour récupèrer la liste de tuiles correspondant au type spécifié.
     * @param kind Le type de tuiles (START, NORMAL, MENHIR).
     * @return La liste de tuiles correspondant au type spécifié.
     */
    private List<Tile> switchKind(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles;
            case NORMAL -> normalTiles;
            case MENHIR -> menhirTiles;
        };
    }
}

