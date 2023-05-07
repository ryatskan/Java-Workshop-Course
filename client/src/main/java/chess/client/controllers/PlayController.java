package chess.client.controllers;

import chess.client.Data;
import chess.client.board.BoardPane;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import static chess.client.Data.*;
import static chess.client.board.BoardCell.CellStatus.*;

// TODO
//
/*
 * Controller of the chess game screen.
 */
public class PlayController implements Initializable {
    @FXML
    Text whitePlayerName;
    @FXML
    Text blackPlayerName;
    int clickCount = 0;
    //public CapturedPieceGrid capturedPiecesGrid;
    BoardPane boardPane;
    @FXML
    GridPane main;
    @FXML
    MFXButton leaveButton;
    @FXML
    MFXButton drawButton;
    @FXML
    Text gameName;
    @FXML
    Text turnText;

    private Set<Square> lastActivated = new HashSet<>();

    private Square lastSelected;

    public PlayController() {
        playController = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        leaveButton.setOnMouseClicked(mouseEvent -> gameController.leaveGame());
        drawButton.setOnMouseClicked(mouseEvent -> gameController.offerDraw());
    }

    public void setGameName(String text) {
        gameName.setText(text);
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName.setText(whitePlayerName);
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName.setText(blackPlayerName);
    }

    public void initializeNewGame(Color color) {
        cleanFields();
        main.getChildren().clear();
        this.gameName.setText("");
        boardPane = new BoardPane(color == Color.White);
        boardPane.setEventHandlers();
        main.add(boardPane, 1, 1);
    }

    public void cleanFields() {
        this.gameName.setText("");
        this.blackPlayerName.setText("");
        this.whitePlayerName.setText("");
        this.turnText.setText("");
    }

    /*
     * When clicked on a square
     */
    public void onClick(Square sq) {
        if (!gameController.insideGame || !gameController.isMyTurn()) return;
        if (clickCount == 1) {
            clickCount = 0;
            if (lastActivated.contains(sq)) {
                try {
                    gameController.makeMove(username, gameController.gameName, this.lastSelected, sq);
                    removeSelectionAndActivation(lastActivated, lastSelected);
                    lastSelected = null;
                } catch (RemoteException e) {
                    System.out.println("Failed to make a move");
                    throw new RuntimeException(e);
                }
            } else {
                onClick(sq);
            }
        } else {
            try {
                removeSelectionAndActivation(lastActivated, lastSelected);
                lastActivated = actionInServer.getLegalMoves(username, gameController.gameName, sq);
                lastSelected = sq;
                boardPane.setStatus(lastSelected, SELECTED);
                activateSquares(lastActivated);
                clickCount++;
            } catch (RemoteException e) {
                System.out.println("Failed to get legal moves");
                throw new RuntimeException(e);
            }
        }
        System.out.println(sq.getRow() + " : " + sq.getColumn());
    }

    private void removeSelectionAndActivation(Set<Square> lastActivated, Square lastSelected) {
        if (lastSelected != null) boardPane.setStatus(lastSelected, NONE);
        lastActivated.forEach(x -> boardPane.setStatus(x, NONE));
    }
    // Show dialog for promotion piece selection.
    public void getPromotionAndMove(Square from, Square to) {

        MFXStageDialog dialog1 = createNodelessDialog("Choose which piece to promote to", "Promotion",
                false, false);
        ((MFXGenericDialog) dialog1.getContent()).addActions(
                Map.entry(new MFXButton("Queen"), event -> {
                    dialog1.close();
                    gameController.makePromotionMove(from, to, 'q');
                }),
                Map.entry(new MFXButton("Rook"), event -> {
                    dialog1.close();
                    gameController.makePromotionMove(from, to, 'r');
                }),
                Map.entry(new MFXButton("Bishop"), event -> {
                    dialog1.close();
                    gameController.makePromotionMove(from, to, 'b');
                }),
                Map.entry(new MFXButton("Knight"), event -> {
                    dialog1.close();
                    gameController.makePromotionMove(from, to, 'n');
                })
        );
    }

    public void drawOffer() {
        Platform.runLater(() -> {

            MFXStageDialog dialog1 = createNodelessDialog("Your opponent is offering you a draw. Accept the draw or decline", "Draw Offer",
                    false, false);
            ((MFXGenericDialog) dialog1.getContent()).addActions(Map.entry(new MFXButton("Accept"), event -> {
                        try {
                            Data.actionInServer.acceptDraw(username, gameController.getCurrentGame(), true);
                        } catch (RemoteException e) {
                            System.out.println("Failed to accept draw");
                            throw new RuntimeException(e);
                        }
                        dialog1.close();
                    }),
                    Map.entry(new MFXButton("Decline"), event -> dialog1.close())
            );
        });
    }
    // Generate dialog builder.
    MFXStageDialog createNodelessDialog(String contextText, String title, boolean allowHiding, boolean allowClosing) {
        MFXStageDialog dialog;
        MFXGenericDialog dialogContent;
        dialogContent = MFXGenericDialogBuilder.build()
                .setContentText(contextText)
                .makeScrollable(true)
                .get();
        dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(stage)
                .setOwnerNode(boardPane)
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(false)
                .setTitle(title)
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();
        if (allowHiding) dialog.setOnHiding(event -> dialog.close());
        if (allowClosing) dialog.setOnCloseRequest(event -> dialog.close());
        dialogContent.setMaxSize(400, 200);
        dialogContent.setHeaderIcon(null);
        dialogContent.setHeaderText(title);

        dialog.showDialog();
        return dialog;
    }
    private void activateSquares(Set<Square> sqs) {
        for (Square sq : sqs) {
            boardPane.setStatus(sq, ACTIVATED);
        }
    }

    public void setTurnText(Color color) {
        this.turnText.setText(color.toString());
    }
    // Print end of game dialog
    public void printHeadline(String print) {
        Platform.runLater(() -> {

            MFXStageDialog dialog1 = createNodelessDialog(print, "Game End", true, true);
            ((MFXGenericDialog) dialog1.getContent()).addActions(
                    Map.entry(new MFXButton("OK"), event -> dialog1.close())
            );
        });
    }

    public void refreshGame
            (GameUpdate gameUpdate) {
        Platform.runLater(() -> {
            try {
                setGameName(gameUpdate.getGameName());
                setBlackPlayerName(gameUpdate.getBlackPlayerName());
                setWhitePlayerName(gameUpdate.getWhitePlayerName());
                setBoard(gameUpdate);
                setTurnText(gameUpdate.getCurrTurn());
                // LOGIC
                System.out.println("Successfully refreshed chess game");
            } catch (Exception e) {
                System.out.println("Failed to refresh game state");
                throw e;
            }
        });
    }

    /*
     * Server call to update the board in the client after a move was made.
     */
    public void setBoard(GameUpdate input) {
        boardPane.setNoImage();
        input.getPositionList().parallelStream().forEach(
                v -> boardPane.setImageByCell(v.getImage(), v.getSquare()));
    }
}
