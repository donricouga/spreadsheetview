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
public class ProbabilityOfProfitEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList;

    public ProbabilityOfProfitEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList) {
        this.spreadsheetDataList = spreadsheetDataList;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);
        Double probOfProfit = (Double) newValue;

        //Update Persistent File with new Manual Value
        String account = rowList.get(ACCOUNT.getIndex()).getText();
        String contractId = rowList.get(CONTRACTID.getIndex()).getText();
        PersistentFields.setValue(account, Integer.valueOf(contractId), PROBPROFIT.getIndex(), probOfProfit);

        Platform.runLater(() -> {

            //Update KC Loss %
            Double kcProfitPer = (Double) rowList.get(KCPROFITPER.getIndex()).getItem();
            Double kcEdge = (Double) rowList.get(KCEDGE.getIndex()).getItem();
            Double kcLossPer = (kcProfitPer) / ((1 / (probOfProfit - kcEdge)) - 1);
            updateCellValue(rowList.get(KCLOSSPER.getIndex()), kcLossPer);

            //Update KC Take Loss $
            Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
            Double kcTakeLoss$ = entry$ * kcLossPer;
            updateCellValue(rowList.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);

            //Update KC Net Loss $
            Double kcNetLoss$ = entry$ - kcTakeLoss$;
            updateCellValue(rowList.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

        });
    }

}