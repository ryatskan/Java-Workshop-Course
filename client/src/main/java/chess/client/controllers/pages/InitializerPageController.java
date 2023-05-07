package chess.client.controllers.pages;

import chess.client.sharedCode.helper.Color;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static chess.client.Application.load;
import static chess.client.Data.gameController;

/*
 * Controller for the game initialization page.
 */
public class InitializerPageController implements Initializable {

    public Label header;
    @FXML
    MFXTextField singleGameName;
    @FXML
    MFXTextField multGameName;
    @FXML
    MFXComboBox singleGameColor;
    @FXML
    MFXComboBox multGameColor;
    @FXML
    MFXButton singleOKButton;
    @FXML
    MFXButton multOKButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MFXIconWrapper icon = new MFXIconWrapper();//new MFXIconWrapper(RandomUtils.randFromArray(Model.notificationsIcons).getDescription(), 16, 32);
        Label headerLabel = new Label();
        StackPane placeHolder = new StackPane();
        placeHolder.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(placeHolder, Priority.ALWAYS);
        HBox header = new HBox(10, icon, headerLabel, placeHolder);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(InsetsFactory.of(5, 0, 5, 0));
        header.setMaxWidth(Double.MAX_VALUE);


        Label contentLabel = new Label();
        contentLabel.getStyleClass().add("content");
        contentLabel.setWrapText(true);
        contentLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentLabel.setAlignment(Pos.TOP_LEFT);

        MFXButton action1 = new MFXButton("Action 1");
        MFXButton action2 = new MFXButton("Action 2");
        HBox actionsBar = new HBox(15, action1, action2);
        actionsBar.getStyleClass().add("actions-bar");
        actionsBar.setAlignment(Pos.CENTER_RIGHT);
        actionsBar.setPadding(InsetsFactory.all(5));

        BorderPane container = new BorderPane();
        container.getStyleClass().add("notification");
        container.setTop(header);
        container.setCenter(contentLabel);
        container.setBottom(actionsBar);
        container.getStylesheets().add(load("/css/ExampleNotification.css"));
        container.setMinHeight(200);
        container.setMaxWidth(400);


        ObservableList<String> observableList = new ObservableListBase<String>() {
            @Override
            public String get(int index) {
                if (index == 0) return "White";
                if (index == 1) return "Black";
                return "Random";
            }

            @Override
            public int size() {
                return 3;
            }
        };
        singleGameName.setTextLimit(10);
        multGameColor.setTextLimit(10);
        singleGameColor.setItems(observableList);
        multGameColor.setItems(observableList);
        singleOKButton.setOnMouseClicked(event -> createGame(true));
        multOKButton.setOnMouseClicked(event -> createGame(false));

    }

    void createGame(boolean singlePlayer) {
        String colorButton, name;
        Color color;
        if (singlePlayer) {
            name = singleGameName.getText();
            colorButton = (String) singleGameColor.getValue();
        } else {
            name = multGameName.getText();
            colorButton = (String) multGameColor.getValue();

        }
        if (colorButton == null) return;

        switch (colorButton) {
            case "White" -> color = Color.White;
            case "Black" -> color = Color.Black;
            default -> {
                if (new Random().nextInt() < 0.5) {
                    color = Color.White;
                } else color = Color.Black;
            }
        }
        System.out.println(color + " " + name);
        gameController.newGame(name, color, singlePlayer);
    }
}
