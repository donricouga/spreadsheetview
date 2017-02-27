package ca.riveros.ib.events;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.calc$Symbol;
import static ca.riveros.ib.Common.calc$Traded;
import static ca.riveros.ib.Common.calcContract;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNTNUM;
import static ca.riveros.ib.TableColumnIndexes.BTCONTRACT;
import static ca.riveros.ib.TableColumnIndexes.DOLSYMBOL;
import static ca.riveros.ib.TableColumnIndexes.DOLTRADED;
import static ca.riveros.ib.TableColumnIndexes.NETLIQ;
import static ca.riveros.ib.data.PersistentFields.getMargin;
import static ca.riveros.ib.data.PersistentFields.getPercentSymbol;
import static ca.riveros.ib.data.PersistentFields.getPercentTraded;


/**
 * Created by ricardo on 2/11/17.
 */
public class RowChangeListenerBT implements ListChangeListener<SpreadsheetCell> {

    @Override
    public void onChanged(ListChangeListener.Change<? extends SpreadsheetCell> c) {
        ObservableList<SpreadsheetCell> modifiedList = (ObservableList<SpreadsheetCell>) c.getList();
        String account = (String) modifiedList.get(ACCOUNTNUM.getIndex()).getItem();
        Double netLiq = (Double) modifiedList.get(NETLIQ.getIndex()).getItem();
        Double percentTraded = getPercentTraded(account, 0.27);
        Double dollarTraded = calc$Traded(netLiq, percentTraded);
        Double percentSymbol = getPercentSymbol(account, 0.012);
        Double dollarSymbol = calc$Symbol(dollarTraded, percentSymbol);
        Double margin = getMargin(account, 1600.00);
        Double contract = calcContract(dollarSymbol, margin);

        Platform.runLater(() -> {
            updateCellValue(modifiedList.get(DOLTRADED.getIndex()), dollarTraded);
            updateCellValue(modifiedList.get(DOLSYMBOL.getIndex()), dollarSymbol);
            updateCellValue(modifiedList.get(BTCONTRACT.getIndex()), contract);
        });

    }

}
