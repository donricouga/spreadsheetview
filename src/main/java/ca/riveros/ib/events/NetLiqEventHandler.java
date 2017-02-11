package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.calc$Symbol;
import static ca.riveros.ib.Common.calc$Traded;
import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.calcKcMaxLoss;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.BTMARGIN;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.DOLTRADED;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.KCCONTRACTNUM;
import static ca.riveros.ib.TableColumnIndexes.KCEDGE;
import static ca.riveros.ib.TableColumnIndexes.KCMAXLOSS;
import static ca.riveros.ib.TableColumnIndexes.KCPERPORT;
import static ca.riveros.ib.TableColumnIndexes.MARGIN;
import static ca.riveros.ib.TableColumnIndexes.NETLIQ;
import static ca.riveros.ib.TableColumnIndexes.PEROFPORT;
import static ca.riveros.ib.TableColumnIndexes.PERSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.PERTRADED;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

/**
 * Created by ricardo on 2/11/17.
 */
public class NetLiqEventHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        SpreadsheetCell cell = (SpreadsheetCell) event.getTarget();
        List<SpreadsheetCell> blockTradingRow = Mediator.INSTANCE.getBlockTradingSpreadSheetView().getGrid().getRows().get(cell.getRow());

        //Get Needed Values
        Double percentTraded = (Double) blockTradingRow.get(PERTRADED.getIndex()).getItem();
        Double netLiq = (Double) blockTradingRow.get(NETLIQ.getIndex()).getItem();
        Double percentSymbol = (Double) blockTradingRow.get(PERSYMBOL.getIndex()).getItem();
        Double margin = (Double) blockTradingRow.get(BTMARGIN.getIndex()).getItem();
        String accountNum = (String) blockTradingRow.get(ACCOUNTNUM.getIndex()).getItem();

        //Now Calculate
        Double dolTraded = calc$Traded(netLiq, percentTraded);
        Double dolSymbol = calc$Symbol(dolTraded, percentSymbol);
        Double contract = calcContract(dolSymbol, margin);

        //Update Spreadsheet
        Platform.runLater(() -> {
            updateCellValue(blockTradingRow.get(DOLTRADED.getIndex()), dolTraded);
            updateCellValue(blockTradingRow.get(DOLSYMBOL.getIndex()), dolSymbol);
            updateCellValue(blockTradingRow.get(BTCONTRACT.getIndex()), contract);
            updateCellValue(blockTradingRow.get(NETLIQ.getIndex()), netLiq);
        });


        //Also update rows based on whether the currently selected account is being displayed
        if(accountNum.equals(Mediator.INSTANCE.getSelectedAccount())) {
            ObservableList<ObservableList<SpreadsheetCell>> list = Mediator.INSTANCE.getSpreadSheetCells();
            ObservableList<ObservableList<SpreadsheetCell>> list2 = Mediator.INSTANCE.getSpreadSheetCells2();
            AtomicInteger counter = new AtomicInteger(0);
            list.forEach(ssRow -> {

                Double accountNetLiq = Double.valueOf(netLiq);

                Platform.runLater(() -> {

                    //Update Percent Of Port
                    Double marg = (Double) ssRow.get(MARGIN.getIndex()).getItem();
                    updateCellValue(ssRow.get(PEROFPORT.getIndex()), marg / accountNetLiq);

                    //Update KC Max Loss
                    Double kcPerPort = (Double) list2.get(counter.get()).get(KCPERPORT.getIndex()).getItem();
                    Double kcMaxLoss = calcKcMaxLoss(accountNetLiq, Mediator.INSTANCE.getPercentCapitalToTradeByAccountNumber(accountNum), kcPerPort);
                    updateCellValue(list2.get(counter.get()).get(KCMAXLOSS.getIndex()), kcMaxLoss);

                    //Update KC-Qty
                    Double kcEdge = (Double) list2.get(counter.get()).get(KCEDGE.getIndex()).getItem();
                    Double entry$ = (Double) ssRow.get(ENTRYDOL.getIndex()).getItem();
                    Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * -100);
                    updateCellValue(list2.get(counter.get()).get(KCCONTRACTNUM.getIndex()), kcQty);

                    //Update Qty. Open/Close
                    Double qty = (Double) ssRow.get(QTY.getIndex()).getItem();
                    updateCellValue(list2.get(counter.get()).get(QTYOPENCLOSE.getIndex()), kcQty - qty);

                    counter.incrementAndGet();
                });
            });
        }

    }


}
