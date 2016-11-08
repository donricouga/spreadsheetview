package ca.riveros.ib;

import ca.riveros.ib.handlers.AccountInfoHandler;
import ca.riveros.ib.handlers.ConnectionHandler;
import ca.riveros.ib.handlers.Logger;
import ca.riveros.ib.model.SpreadsheetModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.List;

public class Mediator extends Application {

    private TwsIbSpreadSheetView mainWindow;
    private Stage primaryStage;

    //TWS API Handlers
    private ConnectionHandler connectionHandler;
    private Logger inLogger;
    private Logger outLogger;
    private AccountInfoHandler accountInfoHandler;

    public Mediator() {
        mainWindow = new TwsIbSpreadSheetView(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Set Stage
        primaryStage = stage;
        mainWindow.start(primaryStage);

        //Create TWS Connection
        inLogger = new Logger();
        outLogger = new Logger();
        connectionHandler = new ConnectionHandler(this, inLogger, outLogger);
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
        outLogger.log("Requesting account Updates for " + account);
        accountInfoHandler = new AccountInfoHandler(this, inLogger);
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

    public static void main(String ...args) {
        launch(args);
    }
}
