package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.updateCellValue;
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
    private Double kcProfitPercent;

    @Override
    public void handle(Event event) {
        SpreadsheetCell cell = (SpreadsheetCell) event.getTarget();
        rowList = Mediator.INSTANCE.getSpreadSheetCells().get(cell.getRow());

        //Get Fields sent in by TWS
        entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        netLiq = Mediator.INSTANCE.getAccountNetLiq();
        qty = (Double) rowList.get(QTY.getIndex()).getItem();

        //Let's get all the manual Fields First since they are already there.
        probProfit = (Double) rowList.get(PROBPROFIT.getIndex()).getItem();
        kcPercentPort = (Double) rowList.get(KCPERPORT.getIndex()).getItem();
        profitPercent = (Double) rowList.get(PROFITPER.getIndex()).getItem();
        kcEdge = (Double) rowList.get(KCEDGE.getIndex()).getItem();

        //Lets update KC Profit % Since it's the same as Profit %
        kcProfitPercent = (Double) rowList.get(PROFITPER.getIndex()).getItem();
        updateCellValue(rowList.get(KCPROFITPER.getIndex()), profitPercent);
        updateCalculatedFields();
    }

    private void updateCalculatedFields() {

        //Calculate KC Loss %
        Double kcLossPercent = (kcProfitPercent) / ((1/(probProfit - kcEdge))-1);
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
        Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * - 100);
        updateCellValue(rowList.get(KCQTY.getIndex()), kcQty);

        //Calculate Qty. Open/Close
        updateCellValue(rowList.get(QTYOPENCLOSE.getIndex()), kcQty - qty);
    }



}
