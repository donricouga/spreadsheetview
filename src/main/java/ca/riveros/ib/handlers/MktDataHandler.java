package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.ib.controller.ApiController;

/**
 * Created by admin on 11/8/16.
 */
public class MktDataHandler implements ApiController.IOptHandler {

    //Mediator
    private Mediator mediator;

    //Logger
    private Logger logger;

    public MktDataHandler(Mediator mediator, Logger logger) {
        this.mediator = mediator;
        this.logger = logger;
    }

    @Override
    public void tickOptionComputation(TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
    }

    @Override
    public void tickPrice(TickType tickType, double price, int canAutoExecute) {
        if("BID".equals(tickType.name()) || "ASK".equals(tickType.name())) {
            logger.log("RECEIVED " + tickType.name() + " at " + price );
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
        logger.log("RECEIVED ALL MARKET DATA!");
    }

    @Override
    public void marketDataType(Types.MktDataType marketDataType) {

    }
}
