package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**MAIN
 * Cette classe instanciable est le point d'entrée principal pour l'application ChaCuN, permettant de lancer le jeu.
 * Elle initialise l'état du jeu, crée le graphe de scène JavaFX, et configure les gestionnaires d'événements.
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public class Main extends Application {
    /**
     * Méthode privée qui applique une opération unitaire à la propriété donnée et met à jour sa valeur.
     *
     * @param property La propriété à mettre à jour.
     * @param operator L'opérateur unitaire à appliquer.
     * @param <T> Le type de la valeur contenue dans la propriété.
     */
    private static <T> void updateProperty(ObjectProperty<T> property, UnaryOperator<T> operator){
        property.setValue(operator.apply(property.getValue()));
    }

    /**
     * Méthode privée qui met à jour la propriété des actions avec une nouvelle action encodée et
     * met à jour l'état du jeu.
     *
     * @param gameStateProperty La propriété de l'état du jeu.
     * @param actionsProperty La propriété des actions encodées.
     * @param stateAction L'action encodée et l'état du jeu mis à jour.
     */
    private static void updateActions(ObjectProperty<GameState> gameStateProperty,
                                      ObjectProperty<List<String>> actionsProperty,
                                      ActionEncoder.StateAction stateAction){
        updateProperty(actionsProperty,
                list -> {
                    ArrayList<String> copiedList= new ArrayList<>(list); // copie pour immuabilité
                    copiedList.add(stateAction.actionEncoded0());
                    return copiedList;
                });
        gameStateProperty.setValue(stateAction.gameState0());
    }

    /**
     * Méthode privée qui génère un ensemble de paquets de tuiles (TileDecks) en fonction d'une graine
     * donnée pour initialiser le générateur aléatoire.
     * Cette méthode mélange les tuiles de manière déterministe si une graine est fournie,
     * ou de manière aléatoire sinon.
     * Les tuiles sont ensuite séparées en différents paquets selon leur type : START, NORMAL et MENHIR.
     *
     * @param seed La graine utilisée pour initialiser le générateur aléatoire. Si aucune graine n'est passée
     * en argument, un générateur aléatoire est utilisé.
     * @return Un objet TileDecks contenant les paquets de tuiles START, NORMAL et MENHIR.
     */
    private static TileDecks tileDecksGenerator(String seed){
        // obtenir un générateur aléatoire à partir de la graine
        RandomGenerator randomGenerator=
                Optional.ofNullable(seed)
                        .map(Long::parseUnsignedLong)
                        .map(RandomGeneratorFactory.getDefault()::create)
                        .orElse(RandomGeneratorFactory.getDefault().create());
        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, randomGenerator); //mélange aléatoire des tuiles
        //séparer le mélange de tuile par leur type (START,NORMAL OU MENHIR)
        Map<Tile.Kind, List<Tile>> groupedTiles = tiles.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        List<Tile> startTiles = groupedTiles.getOrDefault(Tile.Kind.START, Collections.emptyList());
        List<Tile> normalTiles = groupedTiles.getOrDefault(Tile.Kind.NORMAL, Collections.emptyList());
        List<Tile> menhirTiles = groupedTiles.getOrDefault(Tile.Kind.MENHIR, Collections.emptyList());
        return new TileDecks(startTiles, normalTiles, menhirTiles);
    }

    /**
     * Méthode privée qui génère une carte EnumMap associant les couleurs des joueurs à leurs noms respectifs.
     * La méthode vérifie que le nombre de joueurs est compris entre 2 et 5, puis attribue à
     * chaque joueur une couleur unique.
     *
     * @param playersNames Une liste contenant les noms des joueurs.
     * @return Une EnumMap associant chaque couleur de joueur (PlayerColor) à son nom (String).
     * @throws IllegalArgumentException Si le nombre de joueurs est inférieur à 2 ou supérieur à 5.
     */
    private static EnumMap<PlayerColor, String > playersMapGenerator(List<String> playersNames){
        Preconditions.checkArgument(playersNames.size() >= 2 && playersNames.size() <=5);
        List<PlayerColor> playersColors = PlayerColor.ALL.subList(0, playersNames.size());
        EnumMap<PlayerColor, String> players = new EnumMap<>(PlayerColor.class);
        playersNames.forEach(player ->
                players.put(playersColors.get(playersNames.indexOf(player)), player));
        return players;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Gestion des arguments
        //Gestion des arguments (anonymes) passés au programme afin d'obtenir les noms des joueurs
        EnumMap<PlayerColor, String> players = playersMapGenerator(getParameters().getUnnamed());
        //Mélange des tuiles et gestion des arguments (nommés) pour obtenir la graine
        TileDecks tileDecks = tileDecksGenerator(getParameters().getNamed().get("seed"));
        TextMaker textMaker = new TextMakerFr(players);

        //Propriétés JavaFX
        GameState initialState = GameState.initial(players.keySet().stream().toList(), tileDecks, textMaker);
        ObjectProperty<GameState> gameStateProperty = new SimpleObjectProperty<>(initialState);
        ObjectProperty<Rotation> rotationProperty = new SimpleObjectProperty<>(Rotation.NONE);
        ObjectProperty<Set<Integer>> tilesHighlightProperty = new SimpleObjectProperty<>(Set.of());
        ObjectProperty<List<String>> actionsProperty= new SimpleObjectProperty<>(List.of());

        ObservableValue<List<MessageBoard.Message>> observableMessages = gameStateProperty.map(
                list -> list.messageBoard().messages());
        ObservableValue<Tile> observableTile= gameStateProperty.map(GameState::tileToPlace);
        ObservableValue<Set<Occupant>> visibleOccupants = gameStateProperty.map(occupants ->
                occupants.nextAction() == GameState.Action.OCCUPY_TILE ?
                        Stream.concat(occupants.board().occupants().stream(),
                                        occupants.lastTilePotentialOccupants().stream())
                                .collect(Collectors.toSet()): occupants.board().occupants() );
        ObservableValue<String> observableText = gameStateProperty.map(text ->
                switch (text.nextAction()) {
                    case RETAKE_PAWN -> textMaker.clickToUnoccupy();
                    case OCCUPY_TILE -> textMaker.clickToOccupy();
                    default -> "";
                });

        //Gestionnaires d'événements: création des consommateurs pour les interactions des joueurs
        Consumer<Occupant> occupantConsumer = occupant -> {
            GameState currentState = gameStateProperty.get();
            Board currentBoard= currentState.board();
            int tileId = (occupant==null)? -1: Zone.tileId(occupant.zoneId());
            switch(currentState.nextAction()){
                case RETAKE_PAWN->{
                    if(currentBoard.tileWithId(tileId).placer()==currentState.currentPlayer()
                            &&  (occupant==null || occupant.kind()== Occupant.Kind.PAWN) ){
                        ActionEncoder.StateAction stateAction = ActionEncoder
                                .withOccupantRemoved(currentState, occupant);
                        updateActions(gameStateProperty, actionsProperty,stateAction); }
                }
                case OCCUPY_TILE->{
                    if(occupant==null || currentBoard.lastPlacedTile().id()==tileId){
                        ActionEncoder.StateAction stateAction = ActionEncoder
                                .withNewOccupant(currentState, occupant);
                        updateActions(gameStateProperty, actionsProperty,stateAction); }
                }
            }
        };

        Consumer<Rotation> rotationConsumer= rotation -> updateProperty(rotationProperty, rotation::add);
        Consumer<Pos> posConsumer= position -> {
            GameState currentState = gameStateProperty.get();
            PlacedTile newPlacedTile= new PlacedTile(observableTile.getValue(),
                    currentState.currentPlayer(),rotationProperty.get(),position);
            if(currentState.board().canAddTile(newPlacedTile)){
                ActionEncoder.StateAction stateAction = ActionEncoder.withPlacedTile(currentState,newPlacedTile);
                updateActions(gameStateProperty,actionsProperty,stateAction);
                rotationProperty.set(Rotation.NONE);
            }
        };
        Consumer<String> actionsConsumer = actionText -> {
            ActionEncoder.StateAction st = ActionEncoder.decodeAndApply(gameStateProperty.get(), actionText);
            if (st != null) {
                updateActions(gameStateProperty, actionsProperty, st);
            }
        };

        //Création du graphe de scène complet de la fenêtre du programme
        //Les noeuds sont créés à l'aide des méthodes create des classes du sous-paquetage gui.
        Node decksUI = DecksUI.create(observableTile,
                gameStateProperty.map(gameState -> gameState.tileDecks().deckSize(Tile.Kind.NORMAL)),
                gameStateProperty.map(gameState -> gameState.tileDecks().deckSize(Tile.Kind.MENHIR)),
                observableText,
                occupantConsumer);
        Node playersUI = PlayersUI.create(
                gameStateProperty,
                textMaker);
        Node messageBoardUI = MessageBoardUI.create(
                observableMessages,
                tilesHighlightProperty);
        Node boardUI = BoardUI.create(
                Board.REACH,
                gameStateProperty,
                rotationProperty,
                visibleOccupants,
                tilesHighlightProperty,
                rotationConsumer,
                posConsumer,
                occupantConsumer);
        Node actionsUI = ActionUI.create(
                actionsProperty,
                actionsConsumer);

        VBox vBox = new VBox(actionsUI, decksUI);
        BorderPane secondBorderPane = new BorderPane(messageBoardUI, playersUI, null, vBox, null);
        BorderPane mainBorderPane =new BorderPane(boardUI, null, secondBorderPane, null, null);
        Scene mainScene = new Scene(mainBorderPane);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("ChaCuN");
        primaryStage.setHeight(1080);
        primaryStage.setWidth(1440);
        primaryStage.show();

        //Mise à jour du GameState
        updateProperty(gameStateProperty, GameState::withStartingTilePlaced); //mise à jour de l'état de jeu
    }


    /**
     * La méthode main est le point d'entrée de l'application.
     * Elle appelle la méthode launch pour démarrer l'application.
     *
     * @param args Les arguments de ligne de commande passés sous forme d'un tableau de chaînes de caractères.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
