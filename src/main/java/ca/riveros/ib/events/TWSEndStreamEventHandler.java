package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.*;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * We need to handle all data calculations in a single sweep instead of firing update events from each cell.
 * This prevents Race conditions for updating spreadsheet cells.
 * This event handler is called after the mid field is updated which signifies the end of the TWS Stream
 */
public class TWSEndStreamEventHandler implements EventHandler<Event> {

    private ObservableList<SpreadsheetCell> rowList = null;
    private Double entry$;
    private Double netLiq;
    private Double qty;
    private Double probProfit;
    private Double kcPercentPort;
    private Double profitPercent;
    private Double kcEdge;
    private Double kcTakeProfitPer;

    @Override
    public void handle(Event event) {
        SpreadsheetCell cell = (SpreadsheetCell) event.getTarget();
        rowList = Mediator.INSTANCE.getSpreadSheetCells().get(cell.getRow());

        //Get Fields sent in by TWS
        entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        netLiq = Mediator.INSTANCE.getAccountNetLiq();
        qty = (Double) rowList.get(QTY.getIndex()).getItem();

        //Let's get all the manual Fields First since they are already there.
        probProfit = (Double) rowList.get(KCPROBPROFIT.getIndex()).getItem();
        kcPercentPort = (Double) rowList.get(KCPERPORT.getIndex()).getItem();
        profitPercent = (Double) rowList.get(PROFITPER.getIndex()).getItem();
        kcEdge = (Double) rowList.get(KCEDGE.getIndex()).getItem();
        kcTakeProfitPer = (Double) rowList.get(KCTAKEPROFITPER.getIndex()).getItem();

        Platform.runLater(() -> updateCalculatedFields());
    }

    /** Run this in a background UI thread to update fields **/
    private void updateCalculatedFields() {

        //Calculate KC Take Profit $
        Double kcTakeProfit$ = calcKCTakeProfit$(entry$, kcTakeProfitPer);
        updateCellValue(rowList.get(KCTAKEPROFITDOL.getIndex()), kcTakeProfit$);

        //Calculate KC Net Profit $
        Double kcNetProfit$ = calcKcNetProfit$(entry$, kcTakeProfit$);
        updateCellValue(rowList.get(KCNETPROFITDOL.getIndex()), kcNetProfit$);

        //KC Loss %
        Double kcLossLevel = calcKcLossLevel(kcTakeProfitPer, probProfit, kcEdge);
        updateCellValue(rowList.get(KCLOSSPER.getIndex()), kcLossLevel);

        //Calculate KC Take Loss $
        Double kcTakeLoss$ = calcKcTakeLoss$(entry$, kcLossLevel);
        updateCellValue(rowList.get(KCTAKELOSSDOL.getIndex()),kcTakeLoss$);

        //Calculate KC Net Loss $
        Double kcNetLoss$ = calcKcNetLoss$(entry$, kcTakeLoss$);
        updateCellValue(rowList.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);

        //Calculate KC Max Loss
        Double kcMaxLoss = calcKcMaxLoss(netLiq, kcPercentPort);
        updateCellValue(rowList.get(KCMAXLOSS.getIndex()), kcMaxLoss);

        //Calculate KC Contract# (KC-Qty)
        Double kcContractNum = calcKcContractNum(kcMaxLoss, kcNetLoss$);
        updateCellValue(rowList.get(KCCONTRACTNUM.getIndex()), kcContractNum);

        //Calculate Qty. Open/Close
        Double qtyOpenClose = calcQtyOpenClose(kcContractNum, qty);
        updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);
    }



}
