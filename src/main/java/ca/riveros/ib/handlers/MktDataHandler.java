package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.ib.controller.ApiController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

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
    private Double bid;
    private Double ask;

    /** Contract ID for this Mkt Request **/
    private Contract contract;

    public MktDataHandler(Mediator mediator, Logger logger, Contract contract) {
        this.mediator = mediator;
        this.logger = logger;
        this.contract = contract;
    }

    @Override
    public void tickOptionComputation(TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
    }

    @Override
    public void tickPrice(TickType tickType, double price, int canAutoExecute) {
        if("BID".equals(tickType.name())) {
            logger.log("Received BID price at " + price + " for account " + contract.description() + " " + contract.conid());
            bid = price;
        }
        else if("ASK".equals(tickType.name())) {
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
        logger.log("Finished receiving market data for contract " + contract.description() + " " + contract.conid());
        ObservableList<ObservableList<SpreadsheetCell>> spreadSheetData = mediator.getSpreadSheetCells();
        spreadSheetData.forEach(obList -> {
            if((Integer) obList.get(CONTRACTID.getIndex()).getItem() == contract.conid()) {
                Platform.runLater(() -> {
                    obList.get(BID.getIndex()).setItem(bid);
                    obList.get(ASK.getIndex()).setItem(ask);
                    obList.get(MID.getIndex()).setItem((bid + ask) / 2);
                });
            }
        });
    }

    @Override
    public void marketDataType(Types.MktDataType marketDataType) {

    }
}
