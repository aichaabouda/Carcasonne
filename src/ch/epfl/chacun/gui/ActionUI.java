package ch.epfl.chacun.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.chacun.Base32.ALPHABET;

/**
 * La classe ActionUI contient le code de création de la partie de l'interface graphique
 * permettant la saisie et l'affichage des actions.
 * Elle fournit une méthode statique pour créer un composant d'interface graphique
 * représentant les actions.

 * Cette classe utilise JavaFX pour générer et manipuler les éléments graphiques.
 * Elle est destinée à être utilisée dans une application JavaFX.
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */
public final class ActionUI {

    private static final int VISIBLE_MESSAGES = 4;

    private ActionUI() {} // Constructeur privé pour une classe non instanciable

    /**
     * Crée un composant d'interface graphique représentant les actions.
     *
     * @param observableEncodedActions une propriété observable contenant la liste des actions encodées à afficher
     * @param actionsConsumer un consommateur pour gérer les actions saisies par l'utilisateur
     * @return un composant d'interface graphique représentant les actions
     */
    public static Node create(ObservableValue<List<String>> observableEncodedActions,
                              Consumer<String> actionsConsumer){
        Text actionText = new Text();
        actionText.textProperty().bind(observableEncodedActions.map(actions ->
                IntStream.rangeClosed(1, actions.size()).skip(Math.max(0, actions.size() - VISIBLE_MESSAGES))
                        .mapToObj(i -> STR."\{i}:\{actions.get(i - 1)}").collect(Collectors.joining(", "))));

        TextField actionTextField = new TextField();
        actionTextField.setId("action-field");
        actionTextField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().chars().map(Character::toUpperCase)
                    .filter(c -> ALPHABET.indexOf(c) != -1)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining()));
            return change;
        }));
        actionTextField.setOnAction(_ -> {
            actionsConsumer.accept(actionTextField.getText());
            actionTextField.clear();
        });

        HBox actionsHBox = new HBox(actionText, actionTextField);
        actionsHBox.setId("actions");
        actionsHBox.getStylesheets().add("actions.css");
        return actionsHBox;
    }
}
