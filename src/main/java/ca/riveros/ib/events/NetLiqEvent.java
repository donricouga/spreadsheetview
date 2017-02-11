package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.calc$Symbol;
import static ca.riveros.ib.Common.calc$Traded;
import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.calcKcMaxLoss;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.BTMARGIN;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.DOLTRADED;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.KCCONTRACTNUM;
import static ca.riveros.ib.TableColumnIndexes.KCEDGE;
import static ca.riveros.ib.TableColumnIndexes.KCMAXLOSS;
import static ca.riveros.ib.TableColumnIndexes.KCPERPORT;
import static ca.riveros.ib.TableColumnIndexes.MARGIN;
import static ca.riveros.ib.TableColumnIndexes.PEROFPORT;
import static ca.riveros.ib.TableColumnIndexes.PERSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.PERTRADED;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

public class NetLiqEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> blockTradingDataList;
    private SpreadsheetView view;
    private SpreadsheetView view2;



    public NetLiqEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList,
                       SpreadsheetView view, SpreadsheetView view2) {
        this.blockTradingDataList = spreadsheetDataList;
        this.view = view;
        this.view2 = view2;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = blockTradingDataList.get(row);

        //Get Needed Values
        Double percentTraded = (Double) rowList.get(PERTRADED.getIndex()).getItem();
        Double netLiq = (Double) newValue;
        Double percentSymbol = (Double) rowList.get(PERSYMBOL.getIndex()).getItem();
        Double margin = (Double)  rowList.get(BTMARGIN.getIndex()).getItem();
        String accountNum = (String) rowList.get(ACCOUNTNUM.getIndex()).getItem();

        //Now Calculate
        Double dolTraded = calc$Traded(netLiq, percentTraded);
        Double dolSymbol = calc$Symbol(dolTraded, percentSymbol);
        Double contract = calcContract(dolSymbol, margin);

        //Update Spreadsheet
        updateCellValue(rowList.get(DOLTRADED.getIndex()), dolTraded);
        updateCellValue(rowList.get(DOLSYMBOL.getIndex()), dolSymbol);
        updateCellValue(rowList.get(BTCONTRACT.getIndex()), contract);

        //Also update rows based on whether the currently selected account is being displayed
        if(accountNum.equals(Mediator.INSTANCE.getSelectedAccount())) {
            ObservableList<ObservableList<SpreadsheetCell>> list = view.getGrid().getRows();
            ObservableList<ObservableList<SpreadsheetCell>> list2 = view2.getGrid().getRows();
            AtomicInteger counter = new AtomicInteger(0);
            list.forEach(ssRow -> {

                Double accountNetLiq = Double.valueOf(netLiq);

                Platform.runLater(() -> {

                    //Update Percent Of Port
                    Double marg = (Double) ssRow.get(MARGIN.getIndex()).getItem();
                    updateCellValue(ssRow.get(PEROFPORT.getIndex()), marg / accountNetLiq);

                    //Update KC Max Loss
                    Double kcPerPort = (Double) list2.get(counter.get()).get(KCPERPORT.getIndex()).getItem();
                    Double kcMaxLoss = calcKcMaxLoss(accountNetLiq, Mediator.INSTANCE.getPercentCapitalToTradeByAccountNumber(accountNum), kcPerPort);
                    updateCellValue(list2.get(counter.get()).get(KCMAXLOSS.getIndex()), kcMaxLoss);

                    //Update KC-Qty
                    Double kcEdge = (Double) list2.get(counter.get()).get(KCEDGE.getIndex()).getItem();
                    Double entry$ = (Double) ssRow.get(ENTRYDOL.getIndex()).getItem();
                    Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * -100);
                    updateCellValue(list2.get(counter.get()).get(KCCONTRACTNUM.getIndex()), kcQty);

                    //Update Qty. Open/Close
                    Double qty = (Double) ssRow.get(QTY.getIndex()).getItem();
                    updateCellValue(list2.get(counter.get()).get(QTYOPENCLOSE.getIndex()), kcQty - qty);

                    counter.incrementAndGet();
                });
            });
        }

    }
}