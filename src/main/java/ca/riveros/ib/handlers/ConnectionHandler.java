package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import com.ib.controller.ApiController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionHandler implements ApiController.IConnectionHandler {

    //Mediator
    Mediator mediator;

    //Data
    private ApiController apiController;

    //Logging
    private Logger inLogger;

    public ConnectionHandler(Mediator mediator, Logger inLogger, Logger outLogger) {
        this.mediator = mediator;
        this.inLogger = inLogger;
        apiController = new ApiController(this, inLogger, outLogger);
        apiController.connect("127.0.0.1", 7497, 0, null);
    }

    public ApiController getApiController() {
        return apiController;
    }

    @Override
    public void connected() {
        inLogger.log(" ------- Connected to TWS Interactive Brokers ------ ");
    }

    @Override
    public void disconnected() {
        System.out.println(" ------- Disconnected from TWS Interactive Brokers  ------ ");
    }

    @Override
    public void accountList(ArrayList<String> list) {
        List accounts = list.stream().map(account -> {
            if(account.startsWith("DF"))
                account = account.concat("A");
            return account;
        })
        .collect(Collectors.toList());
        mediator.setAccountsOnMainWindow(accounts);
    }

    @Override
    public void error(Exception e) {
        inLogger.log("*** Error Received : " + e);
    }

    @Override
    public void message(int id, int errorCode, String errorMsg) {
        inLogger.log("Received the following message from TWS Interactive Brokers : ");
        inLogger.log("(" + id + ":" + errorCode + ") --> " + errorMsg);
    }

    @Override
    public void show(String string) {
        inLogger.log("TWS IS Showing " + string);
    }

}
