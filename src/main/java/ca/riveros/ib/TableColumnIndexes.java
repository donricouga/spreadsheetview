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
    KCCALCTAKELOSSAT(12),
    KCCREDITREC(13), //NEW
    KCTAKEPROFITPER(14), //NEW THIS IS THE OLD KC PROFIT %
    KCTAKEPROFITDOL(15),
    KCNETPROFITDOL(16),
    KCLOSSPER(17),
    KCTAKELOSSDOL(18),
    KCNETLOSSDOL(19),
    KCPERPORT(20),  //editable
    KCMAXLOSS(21),
    KCCONTRACTNUM(22),
    QTYOPENCLOSE(23),
    MARKETDOL(24),
    NOTIONAL(25),
    DELTA(26),
    IMPVOLPER(27),
    PERPL(28),
    BID(29),  //Hide This Column
    ASK(30),  //Hide This Column
    CONTRACTID(31), //Hide This Column
    SYMBOL(32), //Hide this Column
    ACCOUNT(33); //Hide this Column

    private int index;

    private TableColumnIndexes(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

