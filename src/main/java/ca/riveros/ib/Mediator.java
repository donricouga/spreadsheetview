package ca.riveros.ib;

import ca.riveros.ib.handlers.*;
import ca.riveros.ib.model.SpreadsheetModel;
import com.ib.controller.AccountSummaryTag;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.List;

import static ca.riveros.ib.Common.findCellByAccountNumberAndColumn;
import static ca.riveros.ib.TableColumnIndexes.PERTRADED;

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
    private String selectedAccount;

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
        this.selectedAccount = account;

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
        return mainWindow.spreadsheetView.getGrid().getRows();
    }

    public ObservableList<ObservableList<SpreadsheetCell>> getSpreadSheetCells2() {
        return mainWindow.spreadsheetView2.getGrid().getRows();
    }

    public ObservableList<ObservableList<SpreadsheetCell>> getSpreadSheetCells3() {
        return mainWindow.spreadsheetView3.getGrid().getRows();
    }

    public SpreadsheetView getBlockTradingSpreadSheetView(){
        return mainWindow.spreadsheetView4;
    }

    public String getSelectedAccount() {
        return selectedAccount;
    }

    public Double getPercentCapitalToTradeByAccountNumber(String accountNumber) {
        SpreadsheetView blockView = getBlockTradingSpreadSheetView();
        SpreadsheetCell cell = findCellByAccountNumberAndColumn(blockView, accountNumber, PERTRADED.getIndex());
        return (Double) cell.getItem();
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

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public TwsIbSpreadSheetView getMainWindow() {
        return mainWindow;
    }

    public static void main(String ...args) {
        launch(args);
    }
}
