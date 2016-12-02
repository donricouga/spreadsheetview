package ca.riveros.ib;

import ca.riveros.ib.events.NetLiqChangeListener;
import ca.riveros.ib.handlers.*;
import ca.riveros.ib.model.SpreadsheetModel;
import com.ib.controller.AccountSummaryTag;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import java.util.List;

public class Mediator extends Application {

    private TwsIbSpreadSheetView mainWindow;
    private Stage primaryStage;

    public static Mediator INSTANCE = null;

    //TWS API Handlers
    private ConnectionHandler connectionHandler;
    private Logger inLogger;
    private Logger outLogger;
    private Logger messageLogger;
    private AccountInfoHandler accountInfoHandler;

    private Boolean addedAccountNetLiqHandler = false;

    public Mediator() {
        mainWindow = new TwsIbSpreadSheetView(this);
        INSTANCE = this;
    }

    @Override
    public void start(Stage stage) throws Exception {

        //Create TWS Connection
        inLogger = new Logger(mainWindow.inLoggerText);
        outLogger = new Logger(mainWindow.outLoggerText);
        messageLogger = new Logger(mainWindow.messagesLoggerText);
        connectionHandler = new ConnectionHandler(this, inLogger, outLogger, messageLogger);

        //Set Stage
        primaryStage = stage;
        mainWindow.start(primaryStage);

        //Get account summary data
        connectionHandler.getApiController()
                .reqAccountSummary("All", AccountSummaryTag.values(), new AccountSummaryHandler(this, inLogger));
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

        //Only one instance of a handler should exist.
        if(accountInfoHandler == null)
            accountInfoHandler = new AccountInfoHandler(this, account, inLogger);
        connectionHandler.getApiController().reqAccountUpdates(true, account, accountInfoHandler);
    }

    /**
     * Lets the Account Update Handler add the results of the reqAccountUpdates() API to the SpreadsheetView Grid
     * @param list
     */
    public void updateSpreadsheetViewGrid(List<SpreadsheetModel> list ) {
        mainWindow.updateSpreadsheetViewGrid(list);
    }

    public ObservableList<ObservableList<SpreadsheetCell>> getSpreadSheetCells() {
        return mainWindow.spreadSheetView.getGrid().getRows();
    }

    /**
     * Update the Net Liquidity of this portfolio
     * @param value Dollar amount received from TWS IB
     */
    public void updateAccountNetLiq(String value) {
        mainWindow.accountNetLiqTextField.setText(value);
    }

    /**
     * Update the total net Liq for all accounts
     * @return
     */
    public void updateTotalNetLiq(String value) {
        mainWindow.totalNetLiqTextField.setText(value);
    }

    /**
     * Update the total Init Margin for all accounts
     * @return
     */
    public void updateTotalInitMargin(String value) {
        mainWindow.totalInitMarginTextField.setText(value);
    }

    /**
     * Get the Total NetLiq
     * @return
     */
    public Double getAccountNetLiq() {
        try {
            return Double.valueOf(mainWindow.accountNetLiqTextField.getText());
        } catch(NumberFormatException nfe) {
            return 0.0;
        }
    }

    public void addAccountNetLiqChangeListener() {
        if(!addedAccountNetLiqHandler)
            mainWindow.accountNetLiqTextField.textProperty().addListener(new NetLiqChangeListener(mainWindow.spreadSheetView));

    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }


    public static void main(String ...args) {
        launch(args);
    }
}
