package ca.riveros.ib.model;

import com.ib.client.Contract;
import javafx.beans.property.*;

public class SpreadsheetModel {

    private String contract;
    private Double qty;
    private Double kcContractNum;
    private Double qtyOpenClose;
    private Double entry$;
    private Double mid;
    private Double market$;
    private Double unrealPL;
    private Double realPL;
    private Double percentOfPort;
    private Double percentPL;
    private Double margin;
    private Double probOfProfit;
    private Double kcPercentagePort;
    private Double profitPercentage;
    private Double lossPercentage;
    private Double kcEdge;
    private Double kcProfitPercentage;
    private Double kcLossLevel;
    private Double kcTakeProfit$;
    private Double kcTakeLoss$;
    private Double kcNetProfit$;
    private Double kcNetLoss$;
    private Double kcMaxLoss;
    private Double notional;
    private Double delta;
    private Double impVolPercentage;
    private Double bid;
    private Double ask;
    private Integer contractId;
    private String symbol;
    private String account;

    private Contract twsContract;

    public String getContract() {
        return contract;
    }

    public String contractProperty() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Double getQty() {
        return qty;
    }

    public Double qtyProperty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getKcContractNum() {
        return kcContractNum;
    }

    public Double kcContractNumProperty() {
        return kcContractNum;
    }

    public void setKcContractNum(Double kcContractNum) {
        this.kcContractNum = kcContractNum;
    }

    public Double getQtyOpenClose() {
        return qtyOpenClose;
    }

    public Double qtyOpenCloseProperty() {
        return qtyOpenClose;
    }

    public void setQtyOpenClose(Double qtyOpenClose) {
        this.qtyOpenClose = qtyOpenClose;
    }

    public Double getEntry$() {
        return entry$;
    }

    public Double entry$Property() {
        return entry$;
    }

    public void setEntry$(Double entry$) {
        this.entry$ = entry$;
    }

    public Double getMid() {
        return mid;
    }

    public Double midProperty() {
        return mid;
    }

    public void setMid(Double mid) {
        this.mid = mid;
    }

    public Double getMarket$() {
        return market$;
    }

    public Double market$Property() {
        return market$;
    }

    public void setMarket$(Double market$) {
        this.market$ = market$;
    }

    public Double getUnrealPL() {
        return unrealPL;
    }

    public Double unrealPLProperty() {
        return unrealPL;
    }

    public void setUnrealPL(Double unrealPL) {
        this.unrealPL = unrealPL;
    }

    public Double getRealPL() {
        return realPL;
    }

    public Double realPLProperty() {
        return realPL;
    }

    public void setRealPL(Double realPL) {
        this.realPL = realPL;
    }

    public Double getPercentOfPort() {
        return percentOfPort;
    }

    public Double percentOfPortProperty() {
        return percentOfPort;
    }

    public void setPercentOfPort(Double percentOfPort) {
        this.percentOfPort = percentOfPort;
    }

    public Double getPercentPL() {
        return percentPL;
    }

    public Double percentPLProperty() {
        return percentPL;
    }

    public void setPercentPL(Double percentPL) {
        this.percentPL = percentPL;
    }

    public Double getMargin() {
        return margin;
    }

    public Double marginProperty() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public Double getProbOfProfit() {
        return probOfProfit;
    }

    public Double probOfProfitProperty() {
        return probOfProfit;
    }

    public void setProbOfProfit(Double probOfProfit) {
        this.probOfProfit = probOfProfit;
    }

    public Double getKcPercentagePort() {
        return kcPercentagePort;
    }

    public Double kcPercentagePortProperty() {
        return kcPercentagePort;
    }

    public void setKcPercentagePort(Double kcPercentagePort) {
        this.kcPercentagePort = kcPercentagePort;
    }

    public Double getProfitPercentage() {
        return profitPercentage;
    }

    public Double profitPercentageProperty() {
        return profitPercentage;
    }

    public void setProfitPercentage(Double profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public Double getLossPercentage() {
        return lossPercentage;
    }

    public Double lossPercentageProperty() {
        return lossPercentage;
    }

    public void setLossPercentage(Double lossPercentage) {
        this.lossPercentage = lossPercentage;
    }

    public Double getKcEdge() {
        return kcEdge;
    }

    public Double kcEdgeProperty() {
        return kcEdge;
    }

    public void setKcEdge(Double kcEdge) {
        this.kcEdge = kcEdge;
    }

    public Double getKcProfitPercentage() {
        return kcProfitPercentage;
    }

    public Double kcProfitPercentageProperty() {
        return kcProfitPercentage;
    }

    public void setKcProfitPercentage(Double kcProfitPercentage) {
        this.kcProfitPercentage = kcProfitPercentage;
    }

    public Double getKcLossLevel() {
        return kcLossLevel;
    }

    public Double kcLossLevelProperty() {
        return kcLossLevel;
    }

    public void setKcLossLevel(Double kcLossLevel) {
        this.kcLossLevel = kcLossLevel;
    }

    public Double getKcTakeProfit$() {
        return kcTakeProfit$;
    }

    public Double kcTakeProfit$Property() {
        return kcTakeProfit$;
    }

    public void setKcTakeProfit$(Double kcTakeProfit$) {
        this.kcTakeProfit$ = kcTakeProfit$;
    }

    public Double getKcTakeLoss$() {
        return kcTakeLoss$;
    }

    public Double kcTakeLoss$Property() {
        return kcTakeLoss$;
    }

    public void setKcTakeLoss$(Double kcTakeLoss$) {
        this.kcTakeLoss$ = kcTakeLoss$;
    }

    public Double getKcNetProfit$() {
        return kcNetProfit$;
    }

    public Double kcNetProfit$Property() {
        return kcNetProfit$;
    }

    public void setKcNetProfit$(Double kcNetProfit$) {
        this.kcNetProfit$ = kcNetProfit$;
    }

    public Double getKcNetLoss$() {
        return kcNetLoss$;
    }

    public Double kcNetLoss$Property() {
        return kcNetLoss$;
    }

    public void setKcNetLoss$(Double kcNetLoss$) {
        this.kcNetLoss$ = kcNetLoss$;
    }

    public Double getKcMaxLoss() {
        return kcMaxLoss;
    }

    public Double kcMaxLossProperty() {
        return kcMaxLoss;
    }

    public void setKcMaxLoss(Double kcMaxLoss) {
        this.kcMaxLoss = kcMaxLoss;
    }

    public Double getNotional() {
        return notional;
    }

    public Double notionalProperty() {
        return notional;
    }

    public void setNotional(Double notional) {
        this.notional = notional;
    }

    public Double getDelta() {
        return delta;
    }

    public Double deltaProperty() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public Double getImpVolPercentage() {
        return impVolPercentage;
    }

    public Double impVolPercentageProperty() {
        return impVolPercentage;
    }

    public void setImpVolPercentage(Double impVolPercentage) {
        this.impVolPercentage = impVolPercentage;
    }

    public Double getBid() {
        return bid;
    }

    public Double bidProperty() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public Double askProperty() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public int getContractId() {
        return contractId;
    }

    public Integer contractIdProperty() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String symbolProperty() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Contract getTwsContract() {
        return twsContract;
    }

    public void setTwsContract(Contract twsContract) {
        this.twsContract = twsContract;
    }

    public String getAccount() {
        return account;
    }

    public String accountProperty() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "SpreadsheetModel{" +
                "contract='" + contract + '\'' +
                ", qty=" + qty +
                ", kcContractNum=" + kcContractNum +
                ", qtyOpenClose=" + qtyOpenClose +
                ", entry$=" + entry$ +
                ", mid=" + mid +
                ", market$=" + market$ +
                ", unrealPL=" + unrealPL +
                ", realPL=" + realPL +
                ", percentOfPort=" + percentOfPort +
                ", percentPL=" + percentPL +
                ", margin=" + margin +
                ", probOfProfit=" + probOfProfit +
                ", kcPercentagePort=" + kcPercentagePort +
                ", profitPercentage=" + profitPercentage +
                ", lossPercentage=" + lossPercentage +
                ", kcEdge=" + kcEdge +
                ", kcProfitPercentage=" + kcProfitPercentage +
                ", kcLossLevel=" + kcLossLevel +
                ", kcTakeProfit$=" + kcTakeProfit$ +
                ", kcTakeLoss$=" + kcTakeLoss$ +
                ", kcNetProfit$=" + kcNetProfit$ +
                ", kcNetLoss$=" + kcNetLoss$ +
                ", kcMaxLoss=" + kcMaxLoss +
                ", notional=" + notional +
                ", delta=" + delta +
                ", impVolPercentage=" + impVolPercentage +
                ", bid=" + bid +
                ", ask=" + ask +
                ", contractId=" + contractId +
                ", symbol='" + symbol + '\'' +
                ", account='" + account + '\'' +
                ", twsContract=" + twsContract +
                '}';
    }
}
