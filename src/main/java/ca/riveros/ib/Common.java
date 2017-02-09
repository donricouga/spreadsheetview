package ca.riveros.ib;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by admin on 11/13/16.
 */
public class Common {

    public static Predicate<List<?>> hasElements = (list) -> list != null && list.size() > 0;

    public static void updateCellValue(SpreadsheetCell cell, Double value) {
        cell.setEditable(true);
        cell.setItem(value);
        cell.setEditable(false);
    }

    //% of Port
    public static Double calcPerOfPort(Double margin, Double netLiq) {
        if(netLiq == 0)
            return 0.0;
        return margin / netLiq;
    }

    //KC Take Profit $
    public static Double calcKCTakeProfit$(Double entry$, Double kcTakeProfitPer) {
        return entry$ * (1 - kcTakeProfitPer);
    }

    //KC Net Profit $
    public static Double calcKcNetProfit$(Double entry$, Double kcTakeProfit$) {
        return entry$ - kcTakeProfit$;
    }

    //KC Loss Level (KC Loss %)
    public static Double calcKcLossLevel(Double kcTakeProfitPer, Double kcProbOfProfit, Double kcEdge) {
        return (kcTakeProfitPer) / ((1 / (kcProbOfProfit - kcEdge)) - 1);
    }

    //KC Take Loss $
    public static Double calcKcTakeLoss$(Double entry$, Double kcLossLevel) {
        return entry$ * (1 + kcLossLevel);
    }

    //KC Net Loss $
    public static Double calcKcNetLoss$(Double entry$, Double kcTakeLoss$) {
        return entry$ - kcTakeLoss$;
    }

    //KC Max Loss
    public static Double calcKcMaxLoss(Double netLiq, Double kcPerPort) {
        return netLiq * kcPerPort;
    }

    //KC Contract # (KC - Qty)
    public static Double calcKcContractNum(Double kcMaxLoss, Double kcNetLoss$) {
        if(kcNetLoss$ == 0)
            return 0.0;
        return Math.floor(kcMaxLoss / (kcNetLoss$ * -100));
    }

    //Qty. Open/Close
    public static Double calcQtyOpenClose(Double kcContractNum, Double qty) {
        return kcContractNum - qty;
    }

    //KC Calculate Take Loss At
    public static Double calcKcCalculateTakeLossAt(Double kcCreditReceived, Double kcProbProfit, Double kcTakeLoss$) {
        return ((kcCreditReceived * (kcProbProfit * 100)) * 100) - ((kcTakeLoss$ * (1 - (kcProbProfit * 100) * 100)));
    }


    ////// UI

    public static SpreadsheetCell createCell(int row, int col, String value, Boolean editable) {
        SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, col, 1, 1, value);
        cell.setEditable(editable);
        return cell;
    }

    public static SpreadsheetCell createCell(int row, int col, Double value, Boolean editable) {
        SpreadsheetCell cell = SpreadsheetCellType.DOUBLE.createCell(row, col, 1, 1, value);
        cell.setEditable(editable);
        return cell;
    }

    public static SpreadsheetCell createCell(int row, int col, Double value, Boolean editable, String format) {
        SpreadsheetCell cell = SpreadsheetCellType.DOUBLE.createCell(row, col, 1, 1, value);
        cell.setEditable(editable);
        cell.setFormat(format);
        return cell;
    }

    public static SpreadsheetCell createCell(int row, int col, Double value, Boolean editable, String cssClass, ChangeListener cl) {
        SpreadsheetCell cell = createCell(row,col,value,editable);
        cell.getStyleClass().add(cssClass);
        cell.itemProperty().addListener(cl);
        return cell;
    }

    public static SpreadsheetCell createCell(int row, int col, Double value, Boolean editable, String cssClass, ChangeListener cl, String format) {
        SpreadsheetCell cell = createCell(row,col,value,editable, cssClass, cl);
        cell.setFormat(format);
        return cell;
    }

}
