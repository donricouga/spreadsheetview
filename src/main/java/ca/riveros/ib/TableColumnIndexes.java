package ca.riveros.ib;

public enum TableColumnIndexes {
    CONTRACT(0),
    QTY(1),
    KCQTY(2),
    QTYOPENCLOSE(3),
    ENTRYDOL(4),
    MID(5), //CHANGE NUMBERS
    MARKETDOL(6),
    UNREALPNL(7),
    REALPNL(8),
    PEROFPORT(9),
    PERPL(10),
    MARGIN(11), //editable
    PROBPROFIT(12), //editable
    KCPERPORT(13),  //editable
    PROFITPER(14),  //editable
    LOSSPER(15), //editable
    KCEDGE(16), //editable
    KCPROFITPER(17),
    KCLOSSPER(18),
    KCTAKEPROFITDOL(19),
    KCTAKELOSSDOL(20),
    KCNETPROFITDOL(21),
    KCNETLOSSDOL(22),
    KCMAXLOSS(23),
    NOTIONAL(24),
    DELTA(25),
    IMPVOLPER(26),
    BID(27),  //Hide This Column
    ASK(28),  //Hide This Column
    CONTRACTID(29), //Hide This Column
    SYMBOL(30), //Hide this Column
    ACCOUNT(31); //Hide this Column

    private int index;

    private TableColumnIndexes(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

