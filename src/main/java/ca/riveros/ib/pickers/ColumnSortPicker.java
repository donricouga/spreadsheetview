package ca.riveros.ib.pickers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
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
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList = FXCollections.observableArrayList();

        //Get Current SpreadsheetList
        ObservableList<ObservableList<SpreadsheetCell>> list = view.getGrid().getRows();
        FXCollections.sort(list, new ColumnSortComparator(index));
        list.forEach(rowList -> {

            ObservableList<SpreadsheetCell> rows = FXCollections.observableArrayList();
            rowList.forEach(oldCell -> {
                rows.add(createDeepCopyOfCell(oldCell, spreadsheetModelObservableList));
            });

            spreadsheetModelObservableList.add(rows);

        });
        Grid grid = view.getGrid();
        grid.setRows(spreadsheetModelObservableList);
        view.setGrid(grid);
    }

    private SpreadsheetCell createDeepCopyOfCell(SpreadsheetCell oldCell, ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList) {
        SpreadsheetCellType type = oldCell.getCellType();
        SpreadsheetCell newCell = null;

        int row = oldCell.getRow();
        int col = oldCell.getColumn();
        Object item = oldCell.getItem();
        Boolean editable = oldCell.isEditable();

        if ("double".equals(type.toString())) {
            newCell = SpreadsheetCellType.DOUBLE.createCell(row, col, 1, 1, (Double) item);
        } else if ("string".equals(type.toString())) {
            newCell = SpreadsheetCellType.STRING.createCell(row, col, 1, 1, (String) item);
        } else if ("Integer".equals(type.toString())) {
            newCell = SpreadsheetCellType.INTEGER.createCell(row, col, 1, 1, (Integer) item);
        }

        newCell.setEditable(editable);
        newCell.getStyleClass().addAll(oldCell.getStyleClass());
        addListeners(newCell, col, spreadsheetModelObservableList);
        newCell.setFormat(oldCell.getFormat());
        return newCell;
    }

    private void addListeners(SpreadsheetCell newCell, int col, ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList) {
      
    }
}
