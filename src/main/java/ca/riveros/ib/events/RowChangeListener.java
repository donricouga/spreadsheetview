package ca.riveros.ib.events;

import ca.riveros.ib.Common;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import java.util.ArrayList;
import java.util.List;

import static ca.riveros.ib.Common.updateCellValue;

/**
 * Created by ricardo on 2/11/17.
 */
public class RowChangeListener implements ListChangeListener<SpreadsheetCell> {


    @Override
    public void onChanged(Change<? extends SpreadsheetCell> c) {

    }

    public static void main(String ...args) {

        // Use Java Collections to create the List.
        List<SpreadsheetCell> list = new ArrayList<>();
        list.add(SpreadsheetCellType.DOUBLE.createCell(0, 0, 1, 1, 9.0));
        list.add(SpreadsheetCellType.DOUBLE.createCell(0, 1, 1, 1, 11.0));

        // Now add observability by wrapping it with ObservableList.
        ObservableList<SpreadsheetCell> observableList = FXCollections.observableList(list);
        observableList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("Detected a change! ");
            }
        });

        // Changes to the observableList WILL be reported.
        // This line will print out "Detected a change!"
        observableList.add(SpreadsheetCellType.DOUBLE.createCell(0, 1, 1, 1, 15.0));

        // Changes to the underlying list will NOT be reported
        // Nothing will be printed as a result of the next line.
        //list.add(SpreadsheetCellType.DOUBLE.createCell(0, 1, 1, 1, 19.0));

        //Updates detected?
        SpreadsheetCell testCell = observableList.get(0);
        updateCellValue(testCell, 25.0);
        observableList.set(0, testCell);

        observableList.forEach(cell -> System.out.println(cell.getItem().toString() + ","));
    }

}
