package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

/**TEXTMAKERFR
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 * La classe TextMakerFr fournit les messages à afficher en français pour les différents évènements.
 * Elle implémente l'interface TextMaker.
 * Cette classe est utilisée pour créer des descriptions textuelles pour différentes actions et résultats de jeu.
 */

public final class TextMakerFr implements TextMaker {
    private final Map<PlayerColor, String> players;

    // Attribut final et statique qui contient le nom en français et au singulier
    // correspondant à chaque type d'animal
    private static final Map<Animal.Kind, String> ANIMAL_NAMES = new EnumMap<>(Map.of(
            Animal.Kind.DEER, "cerf",
            Animal.Kind.AUROCHS, "auroch",
            Animal.Kind.MAMMOTH, "mammouth"
    ));

    /**
     * Construit un TextMakerFr avec un map qui associe les noms des joueurs à leurs couleurs dans le jeu.
     *
     * @param players une Map associant chaque PlayerColor à son nom
     */
    public TextMakerFr(Map<PlayerColor, String> players) {
        this.players = Map.copyOf(players);
    }

    /**
     * Retourne le nom du joueur associé à la couleur donnée.
     *
     * @param playerColor la couleur du joueur
     * @return le nom du joueur
     */
    @Override
    public String playerName(PlayerColor playerColor) {
        return players.get(playerColor);
    }

    /**
     * Retourne la chaîne de caractères correspondant au nombre de points marqués, avec la gestion du pluriel.
     * CF la méthode privée plural pour plus d'explication
     *
     * @param points le nombre de points
     * @return une chaîne de caractères représentant les points marqués
     */
    @Override
    public String points(int points) {
        return plural(points, "point");
    }

    /**
     * Retourne une chaîne de caractères indiquant qu'un joueur a fermé une forêt contenant un menhir.
     *
     * @param player la couleur du joueur
     * @return une chaîne de caractères décrivant l'action
     */
    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return STR."\{playerName(player)} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont marqué des points en fermant une forêt.
     *
     * @param scorers les couleurs des joueurs qui ont marqué des points
     * @param points le nombre de points marqués
     * @param mushroomGroupCount le nombre de groupes de champignons dans la forêt
     * @param tileCount le nombre de tuiles composant la forêt
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        String players = STR."\{multipleMajorityOccupants(scorers)} d'une forêt composée de";
        String moreThanOneMushGroup = mushroomGroupCount > 1 ? "groupes de champignons." : "groupe de champignon.";
        String mushroom = mushroomGroupCount == 0 ? "." : "et de" + STR."\{mushroomGroupCount} \{moreThanOneMushGroup}";

        return STR."\{multiplePlayerRemporter(scorers)} \{points(points)} \{players} \{plural(tileCount,
                "tuile")} \{mushroom}";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont marqué des points en fermant une rivière.
     *
     * @param scorers les couleurs des joueurs qui ont marqué
     * @param points le nombre de points marqués
     * @param fishCount le nombre de poissons dans la rivière
     * @param tileCount le nombre de tuiles composant la rivière
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        String players = STR."\{multipleMajorityOccupants(scorers)} d'une rivière composée de";
        String fishCountNonNull = fishCount > 1 ? "poissons." : "poisson.";
        String fish = fishCount == 0 ? "." : "et contenant " + STR."\{fishCount} \{fishCountNonNull}";

        return STR."\{multiplePlayerRemporter(scorers)} \{points(points)} \{players} \{plural(tileCount,
                "tuile")} \{fish}";
    }

    /**
     * Retourne une chaîne de caractères indiquant qu'un joueur a marqué des points en plaçant un piège de chasse.
     *
     * @param scorer la couleur du joueur qui a marqué
     * @param points le nombre de points marqués
     * @param animals une carte des animaux et de leurs quantités entourant le piège
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{playerName(scorer)} a remporté \{points(points)}"
                + " en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de " +
                STR."\{sortAnimals(animals)}";
    }

    /**
     * Retourne une chaîne de caractères indiquant qu'un joueur a marqué des points en plaçant une pirogue.
     *
     * @param scorer la couleur du joueur qui a marqué
     * @param points le nombre de points marqués
     * @param lakeCount le nombre de lacs dans le réseau hydrographique
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return STR."\{playerName(scorer)} a remporté \{points(points)} en " +
                "plaçant la pirogue dans un réseau hydrographique contenant " +
                STR."\{lakeCount} \{lakeCount <= 1 ? "lac." : "lacs."}";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont marqué des points dans un pré.
     *
     * @param scorers les couleurs des joueurs qui ont marqué
     * @param points le nombre de points marqués
     * @param animals une carte des animaux et de leurs quantités présents dans le pré
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        String players = STR."\{multipleMajorityOccupants(scorers)} d'un pré";
        String adjacentAnimals = STR." contenant \{sortAnimals(animals)}";

        return STR."\{multiplePlayerRemporter(scorers)} \{points(points)} \{players} \{adjacentAnimals}";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont marqué des points dans un réseau hydrographique.
     *
     * @param scorers les couleurs des joueurs qui ont marqué
     * @param points le nombre de points marqués
     * @param fishCount le nombre de poissons dans le réseau hydrographique
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        String players = STR."\{multipleMajorityOccupants(scorers)} d'un réseau hydrographique";
        String fish = STR." contenant \{plural(fishCount, "poisson")}.";

        return STR."\{multiplePlayerRemporter(scorers)} \{points(points)} \{players} \{fish}";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont marqué des points en plaçant une grande
     * fosse à pieux.
     *
     * @param scorers les couleurs des joueurs qui ont marqué
     * @param points le nombre de points marqués
     * @param animals une carte des animaux et de leurs quantités entourant la grande fosse à pieux
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        String players = STR."\{multipleMajorityOccupants(scorers)} d'un pré";
        String adjacentAnimals = STR."contenant la grande fosse à pieux entourée de \{sortAnimals(animals)}";

        return STR."\{multiplePlayerRemporter(scorers)} \{points(points)} \{players} \{adjacentAnimals}";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont marqué des points en plaçant un radeau.
     *
     * @param scorers les couleurs des joueurs qui ont marqué
     * @param points le nombre de points marqués
     * @param lakeCount le nombre de lacs dans le réseau hydrographique
     * @return une chaîne de caractères décrivant l'action selon les règles spécifiées
     */
    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        String players = STR."\{multipleMajorityOccupants(scorers)} d'un réseau hydrographique";
        String lake = STR."contenant le radeau et \{plural(lakeCount, "lac")}.";

        return STR."\{multiplePlayerRemporter(scorers)} \{points(points)} \{players} \{lake}";
    }

    /**
     * Retourne une chaîne de caractères indiquant que des joueurs ont remporté la partie avec un
     * certain nombre de points.
     *
     * @param winners les couleurs des joueurs gagnants
     * @param points le nombre de points marqués
     * @return une chaîne de caractères décrivant la victoire selon les règles spécifiées
     */
    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return STR."\{multiplePlayerRemporter(winners)} la partie avec \{points(points)} !";
    }

    /**
     * Retourne une chaîne de caractères indiquant d'où cliquer pour placer un pion ou une hutte,
     * ou pour ne pas en placer.
     *
     * @return une chaîne de caractères d'instructions pour placer un pion ou une hutte selon les règles spécifiées
     */
    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    /**
     * Retourne une chaîne de caractères indiquant d'où cliquer pour reprendre un pion, ou pour ne pas en reprendre.
     *
     * @return une chaîne de caractères d'instructions pour reprendre un pion selon les règles spécifiées
     */
    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
    }

    /**
     * Méthode utilitaire privée pour vérifier si le pluriel est nécessaire et ajouter 's' si besoin.
     *
     * @param count le nombre d'éléments pour savoir s'il y en a plus d'un
     * @param name la chaîne de caractères au singulier
     * @return une chaîne de caractères avec le pluriel si nécessaire
     */
    private String plural(int count, String name) {
        return STR."\{count} \{name}\{count > 1 ? "s" : ""}";
    }

    /**
     * Méthode utilitaire privée qui trie les joueurs
     * par ordre de couleur (selon leur ordre d'apparition dans l'énumération de PlayerColor)
     * et retourne une chaîne de caractères avec leurs noms
     * séparés d'une virgule et d'un 'et' à la fin.
     *
     * @param players une Map associant les noms des joueurs à leurs couleurs
     * @return une chaîne de caractères listant les noms des joueurs triés
     */
    private String sortPlayers(Map<String, PlayerColor> players) {
        if(players.size()==1){
            return players.keySet().stream().toList().getFirst();
        }
        List<String> sortedColors = players.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().ordinal()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return STR."\{String.join(", ", sortedColors.subList(0, sortedColors.size() - 1))} et \{sortedColors.getLast()}";
    }

    /**
     * Méthode utilitaire privée qui retourne une chaîne de caractères qui indique si un ou plusieurs joueurs ont
     * remporté les points
     * en conjuguant correctement le verbe "remporter"
     *
     * @param scorers un ensemble des couleurs des joueurs
     * @return une chaîne de caractères correctement conjuguée
     */
    private String multiplePlayerRemporter(Set<PlayerColor> scorers) {
        return STR."\{sortPlayers(createPlayerMap(scorers))} \{scorers.size() > 1 ? "ont remporté" : "a remporté"}";
    }

    /**
     * Méthode utilitaire privée qui crée une Map
     * associant les joueurs donnés (PlayerColor) à leurs noms
     * selon l'attribut Map du TextMakerFr
     *
     * @param players un ensemble des couleurs des joueurs
     * @return une carte associant les noms des joueurs à leurs couleurs
     */
    private Map<String, PlayerColor> createPlayerMap(Set<PlayerColor> players) {
        Map<String, PlayerColor> playerMap = new HashMap<>();
        players.forEach(player -> playerMap.put(playerName(player), player));
        return playerMap;
    }



    /**
     * Méthode utilitaire privée qui retourne une chaîne de caractères indiquant si un ou plusieurs joueurs est/sont
     * l.es occupants majoritaires
     *
     * @param scorers un ensemble des couleurs des joueurs
     * @return une chaîne de caractères décrivant les occupants majoritaires avec la bonne orthographe
     */
    private String multipleMajorityOccupants(Set<PlayerColor> scorers) {
        return STR."en tant\{scorers.size() > 1 ? " qu'occupant.e.s majoritaires" : " qu'occupant.e majoritaire"}";
    }


    /**
     * Méthode utilitaire privée qui
     * Trie et renvoie une phrase en français avec le nombre d'animaux par type
     * dans l'ordre spécifié, séparés de virgule puis d'un 'et'.
     *
     * @param animals une Map des types d'animaux et de leurs nombres
     * @return une chaîne de caractères qui spécifie le nombre d'animaux de chaque type.
     */
    private String sortAnimals(Map<Animal.Kind, Integer> animals) {
        Map<String, Integer> nonZero = new HashMap<>();
        for(Animal.Kind k : ANIMAL_NAMES.keySet()){
            if (animals.getOrDefault(k,-1) > 0)
                nonZero.put(ANIMAL_NAMES.get(k), animals.get(k));
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i< nonZero.size(); i++){
            String animalKind = nonZero.keySet().stream().toList().get(i);
            int kindCount = nonZero.get(animalKind);
            builder.append(kindCount);
            builder.append(' ');
            builder.append(plural(kindCount, animalKind));
            if(i == nonZero.size()-2){
                builder.append(" et ");
            }
            else if(i!= nonZero.size()-1){
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
