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
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList2;
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList3;

    public KCPercentPortEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList,
                              ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList2,
                              ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList3) {
        this.spreadsheetDataList = spreadsheetDataList;
        this.spreadsheetDataList2 = spreadsheetDataList2;
        this.spreadsheetDataList3 = spreadsheetDataList3;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        ObservableList<SpreadsheetCell> rowList2 = spreadsheetDataList2.get(row);
        ObservableList<SpreadsheetCell> rowList3 = spreadsheetDataList3.get(row);
        Double kcPerPort = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList3.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList3.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCPERPORT.getIndex(), kcPerPort);

        //Get needed fields
        Double kcNetLoss$ = (Double) rowList2.get(KCNETLOSSDOL.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();
        Double netLiq = Mediator.INSTANCE.getAccountNetLiq();

        Platform.runLater(() -> {

            //Update KC Max Loss
            Double kcMaxLoss = calcKcMaxLoss(netLiq, kcPerPort);
            updateCellValue(rowList2.get(KCMAXLOSS.getIndex()), kcMaxLoss);

            //Calculate KC Contract # (KC-Qty)
            Double kcContractNum = calcKcContractNum(kcMaxLoss, kcNetLoss$);
            updateCellValue(rowList2.get(KCCONTRACTNUM.getIndex()), kcContractNum);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = calcQtyOpenClose(kcContractNum, qty);
            updateCellValue(rowList2.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

        });
    }

}
