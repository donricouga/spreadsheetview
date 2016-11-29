package ca.riveros.ib.pickers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

/**
 * Created by admin on 11/27/16.
 */
public class ColumnSortPicker extends Picker {

    private int index;
    private SpreadsheetView view;

    public ColumnSortPicker(SpreadsheetView view, int index) {
        super();
        this.index = index;
        this.view = view;
    }

    @Override
    public void onClick() {
        ObservableList<ObservableList<SpreadsheetCell>> list = view.getGrid().getRows();
        System.out.println("Clicked " + index);
        FXCollections.sort(list, new ColumnSortComparator(index));
        //Platform.runLater(() -> {
            Grid grid = view.getGrid();
            grid.setRows(list);
            view.setGrid(grid);
        //});
    }
}
