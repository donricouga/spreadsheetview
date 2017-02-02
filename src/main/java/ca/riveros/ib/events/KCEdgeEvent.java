package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcKcCalculateTakeLossAt;
import static ca.riveros.ib.Common.calcKcContractNum;
import static ca.riveros.ib.Common.calcKcLossLevel;
import static ca.riveros.ib.Common.calcKcNetLoss$;
import static ca.riveros.ib.Common.calcKcTakeLoss$;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Created by admin on 11/24/16.
 */
public class KCEdgeEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList2;
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList3;

    public KCEdgeEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList,
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
        Double kcEdge = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList3.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList3.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCEDGE.getIndex(), kcEdge);

        //Get needed fields
        Double kcProbProfit = (Double) rowList2.get(KCPROBPROFIT.getIndex()).getItem();
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double kcMaxLoss = (Double) rowList2.get(KCMAXLOSS.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();
        Double kcCreditReceived = (Double) rowList2.get(KCCREDITREC.getIndex()).getItem();

        Platform.runLater(() -> {

            //Calculate KC Loss Level
            Double kcTakeProfitPer = (Double) rowList2.get(KCTAKEPROFITPER.getIndex()).getItem();
            Double kcLossLevel = calcKcLossLevel(kcTakeProfitPer, kcProbProfit, kcEdge);
            updateCellValue(rowList2.get(KCLOSSLEVEL.getIndex()), kcLossLevel);

            //Calculate KC Take Loss $
            Double kcTakeLoss$ = calcKcTakeLoss$(entry$, kcLossLevel);
            updateCellValue(rowList2.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);

            //Calculate KC Net Loss $
            Double kcNetLoss$ = calcKcNetLoss$(entry$,kcTakeLoss$);
            updateCellValue(rowList2.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

            //Calculate KC Contract # (KC-Qty)
            Double kcQty = calcKcContractNum(kcMaxLoss, kcNetLoss$);
            updateCellValue(rowList2.get(KCCONTRACTNUM.getIndex()), kcQty);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = kcQty - qty;
            updateCellValue(rowList2.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

            //Calculate KC Calculate Take Loss At
            Double kcCalcTakeLossAt = calcKcCalculateTakeLossAt(kcCreditReceived, kcProbProfit, kcTakeLoss$);
            updateCellValue(rowList2.get(KCCALCTAKELOSSAT.getIndex()), kcCalcTakeLossAt);

        });
    }

}