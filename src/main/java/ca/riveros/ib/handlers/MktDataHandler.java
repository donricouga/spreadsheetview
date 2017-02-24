package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
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
            updateField(contract.conid(), BID.getIndex(), price);
        } else if ("ASK".equals(tickType.name())) {
            logger.log("Received ASK price at " + price + " for account " + contract.description() + " " + contract.conid());
            updateField(contract.conid(), ASK.getIndex(), price);
        }
    }

    private void updateField(Integer contractId, Integer index, Double value) {
        ObservableList<ObservableList<SpreadsheetCell>> rows = Mediator.INSTANCE.getSpreadSheetCells3();
        for (int i = 0; i < rows.size(); i++) {
            ObservableList<SpreadsheetCell> row = rows.get(i);
            if (row.get(CONTRACTID.getIndex()).getItem().equals(contractId)) {
                updateCellByIndex(row, index, value);
            }
        }

    }

    private void updateCellByIndex(ObservableList<SpreadsheetCell> row, Integer index, Double value) {
        Platform.runLater(() -> {
            SpreadsheetCell cell = row.get(index);
            updateCellValue(cell, value);
            row.set(index, cell);
        });
    }

    @Override
    public void tickSize(TickType tickType, int size) {

    }

    @Override
    public void tickString(TickType tickType, String value) {

    }

    @Override
    public void tickSnapshotEnd() {
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
