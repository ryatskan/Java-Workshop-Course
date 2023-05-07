package chess.client.controllers;

import chess.client.sharedCode.communication.ActionInServer;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.css.PseudoClass;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

import static chess.client.Application.loadURL;
import static chess.client.Data.*;

/*
 * Initializes the components of the SuperController.
 */
public class Initializer {
    private double xOffset;
    private double yOffset;
    SuperController contr;
    Initializer(SuperController contr) {
        this.contr = contr;
    }

    public MFXLoader initializeHelper( VBox navBar) {
        MFXLoader loader = new MFXLoader();

        loader.addView(MFXLoaderBean.of("PLAY", loadURL("/fxml/Play.fxml")).setBeanToNodeMapper(() -> contr.createToggle("mfx-stepper", "Chess Game")).get());
        loader.addView(MFXLoaderBean.of("HomePage", loadURL("/fxml/HomePage.fxml")).setBeanToNodeMapper(() -> contr.createToggle("mfx-list-dropdown", "Home")).get());
        loader.addView(MFXLoaderBean.of("GAME-AI", loadURL("/fxml/InitializeGame.fxml")).setBeanToNodeMapper(() -> contr.createToggle("mfx-input-pipe-alt", "New Game")).get());
        loader.addView(MFXLoaderBean.of("ABOUT", loadURL("/fxml/About.fxml")).setBeanToNodeMapper(() -> contr.createToggle("mfx-circle-dot", "About")).get());



        loader.setOnLoadedAction(beans -> {
            List<ToggleButton
                    > nodes = new java.util.ArrayList<>(beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        toggle.setOnAction(event -> {
                            if (gameController.insideGame) {
                                superController.setContent(false);
                                System.out.println("H");
                                return;
                            }
                            toggle.setSelected(true);
                            contr.setContent(bean.getRoot());
                        });
                        if (bean.isDefaultView()) {
                            contr.setContent(bean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;

                    })
                    .toList());
            nodes.remove(0);
            navBar.getChildren().setAll(nodes);
        });
        //toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
        loader.start();
        playScreen = loader.getView("PLAY").getRoot();
        homeScreen = loader.getView("HomePage").getRoot();
        return loader;
    }

    public void initializeChat(MFXButton sendMsgButton, MFXFilterComboBox playerFilter,
                               ActionInServer actionInServer, String username) {
        sendMsgButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> superController.onChatBoxClick());
        playerFilter.valueProperty().addListener((observableValue, o, t1) -> {
            String chosen = (String) observableValue.getValue();
            if (chosen != null) {
                try {
                    contr.setChatBox(actionInServer.getChat(username, chosen));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }

    public void initializeTheRest(MFXFontIcon closeIcon, MFXFontIcon minimizeIcon, MFXFontIcon alwaysOnTopIcon, HBox windowHeader, AnchorPane rootPane, Stage stage) {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !stage.isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            stage.setAlwaysOnTop(newVal);
        });

        windowHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });


    }

}
