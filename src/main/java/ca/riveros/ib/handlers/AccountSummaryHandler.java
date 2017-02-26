package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.events.BTMarginEvent;
import ca.riveros.ib.events.NetLiqEventHandler;
import ca.riveros.ib.events.PercentSymbolEvent;
import ca.riveros.ib.events.PercentTradedEvent;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.calc$Symbol;
import static ca.riveros.ib.Common.calc$Traded;
import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.calcPerOfPort;
import static ca.riveros.ib.Common.createCell;
import static ca.riveros.ib.Common.dollarFormat;
import static ca.riveros.ib.Common.twoPercentFormat;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.BTMARGIN;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.DOLTRADED;
import static ca.riveros.ib.TableColumnIndexes.MARGIN;
import static ca.riveros.ib.TableColumnIndexes.NETLIQ;
import static ca.riveros.ib.TableColumnIndexes.PEROFPORT;
import static ca.riveros.ib.TableColumnIndexes.PERSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.PERTRADED;
import static ca.riveros.ib.events.EventTypes.netLiqEventType;
import static ca.riveros.ib.data.PersistentFields.getMargin;
import static ca.riveros.ib.data.PersistentFields.getPercentSymbol;
import static ca.riveros.ib.data.PersistentFields.getPercentTraded;

/**
 * Created by admin on 11/17/16.
 */
public class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

    private Mediator mediator;
    private Logger inLogger;

    private Map<String, Double> initMarginReqMap = new HashMap<>(15);
    private Map<String, Double> netLiqMap = new HashMap<>(15);


    public AccountSummaryHandler(Mediator mediator, Logger inLogger) {
        this.mediator = mediator;
        this.inLogger = inLogger;
    }

    @Override
    public void accountSummary(String account, AccountSummaryTag tag, String value, String currency) {

        if ("InitMarginReq".equals(tag.name())) {
            inLogger.log("INIT MARGIN REQ " + value + " FOR ACCOUNT " + account);
            initMarginReqMap.put(account, Double.valueOf(value));
                updateTotalInitMargin();
        }
        if ("NetLiquidation".equals(tag.name())) {
            inLogger.log("NET LIQ " + value + " FOR ACCOUNT " + account);
            netLiqMap.put(account, Double.valueOf(value));
                updateTotalNetLiq();
        }

    }

    @Override
    public void accountSummaryEnd() {
        inLogger.log("Finished downloading account summary.");
        createInitialSheet();
    }

    private void updateTotalInitMargin() {
        Double totalInitMargin = 0.0;
        Collection<Double> values = initMarginReqMap.values();
        for (Iterator<Double> iterator = values.iterator(); iterator.hasNext(); ) {
            totalInitMargin += iterator.next();
        }
        mediator.updateTotalInitMargin(totalInitMargin);
    }

    private void updateTotalNetLiq() {
        Double totalNetLiq = 0.0;
        Collection<Double> values = netLiqMap.values();
        for (Iterator<Double> iterator = values.iterator(); iterator.hasNext(); ) {
            totalNetLiq += iterator.next();
        }
        mediator.updateTotalNetLiq(totalNetLiq);

        final Double tnl = totalNetLiq;

        if (mediator.isAccountDataLoaded()) {
            ObservableList<ObservableList<SpreadsheetCell>> rows = Mediator.INSTANCE.getSpreadSheetCells();
            rows.forEach(row -> {
                Double margin = (Double) row.get(MARGIN.getIndex()).getItem();
                Double perOfPort = calcPerOfPort(margin, tnl);
                SpreadsheetCell cell = row.get(PEROFPORT.getIndex());
                Platform.runLater(() -> updateCellValue(cell, perOfPort));
                row.set(PEROFPORT.getIndex(), cell);
            });
        }

    }

    private void createInitialSheet() {
        SpreadsheetView blockTrading = mediator.getBlockTradingSpreadSheetView();
        Grid g = blockTrading.getGrid();
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList = FXCollections.observableArrayList();
        AtomicInteger counter = new AtomicInteger(0);
        netLiqMap.forEach((k, v) -> {
            ObservableList<SpreadsheetCell> rowsList = FXCollections.observableArrayList();
            Double netLiq = Double.valueOf(v);
            if(k.startsWith("DF"))
                k = k.concat("A");
            rowsList.add(createCell(counter.intValue(), ACCOUNTNUM.getIndex(), k, false));

            SpreadsheetCell netLiqCell = createCell(counter.intValue(), NETLIQ.getIndex(), netLiq, false, dollarFormat);
            netLiqCell.addEventHandler(netLiqEventType, new NetLiqEventHandler());
            rowsList.add(netLiqCell);
            Double percentTraded = getPercentTraded(k, 0.27);

            rowsList.add(createCell(counter.intValue(), PERTRADED.getIndex(), getPercentTraded(k, 0.27), true,
                    "manualy", new PercentTradedEvent(spreadsheetModelObservableList, mediator.getSpreadSheetCells2()), twoPercentFormat));
            Double dollarTraded = calc$Traded(netLiq, percentTraded);

            rowsList.add(createCell(counter.intValue(), DOLTRADED.getIndex(), dollarTraded, false, dollarFormat));
            Double percentSymbol = getPercentSymbol(k, 0.012);

            rowsList.add(createCell(counter.intValue(), PERSYMBOL.getIndex(), percentSymbol, true,
                    "manualy", new PercentSymbolEvent(spreadsheetModelObservableList), twoPercentFormat));
            Double dollarSymbol = calc$Symbol(dollarTraded, percentSymbol);

            rowsList.add(createCell(counter.intValue(), DOLSYMBOL.getIndex(), dollarSymbol, false, dollarFormat));
            Double margin = getMargin(k, 1600.00);

            rowsList.add(createCell(counter.intValue(), BTMARGIN.getIndex(), margin, true,
                    "manualy", new BTMarginEvent(spreadsheetModelObservableList)));

            rowsList.add(createCell(counter.intValue(), BTCONTRACT.getIndex(), calcContract(dollarSymbol, margin), false));

            spreadsheetModelObservableList.add(rowsList);
            counter.incrementAndGet();
        });

        g.setRows(spreadsheetModelObservableList);
        mediator.getBlockTradingSpreadSheetView().setGrid(g);
    }


}
