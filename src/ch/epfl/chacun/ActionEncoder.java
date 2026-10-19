package ch.epfl.chacun;

import java.util.Comparator;
import java.util.List;

/**
 * ACTIONENCODER
 * Classe utilitaire non instanciable pour encoder et décoder les actions de jeu.
 * Elle fournit des méthodes pour appliquer des actions spécifiques au jeu et générer
 * des représentations encodées de ces actions.
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public final class ActionEncoder {

    private ActionEncoder() {}// Constructeur privé pour une classe non instanciable

    /**
     * Enregistrement imbriqué pour contenir l'état du jeu et l'action encodée.
     */
    public record StateAction(GameState gameState0, String actionEncoded0) {}

    private static final int NO_OCCUPANT = 0b11111;

    /**
     * Applique l'action de placement d'une tuile et génère la représentation encodée de cette action.
     *
     * @param gameState L'état actuel du jeu.
     * @param placedTile La tuile placée sur le plateau.
     * @return Un enregistrement StateAction contenant le nouvel état du jeu et l'action encodée.
     */
    public static StateAction withPlacedTile(GameState gameState, PlacedTile placedTile) {
        String actionEncoded = Base32.encodeBits10((
                sortedPositions(gameState).indexOf(placedTile.pos()) << 2)
                | placedTile.rotation().ordinal());
        return new StateAction(gameState.withPlacedTile(placedTile), actionEncoded);
    }


    /**
     * Applique l'action d'ajout d'un nouvel occupant et génère la représentation encodée de cette action.
     *
     * @param gameState L'état actuel du jeu.
     * @param occupant Le nouvel occupant à ajouter.
     * @return Un enregistrement StateAction contenant le nouvel état du jeu et l'action encodée.
     */
    public static StateAction withNewOccupant(GameState gameState, Occupant occupant) {
        GameState newState = gameState.withNewOccupant(occupant);
        return (occupant==null) ?
                new StateAction(newState,Base32.encodeBits5(NO_OCCUPANT))
                : new StateAction(newState, Base32.encodeBits5
                ((occupant.kind().ordinal() << 4)
                                | Zone.localId(occupant.zoneId()) ));
    }

    /**
     * Applique l'action de retrait d'un occupant et génère la représentation encodée de cette action.
     *
     * @param gameState L'état actuel du jeu.
     * @param occupant L'occupant à retirer.
     * @return Un enregistrement StateAction contenant le nouvel état du jeu et l'action encodée.
     */
    public static StateAction withOccupantRemoved(GameState gameState, Occupant occupant) {
        GameState newState = gameState.withOccupantRemoved(occupant);
        return occupant== null ?
                new StateAction(newState,Base32.encodeBits5(NO_OCCUPANT))
                : new StateAction(newState, Base32.encodeBits5
                ( sortedPawn(gameState).indexOf(occupant) ));
    }

    /**
     * Décode et applique une action encodée à l'état du jeu.
     *
     * @param gameState L'état actuel du jeu.
     * @param encodedAction L'action encodée sous forme de chaîne de caractères.
     * @return Un enregistrement StateAction contenant le nouvel état du jeu et l'action encodée,
     * ou null en cas d'erreur.
     */

    public static StateAction decodeAndApply(GameState gameState, String encodedAction) {
        try { return decodeAction(gameState,encodedAction);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Décode et applique une action encodée à l'état du jeu.
     *
     * @param gameState L'état actuel du jeu.
     * @param encodedAction L'action encodée sous forme de chaîne de caractères.
     * @return Un enregistrement StateAction contenant le nouvel état du jeu et l'action encodée.
     * @throws Exception Si l'action encodée est invalide ou ne peut pas être appliquée.
     */

    private static StateAction decodeAction(GameState gameState, String encodedAction) throws Exception {
        if (!Base32.isValid(encodedAction)) {
            throw new Exception();
        }

        GameState newGameState= switch (gameState.nextAction()) {
            case PLACE_TILE -> {
                if (encodedAction.length() !=2) { throw new Exception();}
                int actionValue = Base32.decode(encodedAction);
                //extraction de la position de la case de la frange sur laquelle la tuile doit être placée
                Pos position = isValid(sortedPositions(gameState), actionValue >> 2 );
                Rotation rotation = Rotation.ALL.get(actionValue & 0x03); //masque pour conserver uniquement les
                // 2 bits de poids faible de "actionValue" qui represente la rotation
                PlacedTile newPlacedTile = new PlacedTile(
                        gameState.tileToPlace(),
                        gameState.currentPlayer(),
                        rotation,
                        position);
                if(!gameState.board().canAddTile(newPlacedTile)) {throw new Exception();}
                yield gameState.withPlacedTile(newPlacedTile);
            }

            case OCCUPY_TILE -> {
                if (encodedAction.length() !=1) {throw new Exception();}

                int actionValue = Base32.decode(encodedAction);
                if( actionValue==NO_OCCUPANT){
                    yield gameState.withNewOccupant(null);
                }

                int indexKind = actionValue >> 4;
                Occupant.Kind kind=Occupant.Kind.values()[indexKind];
                int zone = actionValue & 0x0F;

                yield gameState.lastTilePotentialOccupants().stream()
                        .filter(o->o.kind()==kind && Zone.localId(o.zoneId())==zone)
                        .findFirst()
                        .map(gameState::withNewOccupant)
                        .orElseThrow(Exception::new);
            }

            case RETAKE_PAWN -> {
                if (encodedAction.length() !=1) {throw new Exception();}
                int actionValue = Base32.decode(encodedAction);
                if( actionValue==NO_OCCUPANT){
                    yield gameState.withOccupantRemoved(null);
                }
                Occupant occupant = isValid(sortedPawn(gameState),actionValue);
                PlayerColor playerColor=gameState.board().tileWithId(Zone.tileId(occupant.zoneId())).placer();
                if(playerColor!=gameState.currentPlayer()){throw new Exception();}
                yield gameState.withOccupantRemoved(occupant);

            }
            case START_GAME, END_GAME -> throw new Exception();
        };
        return new StateAction(newGameState, encodedAction);

    }

    /**
     * Vérifie si l'index donné est valide pour la liste spécifiée et retourne l'élément correspondant.
     *
     * @param <T> Le type des éléments dans la liste.
     * @param list La liste dans laquelle vérifier l'index.
     * @param index L'index à vérifier.
     * @return L'élément de la liste à l'index spécifié.
     * @throws Exception Si l'index est invalide (c'est-à-dire qu'il est hors des limites de la liste).
     */
    private static <T> T isValid(List<T> list, int index) throws Exception{
        if (!(index<list.size())){
            throw new Exception();
        }
        return list.get(index);
    }

    /**
     * Retourne une liste des positions d'insertion possibles pour les tuiles sur le plateau,
     * triée par coordonnées x puis y.
     *
     * @param gameState L'état actuel du jeu.
     * @return Une liste triée des positions d'insertion possibles sur le plateau.
     */
    private static List<Pos> sortedPositions(GameState gameState) {
        return gameState.board().insertionPositions()
                .stream()
                .sorted(Comparator.comparingInt(Pos::x).thenComparingInt(Pos::y))
                .toList();
    }

    /**
     * Retourne une liste triée de tous les pions (PAWN) sur le plateau de jeu,
     * triée par leur identifiant de zone.
     *
     * @param gameState L'état actuel du jeu.
     * @return Une liste triée de tous les pions sur le plateau de jeu.
     */
    private static List<Occupant> sortedPawn(GameState gameState){
        return gameState.board().occupants()
                .stream()
                .filter(e-> e.kind() == Occupant.Kind.PAWN)
                .sorted(Comparator.comparingInt(Occupant::zoneId))
                .toList();
    }
}
