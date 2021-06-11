package DST;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Die grafische Oberfläche des Programms
 *
 * @author simon
 */
public class GUI extends Application {

    /**
     * Label für Output von Duplikaten
     */
    private Label txtDuplicateOut;
    /**
     * FileChooser für den zu durchsuchenden Ordner (Duplikate)
     */
    private TextField tfChooseFolder;
    /**
     * Checkboxen: Suche der Duplikate nach Name und/oder nach Inhalt
     */
    private CheckBox checkBoxName, checkBoxContent;
    /**
     * Ausgabe VBox für Duplikate
     */
    private VBox vboxDuplOut;
    /**
     * Path Array mit Duplikaten
     */
    private Path[] duplicatePaths;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setTitle("DriveSpaceTool");

        BorderPane borderPane = new BorderPane();


        //DUPLICATES VBOX
        VBox vboxDuplicates = new VBox();
        HBox hBoxChooseDir = new HBox();
        Button bChooseDir = new Button("Choose Folder");
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Folder");
        dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        tfChooseFolder = new TextField();
        tfChooseFolder.setPromptText("e.g. C:\\Users\\USERNAME\\example");
        tfChooseFolder.setPrefWidth(420);
        hBoxChooseDir.getChildren().addAll(bChooseDir, tfChooseFolder);
        Label txtCompareBy = new Label("Compare by:");
        checkBoxName = new CheckBox();
        checkBoxName.setText("Name");
        checkBoxName.setSelected(true);
        checkBoxContent = new CheckBox();
        checkBoxContent.setText("Content");
        Button bFindDuplicates = new Button("Find");
        HBox hboxDupliateFunctions = new HBox();
        Button bDelDuplicates = new Button("Delete Selected");
        Button bSelectDuplicates = new Button("Select All");
        Button bDeselectDuplicates = new Button("Deselect All");
        hboxDupliateFunctions.getChildren().addAll(bDelDuplicates, bSelectDuplicates);
        txtDuplicateOut = new Label("");
        vboxDuplOut = new VBox();
        final ScrollPane[] scrollPaneDuplicatesOut = {new ScrollPane()};
        scrollPaneDuplicatesOut[0].setContent(vboxDuplOut);
        ArrayList<CheckBox> checkBoxArrayDupl = new ArrayList<>();

        bChooseDir.setOnAction(e -> tfChooseFolder.setText(String.valueOf(dirChooser.showDialog(stage))));
        bFindDuplicates.setOnAction(e -> {
            duplicatePaths = new Path[0];
            CheckBox[] checkBoxes = findDuplicates();
            vboxDuplOut = new VBox();
            txtDuplicateOut.setText("");
            if (checkBoxes != null) {
                checkBoxArrayDupl.addAll(Arrays.asList(checkBoxes));
            } else {
                PopUp.start("Permission Error", "You dont have permissions for one or multiple subdirectories!");
            }
            vboxDuplicates.getChildren().removeAll(hboxDupliateFunctions, scrollPaneDuplicatesOut[0]);
            vboxDuplicates.getChildren().addAll(hboxDupliateFunctions, scrollPaneDuplicatesOut[0]);
        });
        vboxDuplicates.getChildren().addAll(hBoxChooseDir, txtCompareBy, checkBoxName, checkBoxContent,
                bFindDuplicates, scrollPaneDuplicatesOut[0]); //tf2, tf1
        bSelectDuplicates.setOnAction(e -> {
            for (int i = 0; i < checkBoxArrayDupl.size(); i++) {
                if (!checkBoxArrayDupl.get(i).isSelected() && i % 2 == 0) checkBoxArrayDupl.get(i).fire();
            }
            hboxDupliateFunctions.getChildren().remove(bSelectDuplicates);
            hboxDupliateFunctions.getChildren().add(bDeselectDuplicates);
        });
        bDeselectDuplicates.setOnAction(e -> {
            for (CheckBox checkBox : checkBoxArrayDupl) {
                if (checkBox.isSelected())
                    checkBox.fire();
            }
            hboxDupliateFunctions.getChildren().remove(bDeselectDuplicates);
            hboxDupliateFunctions.getChildren().addAll(bSelectDuplicates);
        });
        bDelDuplicates.setOnAction(e -> {
            for (int i = 0; i < checkBoxArrayDupl.size() - 1; i++) {
                if (checkBoxArrayDupl.get(i).isSelected()) {
                    try {
                        Files.deleteIfExists(Paths.get(String.valueOf(duplicatePaths[i])));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
            PopUp.start("Deleted Selection.", "Deleted Selection.");
        });


        //VBOX iMages
        VBox vboxImages = new VBox();
        String allowedFileTypes = "*.jpg*.jpe*.jpeg*.png*.tif*.tiff*.bmp*.rle*.dib";

        HBox hboxOriginal = new HBox();
        FileChooser fileChooserOriginal = new FileChooser();
        fileChooserOriginal.setTitle("Choose Image to resize!");
        fileChooserOriginal.setInitialDirectory(new File(System.getProperty("user.home")));
        Button bChooseOriginal = new Button("Choose Image to resize!");//downscale
        TextField tfChooseOriginal = new TextField();
        tfChooseOriginal.setPromptText("e.g. C:\\Users\\USERNAME\\example.jpg");
        tfChooseOriginal.setPrefWidth(420);
        bChooseOriginal.setOnAction(e -> {
            fileChooserOriginal.getExtensionFilters().addAll
                    (new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg", "*.jpe"),
                            new FileChooser.ExtensionFilter("PNG", "*.png"),
                            new FileChooser.ExtensionFilter("BMP", "*.bmp", "*.rle", "*.dib"),
                            new FileChooser.ExtensionFilter("TIFF", "*.tif", "*.tiff"));
            tfChooseOriginal.setText(String.valueOf(fileChooserOriginal.showOpenDialog(stage)));
        });
        hboxOriginal.getChildren().addAll(bChooseOriginal, tfChooseOriginal);

        HBox hboxResized = new HBox();
        FileChooser fileChooserResized = new FileChooser();
        fileChooserResized.setTitle("Choose Save Folder & Name!");
        fileChooserResized.setInitialDirectory(new File(System.getProperty("user.home")));
        Button bChooseResized = new Button("Choose Save Folder & Name!");
        TextField tfChooseResized = new TextField();
        tfChooseResized.setPromptText("e.g. C:\\Users\\USERNAME\\resized.jpg");
        tfChooseResized.setPrefWidth(420);
        bChooseResized.setOnAction(e -> {
            fileChooserResized.getExtensionFilters().addAll
                    (new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg", "*.jpe"),
                            new FileChooser.ExtensionFilter("PNG", "*.png"),
                            new FileChooser.ExtensionFilter("BMP", "*.bmp", "*.rle", "*.dib"),
                            new FileChooser.ExtensionFilter("TIFF", "*.tif", "*.tiff"));
            String resizedImg = String.valueOf(fileChooserResized.showSaveDialog(stage));
            if (resizedImg.contains(".") && allowedFileTypes.contains(resizedImg.substring(resizedImg.lastIndexOf('.')))) {
                tfChooseResized.setText(resizedImg);
            }
        });
        hboxResized.getChildren().addAll(bChooseResized, tfChooseResized);

        HBox hboxHeightWidth = new HBox();
        TextField tfWidth = new TextField();
        tfWidth.setPromptText("New Width");
        TextField tfHeight = new TextField();
        tfHeight.setPromptText("New Height");
        hboxHeightWidth.getChildren().addAll(tfWidth, tfHeight);

        HBox hboxFileTypeButtons = new HBox();
        final ToggleGroup toggleGroupType = new ToggleGroup();
        RadioButton radioButtonJPEG = new RadioButton("JPEG");
        radioButtonJPEG.setToggleGroup(toggleGroupType);
        radioButtonJPEG.getStyleClass().remove("radio-button");
        radioButtonJPEG.getStyleClass().add("toggle-button");
        RadioButton radioButtonPNG = new RadioButton("PNG");
        radioButtonPNG.setToggleGroup(toggleGroupType);
        radioButtonPNG.getStyleClass().remove("radio-button");
        radioButtonPNG.getStyleClass().add("toggle-button");
        RadioButton radioButtonBMP = new RadioButton("BMP");
        radioButtonBMP.setToggleGroup(toggleGroupType);
        radioButtonBMP.getStyleClass().remove("radio-button");
        radioButtonBMP.getStyleClass().add("toggle-button");
        RadioButton radioButtonTIFF = new RadioButton("TIFF");
        radioButtonTIFF.setToggleGroup(toggleGroupType);
        radioButtonTIFF.getStyleClass().remove("radio-button");
        radioButtonTIFF.getStyleClass().add("toggle-button");
        hboxFileTypeButtons.getChildren().addAll(radioButtonBMP, radioButtonJPEG, radioButtonPNG, radioButtonTIFF);


        CheckBox checkBoxDeleteOriginal = new CheckBox("Delete Original");
        CheckBox checkBoxOpenResized = new CheckBox("View Resized File when done");
        checkBoxDeleteOriginal.setSelected(true);
        Button imgResize = new Button("Resize");
        Label txtImagesOut = new Label("");
        imgResize.setOnAction(e -> {
            if (toggleGroupType.getSelectedToggle() != null && tfHeight.getText() != null && tfWidth.getText() != null
                    && Files.exists(Paths.get(tfChooseResized.getText().substring
                    (0, tfChooseResized.getText().lastIndexOf("\\"))))
                    && Files.exists(Paths.get(tfChooseOriginal.getText()))
                    && !tfHeight.getText().equals("") && !tfWidth.getText().equals("")) {
                String toogle = toggleGroupType.getSelectedToggle().toString();
                String part = toogle.substring(toogle.substring(0, toogle.length() - 1).lastIndexOf("'"));
                int height;
                int width;
                try {
                    width = Integer.parseInt(tfWidth.getText());
                } catch (NumberFormatException numberFormatException) {
                    PopUpUserError.start("Width is not a Number!");
                    return;
                }
                try {
                    height = Integer.parseInt(tfHeight.getText());
                } catch (NumberFormatException numberFormatException) {
                    PopUpUserError.start("Height is not a Number!");
                    return;
                }
                ImageResizer imgRszr = new ImageResizer(tfChooseOriginal.getText(), tfChooseResized.getText(),
                        width, height, part.substring(1, part.length() - 1));

                try {
                    String output = imgRszr.resizeImg();
                    PopUp.start(output, output);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    return;
                }
                if (checkBoxDeleteOriginal.isSelected()) imgRszr.deleteOriginal();
                if (checkBoxOpenResized.isSelected()) {
                    try {
                        Desktop.getDesktop().open(new File(tfChooseResized.getText()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else {
                PopUpUserError.start("Invalid Input!");
            }
        });

        vboxImages.getChildren().addAll
                (hboxOriginal, hboxResized, hboxHeightWidth, hboxFileTypeButtons,
                        checkBoxDeleteOriginal, checkBoxOpenResized, imgResize, txtImagesOut);

        HBox tabHBox = new HBox();
        tabHBox.setAlignment(Pos.BASELINE_CENTER);
        tabHBox.setStyle("-fx-background-color: #202020;-fx-padding: 5;");
        Button bTabDuplicates = new Button("DUPLICATE FILES");
        bTabDuplicates.setUnderline(true);
        Button bTabImages = new Button("IMAGE RESIZER");
        bTabDuplicates.setOnAction(e -> {
            borderPane.setCenter(vboxDuplicates);
            bTabDuplicates.setUnderline(true);
            bTabImages.setUnderline(false);
        });
        bTabImages.setOnAction(e -> {
            borderPane.setCenter(vboxImages);
            bTabDuplicates.setUnderline(false);
            bTabImages.setUnderline(true);
        });
        tabHBox.getChildren().addAll(bTabDuplicates, bTabImages);

        borderPane.setTop(tabHBox);
        borderPane.setCenter(vboxDuplicates);
        Label lAuthor = new Label("Simon Berthold");
        BorderPane.setAlignment(lAuthor, Pos.CENTER_RIGHT);
        BorderPane.setMargin(lAuthor, new Insets(5, 5, 5, 5));
        borderPane.setBottom(lAuthor);

        Scene scene = new Scene(borderPane, 800, 400);

        //style
        scene.getStylesheets().add("style.css");
        tabHBox.getStylesheets().add("style.css");
        borderPane.getStylesheets().add("style.css");
        vboxImages.getStylesheets().add("style.css");
        vboxDuplicates.getStylesheets().add("style.css");
        bTabDuplicates.getStyleClass().add("tab");
        bTabImages.getStyleClass().add("tab");
        bChooseDir.getStyleClass().add("button");
        bFindDuplicates.getStyleClass().add("button");
        checkBoxDeleteOriginal.getStyleClass().add("checkbox");
        checkBoxOpenResized.getStyleClass().add("checkbox");
        checkBoxContent.getStyleClass().add("checkbox");
        checkBoxName.getStyleClass().add("checkbox");
        txtCompareBy.getStyleClass().add("text");
        txtDuplicateOut.getStyleClass().add("text");
        txtImagesOut.getStyleClass().add("text");
        tfChooseFolder.getStyleClass().add("textfield");
        tfChooseOriginal.getStyleClass().add("textfield");
        tfChooseResized.getStyleClass().add("textfield");
        tfWidth.getStyleClass().add("textfield");
        tfHeight.getStyleClass().add("textfield");
        hboxFileTypeButtons.setStyle("-fx-padding: 5;");

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private CheckBox[] findDuplicates() {
        txtDuplicateOut.setText("");
        duplicatePaths = new Path[0];
        Duplicate d = new Duplicate(new File(tfChooseFolder.getText()));
        long t0 = System.currentTimeMillis();
        CheckBox[] checkboxArray = new CheckBox[0];
        try {
            Duplicate.Level level = null;
            if (checkBoxName.isSelected()) level = Duplicate.Level.NAME;
            if (checkBoxContent.isSelected()) level = Duplicate.Level.CONTENT;
            if (checkBoxContent.isSelected() && checkBoxName.isSelected()) level = Duplicate.Level.BOTH;
            duplicatePaths = d.findDuplicates(level);
            if (duplicatePaths != null) checkboxArray = addCheckboxes();
            else PopUpUserError.start("Invalid Path entered!");
        } catch (Exception e) {
            return null;
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");

        return checkboxArray;
    }

    /**
     * Adds Checkboxes to Duplicate Output
     *
     * @return Checkbox Array
     */
    public CheckBox[] addCheckboxes() {
        vboxDuplOut.getChildren().removeAll();
        String[] duplicates = Duplicate.toStringArray(duplicatePaths);
        CheckBox[] checkboxArray = new CheckBox[duplicates.length];
        for (int i = 0; i < duplicates.length; i += 2) {
            checkboxArray[i] = new CheckBox();
            checkboxArray[i + 1] = new CheckBox();
            checkboxArray[i].getStyleClass().add("checkbox");
            checkboxArray[i + 1].getStyleClass().add("checkbox");
            HBox nh = new HBox();
            Label li = new Label(duplicates[i]);
            li.getStyleClass().add("text");
            Label lipluseins = new Label(duplicates[i + 1]);
            lipluseins.getStyleClass().add("text");
            Label number = new Label(" " + ((i + 2) / 2) + ". Duplicate");
            number.getStyleClass().add("text");
            Button b = new Button("Copy Path");
            b.getStyleClass().add("button");
            final String duplicatesoni = duplicates[i];//for lambda
            b.setOnAction(e -> {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(duplicatesoni), null);
            });
            Button bpluseins = new Button("Copy Path");
            b.getStyleClass().add("button");
            final String duplicatesonipluseins = duplicates[i];//for lambda
            b.setOnAction(e -> {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(duplicatesonipluseins), null);
            });
            nh.getChildren().addAll(number, checkboxArray[i], li, b, checkboxArray[i + 1], lipluseins, bpluseins);
            vboxDuplOut.getChildren().addAll(nh);
        }
        return checkboxArray;
    }
}