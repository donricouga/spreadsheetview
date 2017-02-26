package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.events.KCEdgeEvent;
import ca.riveros.ib.events.KCPercentPortEvent;
import ca.riveros.ib.events.KCProbabilityOfProfitEvent;
import ca.riveros.ib.events.KCTakeProfitDolEvent;
import ca.riveros.ib.events.KCTakeProfitPerEvent;
import ca.riveros.ib.events.LossPercentageEvent;
import ca.riveros.ib.events.MarginActionEvent;
import ca.riveros.ib.events.ProfitPercentageEvent;
import ca.riveros.ib.events.QtyOpenCloseEvent;
import ca.riveros.ib.events.RowChangeListener;
import ca.riveros.ib.events.TWSEndStreamEventHandler;
import ca.riveros.ib.model.SpreadsheetModel;
import com.ib.client.Contract;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;
import com.ib.controller.Position;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.createCell;
import static ca.riveros.ib.Common.decimalFormat;
import static ca.riveros.ib.Common.dollarFormat;
import static ca.riveros.ib.Common.noDecimals;
import static ca.riveros.ib.Common.percentFormat;
import static ca.riveros.ib.Common.twoDecimalFormat;
import static ca.riveros.ib.Common.twoPercentFormat;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNT;
import static ca.riveros.ib.TableColumnIndexes.ASK;
import static ca.riveros.ib.TableColumnIndexes.BID;
import static ca.riveros.ib.TableColumnIndexes.CONTRACT;
import static ca.riveros.ib.TableColumnIndexes.CONTRACTID;
import static ca.riveros.ib.TableColumnIndexes.DELTA;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.IMPVOLPER;
import static ca.riveros.ib.TableColumnIndexes.KCCALCTAKELOSSAT;
import static ca.riveros.ib.TableColumnIndexes.KCCONTRACTNUM;
import static ca.riveros.ib.TableColumnIndexes.KCCREDITREC;
import static ca.riveros.ib.TableColumnIndexes.KCEDGE;
import static ca.riveros.ib.TableColumnIndexes.KCLOSSLEVEL;
import static ca.riveros.ib.TableColumnIndexes.KCMAXLOSS;
import static ca.riveros.ib.TableColumnIndexes.KCNETLOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCNETPROFITDOL;
import static ca.riveros.ib.TableColumnIndexes.KCPERPORT;
import static ca.riveros.ib.TableColumnIndexes.KCPROBPROFIT;
import static ca.riveros.ib.TableColumnIndexes.KCTAKELOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITPER;
import static ca.riveros.ib.TableColumnIndexes.LOSSPER;
import static ca.riveros.ib.TableColumnIndexes.MARGIN;
import static ca.riveros.ib.TableColumnIndexes.MARKETDOL;
import static ca.riveros.ib.TableColumnIndexes.MID;
import static ca.riveros.ib.TableColumnIndexes.NOTIONAL;
import static ca.riveros.ib.TableColumnIndexes.PEROFPORT;
import static ca.riveros.ib.TableColumnIndexes.PERPL;
import static ca.riveros.ib.TableColumnIndexes.PROFITPER;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;
import static ca.riveros.ib.TableColumnIndexes.REALPNL;
import static ca.riveros.ib.TableColumnIndexes.SYMBOL;
import static ca.riveros.ib.TableColumnIndexes.UNREALPNL;
import static ca.riveros.ib.data.PersistentFields.getValue;
import static ca.riveros.ib.events.EventTypes.twsEndStreamEventType;

public class AccountInfoHandler implements ApiController.IAccountHandler {

    //Mediator
    private Mediator mediator;

    //Handlers
    private ContractDetailsHandler contractDetailsHandler;
    private Logger inLogger;

    //Reference Data
    private String account;
    private List<Position> positionsList = new ArrayList<>(30);
    private Boolean initialLoadComplete = false;

    public AccountInfoHandler(Mediator mediator, String account, Logger inLogger) {
        this.inLogger = inLogger;
        this.mediator = mediator;
        this.account = account;
        contractDetailsHandler = new ContractDetailsHandler(mediator, inLogger);
    }

    public void reset(String account) {
        this.account = account;
        this.initialLoadComplete = false;
        positionsList.clear();
    }

    @Override
    public void accountValue(String account, String key, String value, String currency) {
        //inLogger.log("account : " + account + " key : " + key + " value : " + value + " currency : " + currency);
        if ("NetLiquidation".equals(key)) {
            inLogger.log("Received Net Liquidation " + value + " for account " + account);
            mediator.updateAccountNetLiq(value);
        }
    }

    @Override
    public void accountTime(String timeStamp) {
        inLogger.log("Received account information at " + timeStamp);
    }

    /**
     * This only gets executed once!!!!!! If this ever changes, the logic will break.
     * @param account
     */
    @Override
    public void accountDownloadEnd(String account) {
        inLogger.log("Finished receiving account stream");
        initialLoadComplete = true;
        addRowsToSpreadsheet();
    }

    @Override
    public void updatePortfolio(Position position) {
        inLogger.log("Received contract " + generateContractName(position.contract()));
        SearchEncapsulation se =
                getSpreadsheetRowIfExists(position.account(), position.conid());

        ObservableList<SpreadsheetCell> row3 = se.row;
        Integer index = se.rowIndex;

        if (row3 == null && !initialLoadComplete) {
            positionsList.add(position);

            //Call Account Details
            Contract contract = position.contract();

            //Set Exchange to empty to let TWS decide what exchange to use.
            contract.exchange("");
            mediator.getConnectionHandler().getApiController().reqContractDetails(
                    contract, contractDetailsHandler);
        }
        else {
            //Get the relevant Spreadsheets needed for the update
            ObservableList<SpreadsheetCell> row1 = Mediator.INSTANCE.getSpreadSheetCells().get(index);
            ObservableList<SpreadsheetCell> row2 = Mediator.INSTANCE.getSpreadSheetCells2().get(index);
            Platform.runLater(() -> updateSpreadsheet(row1, row2,  row3, position));


        }
    }


    private void updateSpreadsheet(ObservableList<SpreadsheetCell> row, ObservableList<SpreadsheetCell> row2,
                                   ObservableList<SpreadsheetCell> row3, Position pos) {

        //Update Entry$
        SpreadsheetCell entry$Cell = row.get(ENTRYDOL.getIndex());
        updateCellValue(entry$Cell, calculateAvgCost(pos.contract(), pos.averageCost()));
        //row.set(ENTRYDOL.getIndex(), entry$Cell);

        SpreadsheetCell kcCreditReceived = row2.get(KCCREDITREC.getIndex());
        updateCellValue(kcCreditReceived, calculateAvgCost(pos.contract(), pos.averageCost()));
        //row2.set(KCCREDITREC.getIndex(), kcCreditReceived);

        //Update Qty
        SpreadsheetCell qtyCell = row.get(QTY.getIndex());
        updateCellValue(qtyCell, (double) pos.position());
        //row.set(QTY.getIndex(), qtyCell);

        //Update Market$
        SpreadsheetCell market$Cell = row3.get(MARKETDOL.getIndex());
        updateCellValue(market$Cell, pos.marketPrice());
        //row3.set(MARKETDOL.getIndex(), market$Cell);

        //Update Notional
        SpreadsheetCell notionalCell = row3.get(NOTIONAL.getIndex());
        updateCellValue(notionalCell, pos.marketValue());
        //row3.set(NOTIONAL.getIndex(), notionalCell);

        //Update RealPL
        SpreadsheetCell realPlCell = row.get(REALPNL.getIndex());
        updateCellValue(realPlCell, pos.realPnl());
        //row.set(REALPNL.getIndex(), realPlCell);

        //Update UnrealPL and do a row.set to trigger the row to be all recalculated
        SpreadsheetCell unrealPlCell = row.get(UNREALPNL.getIndex());
        updateCellValue(unrealPlCell, pos.unrealPnl());
        row.set(UNREALPNL.getIndex(), unrealPlCell);
    }

    private void addRowsToSpreadsheet() {
        //Get Spreadsheet views
        SpreadsheetView spreadsheetView = Mediator.INSTANCE.getMainWindow().getSpreadsheetView();
        SpreadsheetView spreadsheetView2 = Mediator.INSTANCE.getMainWindow().getSpreadsheetView2();
        SpreadsheetView spreadsheetView3 = Mediator.INSTANCE.getMainWindow().getSpreadsheetView3();

        //Grids
        Grid g = spreadsheetView.getGrid();
        Grid g2 = spreadsheetView2.getGrid();
        Grid g3 = spreadsheetView3.getGrid();

        //Create a Collection<ObservableList<SpreadsheetCell>>
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList = FXCollections.observableArrayList();
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList2 = FXCollections.observableArrayList();
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList3 = FXCollections.observableArrayList();

        AtomicInteger counter = new AtomicInteger(0);
        positionsList.forEach(pos -> {
            ObservableList<SpreadsheetCell> rowsList = FXCollections.observableArrayList();
            ObservableList<SpreadsheetCell> rowsList2 = FXCollections.observableArrayList();
            ObservableList<SpreadsheetCell> rowsList3 = FXCollections.observableArrayList();
            Double entry$ = calculateAvgCost(pos.contract(), pos.averageCost());

            rowsList.add(SpreadsheetCellType.STRING.createCell(counter.intValue(), CONTRACT.getIndex(), 1, 1, generateContractName(pos.contract())));
            rowsList.add(createCell(counter.intValue(), QTY.getIndex(), (double) pos.position(), false));
            rowsList.add(createCell(counter.intValue(), ENTRYDOL.getIndex(), entry$, false, dollarFormat));
            SpreadsheetCell mid = createCell(counter.intValue(), MID.getIndex(), 0.0, false, decimalFormat);
            mid.addEventHandler(twsEndStreamEventType, new TWSEndStreamEventHandler());
            rowsList.add(mid);
            SpreadsheetCell unrealPNLCell = createCell(counter.intValue(), UNREALPNL.getIndex(), pos.unrealPnl(), false, dollarFormat);
            if (pos.unrealPnl() > 0)
                unrealPNLCell.getStyleClass().add("positive");
            else
                unrealPNLCell.getStyleClass().add("negative");
            rowsList.add(unrealPNLCell);
            SpreadsheetCell realPNLCell = createCell(counter.intValue(), REALPNL.getIndex(), pos.realPnl(), false, dollarFormat);
            if (pos.realPnl() > 0)
                realPNLCell.getStyleClass().add("positive");
            else if (pos.realPnl() < 0)
                realPNLCell.getStyleClass().add("negative");
            rowsList.add(realPNLCell);
            rowsList.add(createCell(counter.intValue(), PEROFPORT.getIndex(), 0.0, false, "##.#############" + "%"));
            rowsList.add(createCell(counter.intValue(), MARGIN.getIndex(), getValue(account, pos.conid(), MARGIN.getIndex(), 0.0), true, "manualy",
                    /*new MarginActionEvent(spreadsheetModelObservableList, spreadsheetModelObservableList3, Double.valueOf(accountNetLiqTextField.getText()))*/null, percentFormat));
            rowsList.add(createCell(counter.intValue(), PROFITPER.getIndex(), getValue(account, pos.conid(), PROFITPER.getIndex(), 0.57), true, "manualy",
                    /*new ProfitPercentageEvent(spreadsheetModelObservableList)*/null, percentFormat));
            rowsList.add(createCell(counter.intValue(), LOSSPER.getIndex(), getValue(account, pos.conid(), LOSSPER.getIndex(), 2.0), true, "manualy",
                    /*new LossPercentageEvent(spreadsheetModelObservableList)*/null, percentFormat));
            rowsList2.add(createCell(counter.intValue(), KCPROBPROFIT.getIndex(), getValue(account, pos.conid(), KCPROBPROFIT.getIndex(), 0.91), true, "manualy",
                    /*new KCProbabilityOfProfitEvent(spreadsheetModelObservableList, spreadsheetModelObservableList2, spreadsheetModelObservableList3)*/null, percentFormat));
            rowsList2.add(createCell(counter.intValue(), KCEDGE.getIndex(), getValue(account, pos.conid(), KCEDGE.getIndex(), 0.1), true, "manualy",
                    /*new KCEdgeEvent(spreadsheetModelObservableList, spreadsheetModelObservableList2, spreadsheetModelObservableList3)*/null, percentFormat));
            rowsList2.add(createCell(counter.intValue(), KCCALCTAKELOSSAT.getIndex(), 0.0, false, dollarFormat));
            rowsList2.add(createCell(counter.intValue(), KCCREDITREC.getIndex(), entry$, false, dollarFormat));
            rowsList2.add(createCell(counter.intValue(), KCTAKEPROFITPER.getIndex(), getValue(account, pos.conid(), KCTAKEPROFITPER.getIndex(), 0.42), true, "manualy",
                    /*new KCTakeProfitPerEvent(spreadsheetModelObservableList, spreadsheetModelObservableList2, spreadsheetModelObservableList3)*/null, percentFormat));
            rowsList2.add(createCell(counter.intValue(), KCTAKEPROFITDOL.getIndex(), 0.0, false, dollarFormat));
            rowsList2.add(createCell(counter.intValue(), KCNETPROFITDOL.getIndex(), 0.0, false, dollarFormat));
            rowsList2.add(createCell(counter.intValue(), KCLOSSLEVEL.getIndex(), 0.0, false, percentFormat));
            rowsList2.add(createCell(counter.intValue(), KCTAKELOSSDOL.getIndex(), 0.0, false, dollarFormat));
            rowsList2.add(createCell(counter.intValue(), KCNETLOSSDOL.getIndex(), 0.0, false, dollarFormat));
            rowsList2.add(createCell(counter.intValue(), KCPERPORT.getIndex(), getValue(account, pos.conid(), KCPERPORT.getIndex(), 0.0075), true, "manualy",
                    /*new KCPercentPortEvent(spreadsheetModelObservableList, spreadsheetModelObservableList2, spreadsheetModelObservableList3)*/null, percentFormat));
            rowsList2.add(createCell(counter.intValue(), KCMAXLOSS.getIndex(), 0.0, false, noDecimals));
            rowsList2.add(createCell(counter.intValue(), KCCONTRACTNUM.getIndex(), 0.0, false, noDecimals));
            SpreadsheetCell qtyOpenClose = createCell(counter.intValue(), QTYOPENCLOSE.getIndex(), 0.0, false, decimalFormat);
            //qtyOpenClose.itemProperty().addListener(new QtyOpenCloseEvent(spreadsheetModelObservableList2));
            rowsList2.add(qtyOpenClose);
            rowsList3.add(createCell(counter.intValue(), MARKETDOL.getIndex(), pos.marketPrice(), false, dollarFormat));
            rowsList3.add(createCell(counter.intValue(), NOTIONAL.getIndex(), pos.marketValue(), false, noDecimals));
            rowsList3.add(createCell(counter.intValue(), DELTA.getIndex(), 0.0, false, twoDecimalFormat));
            rowsList3.add(createCell(counter.intValue(), IMPVOLPER.getIndex(), 0.0, false, twoDecimalFormat));
            rowsList3.add(createCell(counter.intValue(), PERPL.getIndex(), 0.0, false, twoPercentFormat));
            rowsList3.add(createCell(counter.intValue(), BID.getIndex(), 0.0, false)); //BID
            rowsList3.add(createCell(counter.intValue(), ASK.getIndex(), 0.0, false)); //ASK
            rowsList3.add(SpreadsheetCellType.INTEGER.createCell(counter.intValue(), CONTRACTID.getIndex(), 1, 1, pos.conid()));
            rowsList3.add(SpreadsheetCellType.STRING.createCell(counter.intValue(), SYMBOL.getIndex(), 1, 1, pos.contract().symbol()));
            rowsList3.add(SpreadsheetCellType.STRING.createCell(counter.intValue(), ACCOUNT.getIndex(), 1, 1, pos.account()));

            //Add Listeners
            rowsList.addListener(new RowChangeListener(counter.get()));
            rowsList2.addListener(new RowChangeListener(counter.get()));
            rowsList3.addListener(new RowChangeListener(counter.get()));

            counter.incrementAndGet();

            spreadsheetModelObservableList.add(rowsList);
            spreadsheetModelObservableList2.add(rowsList2);
            spreadsheetModelObservableList3.add(rowsList3);
        });

        //set grid and display
        g.setRows(spreadsheetModelObservableList);
        spreadsheetView.setGrid(g);
        g2.setRows(spreadsheetModelObservableList2);
        spreadsheetView2.setGrid(g2);
        g3.setRows(spreadsheetModelObservableList3);
        spreadsheetView3.setGrid(g3);
    }

    private SearchEncapsulation getSpreadsheetRowIfExists(String account, Integer contractId) {
        ObservableList<ObservableList<SpreadsheetCell>> rows = Mediator.INSTANCE.getSpreadSheetCells3();
        for(int i = 0; i < rows.size(); i++) {
            ObservableList<SpreadsheetCell> row = rows.get(i);
            if(row.get(CONTRACTID.getIndex()).getItem().equals(contractId) && row.get(ACCOUNT.getIndex()).getItem().equals(account)) {
                return new SearchEncapsulation(row, i);
            }
        }
        return new SearchEncapsulation(null, 0);
    }


    /**
     * Generates the custom contract name using data obtained from contract fields.
     *
     * @param contract
     * @return
     */
    private String generateContractName(Contract contract) {
        String symbol = contract.symbol();
        String secType = contract.secType().getApiString();
        String tradingClass = contract.tradingClass();
        String expiry = contract.lastTradeDateOrContractMonth();
        Double strike = contract.strike();
        String right = contract.right().getApiString(); //"None" is default
        String exchange = contract.exchange() == null ? "" : contract.exchange();

        if (expiry != null && !expiry.isEmpty()) {
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
                DateFormat targetFormat = new SimpleDateFormat("MMMdd''yy");
                Date date = originalFormat.parse(expiry);
                expiry = targetFormat.format(date);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        } else {
            expiry = "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(symbol).append(" ").append(secType).append(" (").append(tradingClass).append(") ")
                .append(expiry).append(" ");
        if (strike != 0.0)
            sb.append(strike).append(" ");
        if (!"None".equals(right))
            sb.append(right).append(" ");
        sb.append("@").append(exchange);

        return sb.toString();

    }

    /**
     * returns the calculated avg cost based on SEC Type
     **/
    private double calculateAvgCost(Contract con, double averageCost) {
        if ("OPT".equals(con.secType().getApiString()))
            return averageCost / 100;
        return averageCost;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    class SearchEncapsulation {
        ObservableList<SpreadsheetCell> row;
        Integer rowIndex;

        public SearchEncapsulation(ObservableList<SpreadsheetCell> row, Integer rowIndex) {
            this.row = row;
            this.rowIndex = rowIndex;
        }
    }
}
