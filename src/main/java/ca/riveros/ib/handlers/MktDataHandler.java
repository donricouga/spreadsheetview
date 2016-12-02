package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.events.NetLiqChangeListener;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.ib.controller.ApiController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;

import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.events.EventTypes.twsEndStreamEventType;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Created by admin on 11/8/16.
 */
public class MktDataHandler implements ApiController.IOptHandler {

    //Mediator
    private Mediator mediator;

    //Logger
    private Logger logger;

    //Main BID AND ASK DATA
    private Double bid = 0.0;
    private Double ask = 0.0;
    private Double delta = 0.0;
    private Double impliedVol = 0.0;

    /**
     * Contract ID for this Mkt Request
     **/
    private Contract contract;

    public MktDataHandler(Mediator mediator, Logger logger, Contract contract) {
        this.mediator = mediator;
        this.logger = logger;
        this.contract = contract;
    }

    @Override
    public void tickOptionComputation(TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        if (TickType.ASK_OPTION.equals(tickType)) {
            logger.log("Received " + TickType.ASK_OPTION + " with delta " + delta + " and implied volatility " + impliedVol);
            this.delta = delta;
            this.impliedVol = impliedVol;
        }
    }

    @Override
    public void tickPrice(TickType tickType, double price, int canAutoExecute) {
        if ("BID".equals(tickType.name())) {
            logger.log("Received BID price at " + price + " for account " + contract.description() + " " + contract.conid());
            bid = price;
        } else if ("ASK".equals(tickType.name())) {
            logger.log("Received ASK price at " + price + " for account " + contract.description() + " " + contract.conid());
            ask = price;
        }
    }

    @Override
    public void tickSize(TickType tickType, int size) {

    }

    @Override
    public void tickString(TickType tickType, String value) {

    }

    @Override
    public void tickSnapshotEnd() {
        ObservableList<ObservableList<SpreadsheetCell>> spreadSheetData = mediator.getSpreadSheetCells();
        spreadSheetData.forEach(obList -> {
            if ((Integer) obList.get(CONTRACTID.getIndex()).getItem() == contract.conid()) {
                Platform.runLater(() -> {
                    updateCellValue(obList.get(BID.getIndex()), bid);
                    updateCellValue(obList.get(ASK.getIndex()), ask);
                    Double mid = (bid + ask) / 2;
                    logger.log("Setting Mid Price for " + contract.description() + " " + contract.conid() + " to " + mid);
                    SpreadsheetCell midCell = obList.get(MID.getIndex());
                    SpreadsheetCell perPl = obList.get(PERPL.getIndex());
                    SpreadsheetCell deltaCell = obList.get(DELTA.getIndex());
                    SpreadsheetCell impVolCell = obList.get(IMPVOLPER.getIndex());
                    Double entry$ = (Double) obList.get(ENTRYDOL.getIndex()).getItem();
                    updateCellValue(midCell, mid);
                    updateCellValue(perPl, calculatePercentPL(mid, entry$));
                    updateCellValue(impVolCell, this.impliedVol);
                    updateCellValue(deltaCell, this.delta);

                    //After all the data is passed in by tws, fire an event to begin calculations of each row.
                    Event.fireEvent((SpreadsheetCellBase) midCell, new Event(twsEndStreamEventType));
                });
            }
        });

        //We need to add the Account Net Liq Listener here because we want it to start listening after initial load
        mediator.addAccountNetLiqChangeListener();

    }

    @Override
    public void marketDataType(Types.MktDataType marketDataType) {

    }

    private Double calculatePercentPL(Double mid, Double entry$) {
        if (entry$ < 0)
            return (entry$ - mid) / entry$;
        else
            return (mid - entry$) / entry$;
    }
}
