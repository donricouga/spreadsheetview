package ca.riveros.ib.events;

import ca.riveros.ib.Common;
import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import java.util.ArrayList;
import java.util.List;

import static ca.riveros.ib.Common.calcMid;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ASK;
import static ca.riveros.ib.TableColumnIndexes.BID;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.MID;

/**
 * Created by ricardo on 2/11/17.
 */
public class RowChangeListener implements ListChangeListener<SpreadsheetCell> {

    private Integer index;

    public RowChangeListener(Integer index) {
        this.index = index;
    }

    @Override
    public void onChanged(ListChangeListener.Change<? extends SpreadsheetCell> c) {
        List<SpreadsheetCell> row = Mediator.INSTANCE.getSpreadSheetCells().get(index);
        List<SpreadsheetCell> row2 = Mediator.INSTANCE.getSpreadSheetCells2().get(index);
        List<SpreadsheetCell> row3 = Mediator.INSTANCE.getSpreadSheetCells3().get(index);

        //Now simply update the cells without trigerring the RowChangeListener.

        //Get Data that is update from Interactive Brokers (TWS)
        Double bid = (Double) row3.get(BID.getIndex()).getItem();
        Double ask = (Double) row3.get(ASK.getIndex()).getItem();
        Double entry$ = (Double) row.get(ENTRYDOL.getIndex()).getItem();

        //Update table
        Platform.runLater(() -> {
            updateCellValue(row.get(MID.getIndex()), calcMid(bid,ask));
        });

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
