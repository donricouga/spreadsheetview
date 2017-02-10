package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calc$Symbol;
import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.BTMARGIN;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.DOLTRADED;

public class PercentSymbolEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public PercentSymbolEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);

        //Get Needed Values
        Double percentSymbol = (Double) newValue;
        Double dolTraded = (Double) rowList.get(DOLTRADED.getIndex()).getItem();
        String account = (String) rowList.get(ACCOUNTNUM.getIndex()).getItem();
        Double margin = (Double)  rowList.get(BTMARGIN.getIndex()).getItem();

        //Now Calculate
        Double dolSymbol = calc$Symbol(dolTraded, percentSymbol);
        Double contract = calcContract(dolSymbol, margin);

        //Update Spreadsheet
        updateCellValue(rowList.get(DOLSYMBOL.getIndex()), dolSymbol);
        updateCellValue(rowList.get(BTCONTRACT.getIndex()), contract);

        //Persist Manual changed field
        PersistentFields.setPercentSymbol(account, percentSymbol);
    }
}