package ca.riveros.ib.events;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

/**
 * Used only to monitor whether to change colour of field or not.
 */
public class QtyOpenCloseEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;


    public QtyOpenCloseEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        SpreadsheetCell qtyOpenCloseCell = spreadsheetDataList.get(row).get(QTYOPENCLOSE.getIndex());
        Double val = (Double) newValue;
        if(val > 0)
            qtyOpenCloseCell.getStyleClass().add("positive");
        else
            qtyOpenCloseCell.getStyleClass().add("negative");
    }

}