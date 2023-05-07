package chess.client.board;

import chess.client.sharedCode.gamerelated.Square;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileInputStream;

import static chess.client.Data.playController;
import static chess.client.sharedCode.gamerelated.Square.toSquare;
import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;

// Inspired from https://github.com/gameofjess/Jess
public class BoardPane extends HBox {

   static final int size = 8;
   static final int width = 300;
   static final int height = 300;
    private GridPane boardGrid;

    BoardCell[] cells = new BoardCell[100];


    public BoardPane(boolean isWhite) {
        initializeGrid();
        initializeCells(isWhite);
    }

    void initializeGrid() {
        VBox vBox = new VBox();

        vBox.alignmentProperty().set(Pos.CENTER);
        alignmentProperty().set(Pos.CENTER);

        boardGrid = new GridPane();

        final NumberBinding binding = Bindings.min(widthProperty(), heightProperty());

        boardGrid.setMinSize(width, height);
        vBox.prefWidthProperty().bind(binding);
        vBox.prefHeightProperty().bind(binding);
        vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        vBox.setFillWidth(true);
        VBox.setVgrow(boardGrid, Priority.ALWAYS);

        for (int i = 0; i < size; i++) {
            final ColumnConstraints columnConstraints = new ColumnConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
            columnConstraints.setHgrow(Priority.SOMETIMES);
            boardGrid.getColumnConstraints().add(columnConstraints);

            final RowConstraints rowConstraints = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
            rowConstraints.setVgrow(Priority.SOMETIMES);
            boardGrid.getRowConstraints().add(rowConstraints);
        }

        vBox.getChildren().add(boardGrid);
        HBox.setHgrow(this, Priority.ALWAYS);
        getChildren().add(vBox);
    }
    /*
     * Initializes all BoardCells, assign them a Square.
     */
    void initializeCells(boolean isWhite) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Square sq = toSquare(i,j);
                final BoardCell child;

                if ((i + j) % 2 == 1) {
                    child = new BoardCell(sq, Black);
                } else child = new BoardCell(sq,White);


                assert sq != null;
                cells[sq.numVal()] = child;
                child.maxWidthProperty().bind(boardGrid.widthProperty().divide(size));
                child.maxHeightProperty().bind(boardGrid.heightProperty().divide(size));


                child.minWidthProperty().bind(boardGrid.widthProperty().divide(size));
                child.minHeightProperty().bind(boardGrid.heightProperty().divide(size));



                if (!isWhite) {
                    GridPane.setRowIndex(child,child.getSquare().getRow());
                } else {GridPane.setRowIndex( child,(7-child.getSquare().getRow())%size); }
                GridPane.setColumnIndex(child, child.getSquare().getColumn());
                boardGrid.getChildren().add(child);
            }
        }
    }
    public void setStatus(Square sq, BoardCell.CellStatus status) {
        getCell(sq).setStatus(status);
    }
    public void setImageByCell(File img, Square sq) {
        try {
            Image image = new Image(new FileInputStream(img));
            getCell(sq).setImage(image);
        } catch (Exception e) {
            System.out.println( e.getMessage() + " FAILED TO LOAD IMAGE");

        }
    }

    public void setEventHandlers() {
        for (Node n : boardGrid.getChildren()) {
            BoardCell cell = (BoardCell) n;
            cell.setEventHandler(playController);
        }
    }
    private BoardCell getCell(Square sq) {
        return cells[sq.numVal()];
    }

    public void setNoImage() {
        for (Square sq : Square.values()) {
            getCell(sq).clearImage();
        }
    }
}
