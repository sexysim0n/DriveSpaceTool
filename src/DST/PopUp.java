package DST;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * PopUp für viele Zwecke
 */
public class PopUp {

    /**
     * @param title   Window Title
     * @param message Nachricht die im Fenster angezeigt werden soll
     */
    public static void start(String title, String message) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle(title);
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Label l1 = new Label(message);
        Button b1 = new Button("OK");
        b1.setOnAction(e -> primaryStage.close());
        hbox.getChildren().addAll(b1);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(l1, hbox);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 420, 69);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        b1.getStyleClass().add("button");
        l1.getStyleClass().add("text");

        primaryStage.focusedProperty().addListener
                ((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) primaryStage.close();
                });

        primaryStage.showAndWait();
    }

}
