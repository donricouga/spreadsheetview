package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.model.SpreadsheetModel;
import com.ib.client.Contract;
import com.ib.controller.ApiController;
import com.ib.controller.Position;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountInfoHandler implements ApiController.IAccountHandler {

    //Mediator
    private Mediator mediator;

    //Handlers
    private ContractDetailsHandler contractDetailsHandler;
    private Logger inLogger;

    //Reference Data
    private String account;

    private List <SpreadsheetModel>positionsList = new ArrayList<>(30);

    public AccountInfoHandler(Mediator mediator, String account, Logger inLogger) {
        this.inLogger = inLogger;
        this.mediator = mediator;
        this.account = account;
        contractDetailsHandler = new ContractDetailsHandler(mediator, inLogger);
    }

    @Override
    public void accountValue(String account, String key, String value, String currency) {
        //inLogger.log("account : " + account + " key : " + key + " value : " + value + " currency : " + currency);
        if("NetLiquidation".equals(key)) {
            inLogger.log("Received Net Liquidation " + value + " for account " + account);
            mediator.updateAccountNetLiq(value);
        }
    }

    @Override
    public void accountTime(String timeStamp) {
        inLogger.log("Received account information at " + timeStamp);
    }

    @Override
    public void accountDownloadEnd(String account) {
        inLogger.log("Finished with account " + account);
        mediator.updateSpreadsheetViewGrid(positionsList);

        //Call Contract Detail Handler
        positionsList.forEach(s -> {
            Contract contract = s.getTwsContract();

            //Set Exchange to empty to let TWS decide what exchange to use.
            contract.exchange("");
            mediator.getConnectionHandler().getApiController().reqContractDetails(
                    contract, contractDetailsHandler);
        });

        //clear for next call
        positionsList.clear();
    }

    @Override
    public void updatePortfolio(Position position) {
        inLogger.log("Received position " + position.account());
        SpreadsheetModel model = createSpreadsheetModel(position);
        model.setTwsContract(position.contract());
        positionsList.add(model);
    }

    private SpreadsheetModel createSpreadsheetModel(Position pos) {
        SpreadsheetModel model = new SpreadsheetModel();
        model.setContract(generateContractName(pos.contract()));
        model.setContractId(pos.contract().conid());
        model.setEntry$(calculateAvgCost(pos.contract(),pos.averageCost()));
        model.setMarket$(pos.marketPrice());
        model.setNotional(pos.marketValue());
        model.setRealPL(pos.realPnl());
        model.setUnrealPL(pos.unrealPnl());
        model.setAccount(pos.account());

        return model;
    }


    /**
     * Generates the custom contract name using data obtained from contract fields.
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

        if(expiry != null && !expiry.isEmpty()) {
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
                DateFormat targetFormat = new SimpleDateFormat("MMMdd''yy");
                Date date = originalFormat.parse(expiry);
                expiry = targetFormat.format(date);
            }catch (ParseException pe) {
                pe.printStackTrace();
            }
        } else {
            expiry = "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(symbol).append(" ").append(secType).append(" (").append(tradingClass).append(") ")
                .append(expiry).append(" ");
        if(strike != 0.0)
            sb.append(strike).append(" ");
        if(!"None".equals(right))
            sb.append(right).append(" ");
        sb.append("@").append(exchange);

        return sb.toString();

    }

    /** returns the calculated avg cost based on SEC Type **/
    private double calculateAvgCost(Contract con, double averageCost) {
        if("OPT".equals(con.secType().getApiString()))
            return averageCost / 100;
        return averageCost;
    }

    public String getAccount() {
        return account;
    }
}
