package ch.epfl.chacun;
/** BOARD
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.*;

import static ch.epfl.chacun.Zone.tileId;

/**
 * La classe Board représente le plateau de jeu du jeu ChaCuN.
 * Elle maintient les tuiles placées, les index des tuiles, les zones de partition et les animaux annulés.
 */
public class Board {
    private final PlacedTile[] tilesTab;
    private final int[] indexTab;
    private final ZonePartitions zonePartitions;
    private final Set<Animal> cancelledAnimals;

    /** La portée maximale du plateau. */
    public static final int REACH = 12;
    private static final int BOARD_LENGTH = REACH*REACH +1;
    private static final int BOARD_SIZE = BOARD_LENGTH*BOARD_LENGTH;

    /** Le plateau vide. */
    public static final Board EMPTY = new Board(new PlacedTile[BOARD_SIZE], new int[0], ZonePartitions.EMPTY, Set.of());
    /**
     * Crée un nouveau plateau de jeu avec les tuiles placées, les index des tuiles, les zones de partition et
     * les animaux annulés donnés.
     * @param tilesTab Les tuiles placées sur le plateau.
     * @param indexTab Les index des tuiles placées.
     * @param tileZones Les zones de partition des tuiles.
     * @param cancelledAnimals Les animaux annulés.
     */
    private Board(PlacedTile[] tilesTab, int[] indexTab, ZonePartitions tileZones, Set<Animal> cancelledAnimals) {
        this.tilesTab = tilesTab;
        this.indexTab = indexTab;
        this.zonePartitions = tileZones;
        this.cancelledAnimals = cancelledAnimals;
    }



    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Board other) && Arrays.equals(this.tilesTab, other.tilesTab) &&
                Arrays.equals(this.indexTab, other.indexTab) &&
                this.zonePartitions.equals(other.zonePartitions) &&
                this.cancelledAnimals.equals(other.cancelledAnimals);
    }


    @Override
    public int hashCode() {
        int tilesHash = Arrays.hashCode(tilesTab);
        int indexHash = Arrays.hashCode(indexTab);
        return Objects.hash(tilesHash, indexHash, zonePartitions, cancelledAnimals);
    }

    /**
     * Convertit une position (x, y) en un index correspondant dans le tableau de tuiles.
     *
     * @param pos La position à convertir en index.
     * @return L'index correspondant à la position spécifiée.
     */

    private int posToIndex (Pos pos){
        // Calcule l'index en fonction des coordonnées normalisées
        return isInBoard(pos) ?
                BOARD_LENGTH* (pos.y() + REACH) + (pos.x() + REACH)
                : -1;
    }

    /**
     * Renvoie la tuile à la position donnée sur le plateau.
     * @param pos La position de la tuile.
     * @return La tuile à la position donnée, ou null s'il n'y a pas de tuile à cette position.
     */

    public PlacedTile tileAt(Pos pos){
        return posToIndex(pos)==-1 ? null : tilesTab[posToIndex(pos)];
    }

    /**
     * Renvoie la tuile avec l'identifiant donné sur le plateau.
     * @param tileId L'identifiant de la tuile recherchée.
     * @return La tuile avec l'identifiant donné.
     * @throws IllegalArgumentException Si aucune tuile avec l'identifiant donné n'est présente sur le plateau.
     */

    public PlacedTile tileWithId(int tileId) {
        for (int index : indexTab) {
            if (tilesTab[index].id() == (tileId)) {
                return tilesTab[index];
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Renvoie l'ensemble des animaux annulés sur le plateau.
     * @return L'ensemble des animaux annulés.
     */

    public Set<Animal> cancelledAnimals() {
        return this.cancelledAnimals;
    }

    /**
     * Renvoie l'ensemble de tous les occupants présents sur les tuiles du plateau.
     *
     * @return L'ensemble de tous les occupants présents sur les tuiles du plateau.
     */

    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();
        for (int index : indexTab) {
            occupants.add(tilesTab[index].occupant());
        }
        occupants.remove(null);
        return occupants;
    }
    /**
     * Renvoie l'aire de la forêt contenant la zone donnée.
     *
     * @param forest La zone de la forêt pour laquelle trouver l'aire.
     * @return L'aire de la forêt contenant la zone spécifiée.
     * @throws IllegalArgumentException si la zone en question n'appartient pas au plateau.
     */

    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return zonePartitions.forests().areaContaining(forest);
    }
    /**
     * Renvoie l'aire du pré contenant la zone donnée.
     *
     * @param meadow La zone du pré pour laquelle trouver l'aire.
     * @return L'aire du pré contenant la zone spécifiée.
     * @throws IllegalArgumentException si la zone en question n'appartient pas au plateau.
     */

    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return zonePartitions.meadows().areaContaining(meadow);
    }

    /**
     * Renvoie l'aire de la rivière contenant la zone donnée.
     *
     * @param riverZone La zone de la rivière pour laquelle trouver l'aire.
     * @return L'aire de la rivière contenant la zone spécifiée.
     * @throws IllegalArgumentException si la zone en question n'appartient pas au plateau.
     */

    public Area<Zone.River> riverArea(Zone.River riverZone) {
        return zonePartitions.rivers().areaContaining(riverZone);
    }
    /**
     * Renvoie l'aire du réseau hydrographique contenant la zone donnée.
     *
     * @param water La zone du réseau hydrographique pour laquelle trouver l'aire.
     * @return L'aire du réseau hydrographique contenant la zone spécifiée.
     * @throws IllegalArgumentException si la zone en question n'appartient pas au plateau.
     */

    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        return zonePartitions.riverSystems().areaContaining(water);
    }

    /**
     * Renvoie l'ensemble de toutes les aires de pré sur le plateau.
     *
     * @return L'ensemble de toutes les aires de pré sur le plateau.
     */

    public Set<Area<Zone.Meadow>> meadowAreas() {
        return zonePartitions.meadows().areas();
    }
    /**
     * Renvoie l'ensemble de toutes les aires des réseaux hydrographiques sur le plateau.
     *
     * @return L'ensemble de toutes les aires des réseaux hydrographiques sur le plateau.
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return zonePartitions.riverSystems().areas();
    }

    /**
     * Retourne l'aire du pré adjacent à la zone donnée, sous la forme d'une aire qui ne contient que les zones
     * de ce pré mais tous les occupants du pré complet.
     * Cette aire, pour simplifier, ne possède aucune connexion ouverte.
     *
     * @param pos La position à partir de laquelle rechercher le pré adjacent.
     * @param meadowZone La zone de pré pour laquelle rechercher le pré adjacent.
     * @return L'aire du pré adjacent à la zone donnée.
     */

    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Set<Zone.Meadow> adjacentMeadow= new HashSet<>();
        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                PlacedTile tile = tileAt(pos.translated(i,j));
                if (tile!=null){
                    adjacentMeadow.addAll(tile.meadowZones());
                }
            }

        }
        Area<Zone.Meadow> meadowArea= meadowArea(meadowZone);
        adjacentMeadow.retainAll(meadowArea.zones());
        return new Area<>( adjacentMeadow,  meadowArea.occupants()  , 0);

    }

    /**
     * Retourne le nombre d'occupants de la sorte donnée appartenant au joueur donné et se trouvant sur le plateau.
     *
     * @param player Le joueur dont on souhaite compter les occupants.
     * @param occupantKind Le type d'occupant à compter.
     * @return Le nombre d'occupants de la sorte donnée appartenant au joueur spécifié sur le plateau.
     */

    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (int tile : indexTab) {
            PlacedTile placedTile = tilesTab[tile];
            if (placedTile.placer()==player && placedTile.idOfZoneOccupiedBy(occupantKind)!= -1) {
                count++;
            }
        }
        return count;

    }

    /** methode privée qui verifie que les bords de la tuile donnée ne debordent pas du tableau tilesTab
     * @param pos la position de la tuile
     * @return true si la tuile est bien placée sinon false
     * */
    private boolean isInBoard (Pos pos){
        return -REACH <= pos.x() && pos.x() <= REACH && -REACH <= pos.y() && pos.y() <= REACH;
    }
    /**
     * Retourne l'ensemble des positions d'insertion disponibles sur le plateau.
     *
     * @return Un ensemble de positions où il est possible d'insérer de nouvelles tuiles sur le plateau.
     */

    public Set<Pos> insertionPositions() {
        Set<Pos> positions = new HashSet<>();
        for (int index : indexTab) {
            for(Direction directions: Direction.ALL){
                Pos pos = tilesTab[index].pos().neighbor(directions);
                if (isInBoard(pos) && tileAt(pos) == null) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    /**
     * Retourne la dernière tuile posée sur le plateau, ou null si le plateau est vide.
     * La dernière tuile peut être la tuile de départ si la première tuile normale n'a pas encore été placée.
     *
     * @return La dernière tuile posée sur le plateau, ou null si le plateau est vide.
     */
    public PlacedTile lastPlacedTile() {
        return indexTab.length == 0 ?
                null
                : tilesTab[indexTab[indexTab.length-1]];
    }

    /**
     * Retourne l'ensemble de toutes les aires de forêt qui ont été fermées suite à la pose de la dernière tuile,
     * ou un ensemble vide si le plateau est vide.
     *
     * @return L'ensemble des aires de forêt fermées suite à la pose de la dernière tuile, ou un ensemble vide
     * si le plateau est vide.
     */

    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        if (indexTab.length == 0) {
            return  Set.of();
        }
        Set<Area<Zone.Forest>> closedForestArea = new HashSet<>();
        PlacedTile lastPlacedTile = lastPlacedTile();
        for (Zone.Forest forest : lastPlacedTile.forestZones()) {
            if (forestArea(forest).isClosed()) {
                closedForestArea.add(forestArea(forest));
            }
        }

        return closedForestArea;

    }
    /**
     * Retourne l'ensemble de toutes les aires de rivière qui ont été fermées suite à la pose de la dernière tuile,
     * ou un ensemble vide si le plateau est vide.
     *
     * @return L'ensemble des aires de rivière fermées suite à la pose de la dernière tuile, ou un ensemble vide si le plateau est vide.
     */

    public Set<Area<Zone.River>> riversClosedByLastTile() {
        if (indexTab.length == 0) {
            return  Set.of();
        }
        Set<Area<Zone.River>> closedRiverArea = new HashSet<>();
        PlacedTile lastPlacedTile = lastPlacedTile();
        for (Zone.River river : lastPlacedTile.riverZones()) {
            if (riverArea(river).isClosed()) {
                closedRiverArea.add(riverArea(river));
            }
        }
        return closedRiverArea;

    }

    /**
     * Vérifie si une tuile donnée peut être ajoutée au plateau.
     * Une tuile peut être ajoutée si sa position est valide et si chaque bord de la tuile
     * qui touche un bord d'une tuile déjà placée est de la même sorte que celui-ci.
     *
     * @param tile La tuile à vérifier pour l'ajout.
     * @return {@code true} si la tuile peut être ajoutée au plateau, {@code false} sinon.
     */

    public boolean canAddTile(PlacedTile tile){
        if(!isInBoard(tile.pos()) || tileAt(tile.pos())!=null){
            return false;
        }
        boolean cond = false;
        for (Direction direction : Direction.ALL) {
            PlacedTile placedTile = tileAt(tile.pos().neighbor(direction));
            if (placedTile != null) {
                cond=true;
                if (!tile.side(direction).isSameKindAs(placedTile.side(direction.opposite()))) {
                    return false;
                }
            }
        }
        return cond;
    }

    /**
     * Vérifie si une tuile donnée pourrait être placée sur le plateau.
     *
     * @param tile la tuile à tester.
     * @return true si la tuile peut être placée sur une position d'insertion valide du plateau, sinon false.
     */
    public boolean couldPlaceTile(Tile tile){
        for(Rotation r : Rotation.ALL){
            for(Pos p : insertionPositions()){
                PlacedTile testTile = new PlacedTile(tile, null, r, p);
                if(canAddTile(testTile)){return true; }
            }
        }
        return false;
    }

    /**
     * Retourne un nouveau plateau identique au plateau actuel, mais avec la tuile donnée en plus.
     *
     * @param tile La tuile à ajouter au plateau.
     * @return Un nouveau plateau avec la tuile ajoutée,
     * @throws IllegalArgumentException si la tuile ne peut pas être ajoutée et si le plateau n'est pas vide.
     */

    public Board withNewTile(PlacedTile tile){
        Preconditions.checkArgument(indexTab.length == 0 || canAddTile(tile));

        PlacedTile[] newTilesTab = this.tilesTab.clone();
        int index= posToIndex(tile.pos());
        newTilesTab[index]=tile;

        int[] newIndexTab= Arrays.copyOf(indexTab,indexTab.length +1);
        newIndexTab[newIndexTab.length -1]=index;

        ZonePartitions.Builder builder= new ZonePartitions.Builder(zonePartitions);
        builder.addTile(tile.tile());
        for (Direction direction : Direction.ALL) {
            PlacedTile placedTile = tileAt(tile.pos().neighbor(direction));
            if(placedTile!= null){
                builder.connectSides(tile.side(direction), placedTile.side(direction.opposite()));
            }
        }

        return new Board(newTilesTab, newIndexTab, builder.build(), this.cancelledAnimals);
    }

    /**
     * Retourne un nouveau plateau identique au plateau actuel, mais avec l'occupant donné ajouté.
     *
     * @param occupant L'occupant à ajouter sur le plateau.
     * @return Un nouveau plateau avec l'occupant ajouté.
     * @throws IllegalArgumentException Si la tuile sur laquelle se trouve l'occupant est déjà occupée.
     */

    public Board withOccupant(Occupant occupant){
        PlacedTile thisTile = tileWithId(tileId(occupant.zoneId()));

        int indexOfOccupantTile = posToIndex(thisTile.pos());
        PlacedTile[] newTilesTab = this.tilesTab.clone();
        newTilesTab[indexOfOccupantTile] = thisTile.withOccupant(occupant);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(this.zonePartitions);
        builder.addInitialOccupant(thisTile.placer(), occupant.kind(), thisTile.zoneWithId(occupant.zoneId()));
        return new Board (newTilesTab, this.indexTab, builder.build(), this.cancelledAnimals);
    }

    /**
     * Retourne un nouveau plateau identique au plateau actuel, mais avec l'occupant donné retiré.
     *
     * @param occupant L'occupant à retirer du plateau.
     * @return Un nouveau plateau avec l'occupant retiré.
     * @throws IllegalArgumentException Si l'occupant spécifié n'est pas présent sur le plateau.
     */

    public Board withoutOccupant(Occupant occupant){
        PlacedTile thisTile = tileWithId(tileId(occupant.zoneId()));

        int indexOfOccupantTile = posToIndex(thisTile.pos());
        PlacedTile[] newTilesTab = this.tilesTab.clone();
        newTilesTab[indexOfOccupantTile] =thisTile.withNoOccupant();

        ZonePartitions.Builder builder = new ZonePartitions.Builder(this.zonePartitions);
        builder.removePawn(thisTile.placer(), thisTile.zoneWithId(occupant.zoneId()));
        return new Board (newTilesTab, this.indexTab, builder.build(), this.cancelledAnimals);
    }

    /**
     * Retourne un nouveau plateau identique au plateau actuel, mais sans aucun occupant dans les forêts
     * et les rivières spécifiées.
     * @param forests Ensemble des aires forêts dans lesquelles retirer les occupants.
     * @param rivers  Ensemble des aires rivières dans lesquelles retirer les occupants.
     * @return Un nouveau plateau sans occupants dans les aires forêts et rivières spécifiées.
     */

    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(this.zonePartitions);

        PlacedTile[] tiles = this.tilesTab.clone();

        for (Area<Zone.Forest> forest : forests) {
            builder.clearGatherers(forest);
            for(Zone.Forest zone : forest.zones() ) {
                PlacedTile tile = tileWithId(Zone.tileId(zone.id()));
                if(tile.idOfZoneOccupiedBy(Occupant.Kind.PAWN)== zone.id()){
                    tiles[posToIndex(tile.pos())] = tile.withNoOccupant();
                }
            }
        }

        for (Area<Zone.River> river : rivers) {
            builder.clearFishers(river);
            for(Zone.River zone : river.zones() ) {
                PlacedTile tile = tileWithId(Zone.tileId(zone.id()));
                if(tile.idOfZoneOccupiedBy(Occupant.Kind.PAWN)== zone.id()){
                    tiles[posToIndex(tile.pos())] = tile.withNoOccupant();
                }
            }
        }
        return new Board(tiles, this.indexTab, builder.build(), this.cancelledAnimals);
    }

    /**
     * Retourne un nouveau plateau identique au plateau actuel, mais avec l'ensemble des animaux donnés
     * ajouté à l'ensemble des animaux annulés.
     *
     * @param newlyCancelledAnimals Ensemble des nouveaux animaux annulés à ajouter.
     * @return Un nouveau plateau avec les animaux annulés ajoutés.
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals){
        Set<Animal> allCancelledAnimals = new HashSet<>(this.cancelledAnimals);
        allCancelledAnimals.addAll(newlyCancelledAnimals);
        return new Board(this.tilesTab , this.indexTab , this.zonePartitions,
                Collections.unmodifiableSet(allCancelledAnimals));
    }
}
