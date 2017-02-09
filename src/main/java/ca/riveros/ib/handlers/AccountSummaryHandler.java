package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.createCell;
import static ca.riveros.ib.data.PersistentFields.getMargin;
import static ca.riveros.ib.data.PersistentFields.getPercentSymbol;
import static ca.riveros.ib.data.PersistentFields.getPercentTraded;

/**
 * Created by admin on 11/17/16.
 */
public class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

    private Mediator mediator;
    private Logger inLogger;

    private Double totalInitMarginReq = 0.0;
    private Double totalNetLiq = 0.0;

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
            inLogger.log("NET LIQ " + value + " FOR ACCOUNT " + account);
            accountNetLiqMap.put(account, value);
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
    }

    private void createInitialSheet() {
        SpreadsheetView blockTrading = mediator.getBlockTradingSpreadSheetView();
        Grid g = blockTrading.getGrid();
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList = FXCollections.observableArrayList();
        AtomicInteger counter = new AtomicInteger(0);
        accountNetLiqMap.forEach((k,v) -> {
            ObservableList<SpreadsheetCell> rowsList = FXCollections.observableArrayList();
            rowsList.add(createCell(counter.intValue(),0, k, false));
            rowsList.add(createCell(counter.intValue(),1, v, false));
            Double percentTraded = getPercentTraded(k, 0.27);
            Double netLiq = Double.valueOf(v);
            rowsList.add(createCell(counter.intValue(),2, getPercentTraded(k, 0.27), false));
            Double dollarTraded = percentTraded * netLiq;
            rowsList.add(createCell(counter.intValue(),3, dollarTraded, false));
            Double percentSymbol = getPercentSymbol(k, 0.012);
            rowsList.add(createCell(counter.intValue(),4, percentSymbol, false));
            Double dollarSymbol = percentSymbol * dollarTraded;
            rowsList.add(createCell(counter.intValue(),5, dollarSymbol , false));
            Double margin = getMargin(k, 1600.00);
            rowsList.add(createCell(counter.intValue(),6, margin, false));
            rowsList.add(createCell(counter.intValue(),7, Math.floor(dollarSymbol / margin), false));

            spreadsheetModelObservableList.add(rowsList);
            counter.incrementAndGet();
        });

        g.setRows(spreadsheetModelObservableList);
        mediator.getBlockTradingSpreadSheetView().setGrid(g);
    }
}
