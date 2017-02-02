package ca.riveros.ib.events;

import ca.riveros.ib.data.PersistentFields;
import ca.riveros.ib.ui.FlashingAnimation;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calcKCTakeProfit$;
import static ca.riveros.ib.Common.calcKcCalculateTakeLossAt;
import static ca.riveros.ib.Common.calcKcContractNum;
import static ca.riveros.ib.Common.calcKcLossLevel;
import static ca.riveros.ib.Common.calcKcNetLoss$;
import static ca.riveros.ib.Common.calcKcTakeLoss$;
import static ca.riveros.ib.Common.calcQtyOpenClose;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNT;
import static ca.riveros.ib.TableColumnIndexes.CONTRACTID;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.KCCALCTAKELOSSAT;
import static ca.riveros.ib.TableColumnIndexes.KCCREDITREC;
import static ca.riveros.ib.TableColumnIndexes.KCEDGE;
import static ca.riveros.ib.TableColumnIndexes.KCLOSSLEVEL;
import static ca.riveros.ib.TableColumnIndexes.KCMAXLOSS;
import static ca.riveros.ib.TableColumnIndexes.KCNETLOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCPROBPROFIT;
import static ca.riveros.ib.TableColumnIndexes.KCCONTRACTNUM;
import static ca.riveros.ib.TableColumnIndexes.KCTAKELOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITPER;
import static ca.riveros.ib.TableColumnIndexes.MID;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

/**
 * Created by rriveros on 12/29/16.
 */
public class KCTakeProfitPerEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList2;
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList3;

    public KCTakeProfitPerEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList,
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

        Double kcTakeProfitPer = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList3.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList3.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), KCTAKEPROFITPER.getIndex(), kcTakeProfitPer);

        Double kcProbProfit = (Double) rowList2.get(KCPROBPROFIT.getIndex()).getItem();
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double kcMaxLoss = (Double) rowList2.get(KCMAXLOSS.getIndex()).getItem();
        Double qty = (Double) rowList.get(QTY.getIndex()).getItem();
        Double kcEdge = (Double) rowList2.get(KCEDGE.getIndex()).getItem();
        Double kcCreditReceived = (Double) rowList2.get(KCCREDITREC.getIndex()).getItem();
        Double mid = (Double) rowList.get(MID.getIndex()).getItem();

        Platform.runLater(() -> {

            //Calculate KC Take Profit $
            Double kcTakeProfit$ = calcKCTakeProfit$(entry$, kcTakeProfitPer);
            SpreadsheetCell kcTakeProfit$Cell = rowList2.get(KCTAKEPROFITDOL.getIndex());
            updateCellValue(kcTakeProfit$Cell, kcTakeProfit$);

            //Calculate KC Loss Level
            Double kcLossLevel = calcKcLossLevel(kcTakeProfitPer, kcProbProfit, kcEdge);
            updateCellValue(rowList2.get(KCLOSSLEVEL.getIndex()), kcLossLevel);

            //Calculate KC Take Loss $
            Double kcTakeLoss$ = calcKcTakeLoss$(entry$, kcLossLevel);
            updateCellValue(rowList2.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);

            //Calculate KC Net Loss $
            Double kcNetLoss$ = calcKcNetLoss$(entry$,kcTakeLoss$);
            updateCellValue(rowList2.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

            //Calculate KC Contract # (KC-Qty)
            Double kcContractNum = calcKcContractNum(kcMaxLoss, kcNetLoss$);
            updateCellValue(rowList2.get(KCCONTRACTNUM.getIndex()), kcContractNum);

            //Calculate Qty. Open/Close
            Double qtyOpenClose = calcQtyOpenClose(kcContractNum, qty);
            updateCellValue(rowList2.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);

            //Calculate KC Calculate Take Loss At
            Double kcCalcTakeLossAt = calcKcCalculateTakeLossAt(kcCreditReceived, kcProbProfit, kcTakeLoss$);
            updateCellValue(rowList2.get(KCCALCTAKELOSSAT.getIndex()), kcCalcTakeLossAt);

            //Set Flashing Animation to KCTakeProfit$ Cell if MID > KCTakeProfit$
            if (mid > kcTakeProfit$)
                FlashingAnimation.getInstance().playKcTakeProfit$Animation(kcTakeProfit$Cell, FlashingAnimation.NEGATIVE);
            else
                FlashingAnimation.getInstance().stopKcTakeProfit$Animation(kcTakeProfit$Cell);

        });
    }
}
