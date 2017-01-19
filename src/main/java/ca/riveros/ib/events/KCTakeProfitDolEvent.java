package ca.riveros.ib.events;

import ca.riveros.ib.ui.FlashingAnimation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.TableColumnIndexes.MID;

/**
 * Created by ricardo on 1/18/17.
 */
public class KCTakeProfitDolEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public KCTakeProfitDolEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double kcTakeProfit$ = (Double) newValue;
        Double mid = (Double) rowList.get(MID.getIndex()).getItem();

        //Set Flashing Animation to KCTakeProfit$ Cell if MID > KCTakeProfit$
        /*if (mid > kcTakeProfit$)
            FlashingAnimation.getInstance().playKcTakeProfit$Animation(c, FlashingAnimation.NEGATIVE);
        else
            FlashingAnimation.getInstance().stopKcTakeProfit$Animation(c);*/
    }
}