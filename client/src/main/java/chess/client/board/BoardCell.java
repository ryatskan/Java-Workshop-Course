package chess.client.board;

import chess.client.controllers.PlayController;
import chess.client.sharedCode.gamerelated.Square;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.ROSYBROWN;
import static javafx.scene.paint.Color.WHITE;
// Inspired from https://github.com/gameofjess/Jess
public class BoardCell extends StackPane {
    static final Color blackCellColor = ROSYBROWN;
    static final Color whiteCellColor = WHITE;
    final static Color activatedCellColor = Color.YELLOW;
    final static Color selectedCellColor= Color.DARKBLUE;
    private final Square sq;
    private CellStatus status;
    private final chess.client.sharedCode.helper.Color sqColor;
    private final ImageView piece;

    /**
     * Constructs a BoardCell.
     */
    BoardCell(Square sq, chess.client.sharedCode.helper.Color color) {
        this.sq = sq;
        sqColor = color;
        piece = new ImageView();
        status = CellStatus.NONE;
        piece.setSmooth(true);
        piece.fitHeightProperty().bind(heightProperty());
        piece.fitWidthProperty().bind(widthProperty());
        this.getChildren().add(piece);
        updateBackground();
    }

    /**
     * Sets the Image that is drawn on a BoardCell.
     *
     * @param img Image that shall be displayed.
     */
    synchronized void setImage(Image img) {
        piece.setVisible(true);
        piece.setImage(img);
        piece.setPreserveRatio(true);
    }
    synchronized public void clearImage() {
        piece.setVisible(false);
    }

    void setStatus(CellStatus status) {
        this.status = status;
        updateBackground();
    }

    /*
     * Changes the BoardCell's background, depending on its color or if its selected or activated.
     */
    private void updateBackground() {
        BackgroundFill backgroundFillList = null;
        switch (status) {
            case ACTIVATED -> backgroundFillList = new BackgroundFill(activatedCellColor, CornerRadii.EMPTY, Insets.EMPTY);
            case SELECTED -> backgroundFillList = new BackgroundFill(selectedCellColor, CornerRadii.EMPTY, Insets.EMPTY);
            case NONE -> {
                switch (sqColor) {
                    case Black -> backgroundFillList = new BackgroundFill(blackCellColor, CornerRadii.EMPTY, Insets.EMPTY);
                    case White -> backgroundFillList = new BackgroundFill(whiteCellColor, CornerRadii.EMPTY, Insets.EMPTY);
                }
            }
        }
        this.setBackground(new Background(backgroundFillList));
    }
    public void setEventHandler(PlayController controller) {
        this.setOnMouseClicked(mouseEvent -> Platform.runLater(
                () -> controller.onClick(sq)));
    }

    public Square getSquare() {
        return sq;
    }


    public enum CellStatus {
        ACTIVATED,
        SELECTED,
        NONE
    }
}
