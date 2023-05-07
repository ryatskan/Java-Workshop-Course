package chess.client.controllers;

import chess.client.Data;
import chess.client.sharedCode.helper.Chat;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import static chess.client.Data.*;


/*
 * The central controller of the application. Controls the chat, the menu and loading all the sub-scenes.
 * The GameController controls only game logic side.
 */
public class SuperController implements Initializable {
	@FXML
	Text logText;
	@FXML
	public MFXTextField chatTextField;
	@FXML
	public MFXButton sendMsgButton;
	@FXML
	public MFXFilterComboBox playerFilter;
	@FXML
	public MFXScrollPane chatboxPane;
	public ToggleGroup toggleGroup;
	@FXML
	Text playerName;
	@FXML
	private HBox windowHeader;

	@FXML
	private MFXFontIcon closeIcon;

	@FXML
	private MFXFontIcon minimizeIcon;

	@FXML
	private MFXFontIcon alwaysOnTopIcon;

	@FXML
	private AnchorPane rootPane;

	@FXML
	private MFXScrollPane scrollPane;

	@FXML
	private VBox navBar;
	VBox chatVbox = new VBox();

	@FXML
	public StackPane contentPane;

	public SuperController() {
		superController = this;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.toggleGroup = new ToggleGroup();
		ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);

		Initializer init = new Initializer(this);
		//initializeLoader();
		playerName.setText("Username: " +  Data.username);

		chatboxPane.setContent(chatVbox);
		init.initializeChat(sendMsgButton,playerFilter, Data.actionInServer, Data.username  );
		init.initializeTheRest(closeIcon,minimizeIcon,alwaysOnTopIcon,windowHeader, rootPane, stage);
		init.initializeHelper(navBar);

		ScrollUtils.addSmoothScrolling(scrollPane);
	}

	public void setContent(boolean homeScreen) {
		if (homeScreen) {
			setToggleGroup(1);
			setContent(Data.homeScreen);
		} else {
			setContent(playScreen);
			setToggleGroup(0);

		}
	}

	// Dependent on the order loaded in Initializer.
	public void setToggleGroup(int i) {
		toggleGroup.selectToggle(toggleGroup.getToggles().get(i));
	}

	ToggleButton createToggle(String icon, String text) {
		MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);

		MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
		toggleNode.setAlignment(Pos.CENTER_LEFT);
		toggleNode.setMaxWidth(Double.MAX_VALUE);
		toggleNode.setToggleGroup(toggleGroup);

		return toggleNode;
	}
	public void setChatBox(Chat chat) {
		chatVbox.getChildren().clear();
		chatVbox.getChildren().add(new Text());
		chat.chats.forEach(e -> chatVbox.getChildren().add(new Pane(new Text(e))));
	}
	public void setContent(Node node) {
		this.contentPane.getChildren().setAll(node);
	}
	void onChatBoxClick() {
		try {
			// DISABLE
			sendMsgButton.setDisable(true);

			final Timeline animation = new Timeline(
					new KeyFrame(Duration.seconds(2),
							actionEvent -> sendMsgButton.setDisable(false)));
			animation.setCycleCount(1);
			animation.play();

			String to = (String) playerFilter.valueProperty().get();
			if (to != null) {

				actionInServer.sendMessage(to, username, chatTextField.getText());
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}

	}
	public void updateActivePlayers(List<String> activePlayers) {
		activePlayers.remove(username);
		try {
			playerFilter.setItems(FXCollections.observableList(activePlayers));
		} catch (Exception e) {
			System.out.println("Failed to update active players");
		}

	}



}