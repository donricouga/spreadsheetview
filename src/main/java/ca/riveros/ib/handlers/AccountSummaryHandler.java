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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

    Map<String, Double> initMarginReqMap = new HashMap<>(15);
    Map<String, Double> netLiqMap = new HashMap<>(15);


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
        /*inLogger.log("Finished downloading account summary.");
        mediator.updateTotalInitMargin(totalInitMarginReq.toString());
        mediator.updateTotalNetLiq(totalNetLiq.toString());
        totalInitMarginReq = 0.0;
        totalNetLiq = 0.0;
        createInitialSheet();
        finishedInitialLoad = true;*/
    }

    private void updateTotalInitMargin() {
        Double totalInitMargin = 0.0;
        Collection<Double> values = initMarginReqMap.values();
        for(Iterator <Double>iterator = values.iterator(); iterator.hasNext();) {
            totalInitMargin+= iterator.next();
        }
        mediator.updateTotalInitMargin(totalInitMargin);

        //TODO Also need to update a value in the spreadsheetview to trigger a recalculate.
    }

    private void updateTotalNetLiq() {
        Double totalNetLiq = 0.0;
        Collection<Double> values = netLiqMap.values();
        for(Iterator <Double>iterator = values.iterator(); iterator.hasNext();) {
            totalNetLiq+= iterator.next();
        }
        mediator.updateTotalNetLiq(totalNetLiq);

        //TODO Also need to update a value in the spreadsheetview to trigger a recalculate.
    }

}
