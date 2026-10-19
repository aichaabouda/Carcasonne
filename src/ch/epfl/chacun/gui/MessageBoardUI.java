package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.gui.ImageLoader.LARGE_TILE_FIT_SIZE;

/**
 * MESSAGEBOARDUI
 * La classe MessageBoardUI contient le code de création de la partie de l'interface graphique
 * qui affiche le tableau de messages.
 * Elle fournit une méthode statique pour créer un composant d'interface graphique
 * représentant le tableau de messages, tel qu'il est affiché dans le jeu.
 * Elle utilise JavaFX pour générer et manipuler les éléments graphiques.
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public final class MessageBoardUI {

    private MessageBoardUI() {} // Le constructeur est privé pour empêcher l'instanciation de la classe

    /**
     * Crée un composant d'interface graphique représentant le tableau de messages.
     *
     * @param observableMessageBoard une propriété observable contenant la liste des messages à afficher
     * @param highlightedTiles une propriété observable contenant l'ensemble des identifiants de tuiles
     * à mettre en surbrillance
     * @return un composant d'interface graphique représentant le tableau de messages
     */
    public static Node create (ObservableValue<List<MessageBoard.Message>> observableMessageBoard,
                               ObjectProperty<Set<Integer>> highlightedTiles){
        VBox vbox = new VBox();
        ScrollPane messageScrollPane = new ScrollPane(vbox);
        messageScrollPane.getStylesheets().add("message-board.css");
        messageScrollPane.setId("message-board");
        messageScrollPane.layout();
        messageScrollPane.setVvalue(1);

        observableMessageBoard.addListener((_, oldMessage, newMessage) ->
                newMessage.stream()
                        .skip(oldMessage.size()) //pour ne pas retraiter les anciens messages
                        .forEach(m->{
                            Text message= new Text(m.text());
                            message.setWrappingWidth(LARGE_TILE_FIT_SIZE);
                            Set<Integer> tileIds=m.tileIds();
                            if(!tileIds.isEmpty()){
                                message.setOnMouseEntered(_->highlightedTiles.set(tileIds) );
                                message.setOnMouseExited (_ -> highlightedTiles.set(Set.of()));
                            }
                            vbox.getChildren().add(message);
                        })
        );
        return messageScrollPane;
    }


}
