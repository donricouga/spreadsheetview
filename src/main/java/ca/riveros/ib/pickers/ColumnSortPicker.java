package ca.riveros.ib.pickers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

/**
 * Created by admin on 11/27/16.
 */
public class ColumnSortPicker extends Picker {

    private int index;
    private Grid grid;

    public ColumnSortPicker(Grid grid, int index) {
        super();
        this.index = index;
        this.grid = grid;
    }

    @Override
    public void onClick() {
        ObservableList<ObservableList<SpreadsheetCell>> list = grid.getRows();
        System.out.println("Clicked " + index);
        FXCollections.sort(list, new ColumnSortComparator(index));
        Platform.runLater(() -> grid.setRows(list));
    }
}
