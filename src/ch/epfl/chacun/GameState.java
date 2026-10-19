package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;
import static ch.epfl.chacun.GameState.Action.*;
import static ch.epfl.chacun.Preconditions.checkArgument;
import static ch.epfl.chacun.Tile.Kind.MENHIR;
import static ch.epfl.chacun.Tile.Kind.NORMAL;
import static java.lang.Math.min;

/**GAMESTATE
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 * Représente l'état du jeu, comprenant les joueurs, les paquets de tuiles, la tuile actuelle à placer, le plateau de
 * jeu, l'action suivante et le tableau des messages.
 * Cet enregistrement fournit des méthodes pour accéder et manipuler l'état du jeu.
 */
public record GameState(List<PlayerColor> players,TileDecks tileDecks, Tile tileToPlace,
                        Board board, Action nextAction,MessageBoard messageBoard) {

    /**Enumération qui donne toutes les actions possibles d'effectuer :
     START_GAME utilisée seulement au début pour lancer le jeu,
     END_GAME utilisée seulement à la fin pour arrêter le jeu,
     PLACE_TILE,
     RETAKE_PAWN si la tuile avec le chaman vient d'être placée,
     OCCUPY_TILE.
     */
    public enum Action {
        START_GAME, PLACE_TILE, RETAKE_PAWN, OCCUPY_TILE, END_GAME
    }

    /**Construit un nouvel objet GameState.
     * @throws IllegalArgumentException s'il y a (strictement) moins que 2 joueurs,
     * et : si la tuile à placer est nulle et la prochaine action est Place Tile
     * ou si la tuile à placer est non nulle et la prochaine action n'est pas Place Tile
     * @param players la liste d'au moins deux joueurs
     * @param tileDecks les paquets de tuiles qui ne peuvent pas être null
     * @param tileToPlace la tuile actuelle à placer
     * @param board le plateau de jeu qui ne peut pas être null
     * @param nextAction l'action suivante à effectuer qui ne peut pas être null
     * @param messageBoard le tableau des messages qui ne peut pas être null
     */
    public GameState {
        checkArgument(players.size() >= 2);
        checkArgument(tileToPlace == null ^ nextAction == Action.PLACE_TILE);
        Objects.requireNonNull(tileDecks);
        Objects.requireNonNull(board);
        Objects.requireNonNull(nextAction);
        Objects.requireNonNull(messageBoard);
        players = List.copyOf(players);
    }

    /** Crée un état de jeu initial.
     * @param players la liste des joueurs
     * @param tileDecks les paquets de tuiles
     * @param textMaker le créateur de texte
     * @return l'état de jeu initial
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        MessageBoard messageBoard= new MessageBoard(textMaker, List.of());
        return new GameState(players, tileDecks, null, Board.EMPTY,
                Action.START_GAME, messageBoard);
    }

    /** Obtient le joueur actuel si la prochaine action n'est pas END ou START.
     * @return la couleur du joueur actuel ou null s'il n'y en a pas
     */
    public PlayerColor currentPlayer() {
        return this.nextAction== START_GAME || this.nextAction== END_GAME ? null : this.players.getFirst();
    }

    /** Obtient le nombre d'occupants libres pour un joueur d'un type donné.
     * @param player le joueur en question
     * @param kind le type d'occupant
     * @return le nombre d'occupants libres
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - this.board.occupantCount(player, kind);
    }

    /** Obtient les occupants potentiels que peut placer le joueur actuel sur la dernière tuile placée sur le plateau.
     * @return l'ensemble des occupants potentiels
     * @throws IllegalArgumentException si le plateau est vide.
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        checkArgument(board.lastPlacedTile() !=null);

        PlacedTile tile = this.board.lastPlacedTile();
        Set<Occupant> allOccupants = tile.potentialOccupants();
        allOccupants.removeIf(o -> freeOccupantsCount(currentPlayer(), o.kind())==0 ||
                switch (tile.zoneWithId(o.zoneId())){ //enlever l'occupant si le joueur n'en a plus à placer de son type
                    case Zone.Forest forest ->  this.board.forestArea(forest).isOccupied(); //enlever l'occupant si la zone est une forêt et que l'occupant est une hutte ou si la forêt est occupée
                    case Zone.Meadow meadow ->  this.board.meadowArea(meadow).isOccupied(); //enlever l'occupant si la zone est un pré et que l'occupant est une hutte ou si le pré est occupé
                    case Zone.River river when o.kind() == Occupant.Kind.PAWN ->  this.board.riverArea(river).isOccupied(); //enlever l'occupant si la zone est une rivière (sans lac et que l'occupant est une hutte) ou (si la rivière est occupée)
                    case Zone.Water water -> this.board.riverSystemArea(water).isOccupied();
                });
        return allOccupants;
    }

    /**
     * Retourne un nouvel état du jeu après avoir placé la tuile de départ sur le plateau.
     * L'action suivante doit être "START_GAME" pour démarrer le jeu.
     * @return Un nouvel état du jeu avec la tuile de départ placée sur le plateau et les actions suivantes ajustées
     * @throws IllegalArgumentException Si l'action suivante n'est pas "START_GAME"
     */
    public GameState withStartingTilePlaced() {
        checkArgument(this.nextAction == Action.START_GAME);
        Board newBoard = this.board.withNewTile(
                new PlacedTile( tileDecks.topTile(Tile.Kind.START),
                        null, Rotation.NONE, Pos.ORIGIN) );
        return new GameState(players,
                tileDecks.withTopTileDrawn(Tile.Kind.START).withTopTileDrawn(NORMAL),
                tileDecks.topTile(NORMAL),
                newBoard,
                Action.PLACE_TILE, messageBoard);
    }



    /**
     * Retourne un nouvel état du jeu après avoir placé la tuile spécifiée sur le plateau.
     * L'action suivante doit être "PLACE_TILE" et la tuile ne doit pas déjà contenir un occupant.
     * @param tile La tuile à placer sur le plateau
     * @return Un nouvel état du jeu avec la tuile placée sur le plateau et les actions suivantes ajustées
     * @throws IllegalArgumentException Si l'action suivante n'est pas "PLACE_TILE" ou si la tuile contient déjà un occupant
     */
    public GameState withPlacedTile(PlacedTile tile) {
        Preconditions.checkArgument(nextAction==Action.PLACE_TILE && tile.occupant() == null);
        Board newBoard = this.board.withNewTile(tile);
        MessageBoard newMessageBoard= this.messageBoard;

        switch (tile.specialPowerZone()) {
            case Zone.Lake lake when lake.specialPower() == Zone.SpecialPower.LOGBOAT ->
                    newMessageBoard=newMessageBoard.withScoredLogboat(currentPlayer(), newBoard.riverSystemArea(lake));

            case Zone zone when zone.specialPower() == Zone.SpecialPower.SHAMAN -> {
                if(newBoard.occupantCount(currentPlayer(),Occupant.Kind.PAWN  ) >0 ) {
                    return new GameState(this.players, tileDecks, null, newBoard,
                            Action.RETAKE_PAWN, newMessageBoard);
                }
            }

            case Zone.Meadow meadow when meadow.specialPower() == Zone.SpecialPower.HUNTING_TRAP -> {
                Area<Zone.Meadow> adjacentMeadow= newBoard.adjacentMeadow(tile.pos(),meadow);
                Set<Animal> animals = Area.animals(adjacentMeadow, Set.of());
                List<Animal> deer = animals.stream().filter(a -> a.kind() == Animal.Kind.DEER).toList();
                int tigersCount = (int) animals.stream().filter(a -> a.kind() == Animal.Kind.TIGER).count();
                Set<Animal> cancelledDeer = Set.copyOf(deer.subList(0, min(tigersCount, deer.size())));
                newMessageBoard=newMessageBoard.withScoredHuntingTrap(currentPlayer(), adjacentMeadow, cancelledDeer);
                newBoard=newBoard.withMoreCancelledAnimals(animals);
            }

            case null, default -> {}
        }

        GameState gameState= new GameState(this.players, this.tileDecks, null, newBoard,
                Action.OCCUPY_TILE, newMessageBoard);
        return gameState.withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Retourne un nouvel état du jeu après avoir retiré l'occupant spécifié du plateau.
     * @param occupant L'occupant à retirer du plateau, ou null s'il n'y a pas d'occupant à retirer
     * @return Un nouvel état du jeu avec l'occupant retiré du plateau et l'action suivante ajustée
     * @throws IllegalArgumentException Si l'action suivante n'est pas "RETAKE_PAWN" ou si l'occupant est un pion et n'a pas l'action suivante spécifiée
     */
    public GameState withOccupantRemoved(Occupant occupant){
        Preconditions.checkArgument(nextAction == RETAKE_PAWN);
        Preconditions.checkArgument(occupant == null || occupant.kind() == Occupant.Kind.PAWN);

        Board updatedBoard =  occupant!=null ? this.board.withoutOccupant(occupant) : this.board;
        return new GameState(players, tileDecks,tileToPlace,updatedBoard,Action.OCCUPY_TILE,messageBoard)
                .withTurnFinishedIfOccupationImpossible();
    }

    /**
     * Retourne un nouvel état du jeu après avoir ajouté le nouvel occupant spécifié sur le plateau.
     * L'action suivante doit être "OCCUPY_TILE".
     * @param occupant Le nouvel occupant à ajouter sur le plateau
     * @return Un nouvel état du jeu avec le nouvel occupant ajouté sur le plateau et l'action suivante ajustée
     * @throws IllegalArgumentException Si l'action suivante n'est pas "OCCUPY_TILE"
     */
    public GameState withNewOccupant(Occupant occupant){
        checkArgument(this.nextAction == Action.OCCUPY_TILE);
        Board updatedBoard =  occupant!=null ? this.board.withOccupant(occupant) : this.board;
        return ( new GameState(players, tileDecks,tileToPlace,updatedBoard,Action.OCCUPY_TILE,messageBoard) )
                .withTurnFinished() ;
    }

    /**
     * Méthode utilitaire privée pour retourner l'ensemble des cerfs annulés par les tigres sur une tuile donnée.
     * Autant de cerfs que de tigres sont annulés.
     * @param animalsOnTile l'ensemble des animaux présents sur la tuile
     * @return l'ensemble des cerfs annulés par les tigres
     */
    private Set<Animal> cancelledDeerCount(Set<Animal> animalsOnTile) {
        return animalsOnTile.stream()
                .filter(animal -> animal.kind()== Animal.Kind.DEER)
                .limit(animalsOnTile.stream()
                        .filter(animal -> animal.kind()== Animal.Kind.TIGER)
                        .count())
                .collect(Collectors.toSet());
    }

    /**
     * Méthode privée qui vérifie si l'occupation de la dernière tuile posée est possible ou non.
     * @return l'état de jeu actuel si l'occupation de la dernière tuile posée est possible, sinon retourne l'état de jeu avec la fin du tour gérée.
     */
    private GameState withTurnFinishedIfOccupationImpossible(){
        return !lastTilePotentialOccupants().isEmpty() ? this : this.withTurnFinished();
    }


    /**
     * Méthode utilitaire privée pour mettre à jour l'état du jeu après qu'un joueur ait terminé son tour.
     * Cette méthode effectue les actions suivantes :
     * - Vérifie si des zones de forêt ou de rivière ont été complètement fermées par la dernière tuile placée,
     *   puis met à jour le tableau des messages et le plateau en conséquence.
     * - Si une zone de forêt avec menhir a été fermée et que la dernière tuile placée est normale,
     *   le prochain joueur reçoit une tuile avec un menhir s'il en reste dans le paquet de tuiles.
     * - Si aucune zone de forêt avec menhir n'a été fermée ou s'il n'y a plus de tuiles avec menhir,
     *   le prochain joueur reçoit une tuile normale.
     * - Si aucune tuile n'est disponible dans le paquet de tuiles normal, le jeu se termine et les points sont comptés.
     *
     * @return l'état du jeu mis à jour après la fin du tour
     */
    private GameState withTurnFinished() {
        TileDecks newTileDeck = this.tileDecks;
        Board newBoard = this.board;
        MessageBoard newMessageBoard = this.messageBoard;

        // Vérifie les zones de forêt et de rivière fermées par la dernière tuile et mets à jour le tableau d'affichage
        Set<Area<Zone.River>> riversClosed = board.riversClosedByLastTile();
        Set<Area<Zone.Forest>> forestsClosed = board.forestsClosedByLastTile();

        if (!(riversClosed.isEmpty() && forestsClosed.isEmpty())) {
            for (Area<Zone.Forest> forest : forestsClosed) {
                newMessageBoard = newMessageBoard.withScoredForest(forest);
            }
            for (Area<Zone.River> river : riversClosed) {
                newMessageBoard = newMessageBoard.withScoredRiver(river);
            }
            newBoard = newBoard.withoutGatherersOrFishersIn(forestsClosed, riversClosed);
        }


        Area<Zone.Forest> closedForestWithMenhir = forestsClosed.stream()
                .filter(Area::hasMenhir)
                .findFirst()
                .orElse(null);

        // Si une zone de forêt avec menhir a été fermée avec une tuile normale,
        // il y a possibilité pour le joueur actuel de joueur actuel de joueur un second tour (une tuile MENHIR)
        if (closedForestWithMenhir != null
                && newBoard.lastPlacedTile().kind() == NORMAL) {
            newTileDeck = newTileDeck.withTopTileDrawnUntil(MENHIR, newBoard::couldPlaceTile);
        }
        // Si aucune zone de forêt avec menhir n'a été fermée ou s'il n'y a plus de tuiles avec menhir,
        // il y a possibilité pour le prochain joueur de joueur actuel de joueur une tuile NORMALE
        if (closedForestWithMenhir != null
                && newBoard.lastPlacedTile().kind() == NORMAL && newTileDeck.deckSize(MENHIR) > 0) {
            newMessageBoard = newMessageBoard.withClosedForestWithMenhir(currentPlayer(), closedForestWithMenhir);
            Tile nextTile = newTileDeck.topTile(MENHIR);
            newTileDeck = newTileDeck.withTopTileDrawn(MENHIR);
            return new GameState(players, newTileDeck, nextTile, newBoard, Action.PLACE_TILE, newMessageBoard);
        } else {
            newTileDeck = newTileDeck.withTopTileDrawnUntil(Tile.Kind.NORMAL, newBoard::couldPlaceTile);
            if (newTileDeck.deckSize(Tile.Kind.NORMAL) > 0) {
                List<PlayerColor> newPlayers = new ArrayList<>(players);
                Collections.rotate(newPlayers, -1);
                Tile nextTile = newTileDeck.topTile(Tile.Kind.NORMAL);
                newTileDeck = newTileDeck.withTopTileDrawn(Tile.Kind.NORMAL);
                return new GameState(newPlayers, newTileDeck, nextTile, newBoard, PLACE_TILE, newMessageBoard);
            } else {
                return new GameState(players, newTileDeck, tileToPlace, newBoard, END_GAME, newMessageBoard)
                        .withFinalPointsCounted();// Si aucune tuile normale n'est disponible, le jeu se termine et les points sont comptés
            }
        }
    }

    /**
     * Méthode utilitaire privée pour calculer les points finaux de la partie en fonction des actions effectuées jusqu'à présent,
     * met à jour le tableau des messages avec les scores finaux et les gagnants, et retourne l'état
     * du jeu après le calcul des points finaux.
     *
     * @return l'état du jeu final après le calcul des points finaux et les vainqueurs
     */
    private GameState withFinalPointsCounted(){
        MessageBoard messageBoard = this.messageBoard;
        Board newBoard = this.board;

        // Parcourt toutes les zones de prés sur le plateau
        for (Area<Zone.Meadow> meadow : this.board.meadowAreas()) {
            Set<Animal> cancelledAnimals = new HashSet<>();
            Set<Animal> allAnimalsInArea = Area.animals(meadow, Set.of());

            boolean fire = meadow.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE) != null;
            boolean pitTrap = meadow.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP) != null;

            // Si la zone de pré a un feu et la grande fosse à pieux
            if (fire && pitTrap) {
                // Calcule les animaux annulés (aucun car il y a le feu) et met à jour le tableau des messages
                Zone.Meadow pitTrapZone = (Zone.Meadow) meadow.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);
                PlacedTile pitTrapTile = this.board.tileWithId(pitTrapZone.tileId());
                Area<Zone.Meadow> adjacentToPit = newBoard.adjacentMeadow(pitTrapTile.pos(), pitTrapZone);
                messageBoard = messageBoard.withScoredPitTrap(adjacentToPit, cancelledAnimals);


            }

            // Si la zone de prairie n'a que la grande fosse à pieux
            else if (pitTrap) {
                // Zone, tuile et aire adjacente à la grande fosse à pieux
                Zone.Meadow pitTrapZone = (Zone.Meadow) meadow.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);
                PlacedTile pitTrapTile = this.board.tileWithId(pitTrapZone.tileId());
                Area<Zone.Meadow> adjacentToPit = newBoard.adjacentMeadow(pitTrapTile.pos(), pitTrapZone);

                // Calcule les animaux présents dans et en dehors de la zone de la grande fosse à pieux
                Set<Animal> allAnimalsInPitTrap = Area.animals(adjacentToPit, newBoard.cancelledAnimals());
                Set<Animal> allAnimalsOutPitTrap = new HashSet<>(allAnimalsInArea);
                allAnimalsOutPitTrap.removeAll(allAnimalsInPitTrap);

                // Nombre de cerfs en dehors de la zone de la grande fosse à pieux et le nombre de tigres
                // en tout dans l'aire
                int deerCountOut = (int) allAnimalsOutPitTrap.stream()
                        .filter(animal -> animal.kind()== Animal.Kind.DEER).count();
                int allTigerCount = (int) allAnimalsInArea.stream()
                        .filter(animal -> animal.kind()==Animal.Kind.TIGER).count();

                cancelledAnimals = allAnimalsOutPitTrap.stream()
                        .filter(animal -> animal.kind()==Animal.Kind.DEER)
                        .limit(min(deerCountOut, allTigerCount))
                        .collect(Collectors.toSet());

                cancelledAnimals.addAll(allAnimalsInPitTrap.stream()
                        .filter(animal -> animal.kind()==Animal.Kind.DEER)
                        .limit(Math.max(0, allTigerCount - cancelledAnimals.size()))
                        .collect(Collectors.toSet()));

                messageBoard = messageBoard.withScoredPitTrap(adjacentToPit, cancelledAnimals);

            }
            // Si la zone de pré n'a ni le feu ni la grande fosse à pieux
            else if(! (fire||pitTrap)){
                // Calcule les animaux annulés par les tigres et met à jour le tableau des messages
                cancelledAnimals = cancelledDeerCount(allAnimalsInArea);
            }

            // Met à jour le plateau avec les animaux annulés
            newBoard = newBoard.withMoreCancelledAnimals(cancelledAnimals);
            // Met à jour le tableau des messages avec les scores des prés
            messageBoard = messageBoard.withScoredMeadow(meadow, newBoard.cancelledAnimals());
        }

        // Parcourt toutes les zones du système de rivières sur le plateau
        for (Area<Zone.Water>riverSys : this.board.riverSystemAreas()) {
            // Si la zone du système de rivières a un radeau : mise à jour le tableau des messages avec le score du radeau
            if (riverSys.zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                messageBoard = messageBoard.withScoredRaft(riverSys);
            }
            messageBoard = messageBoard.withScoredRiverSystem(riverSys);
        }

        Map<PlayerColor, Integer> points = messageBoard.points();
        int maxPoints = Collections.max(points.values());
        Set<PlayerColor> winners = points.entrySet().stream()
                .filter(player -> player.getValue() == maxPoints)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        messageBoard = messageBoard.withWinners(winners, maxPoints);

        return new GameState(this.players, this.tileDecks, null, newBoard, Action.END_GAME, messageBoard);


    }

}




