package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcKcContractNum;
import static ca.riveros.ib.Common.calcKcLossLevel;
import static ca.riveros.ib.Common.calcKcNetLoss$;
import static ca.riveros.ib.Common.calcKcTakeLoss$;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Created by admin on 11/24/16.
 */
public class KCProbabilityOfProfitEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public KCProbabilityOfProfitEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double kcProbOfProfit = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCPROBPROFIT.getIndex(), kcProbOfProfit);

        //Get needed fields
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double kcMaxLoss = (Double) rowList.get(KCMAXLOSS.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();
        Double kcEdge = (Double) rowList.get(KCEDGE.getIndex()).getItem();
        Double kcTakeProfitPer = (Double) rowList.get(KCTAKEPROFITPER.getIndex()).getItem();


        Platform.runLater(() -> {

            //Calculate KC Loss Level
            Double kcLossLevel = calcKcLossLevel(kcTakeProfitPer, kcProbOfProfit, kcEdge);
            updateCellValue(rowList.get(KCLOSSPER.getIndex()), kcLossLevel);

            //Calculate KC Take Loss $
            Double kcTakeLoss$ = calcKcTakeLoss$(entry$, kcLossLevel);
            updateCellValue(rowList.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);

            //Calculate KC Net Loss $
            Double kcNetLoss$ = calcKcNetLoss$(entry$,kcTakeLoss$);
            updateCellValue(rowList.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

            //Calculate KC Contract # (KC-Qty)
            Double kcQty = calcKcContractNum(kcMaxLoss, kcNetLoss$);
            updateCellValue(rowList.get(KCCONTRACTNUM.getIndex()), kcQty);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = kcQty - qty;
            updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

        });
    }

}