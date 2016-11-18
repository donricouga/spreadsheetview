package ca.riveros.ib.handlers;

import ca.riveros.ib.Mediator;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;

/**
 * Created by admin on 11/17/16.
 */
public class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

    private Mediator mediator;
    private Logger inLogger;

    private Double totalInitMarginReq = 0.0;
    private Double totalNetLiq = 0.0;


    public AccountSummaryHandler(Mediator mediator, Logger inLogger) {
        this.mediator = mediator;
        this.inLogger = inLogger;
    }

    @Override
    public void accountSummary(String account, AccountSummaryTag tag, String value, String currency) {

        System.out.println("TAG = " + tag.name());

        if ("InitMarginReq".equals(tag.name())) {
            inLogger.log("INIT MARGIN REQ " + value + " FOR ACCOUNT " + account);
            totalInitMarginReq = totalInitMarginReq + Double.valueOf(value);
        }
        if ("NetLiquidation".equals(tag.name())) {
            inLogger.log("NET LIQ " + value + " FOR ACCOUNT " + account);
            totalNetLiq = totalNetLiq + Double.valueOf(value);

        }

    }

    @Override
    public void accountSummaryEnd() {
        mediator.updateTotalInitMargin(totalInitMarginReq.toString());
        mediator.updateTotalNetLiq(totalNetLiq.toString());
        totalInitMarginReq = 0.0;
        totalNetLiq = 0.0;
    }
}
