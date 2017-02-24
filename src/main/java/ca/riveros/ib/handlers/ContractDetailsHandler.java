package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.controller.ApiController;

import java.util.ArrayList;

import static ca.riveros.ib.Common.hasElements;

/**
 * Created by admin on 11/8/16.
 */
public class ContractDetailsHandler implements ApiController.IContractDetailsHandler {

    //Handlers Refs
    private Logger inLogger;

    //Mediator
    private Mediator mediator;

    public ContractDetailsHandler(Mediator mediator, Logger inLogger) {
        this.inLogger = inLogger;
        this.mediator = mediator;
    }

    @Override
    public void contractDetails(ArrayList<ContractDetails> list) {
        //in here request market data
        inLogger.log("Received the following Contract Details ...");
        inLogger.log(list.toString());
        if(hasElements.test(list)) {
            Contract contract = list.get(0).contract();
            MktDataHandler mktDataHandler = new MktDataHandler(mediator, inLogger, contract);
            mediator.getConnectionHandler().getApiController().reqOptionMktData(list.get(0).contract(), "", false, mktDataHandler);
        }
    }
}
