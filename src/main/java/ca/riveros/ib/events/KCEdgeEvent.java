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
 * Created by admin on 11/24/16.
 */
public class KCEdgeEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public KCEdgeEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double kcEdge = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCEDGE.getIndex(), kcEdge);

        //Get needed fields
        Double probProfit = (Double) rowList.get(PROBPROFIT.getIndex()).getItem();
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double kcMaxLoss = (Double) rowList.get(KCMAXLOSS.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();

        Platform.runLater(() -> {

            //Calculate KC Loss %
            Double kcProfitPercent = (Double) rowList.get(KCPROFITPER.getIndex()).getItem();
            Double kcLossPercent = (kcProfitPercent) / ((1 / (probProfit - kcEdge)) - 1);
            updateCellValue(rowList.get(KCLOSSPER.getIndex()), kcLossPercent);

            //Calculate KC Take Profit $
            Double kcTakeProfit$ = entry$ * (1 - kcProfitPercent);
            updateCellValue(rowList.get(KCTAKEPROFITDOL.getIndex()), kcTakeProfit$);

            //Calculate KC Take Loss $
            Double kcTakeLoss$ = entry$ * kcLossPercent;
            updateCellValue(rowList.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);

            //Calculate KC Net Profit $
            Double kcNetProfit$ = entry$ - kcTakeProfit$;
            updateCellValue(rowList.get(KCNETPROFITDOL.getIndex()), kcNetProfit$);

            //Calculate KC Net Loss $
            Double kcNetLoss$ = entry$ - kcTakeLoss$;
            updateCellValue(rowList.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

            //Calculate KC-Qty
            Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * -100);
            updateCellValue(rowList.get(KCQTY.getIndex()), kcQty);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = kcQty - qty;
            updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

        });
    }

}