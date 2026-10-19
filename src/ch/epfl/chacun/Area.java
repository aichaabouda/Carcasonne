package ch.epfl.chacun;
/**
 * AREA
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.*;

/**
 * Représente une aire d'un jeu.
 *
 * @param <Z> le type de la zone, qui doit étendre la classe Zone.
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {

    /**
     * Constructeur de la classe Area.
     *
     * @param zones            les zones constituant l'aire.
     * @param occupants        les couleurs des éventuels joueurs occupant l'aire.
     * @param openConnections le nombre de connexions ouvertes de l'aire.
     * @throws IllegalArgumentException si openConnections n'est pas positif ou nul.
     */
    public Area(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {
        Preconditions.checkArgument(openConnections >= 0);
        this.zones = Set.copyOf(zones);
        this.occupants = List.copyOf(colorSort(occupants));
        this.openConnections = openConnections;
    }

    /**
     * Méthode utilitaire privée.
     * Trie la liste des occupants par couleur.
     *
     * @param toSort la liste des occupants à trier.
     * @return la liste triée des occupants.
     */
    private List<PlayerColor> colorSort(List<PlayerColor> toSort) {
        List<PlayerColor> sortedOccupants = new ArrayList<>(toSort);
        Collections.sort(sortedOccupants);
        return sortedOccupants;
    }

    /**
     * Vérifie si l'aire contient un menhir dans une forêt.
     *
     * @param forest l'aire représentant une forêt.
     * @return true si la forêt contient un menhir, sinon false.
     */
    public static boolean hasMenhir(Area<Zone.Forest> forest) {
        for (Zone.Forest f : forest.zones) {
            if (f.kind().equals(Zone.Forest.Kind.WITH_MENHIR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compte le nombre de groupes de champignons dans une forêt.
     *
     * @param forest l'aire représentant une forêt.
     * @return le nombre de groupes de champignons dans la forêt.
     */
    public static int mushroomGroupCount(Area<Zone.Forest> forest) {
        return (int) forest.zones.stream()
                .filter(f -> f.kind().equals(Zone.Forest.Kind.WITH_MUSHROOMS)).count();
    }

    /**
     * Retourne l'ensemble des animaux présents dans un pré, après avoir retiré certains animaux annulés.
     *
     * @param meadow          l'aire représentant un pré.
     * @param cancelledAnimals l'ensemble des animaux annulés.
     * @return l'ensemble des animaux présents dans le pré après annulation.
     */

    public static Set<Animal> animals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> allAnimals = new HashSet<>();
        meadow.zones.forEach(m->allAnimals.addAll(m.animals()));
        allAnimals.removeAll(cancelledAnimals);
        return allAnimals;

    }

    /**
     * Compte le nombre de poissons dans une rivière, y compris ceux présents dans les lacs adjacents.
     *
     * @param river l'aire représentant une rivière.
     * @return le nombre total de poissons dans la rivière.
     */
    public static int riverFishCount(Area<Zone.River> river) {
        int riverFishCount = 0;
        Set<Zone.Lake> allLakes = new HashSet<>();
        for (Zone.River r : river.zones) {
            riverFishCount += r.fishCount();
            if (r.hasLake() && allLakes.add(r.lake())) {
                riverFishCount += r.lake().fishCount();
            }
        }
        return riverFishCount;
    }

    /**
     * Compte le nombre de poissons dans un réseau hydrographique.
     *
     * @param riverSystem l'aire représentant un réseau hydrographique donné.
     * @return le nombre total de poissons dans le réseau hydrographique.
     */
    public static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        int riverSystemFishCount = 0;
        for (Zone.Water w : riverSystem.zones) {
            riverSystemFishCount += w.fishCount();
        }
        return riverSystemFishCount;
    }

    /**
     * Compte le nombre de lacs dans dans un réseau hydrographique.
     *
     * @param riverSystem l'aire représentant un réseau hydrographique..
     * @return le nombre total de lacs dans le réseau hydrographique..
     */
    public static int lakeCount(Area<Zone.Water> riverSystem) {
        return (int)riverSystem.zones.stream()
                .filter(zone -> zone instanceof Zone.Lake).count();
    }

    /**
     * Vérifie si l'aire est fermée, c'est-à-dire si elle n'a aucune connexion ouverte.
     *
     * @return true si l'aire est fermée, sinon false.
     */
    public boolean isClosed() {
        return openConnections == 0;
    }

    /**
     * Vérifie si l'aire est occupée par au moins un occupant.
     *
     * @return true si l'aire est occupée, sinon false.
     */
    public boolean isOccupied() {
        return (!occupants().isEmpty());
    }

    /**
     * Retourne l'ensemble des occupants majoritaires de l'aire.
     *
     * @return l'ensemble des couleurs des joueurs possédant le nombre maximum d'occupants.
     */
    public Set<PlayerColor> majorityOccupants() {
        Set<PlayerColor> majority = new HashSet<>();
        if (occupants.isEmpty()) {
            return majority;
        }

        // Compter le nombre d'occupants par couleur
        Map<PlayerColor, Integer> occupantCount = new HashMap<>();
        for (PlayerColor color : occupants) {
            occupantCount.put(color, occupantCount.getOrDefault(color, 0) + 1);
        }

        // Trouver le nombre maximum d'occupants
        int maxOccupants = Collections.max(occupantCount.values());

        // Ajouter les couleurs avec le nombre maximum d'occupants à l'ensemble des majorités
        for (Map.Entry<PlayerColor, Integer> entry : occupantCount.entrySet()) {
            if (entry.getValue() == maxOccupants) {
                majority.add(entry.getKey());
            }
        }
        return majority;
    }


    /**
     * Connecte l'aire actuelle à une autre aire, en fusionnant leurs zones et occupants si elles sont différentes.
     *
     * @param that l'autre aire à laquelle connecter l'aire actuelle this.
     * @return une nouvelle aire résultant de la connexion.
     */
    public Area<Z> connectTo(Area<Z> that) {
        if (that.equals(this)) {
            return new Area<>(this.zones, this.occupants, this.openConnections -2);
        } else {
            Set<Z> newAreaZones = new HashSet<>(this.zones);
            newAreaZones.addAll(that.zones);
            List<PlayerColor> newAreaOcc = new ArrayList<>(this.occupants);
            newAreaOcc.addAll(that.occupants);
            return new Area<>(newAreaZones, newAreaOcc, that.openConnections()-2 + this.openConnections);
        }
    }

    /**
     * Ajoute un occupant initial à l'aire, si elle n'en contient pas déjà.
     *
     * @param occupant le joueur à ajouter comme occupant initial.
     * @return une nouvelle aire avec l'occupant initial ajouté.
     * @throws IllegalArgumentException si l'aire contient déjà des occupants.
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(!this.isOccupied());
        List<PlayerColor> newOccupant = new ArrayList<>(this.occupants);
        newOccupant.add(occupant);
        return new Area<>(this.zones, newOccupant, this.openConnections);
    }

    /**
     * Retire un occupant de l'aire, s'il est présent.
     *
     * @param occupant le joueur à retirer comme occupant.
     * @return une nouvelle aire sans l'occupant spécifié.
     * @throws IllegalArgumentException si l'occupant spécifié n'est pas présent dans l'aire.
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        Preconditions.checkArgument(this.occupants.contains(occupant));
        List<PlayerColor> newAreaOccupants = new ArrayList<>(this.occupants);
        newAreaOccupants.remove(occupant);
        return new Area<>(zones, newAreaOccupants, openConnections);
    }

    /**
     * Retourne une nouvelle aire identique au récepteur sans aucun occupant.
     *
     * @return une nouvelle aire sans aucun occupant.
     */
    public Area<Z> withoutOccupants() {
        return new Area<>(this.zones, Collections.emptyList(), this.openConnections);
    }

    /**
     * Retourne l'ensemble des identifiants de tuile des zones de l'aire.
     *
     * @return l'ensemble des identifiants de tuile des zones de l'aire.
     */
    public Set<Integer> tileIds() {
        Set<Integer> allTilesId = new HashSet<>();
        for (Zone z : this.zones) {
            allTilesId.add(z.tileId());
        }
        return allTilesId;
    }

    /**
     * Recherche une zone avec un pouvoir spécial spécifié dans l'aire.
     *
     * @param specialPower le pouvoir spécial recherché.
     * @return la première zone avec le pouvoir spécial spécifié, ou null si aucune zone ne le possède.
     */
    public Zone zoneWithSpecialPower(Zone.SpecialPower specialPower) {
        for (Zone z : zones) {
            if (z.specialPower()==(specialPower)) {
                return z;
            }
        }
        return null;
    }
}

