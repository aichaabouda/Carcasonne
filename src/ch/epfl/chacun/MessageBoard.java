package ch.epfl.chacun;

/*MESSAGE BOARD
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import java.util.*;

import static ch.epfl.chacun.Animal.Kind.*;
import static java.util.Objects.requireNonNull;

/**
 *Tableau d'affichage de ChaCuN
 *Ce tableau de messages est utilisé pour enregistrer les événements importants qui se produisent pendant le déroulement du jeu.
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    /**
     * Représente un message dans le tableau de messages.
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        /**
         * Construit un nouveau message avec les valeurs spécifiées.
         * @param text = le texte qu'affiche ce message
         * @param points = les points associés à ce message
         * @param scorers = qui gagne les points
         * @param tileIds = les tuiles concernées par l'action qui génère le message
         */
        public Message {
            requireNonNull(text);
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }

    /**
     * Construit un nouveau tableau de messages avec le texte Maker spécifié et les messages donnés.
     *
     * @param textMaker Le fabricant de texte utilisé pour générer des messages.
     * @param messages  La liste des messages à initialiser le tableau de messages.
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     Calcule les points accumulés par chaque joueur en fonction des messages dans le tableau.
     *
     * @return Une Map associant à chaque joueur ses points totaux.
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> points = new HashMap<>();

        this.messages
                .forEach(message ->
                        message.scorers
                                .forEach(
                                        player -> points.put(player, points.getOrDefault(player, 0) + message.points()))
                );

        return points;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant une forêt marquée.
     *
     * @param forest La zone de forêt marquée.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            int forestTilesSize = forest.tileIds().size();
            int mushGroupCount = Area.mushroomGroupCount(forest);
            int points = Points.forClosedForest(forestTilesSize, mushGroupCount);
            Set<PlayerColor> majOcc = forest.majorityOccupants();
            String text = this.textMaker.playersScoredForest(majOcc, points, mushGroupCount, forestTilesSize);
            return addNewMessage(text, points, majOcc, forest.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant une forêt fermée avec menhir.
     *
     * @param player Le joueur ayant fermé la forêt avec menhir.
     * @param forest La zone de forêt fermée.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        return addNewMessage(this.textMaker.playerClosedForestWithMenhir(player), 0, Set.of(), forest.tileIds());
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant une rivière marquée.
     *
     * @param river La zone de rivière marquée.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            int riverTilesSize = river.tileIds().size();
            int fishCount = Area.riverFishCount(river);
            Set<PlayerColor> majOcc = river.majorityOccupants();
            int points = Points.forClosedRiver(riverTilesSize, fishCount);
            String text = this.textMaker.playersScoredRiver(majOcc, points, fishCount, riverTilesSize);
            return addNewMessage(text, points, majOcc, river.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant un piège de chasse marqué.
     *
     * @param scorer Le joueur ayant marqué le piège de chasse.
     * @param adjacentMeadow La zone de prairie adjacente au piège de chasse.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animals = Area.animals(adjacentMeadow, Set.of());
        animals.removeAll(cancelledAnimals);
        int points = pointsForMeadow(animals);
        if (points > 0) {
            String text = this.textMaker.playerScoredHuntingTrap(scorer, points, animalCount(animals));
            return addNewMessage(text, points, Set.of(scorer), adjacentMeadow.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant un radeau marqué.
     *
     * @param scorer Le joueur ayant marqué le radeau.
     * @param riverSystem Le système de rivière associé au radeau.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        int lakeCount = Area.lakeCount(riverSystem);
        int points = Points.forLogboat(lakeCount);
        String text = this.textMaker.playerScoredLogboat(scorer, points, lakeCount);
        return addNewMessage(text, points, Set.of(scorer), riverSystem.tileIds());
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant une prairie marquée.
     *
     * @param meadow La zone de prairie marquée.
     * @param cancelledAnimals Les animaux annulés de la prairie.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalsLeft = Area.animals(meadow, cancelledAnimals);
        int points = pointsForMeadow(animalsLeft);
        Set<PlayerColor> majOcc = meadow.majorityOccupants();
        if (meadow.isOccupied() && points > 0 ) {
            String text = this.textMaker.playersScoredMeadow(majOcc, points, animalCount(animalsLeft));
            return addNewMessage(text, points, majOcc, meadow.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant un système de rivière marqué.
     *
     * @param riverSystem Le système de rivière marqué.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int fishCount = Area.riverSystemFishCount(riverSystem);
        int points = Points.forRiverSystem(fishCount);
        Set<PlayerColor> majOcc = riverSystem.majorityOccupants();
        if (riverSystem.isOccupied() && (points > 0) ) {
            String text = this.textMaker.playersScoredRiverSystem(majOcc, points, fishCount);
            return addNewMessage(text, points, majOcc, riverSystem.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant un piège de fosse marqué.
     *
     * @param adjacentMeadow La prairie adjacente au piège de fosse.
     * @param cancelledAnimals Les animaux annulés du piège de fosse.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalsLeft = Area.animals(adjacentMeadow, cancelledAnimals);
        int points = pointsForMeadow(animalsLeft);
        Set<PlayerColor> majOcc = adjacentMeadow.majorityOccupants();
        if (adjacentMeadow.isOccupied() &&  points > 0) {
            String text = this.textMaker.playersScoredPitTrap(majOcc, points, animalCount(animalsLeft));
            return addNewMessage(text, points, majOcc , adjacentMeadow.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant un radeau marqué.
     *
     * @param riverSystem Le système de rivière associé au radeau.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        int lakeCount = Area.lakeCount(riverSystem);
        Set<PlayerColor> majOcc = riverSystem.majorityOccupants();
        if (riverSystem.isOccupied()) {
            int points = Points.forRaft(lakeCount);
            String text = this.textMaker.playersScoredRaft(majOcc, points, lakeCount);
            return addNewMessage(text, points, majOcc, riverSystem.tileIds());
        }
        return this;
    }

    /**
     * Ajoute un nouveau message au tableau de messages, représentant les gagnants de la partie.
     *
     * @param winners Les gagnants de la partie.
     * @param points Les points des gagnants.
     * @return Un nouveau tableau de messages incluant le message ajouté.
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        return addNewMessage(this.textMaker.playersWon(winners, points), 0, Set.of(), Set.of());
    }


    /**
     * Méthode utilitaire privée pour ajouter un message au récepteur.
     * @param text le texte associé au Message à ajouter
     * @param points les points associés au Message à ajouter
     * @param scorers les joueurs associés au Message à ajouter
     * @param tileIds la tuile associée au Message à ajouter
     * @return un nouveau messageBoard avec le même textMaker que le récépteur mais avec un message en plus dans la liste.
     */
    private MessageBoard addNewMessage(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds){
        ArrayList<Message> newMessage= new ArrayList<>(messages);
        newMessage.add(new Message(text, points, scorers,tileIds));
        return new MessageBoard(textMaker, newMessage);
    }

    /**
     * Méthode utilitaire privée qui avec un set d'animaux donné renvoie une Map qui associe à chaque type d'animal son nombre dans le set.
     * @param animals animaux à compter par type
     * @return une Map avec le type d'animal et son nombre dans le set en paramètre
     */
    private Map<Animal.Kind, Integer> animalCount(Set<Animal> animals) {
        Map<Animal.Kind, Integer> animalCounts = new HashMap<>();
        animals
                .forEach(
                        animal -> animalCounts.put(animal.kind(), animalCounts.getOrDefault(animal.kind(), 0) + 1));

        return animalCounts;
    }

    /**
     * Méthode utilitaire privée qui renvoie le nombre de points associé à un pré contenant le set d'animaux donné
     * @param animals les animaux dans le pré dont on veut avoir les points rapportés
     * @return les points associés à un pré contenant les animaux en paramètre.
     */
    private int pointsForMeadow(Set<Animal> animals) {
        int mammothCount = animalCount(animals).getOrDefault((MAMMOTH), 0);
        int aurochsCount = animalCount(animals).getOrDefault((AUROCHS), 0);
        int deerCount = animalCount(animals).getOrDefault((DEER), 0);

        return Points.forMeadow(mammothCount, aurochsCount, deerCount);
    }

}