package DST;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * PopUp für Nutzungsfehler
 */
public class PopUpUserError {

    /**
     * @param message Nachricht für den jeweiligen Fehler
     */
    public static void start(String message) {
        Stage primaryStage = new Stage();
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setTitle("USER ERROR!");
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Label l1 = new Label(message);
        Button b1 = new Button("OK");
        Button b2 = new Button("Not OK");
        b1.setOnAction(e -> primaryStage.close());
        b2.setOnAction(e -> primaryStage.close());
        hbox.getChildren().addAll(b1, b2);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(l1, hbox);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 420, 69);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        b1.getStyleClass().add("button");
        b2.getStyleClass().add("button");
        l1.getStyleClass().add("text");


        primaryStage.showAndWait();
    }

}
