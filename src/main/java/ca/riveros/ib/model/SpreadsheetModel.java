package ca.riveros.ib.model;

import com.ib.client.Contract;
import javafx.beans.property.*;

public class SpreadsheetModel {

    private StringProperty contract = new SimpleStringProperty();
    private DoubleProperty qty = new SimpleDoubleProperty();
    private DoubleProperty kcQty = new SimpleDoubleProperty();
    private DoubleProperty qtyOpenClose = new SimpleDoubleProperty();
    private DoubleProperty entry$ = new SimpleDoubleProperty();
    private DoubleProperty mid = new SimpleDoubleProperty();
    private DoubleProperty market$ = new SimpleDoubleProperty();
    private DoubleProperty unrealPL = new SimpleDoubleProperty();
    private DoubleProperty realPL = new SimpleDoubleProperty();
    private DoubleProperty percentOfPort = new SimpleDoubleProperty();
    private DoubleProperty percentPL = new SimpleDoubleProperty();
    private DoubleProperty margin = new SimpleDoubleProperty();
    private DoubleProperty probOfProfit = new SimpleDoubleProperty();
    private DoubleProperty kcPercentagePort = new SimpleDoubleProperty();
    private DoubleProperty profitPercentage = new SimpleDoubleProperty();
    private DoubleProperty lossPercentage = new SimpleDoubleProperty();
    private DoubleProperty kcEdge = new SimpleDoubleProperty();
    private DoubleProperty kcProfitPercentage = new SimpleDoubleProperty();
    private DoubleProperty kcLossPercentage = new SimpleDoubleProperty();
    private DoubleProperty kcTakeProfit$ = new SimpleDoubleProperty();
    private DoubleProperty kcTakeLoss$ = new SimpleDoubleProperty();
    private DoubleProperty kcNetProfit$ = new SimpleDoubleProperty();
    private DoubleProperty kcNetLoss$ = new SimpleDoubleProperty();
    private DoubleProperty kcMaxLoss = new SimpleDoubleProperty();
    private DoubleProperty notional = new SimpleDoubleProperty();
    private DoubleProperty delta = new SimpleDoubleProperty();
    private DoubleProperty impVolPercentage = new SimpleDoubleProperty();
    private DoubleProperty bid = new SimpleDoubleProperty();
    private DoubleProperty ask = new SimpleDoubleProperty();
    private IntegerProperty contractId = new SimpleIntegerProperty();
    private StringProperty symbol = new SimpleStringProperty();

    private Contract twsContract;

    public String getContract() {
        return contract.get();
    }

    public String contractProperty() {
        return contract.get();
    }

    public void setContract(String contract) {
        this.contract.set(contract);
    }

    public Double getQty() {
        return qty.get();
    }

    public Double qtyProperty() {
        return qty.getValue();
    }

    public void setQty(Double qty) {
        this.qty.set(qty);
    }

    public Double getKcQty() {
        return kcQty.get();
    }

    public Double kcQtyProperty() {
        return kcQty.getValue();
    }

    public void setKcQty(Double kcQty) {
        this.kcQty.set(kcQty);
    }

    public Double getQtyOpenClose() {
        return qtyOpenClose.get();
    }

    public Double qtyOpenCloseProperty() {
        return qtyOpenClose.getValue();
    }

    public void setQtyOpenClose(Double qtyOpenClose) {
        this.qtyOpenClose.set(qtyOpenClose);
    }

    public Double getEntry$() {
        return entry$.get();
    }

    public Double entry$Property() {
        return entry$.getValue();
    }

    public void setEntry$(Double entry$) {
        this.entry$.set(entry$);
    }

    public Double getMid() {
        return mid.get();
    }

    public Double midProperty() {
        return mid.get();
    }

    public void setMid(Double mid) {
        this.mid.set(mid);
    }

    public Double getMarket$() {
        return market$.get();
    }

    public Double market$Property() {
        return market$.get();
    }

    public void setMarket$(Double market$) {
        this.market$.set(market$);
    }

    public Double getUnrealPL() {
        return unrealPL.get();
    }

    public Double unrealPLProperty() {
        return unrealPL.get();
    }

    public void setUnrealPL(Double unrealPL) {
        this.unrealPL.set(unrealPL);
    }

    public Double getRealPL() {
        return realPL.get();
    }

    public Double realPLProperty() {
        return realPL.get();
    }

    public void setRealPL(Double realPL) {
        this.realPL.set(realPL);
    }

    public Double getPercentOfPort() {
        return percentOfPort.get();
    }

    public Double percentOfPortProperty() {
        return percentOfPort.get();
    }

    public void setPercentOfPort(Double percentOfPort) {
        this.percentOfPort.set(percentOfPort);
    }

    public Double getPercentPL() {
        return percentPL.get();
    }

    public Double percentPLProperty() {
        return percentPL.get();
    }

    public void setPercentPL(Double percentPL) {
        this.percentPL.set(percentPL);
    }

    public Double getMargin() {
        return margin.get();
    }

    public Double marginProperty() {
        return margin.get();
    }

    public void setMargin(Double margin) {
        this.margin.set(margin);
    }

    public Double getProbOfProfit() {
        return probOfProfit.get();
    }

    public Double probOfProfitProperty() {
        return probOfProfit.get();
    }

    public void setProbOfProfit(Double probOfProfit) {
        this.probOfProfit.set(probOfProfit);
    }

    public Double getKcPercentagePort() {
        return kcPercentagePort.get();
    }

    public Double kcPercentagePortProperty() {
        return kcPercentagePort.get();
    }

    public void setKcPercentagePort(Double kcPercentagePort) {
        this.kcPercentagePort.set(kcPercentagePort);
    }

    public Double getProfitPercentage() {
        return profitPercentage.get();
    }

    public Double profitPercentageProperty() {
        return profitPercentage.get();
    }

    public void setProfitPercentage(Double profitPercentage) {
        this.profitPercentage.set(profitPercentage);
    }

    public Double getLossPercentage() {
        return lossPercentage.get();
    }

    public Double lossPercentageProperty() {
        return lossPercentage.get();
    }

    public void setLossPercentage(Double lossPercentage) {
        this.lossPercentage.set(lossPercentage);
    }

    public Double getKcEdge() {
        return kcEdge.get();
    }

    public Double kcEdgeProperty() {
        return kcEdge.get();
    }

    public void setKcEdge(Double kcEdge) {
        this.kcEdge.set(kcEdge);
    }

    public Double getKcProfitPercentage() {
        return kcProfitPercentage.get();
    }

    public Double kcProfitPercentageProperty() {
        return kcProfitPercentage.get();
    }

    public void setKcProfitPercentage(Double kcProfitPercentage) {
        this.kcProfitPercentage.set(kcProfitPercentage);
    }

    public Double getKcLossPercentage() {
        return kcLossPercentage.get();
    }

    public Double kcLossPercentageProperty() {
        return kcLossPercentage.get();
    }

    public void setKcLossPercentage(Double kcLossPercentage) {
        this.kcLossPercentage.set(kcLossPercentage);
    }

    public Double getKcTakeProfit$() {
        return kcTakeProfit$.get();
    }

    public Double kcTakeProfit$Property() {
        return kcTakeProfit$.get();
    }

    public void setKcTakeProfit$(Double kcTakeProfit$) {
        this.kcTakeProfit$.set(kcTakeProfit$);
    }

    public Double getKcTakeLoss$() {
        return kcTakeLoss$.get();
    }

    public Double kcTakeLoss$Property() {
        return kcTakeLoss$.get();
    }

    public void setKcTakeLoss$(Double kcTakeLoss$) {
        this.kcTakeLoss$.set(kcTakeLoss$);
    }

    public Double getKcNetProfit$() {
        return kcNetProfit$.get();
    }

    public Double kcNetProfit$Property() {
        return kcNetProfit$.get();
    }

    public void setKcNetProfit$(Double kcNetProfit$) {
        this.kcNetProfit$.set(kcNetProfit$);
    }

    public Double getKcNetLoss$() {
        return kcNetLoss$.get();
    }

    public Double kcNetLoss$Property() {
        return kcNetLoss$.get();
    }

    public void setKcNetLoss$(Double kcNetLoss$) {
        this.kcNetLoss$.set(kcNetLoss$);
    }

    public Double getKcMaxLoss() {
        return kcMaxLoss.get();
    }

    public Double kcMaxLossProperty() {
        return kcMaxLoss.get();
    }

    public void setKcMaxLoss(Double kcMaxLoss) {
        this.kcMaxLoss.set(kcMaxLoss);
    }

    public Double getNotional() {
        return notional.get();
    }

    public Double notionalProperty() {
        return notional.get();
    }

    public void setNotional(Double notional) {
        this.notional.set(notional);
    }

    public Double getDelta() {
        return delta.get();
    }

    public Double deltaProperty() {
        return delta.get();
    }

    public void setDelta(Double delta) {
        this.delta.set(delta);
    }

    public Double getImpVolPercentage() {
        return impVolPercentage.get();
    }

    public Double impVolPercentageProperty() {
        return impVolPercentage.get();
    }

    public void setImpVolPercentage(Double impVolPercentage) {
        this.impVolPercentage.set(impVolPercentage);
    }

    public Double getBid() {
        return bid.get();
    }

    public Double bidProperty() {
        return bid.get();
    }

    public void setBid(Double bid) {
        this.bid.set(bid);
    }

    public Double getAsk() {
        return ask.get();
    }

    public Double askProperty() {
        return ask.get();
    }

    public void setAsk(Double ask) {
        this.ask.set(ask);
    }

    public int getContractId() {
        return contractId.get();
    }

    public Integer contractIdProperty() {
        return contractId.get();
    }

    public void setContractId(int contractId) {
        this.contractId.set(contractId);
    }

    public String getSymbol() {
        return symbol.get();
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol.set(symbol);
    }

    public Contract getTwsContract() {
        return twsContract;
    }

    public void setTwsContract(Contract twsContract) {
        this.twsContract = twsContract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpreadsheetModel that = (SpreadsheetModel) o;

        if (contract != null ? !contract.equals(that.contract) : that.contract != null) return false;
        if (qty != null ? !qty.equals(that.qty) : that.qty != null) return false;
        if (kcQty != null ? !kcQty.equals(that.kcQty) : that.kcQty != null) return false;
        if (qtyOpenClose != null ? !qtyOpenClose.equals(that.qtyOpenClose) : that.qtyOpenClose != null) return false;
        if (entry$ != null ? !entry$.equals(that.entry$) : that.entry$ != null) return false;
        if (mid != null ? !mid.equals(that.mid) : that.mid != null) return false;
        if (market$ != null ? !market$.equals(that.market$) : that.market$ != null) return false;
        if (unrealPL != null ? !unrealPL.equals(that.unrealPL) : that.unrealPL != null) return false;
        if (realPL != null ? !realPL.equals(that.realPL) : that.realPL != null) return false;
        if (percentOfPort != null ? !percentOfPort.equals(that.percentOfPort) : that.percentOfPort != null)
            return false;
        if (percentPL != null ? !percentPL.equals(that.percentPL) : that.percentPL != null) return false;
        if (margin != null ? !margin.equals(that.margin) : that.margin != null) return false;
        if (probOfProfit != null ? !probOfProfit.equals(that.probOfProfit) : that.probOfProfit != null) return false;
        if (kcPercentagePort != null ? !kcPercentagePort.equals(that.kcPercentagePort) : that.kcPercentagePort != null)
            return false;
        if (profitPercentage != null ? !profitPercentage.equals(that.profitPercentage) : that.profitPercentage != null)
            return false;
        if (lossPercentage != null ? !lossPercentage.equals(that.lossPercentage) : that.lossPercentage != null)
            return false;
        if (kcEdge != null ? !kcEdge.equals(that.kcEdge) : that.kcEdge != null) return false;
        if (kcProfitPercentage != null ? !kcProfitPercentage.equals(that.kcProfitPercentage) : that.kcProfitPercentage != null)
            return false;
        if (kcLossPercentage != null ? !kcLossPercentage.equals(that.kcLossPercentage) : that.kcLossPercentage != null)
            return false;
        if (kcTakeProfit$ != null ? !kcTakeProfit$.equals(that.kcTakeProfit$) : that.kcTakeProfit$ != null)
            return false;
        if (kcTakeLoss$ != null ? !kcTakeLoss$.equals(that.kcTakeLoss$) : that.kcTakeLoss$ != null) return false;
        if (kcNetProfit$ != null ? !kcNetProfit$.equals(that.kcNetProfit$) : that.kcNetProfit$ != null) return false;
        if (kcNetLoss$ != null ? !kcNetLoss$.equals(that.kcNetLoss$) : that.kcNetLoss$ != null) return false;
        if (kcMaxLoss != null ? !kcMaxLoss.equals(that.kcMaxLoss) : that.kcMaxLoss != null) return false;
        if (notional != null ? !notional.equals(that.notional) : that.notional != null) return false;
        if (delta != null ? !delta.equals(that.delta) : that.delta != null) return false;
        if (impVolPercentage != null ? !impVolPercentage.equals(that.impVolPercentage) : that.impVolPercentage != null)
            return false;
        if (bid != null ? !bid.equals(that.bid) : that.bid != null) return false;
        if (ask != null ? !ask.equals(that.ask) : that.ask != null) return false;
        if (contractId != null ? !contractId.equals(that.contractId) : that.contractId != null) return false;
        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) return false;
        return twsContract != null ? twsContract.equals(that.twsContract) : that.twsContract == null;

    }

    @Override
    public int hashCode() {
        int result = contract != null ? contract.hashCode() : 0;
        result = 31 * result + (qty != null ? qty.hashCode() : 0);
        result = 31 * result + (kcQty != null ? kcQty.hashCode() : 0);
        result = 31 * result + (qtyOpenClose != null ? qtyOpenClose.hashCode() : 0);
        result = 31 * result + (entry$ != null ? entry$.hashCode() : 0);
        result = 31 * result + (mid != null ? mid.hashCode() : 0);
        result = 31 * result + (market$ != null ? market$.hashCode() : 0);
        result = 31 * result + (unrealPL != null ? unrealPL.hashCode() : 0);
        result = 31 * result + (realPL != null ? realPL.hashCode() : 0);
        result = 31 * result + (percentOfPort != null ? percentOfPort.hashCode() : 0);
        result = 31 * result + (percentPL != null ? percentPL.hashCode() : 0);
        result = 31 * result + (margin != null ? margin.hashCode() : 0);
        result = 31 * result + (probOfProfit != null ? probOfProfit.hashCode() : 0);
        result = 31 * result + (kcPercentagePort != null ? kcPercentagePort.hashCode() : 0);
        result = 31 * result + (profitPercentage != null ? profitPercentage.hashCode() : 0);
        result = 31 * result + (lossPercentage != null ? lossPercentage.hashCode() : 0);
        result = 31 * result + (kcEdge != null ? kcEdge.hashCode() : 0);
        result = 31 * result + (kcProfitPercentage != null ? kcProfitPercentage.hashCode() : 0);
        result = 31 * result + (kcLossPercentage != null ? kcLossPercentage.hashCode() : 0);
        result = 31 * result + (kcTakeProfit$ != null ? kcTakeProfit$.hashCode() : 0);
        result = 31 * result + (kcTakeLoss$ != null ? kcTakeLoss$.hashCode() : 0);
        result = 31 * result + (kcNetProfit$ != null ? kcNetProfit$.hashCode() : 0);
        result = 31 * result + (kcNetLoss$ != null ? kcNetLoss$.hashCode() : 0);
        result = 31 * result + (kcMaxLoss != null ? kcMaxLoss.hashCode() : 0);
        result = 31 * result + (notional != null ? notional.hashCode() : 0);
        result = 31 * result + (delta != null ? delta.hashCode() : 0);
        result = 31 * result + (impVolPercentage != null ? impVolPercentage.hashCode() : 0);
        result = 31 * result + (bid != null ? bid.hashCode() : 0);
        result = 31 * result + (ask != null ? ask.hashCode() : 0);
        result = 31 * result + (contractId != null ? contractId.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (twsContract != null ? twsContract.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SpreadsheetModel{" +
                "contract=" + contract +
                ", qty=" + qty +
                ", kcQty=" + kcQty +
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
                ", kcLossPercentage=" + kcLossPercentage +
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
                ", symbol=" + symbol +
                '}';
    }
}
