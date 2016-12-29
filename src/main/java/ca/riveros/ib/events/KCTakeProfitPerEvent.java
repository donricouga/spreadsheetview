package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcKCTakeProfit$;
import static ca.riveros.ib.Common.calcKcContractNum;
import static ca.riveros.ib.Common.calcKcLossLevel;
import static ca.riveros.ib.Common.calcKcNetLoss$;
import static ca.riveros.ib.Common.calcKcTakeLoss$;
import static ca.riveros.ib.Common.calcQtyOpenClose;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNT;
import static ca.riveros.ib.TableColumnIndexes.CONTRACTID;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.KCEDGE;
import static ca.riveros.ib.TableColumnIndexes.KCLOSSPER;
import static ca.riveros.ib.TableColumnIndexes.KCMAXLOSS;
import static ca.riveros.ib.TableColumnIndexes.KCNETLOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCPROBPROFIT;
import static ca.riveros.ib.TableColumnIndexes.KCCONTRACTNUM;
import static ca.riveros.ib.TableColumnIndexes.KCTAKELOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITPER;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

/**
 * Created by rriveros on 12/29/16.
 */
public class KCTakeProfitPerEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public KCTakeProfitPerEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double kcTakeProfitPer = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCTAKEPROFITPER.getIndex(), kcTakeProfitPer);

        Double kcProbProfit = (Double) rowList.get(KCPROBPROFIT.getIndex()).getItem();
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double kcMaxLoss = (Double) rowList.get(KCMAXLOSS.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();
        Double kcEdge = (Double) rowList.get(KCEDGE.getIndex()).getItem();

        Platform.runLater(() -> {

            //Calculate KC Take Profit $
            Double kcTakeProfit$ = calcKCTakeProfit$(entry$, kcTakeProfitPer);
            updateCellValue(rowList.get(KCTAKEPROFITDOL.getIndex()), kcTakeProfit$);

            //Calculate KC Loss Level
            Double kcLossLevel = calcKcLossLevel(kcTakeProfitPer, kcProbProfit, kcEdge);
            updateCellValue(rowList.get(KCLOSSPER.getIndex()), kcLossLevel);

            //Calculate KC Take Loss $
            Double kcTakeLoss$ = calcKcTakeLoss$(entry$, kcLossLevel);
            updateCellValue(rowList.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);

            //Calculate KC Net Loss $
            Double kcNetLoss$ = calcKcNetLoss$(entry$,kcTakeLoss$);
            updateCellValue(rowList.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

            //Calculate KC Contract # (KC-Qty)
            Double kcContractNum = calcKcContractNum(kcMaxLoss, kcNetLoss$);
            updateCellValue(rowList.get(KCCONTRACTNUM.getIndex()), kcContractNum);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = calcQtyOpenClose(kcContractNum, qty);
            updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

        });
    }
}
