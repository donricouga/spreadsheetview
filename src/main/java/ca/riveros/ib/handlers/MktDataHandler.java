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
            updateField(contract.conid(), DELTA.getIndex(), delta, false);
            updateField(contract.conid(), IMPVOLPER.getIndex(), impliedVol, false);
        }
    }

    @Override
    public void tickPrice(TickType tickType, double price, int canAutoExecute) {
        if ("BID".equals(tickType.name())) {
            logger.log("Received BID price at " + price + " for account " + contract.description() + " " + contract.conid());
            updateField(contract.conid(), BID.getIndex(), price, true);
        } else if ("ASK".equals(tickType.name())) {
            logger.log("Received ASK price at " + price + " for account " + contract.description() + " " + contract.conid());
            updateField(contract.conid(), ASK.getIndex(), price, true);
        }
    }

    private void updateField(Integer contractId, Integer index, Double value, Boolean fireRecalculations) {
        ObservableList<ObservableList<SpreadsheetCell>> rows = Mediator.INSTANCE.getSpreadSheetCells3();
        for (int i = 0; i < rows.size(); i++) {
            ObservableList<SpreadsheetCell> row = rows.get(i);
            if (row.get(CONTRACTID.getIndex()).getItem().equals(contractId)) {
                updateCellByIndex(row, index, value, fireRecalculations);
            }
        }

    }

    private void updateCellByIndex(ObservableList<SpreadsheetCell> row, Integer index, Double value, Boolean fireRecalculations) {
        Platform.runLater(() -> {
            SpreadsheetCell cell = row.get(index);
            updateCellValue(cell, value);
            if(fireRecalculations)
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

}
