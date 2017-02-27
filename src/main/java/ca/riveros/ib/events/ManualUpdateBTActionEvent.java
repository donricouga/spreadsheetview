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
public class ManualUpdateBTActionEvent implements ChangeListener<Object> {

    public static final String PERCENT_TRADED = "percent_traded";
    public static final String PERCENT_SYMBOL = "percent_symbol";
    public static final String MARGIN = "margin";

    private String column;

    public ManualUpdateBTActionEvent(String column) {
        this.column = column;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        ObservableList<ObservableList<SpreadsheetCell>> rows4 = Mediator.INSTANCE.getSpreadSheetCells4();
        ObservableList<ObservableList<SpreadsheetCell>> rows3 = Mediator.INSTANCE.getSpreadSheetCells3();
        int row = c.getRow();
        String account = rows4.get(row).get(ACCOUNTNUM.getIndex()).getText();

        //Trigger the RowChangeListener
        Platform.runLater(() -> {

            //We want to trigger the RowChange Listener so that it will do all the recalculations.
            ObservableList<SpreadsheetCell> rowList = rows4.get(row);
            SpreadsheetCell accountNumCell = rowList.get(0);
            updateCellValue(accountNumCell, accountNumCell.getItem());
            rowList.set(0, accountNumCell);

            //Also trigger re calculations in other tables
            if(PERCENT_TRADED.equals(column)) {
                rows3.forEach(r -> {
                    SpreadsheetCell market$Cell = r.get(0);
                    updateCellValue(market$Cell, market$Cell.getItem());
                    r.set(0, market$Cell);
                });
            }

        });

        //Now save the manual field in file.
        switch (column) {
            case PERCENT_TRADED:
                PersistentFields.setPercentTraded(account, (Double) c.getItem());
                break;
            case PERCENT_SYMBOL:
                PersistentFields.setPercentSymbol(account, (Double) c.getItem());
                break;
            case MARGIN:
                PersistentFields.setMargin(account, (Double) c.getItem());
                break;
        }

    }

}
