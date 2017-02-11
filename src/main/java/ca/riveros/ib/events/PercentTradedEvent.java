package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import ca.riveros.ib.data.PersistentFields;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

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
import static ca.riveros.ib.TableColumnIndexes.NETLIQ;
import static ca.riveros.ib.TableColumnIndexes.PERSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

public class PercentTradedEvent implements ChangeListener<Object> {

    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList; //BLOCK TRADING VIEW
    private ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList2;

    public PercentTradedEvent(ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList,
                              ObservableList<ObservableList<SpreadsheetCell>> spreadsheetDataList2) {
        this.spreadsheetDataList = spreadsheetDataList;
        this.spreadsheetDataList2 = spreadsheetDataList2;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        ObjectProperty base = (ObjectProperty) observable;
        SpreadsheetCell c = (SpreadsheetCell) base.getBean();
        int row = c.getRow();
        ObservableList<SpreadsheetCell> rowList = spreadsheetDataList.get(row);

        //Get Needed Values
        Double percentTraded = (Double) newValue;
        Double netLiq = (Double) rowList.get(NETLIQ.getIndex()).getItem();
        Double percentSymbol = (Double) rowList.get(PERSYMBOL.getIndex()).getItem();
        String account = (String) rowList.get(ACCOUNTNUM.getIndex()).getItem();
        Double margin = (Double)  rowList.get(BTMARGIN.getIndex()).getItem();

        //Now Calculate
        Double dolTraded = calc$Traded(netLiq, percentTraded);
        Double dolSymbol = calc$Symbol(dolTraded, percentSymbol);
        Double contract = calcContract(dolSymbol, margin);

        //Update Spreadsheet
        updateCellValue(rowList.get(DOLTRADED.getIndex()), dolTraded);
        updateCellValue(rowList.get(DOLSYMBOL.getIndex()), dolSymbol);
        updateCellValue(rowList.get(BTCONTRACT.getIndex()), contract);

        //Update Other spreadsheet, if same account selected
        if(account.equals(Mediator.INSTANCE.getSelectedAccount())) {
            spreadsheetDataList2.forEach(r -> {
                //Update KC Max Loss
                Double kcPerPort = (Double) r.get(KCPERPORT.getIndex()).getItem();
                Double kcMaxLoss = calcKcMaxLoss(netLiq, percentTraded, kcPerPort);
                updateCellValue(r.get(KCMAXLOSS.getIndex()), kcMaxLoss);

                //Update KC-Qty
                Double kcEdge = (Double) r.get(KCEDGE.getIndex()).getItem();
                Double entry$ = (Double) r.get(ENTRYDOL.getIndex()).getItem();
                Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * -100);
                updateCellValue(r.get(KCCONTRACTNUM.getIndex()), kcQty);

                //Update Qty. Open/Close
                Double qty = (Double) r.get(QTY.getIndex()).getItem();
                updateCellValue(r.get(QTYOPENCLOSE.getIndex()), kcQty - qty);
            });
        }


        //Persist Manual changed field
        PersistentFields.setPercentTraded(account, percentTraded);
    }
}