package chess.client.controllers.pages;

import chess.client.sharedCode.helper.TableEntry;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static chess.client.Data.actionInServer;
import static chess.client.Data.gameController;

/*
 * The home page of the game. Contains a table with all active multiplayer games.
 */
public class HomePageController implements Initializable {
    @FXML
    private MFXButton refreshButton;
    @FXML
    private MFXTableView<TableEntry> table;
    @FXML
    private MFXButton joinButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        table.autosizeColumnsOnInitialization();
    }

    private void setupTable() {
        MFXTableColumn<TableEntry> nameColumn = new MFXTableColumn<>("Game Name", true, Comparator.comparing(TableEntry::getGameName));
        MFXTableColumn<TableEntry> surnameColumn = new MFXTableColumn<>("White", true, Comparator.comparing(TableEntry::getWhite));
        MFXTableColumn<TableEntry> ageColumn = new MFXTableColumn<>("Black", true, Comparator.comparing(TableEntry::getBlack));

        nameColumn.setRowCellFactory(x -> new MFXTableRowCell<>(TableEntry::getGameName));
        surnameColumn.setRowCellFactory(x -> new MFXTableRowCell<>(TableEntry::getWhite));
        ageColumn.setRowCellFactory(x -> new MFXTableRowCell<>(TableEntry::getBlack) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});
        ageColumn.setAlignment(Pos.CENTER_RIGHT);

        table.getTableColumns().addAll(nameColumn, surnameColumn, ageColumn);
        table.getFilters().addAll(
                new StringFilter<>("Game Name", TableEntry::getGameName),
                new StringFilter<>("White", TableEntry::getWhite),
                new StringFilter<>("Black", TableEntry::getBlack)
        );
        //table.setItems(Model.people);


    refreshButton.setOnMouseClicked(e-> {
        try {
            List<TableEntry> input = actionInServer.getAllGames();
            table.setItems(FXCollections.observableArrayList(input));
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    });
    joinButton.setOnMouseClicked(e-> {
        List<TableEntry> selected = table.getSelectionModel().getSelectedValues();
        if (selected.size() == 1 ) {
            gameController.joinGame(selected.get(0).getGameName());
        } else {
            System.out.println("Select only one game!");
        }
    });
    }
}
