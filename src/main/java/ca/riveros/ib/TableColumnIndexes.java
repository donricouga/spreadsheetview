package ca.riveros.ib;

public enum TableColumnIndexes {
    CONTRACT(0),
    QTY(1),
    ENTRYDOL(2),
    MID(3), //CHANGE NUMBERS
    UNREALPNL(4),
    REALPNL(5),
    PEROFPORT(6),
    MARGIN(7), //editable
    PROFITPER(8),  //editable
    LOSSPER(9), //editable
    KCPROBPROFIT(0), //editable
    KCEDGE(1), //editable
    KCCALCTAKELOSSAT(2),
    KCCREDITREC(3), //NEW
    KCTAKEPROFITPER(4), //NEW THIS IS THE OLD KC PROFIT %
    KCTAKEPROFITDOL(5),
    KCNETPROFITDOL(6),
    KCLOSSLEVEL(7),
    KCTAKELOSSDOL(8),
    KCNETLOSSDOL(9),
    KCPERPORT(10),  //editable
    KCMAXLOSS(11),
    KCCONTRACTNUM(12),
    QTYOPENCLOSE(13),
    MARKETDOL(0),
    NOTIONAL(1),
    DELTA(2),
    IMPVOLPER(3),
    PERPL(4),
    BID(5),  //Hide This Column
    ASK(6),  //Hide This Column
    CONTRACTID(7), //Hide This Column
    SYMBOL(8), //Hide this Column
    ACCOUNT(9), //Hide this Column

    //BLOCK TRADING
    ACCOUNTNUM(0),
    NETLIQ(1),
    PERTRADED(2),
    DOLTRADED(3),
    PERSYMBOL(4),
    DOLSYMBOL(5),
    BTMARGIN(6),
    BTCONTRACT(7);

    private int index;

    private TableColumnIndexes(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

