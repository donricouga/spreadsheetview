package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Listener that listens for user to enter a margin and quickly calculates the % of Port which is
 * (Margin / AccountNetLiq)
 */
public class MarginActionEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;
    private Double accountNetLiq;

    public MarginActionEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList, Double accountNetLiq) {
        this.spreadsheetDataList = spreadsheetDataList;
        this.accountNetLiq = accountNetLiq;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        SpreadsheetCell perOfPortCell = spreadsheetDataList.get(row).get(PEROFPORT.getIndex());
        String account = spreadsheetDataList.get(row).get(ACCOUNT.getIndex()).getText();
        String contractId = spreadsheetDataList.get(row).get(CONTRACTID.getIndex()).getText();
        Platform.runLater(() -> updateCellValue(perOfPortCell, ((Double) newValue) / accountNetLiq));
        PersistentFields.setValue(account, Integer.valueOf(contractId), MARGIN.getIndex(), (Double) newValue);
    }

}
