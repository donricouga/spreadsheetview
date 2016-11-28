package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Created by admin on 11/24/16.
 */
public class KCPercentPortEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public KCPercentPortEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        Platform.runLater(() -> {
            String account = spreadsheetDataList.get(row).get(ACCOUNT.getIndex()).getText();
            String contractId = spreadsheetDataList.get(row).get(CONTRACTID.getIndex()).getText();
            PersistentFields.setValue(account, Integer.valueOf(contractId), KCPERPORT.getIndex(), (Double) newValue);
        });
    }

}