package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
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
public class ProfitPercentageEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public ProfitPercentageEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        /*ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double profitPercent = (Double) newValue;

        //Get Fields sent in by TWS
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double netLiq = Mediator.INSTANCE.getAccountNetLiq();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();

        //Let's get all the manual Fields First since they are already there.
        Double probProfit = (Double) rowList.get(KCPROBPROFIT.getIndex()).getItem();
        Double kcPercentPort = (Double) rowList.get(KCPERPORT.getIndex()).getItem();
        Double kcEdge = (Double) rowList.get(KCEDGE.getIndex()).getItem();

        Platform.runLater(() -> {

            //Update Persistent File with new Manual Value
            String account = rowList.get(ACCOUNT.getIndex()).getText();
            String contractId = rowList.get(CONTRACTID.getIndex()).getText();
            PersistentFields.setValue(account, Integer.valueOf(contractId), PROFITPER.getIndex(), profitPercent);
            updateCellValue(rowList.get(KCTAKEPROFITPER.getIndex()), profitPercent);

            //Calculate KC Loss %
            Double kcProfitPercent = profitPercent;
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

            //Calculate KC Max Loss
            Double kcMaxLoss = netLiq * kcPercentPort;
            updateCellValue(rowList.get(KCMAXLOSS.getIndex()), kcMaxLoss);

            //Calculate KC-Qty
            Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * -100);
            updateCellValue(rowList.get(KCCONTRACTNUM.getIndex()), kcQty);

            //Calculate Qty. Open/Close
            updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), kcQty - qty);

        });*/
    }

}