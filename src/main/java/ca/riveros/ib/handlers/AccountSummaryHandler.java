package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.events.BTMarginEvent;
import ca.riveros.ib.events.NetLiqEventHandler;
import ca.riveros.ib.events.PercentSymbolEvent;
import ca.riveros.ib.events.PercentTradedEvent;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.calc$Symbol;
import static ca.riveros.ib.Common.calc$Traded;
import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.createCell;
import static ca.riveros.ib.Common.dollarFormat;
import static ca.riveros.ib.Common.findCellByAccountNumberAndColumn;
import static ca.riveros.ib.Common.twoPercentFormat;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.BTMARGIN;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.DOLTRADED;
import static ca.riveros.ib.TableColumnIndexes.NETLIQ;
import static ca.riveros.ib.TableColumnIndexes.PERSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.PERTRADED;
import static ca.riveros.ib.data.PersistentFields.getMargin;
import static ca.riveros.ib.data.PersistentFields.getPercentSymbol;
import static ca.riveros.ib.data.PersistentFields.getPercentTraded;
import static ca.riveros.ib.events.EventTypes.netLiqEventType;

/**
 * Created by admin on 11/17/16.
 */
public class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

    private Mediator mediator;
    private Logger inLogger;

    private Double totalInitMarginReq = 0.0;
    private Double totalNetLiq = 0.0;

    private Boolean finishedInitialLoad = false;

    HashMap<String, String> accountNetLiqMap = new HashMap<>(15);


    public AccountSummaryHandler(Mediator mediator, Logger inLogger) {
        this.mediator = mediator;
        this.inLogger = inLogger;
    }

    @Override
    public void accountSummary(String account, AccountSummaryTag tag, String value, String currency) {

        if ("InitMarginReq".equals(tag.name())) {
            inLogger.log("INIT MARGIN REQ " + value + " FOR ACCOUNT " + account);
            totalInitMarginReq = totalInitMarginReq + Double.valueOf(value);
        }
        if ("NetLiquidation".equals(tag.name())) {
            if(!finishedInitialLoad) {
                inLogger.log("NET LIQ " + value + " FOR ACCOUNT " + account + " INITIAL LOAD");
                accountNetLiqMap.put(account, value);
            }
            else {
                inLogger.log("NET LIQ " + value + " FOR ACCOUNT " + account);
                Event.fireEvent((SpreadsheetCellBase) findCellByAccountNumberAndColumn(mediator.getBlockTradingSpreadSheetView(),
                        account, NETLIQ.getIndex()),  new Event(netLiqEventType));
            }
            totalNetLiq = totalNetLiq + Double.valueOf(value);

        }

    }

    @Override
    public void accountSummaryEnd() {
        inLogger.log("Finished downloading account summary.");
        mediator.updateTotalInitMargin(totalInitMarginReq.toString());
        mediator.updateTotalNetLiq(totalNetLiq.toString());
        totalInitMarginReq = 0.0;
        totalNetLiq = 0.0;
        createInitialSheet();
        finishedInitialLoad = true;
    }

    private void createInitialSheet() {
        SpreadsheetView blockTrading = mediator.getBlockTradingSpreadSheetView();
        Grid g = blockTrading.getGrid();
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList = FXCollections.observableArrayList();
        AtomicInteger counter = new AtomicInteger(0);
        accountNetLiqMap.forEach((k,v) -> {
            ObservableList<SpreadsheetCell> rowsList = FXCollections.observableArrayList();
            Double netLiq = Double.valueOf(v);
            rowsList.add(createCell(counter.intValue(),ACCOUNTNUM.getIndex(), k, false));

            SpreadsheetCell netLiqCell = createCell(counter.intValue(),NETLIQ.getIndex(), netLiq, false, dollarFormat);
            netLiqCell.addEventHandler(netLiqEventType, new NetLiqEventHandler());
            rowsList.add(netLiqCell);
            Double percentTraded = getPercentTraded(k, 0.27);

            rowsList.add(createCell(counter.intValue(),PERTRADED.getIndex(), getPercentTraded(k, 0.27), true,
                    "manualy", new PercentTradedEvent(spreadsheetModelObservableList, mediator.getSpreadSheetCells2()), twoPercentFormat));
            Double dollarTraded = calc$Traded(netLiq, percentTraded);

            rowsList.add(createCell(counter.intValue(),DOLTRADED.getIndex(), dollarTraded, false, dollarFormat));
            Double percentSymbol = getPercentSymbol(k, 0.012);

            rowsList.add(createCell(counter.intValue(),PERSYMBOL.getIndex(), percentSymbol, true,
                    "manualy", new PercentSymbolEvent(spreadsheetModelObservableList), twoPercentFormat));
            Double dollarSymbol = calc$Symbol(dollarTraded, percentSymbol);

            rowsList.add(createCell(counter.intValue(),DOLSYMBOL.getIndex(), dollarSymbol , false, dollarFormat));
            Double margin = getMargin(k, 1600.00);

            rowsList.add(createCell(counter.intValue(),BTMARGIN.getIndex(), margin, true,
                    "manualy", new BTMarginEvent(spreadsheetModelObservableList)));

            rowsList.add(createCell(counter.intValue(),BTCONTRACT.getIndex(), calcContract(dollarSymbol, margin), false));

            spreadsheetModelObservableList.add(rowsList);
            counter.incrementAndGet();
        });

        g.setRows(spreadsheetModelObservableList);
        mediator.getBlockTradingSpreadSheetView().setGrid(g);
    }

}
