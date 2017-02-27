package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcPerOfPort;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.*;
import static ca.riveros.ib.data.PersistentFields.A_TABLE;

/**
 * Listener that listens for user to enter a margin and quickly calculates the % of Port which is
 * (Margin / AccountNetLiq)
 */
public class ManualUpdateActionEvent implements ChangeListener<Object> {

    private Integer col;

    public ManualUpdateActionEvent(Integer col) {
        this.col = col;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        ObservableList<ObservableList<SpreadsheetCell>> rows3 = Mediator.INSTANCE.getSpreadSheetCells3();
        int row = c.getRow();
        String account = rows3.get(row).get(ACCOUNT.getIndex()).getText();
        String contractId = rows3.get(row).get(CONTRACTID.getIndex()).getText();

        //Trigger the RowChangeListener
        Platform.runLater(() -> {

            //We want to trigger the RowChange Listener so that it will do all the recalculations.
            ObservableList<SpreadsheetCell> rowList = rows3.get(row);
            SpreadsheetCell market$Cell = rowList.get(0);
            updateCellValue(market$Cell, (Double) market$Cell.getItem());
            rowList.set(0, market$Cell);

        });

        //Now save the manual field in file.
        PersistentFields.setValue(account, Integer.valueOf(contractId), A_TABLE, col, (Double) newValue);
    }

}
