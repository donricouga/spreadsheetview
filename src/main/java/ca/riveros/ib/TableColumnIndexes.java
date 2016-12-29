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
    KCPROBPROFIT(10), //editable
    KCEDGE(11), //editable
    KCCREDITREC(12), //NEW
    KCTAKEPROFITPER(13), //NEW THIS IS THE OLD KC PROFIT %
    KCTAKEPROFITDOL(14),
    KCNETPROFITDOL(15),
    KCLOSSPER(16),
    KCTAKELOSSDOL(17),
    KCNETLOSSDOL(18),
    KCPERPORT(19),  //editable
    KCMAXLOSS(20),
    KCCONTRACTNUM(21),
    QTYOPENCLOSE(22),
    MARKETDOL(23),
    NOTIONAL(24),
    DELTA(25),
    IMPVOLPER(26),
    PERPL(27),
    BID(28),  //Hide This Column
    ASK(29),  //Hide This Column
    CONTRACTID(30), //Hide This Column
    SYMBOL(31), //Hide this Column
    ACCOUNT(32); //Hide this Column

    private int index;

    private TableColumnIndexes(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

