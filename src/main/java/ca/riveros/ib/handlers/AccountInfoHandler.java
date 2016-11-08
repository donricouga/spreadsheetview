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

/**
 * Created by admin on 11/7/16.
 */
public class AccountInfoHandler implements ApiController.IAccountHandler {

    //Mediator
    private Mediator mediator;

    //Logger
    private Logger inLogger;

    private List <SpreadsheetModel>positionsList = new ArrayList<>(30);

    public AccountInfoHandler(Mediator mediator, Logger inLogger) {
        this.inLogger = inLogger;
        this.mediator = mediator;
    }

    @Override
    public void accountValue(String account, String key, String value, String currency) {
        inLogger.log("account : " + account + " key : " + key + " value : " + value + " currency : " + currency);
        if("NetLiquidation".equals(key)) {
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
    }

    @Override
    public void updatePortfolio(Position position) {
        SpreadsheetModel model = createSpreadsheetModel(position);
        positionsList.add(model);
    }

    private SpreadsheetModel createSpreadsheetModel(Position pos) {
        SpreadsheetModel model = new SpreadsheetModel();
        model.setContract(generateContractName(pos.contract()));
        model.setContractId(pos.conid());
        model.setEntry$(calculateAvgCost(pos.contract(),pos.averageCost()));
        model.setMarket$(pos.marketPrice());
        model.setNotional(pos.marketValue());
        model.setRealPL(pos.realPnl());
        model.setUnrealPL(pos.unrealPnl());

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
}
