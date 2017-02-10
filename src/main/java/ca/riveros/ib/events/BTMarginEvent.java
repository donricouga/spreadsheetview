package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;

public class BTMarginEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public BTMarginEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double btMargin = (Double) newValue;
        Double dolSymbol = (Double) rowList.get(DOLSYMBOL.getIndex()).getItem();
        String account = (String) rowList.get(ACCOUNTNUM.getIndex()).getItem();
        updateCellValue(rowList.get(BTCONTRACT.getIndex()), calcContract(dolSymbol, btMargin));
        PersistentFields.setMargin(account, btMargin);
    }
}