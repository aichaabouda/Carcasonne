package ch.epfl.chacun;

/*
 * PLACEDTILE
 *
 * @author Soundouss Idrissi Kassimy(359384)
 * @author Aicha Abouda (361079)
 */

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Représente une tuile placée sur le plateau de jeu.
 */
public record PlacedTile (Tile tile, PlayerColor placer, Rotation rotation , Pos pos, Occupant occupant ) {

    /**
     * Constructeur principal avec vérification des arguments non nuls.
     * @throws NullPointerException si l'un des arguments est null.
     */
    public PlacedTile {
        Objects.requireNonNull(tile);
        Objects.requireNonNull(rotation);
        Objects.requireNonNull(pos);
    }

    /**
     * Constructeur sans occupant, avec vérification des arguments non nuls.
     *
     * @param tile      La tuile placée.
     * @param placer    Le placeur de la tuile, ou null pour la tuile de départ.
     * @param rotation  La rotation appliquée à la tuile.
     * @param pos       La position à laquelle la tuile a été placée.
     */
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    /**
     * Retourne l'identifiant de la tuile.
     * @return L'identifiant de la tuile.
     */
    public int id() {
        return tile.id();
    }

    /**
     * Retourne le type de tuile.
     * @return Le type de tuile.
     */
    public Tile.Kind kind() {
        return tile.kind();
    }

    /**
     * Retourne le côté de la tuile dans la direction spécifiée en tenant compte de la rotation appliquée à la tuile.
     * @param direction La direction du côté à récupérer.
     * @return Le côté de la tuile dans la direction spécifiée.
     */
    public TileSide side(Direction direction) {
        return switch (direction.rotated(rotation.negated())){
            case N -> tile.n();
            case E -> tile.e();
            case S ->  tile.s();
            case W ->  tile.w();
        };
    }

    /**
     * Retourne la zone avec l'identifiant spécifié.
     * @param id L'identifiant de la zone à rechercher.
     * @return La zone correspondant à l'identifiant spécifié.
     * @throws IllegalArgumentException si aucun zone ne correspond à l'identifiant donné.
     */
    public Zone zoneWithId(int id){
        for ( Zone z : tile.zones()){
            if(z.id()==id){
                return z ;
            }
        }
        throw new IllegalArgumentException("L'identifiant donné ne correspond à aucune zone de la tuile");
    }

    /**
     * Retourne la zone de la tuile ayant un pouvoir spécial s'il y en a une.
     * @return La zone spéciale de la tuile, ou null s'il n'y en a pas.
     */
    public Zone specialPowerZone(){
        for ( Zone z : tile.zones()){
            if(z.specialPower()!= null){
                return z;
            }
        }
        return null;
    }

    /**
     * Retourne un ensemble des zones forêt de la tuile.
     * @return Un ensemble des zones forêt de la tuile.
     */
    public Set<Zone.Forest> forestZones() {
        Set<Zone.Forest> forests = new HashSet<>();
        for (Zone z : tile.sideZones()) {
            if (z instanceof Zone.Forest forest) {
                forests.add(forest);
            }
        }
        return forests;
    }

    /**
     * Retourne un ensemble des zones pré de la tuile.
     * @return Un ensemble des zones pré de la tuile.
     */
    public Set<Zone.Meadow> meadowZones(){
        Set<Zone.Meadow> meadows = new HashSet<>();
        for (Zone z : tile.sideZones()) {
            if (z instanceof Zone.Meadow meadow) {
                meadows.add(meadow);
            }
        }
        return meadows;
    }

    /**
     * Retourne un ensemble des zones rivière de la tuile.
     * @return Un ensemble des zones rivière de la tuile.
     */
    public Set<Zone.River> riverZones(){
        Set<Zone.River> rivers = new HashSet<>();
        for (Zone z : tile.sideZones()) {
            if (z instanceof Zone.River river) {
                rivers.add(river);
            }
        }
        return rivers;
    }

    /**
     * Retourne un ensemble des occupants potentiels de la tuile.
     * @return Un ensemble des occupants potentiels de la tuile.
     */
    public Set<Occupant> potentialOccupants(){
        Set<Occupant> potentialOccupants = new HashSet<>();
        if(placer==null){
            return potentialOccupants;
        }
        for (Zone z : tile.sideZones() ){
            potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, z.id()));
            if(riverZones().contains(z)){
                Zone.River river = (Zone.River) z;
                potentialOccupants.add(new Occupant(Occupant.Kind.HUT,
                        river.hasLake() ?
                                river.lake().id()
                                : river.id()
                        ));
            }
        }
        return potentialOccupants;
    }


    /**
     * Retourne une nouvelle tuile placée avec l'occupant spécifié.
     * @param occupant L'occupant à placer sur la tuile.
     * @return Une nouvelle tuile placée avec l'occupant spécifié.
     * @throws IllegalArgumentException si la tuile est déjà occupée.
     */
    public PlacedTile withOccupant(Occupant occupant){
        Preconditions.checkArgument(this.occupant == null) ;
        return new PlacedTile(tile, placer, rotation, pos, occupant);

    }

    /**
     * Retourne une nouvelle tuile placée sans occupant.
     * @return Une nouvelle tuile placée sans occupant.
     */
    public PlacedTile withNoOccupant(){
        return new PlacedTile(tile, placer, rotation, pos);
    }

    /**
     * Retourne l'identifiant de la zone occupée par un occupant de la sorte donnée.
     * @param occupantKind La sorte d'occupant pour laquelle rechercher la zone occupée.
     * @return L'identifiant de la zone occupée par un occupant de la sorte donnée,
     * ou -1 si aucune zone n'est occupée ou si l'occupant n'est pas de la bonne sorte.
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        return occupant == null || occupantKind!= occupant.kind() ?
                -1 : occupant.zoneId();
    }
}