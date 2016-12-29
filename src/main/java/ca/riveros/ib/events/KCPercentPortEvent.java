package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcKcContractNum;
import static ca.riveros.ib.Common.calcKcMaxLoss;
import static ca.riveros.ib.Common.calcQtyOpenClose;
import static ca.riveros.ib.Common.updateCellValue;
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
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double kcPerPort = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCPERPORT.getIndex(), kcPerPort);

        //Get needed fields
        Double kcNetLoss$ = (Double) rowList.get(KCNETLOSSDOL.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();
        Double netLiq = Mediator.INSTANCE.getAccountNetLiq();

        Platform.runLater(() -> {

            //Update KC Max Loss
            Double kcMaxLoss = calcKcMaxLoss(netLiq, kcPerPort);
            updateCellValue(rowList.get(KCMAXLOSS.getIndex()), kcMaxLoss);

            //Calculate KC Contract # (KC-Qty)
            Double kcContractNum = calcKcContractNum(kcMaxLoss, kcNetLoss$);
            updateCellValue(rowList.get(KCCONTRACTNUM.getIndex()), kcContractNum);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = calcQtyOpenClose(kcContractNum, qty);
            updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

        });
    }

}
