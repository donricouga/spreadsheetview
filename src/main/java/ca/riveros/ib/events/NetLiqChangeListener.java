package ca.riveros.ib.events;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Created by admin on 12/1/16.
 */
public class NetLiqChangeListener implements ChangeListener<String>{

    private SpreadsheetView view;

    public NetLiqChangeListener(SpreadsheetView view) {
        this.view = view;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        ObservableList<ObservableList<SpreadsheetCell>> list = view.getGrid().getRows();
        list.forEach(row -> {

            Double accountNetLiq = Double.valueOf(newValue);

            //Update Percent Of Port
            Double margin = (Double) row.get(MARGIN.getIndex()).getItem();
            updateCellValue(row.get(PEROFPORT.getIndex()), margin / accountNetLiq);

            //Update KC Max Loss
            Double kcPerPort = (Double) row.get(KCPERPORT.getIndex()).getItem();
            Double kcMaxLoss = kcPerPort * accountNetLiq;
            updateCellValue(row.get(KCMAXLOSS.getIndex()), kcMaxLoss);

            //Update KC-Qty
            Double kcEdge = (Double) row.get(KCEDGE.getIndex()).getItem();
            Double entry$ = (Double) row.get(ENTRYDOL.getIndex()).getItem();
            Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * - 100);
            updateCellValue(row.get(KCQTY.getIndex()), kcQty);

            //Update Qty. Open/Close
            Double qty = (Double) row.get(QTY.getIndex()).getItem();
            updateCellValue(row.get(QTYOPENCLOSE.getIndex()), kcQty - qty);
        });
    }
}
