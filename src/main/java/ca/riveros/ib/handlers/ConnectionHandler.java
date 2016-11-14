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
    private Logger messageLogger;

    public ConnectionHandler(Mediator mediator, Logger inLogger, Logger outLogger, Logger messageLogger) {
        this.mediator = mediator;
        this.inLogger = inLogger;
        this.messageLogger = messageLogger;
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
        inLogger.log(" ------- Disconnected from TWS Interactive Brokers  ------ ");
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
        messageLogger.log("*** Error Received : " + e);
    }

    @Override
    public void message(int id, int errorCode, String errorMsg) {
        messageLogger.log("(" + id + ":" + errorCode + ") --> " + errorMsg);
    }

    @Override
    public void show(String string) {
        messageLogger.log(string);
    }

}
