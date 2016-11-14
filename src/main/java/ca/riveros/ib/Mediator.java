package ca.riveros.ib;

import ca.riveros.ib.handlers.*;
import ca.riveros.ib.model.SpreadsheetModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.util.List;

public class Mediator extends Application {

    private TwsIbSpreadSheetView mainWindow;
    private Stage primaryStage;

    //TWS API Handlers
    private ConnectionHandler connectionHandler;
    private Logger inLogger;
    private Logger outLogger;
    private Logger messageLogger;
    private AccountInfoHandler accountInfoHandler;
    private MktDataHandler mktDataHandler;
    private ContractDetailsHandler contractDetailsHandler;

    public Mediator() {
        mainWindow = new TwsIbSpreadSheetView(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Set Stage
        primaryStage = stage;
        mainWindow.start(primaryStage);

        //Create TWS Connection
        inLogger = new Logger(mainWindow.inLoggerText);
        outLogger = new Logger(mainWindow.outLoggerText);
        messageLogger = new Logger(mainWindow.messagesLoggerText);
        connectionHandler = new ConnectionHandler(this, inLogger, outLogger, messageLogger);
    }

    /**
     * Updates the dropdown/combobox with all accounts
     * @param accountsList
     */
    public void setAccountsOnMainWindow(List<String> accountsList) {
        mainWindow.accountComboBox.setItems(FXCollections.observableArrayList(accountsList));
    }

    /**
     * Lets the UI request Account Updates on a particular account or All accounts using the Financial Advisory account
     * @param account the TWS Account Code
     */
    public void requestAccountUpdate(String account) {
        if(accountInfoHandler != null) {
            outLogger.log("Cancelling subscription for account " + accountInfoHandler.getAccount());
            connectionHandler.getApiController().reqAccountUpdates(false, accountInfoHandler.getAccount(), accountInfoHandler);
        }
        mktDataHandler = new MktDataHandler(this, inLogger);
        accountInfoHandler = new AccountInfoHandler(this, mktDataHandler, account, inLogger);
        connectionHandler.getApiController().reqAccountUpdates(true, account, accountInfoHandler);
    }

    /**
     * Lets the Account Update Handler add the results of the reqAccountUpdates() API to the SpreadsheetView Grid
     * @param list
     */
    public void updateSpreadsheetViewGrid(List<SpreadsheetModel> list ) {
        mainWindow.updateSpreadsheetViewGrid(list);
    }

    /**
     * Update the Net Liquidity of this portfolio
     * @param value Dollar amount received from TWS IB
     */
    public void updateAccountNetLiq(String value) {
        mainWindow.accountNetLiqTextField.setText(value);
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }


    public static void main(String ...args) {
        launch(args);
    }
}
