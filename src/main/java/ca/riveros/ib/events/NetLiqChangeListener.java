package ca.riveros.ib.events;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.*;

/**
 * Created by admin on 12/1/16.
 */
public class NetLiqChangeListener implements ChangeListener<String> {

    private SpreadsheetView view;
    private SpreadsheetView view2;

    public NetLiqChangeListener(SpreadsheetView view, SpreadsheetView view2) {
        this.view = view;
        this.view2 = view2;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        ObservableList<ObservableList<SpreadsheetCell>> list = view.getGrid().getRows();
        ObservableList<ObservableList<SpreadsheetCell>> list2 = view2.getGrid().getRows();
        AtomicInteger counter = new AtomicInteger(0);
        list.forEach(row -> {

            Double accountNetLiq = Double.valueOf(newValue);

            Platform.runLater(() -> {

                //Update Percent Of Port
                Double margin = (Double) row.get(MARGIN.getIndex()).getItem();
                updateCellValue(row.get(PEROFPORT.getIndex()), margin / accountNetLiq);

                //Update KC Max Loss
                Double kcPerPort = (Double) list2.get(counter.get()).get(KCPERPORT.getIndex()).getItem();
                Double kcMaxLoss = kcPerPort * accountNetLiq;
                updateCellValue(row.get(KCMAXLOSS.getIndex()), kcMaxLoss);

                //Update KC-Qty
                Double kcEdge = (Double) list2.get(counter.get()).get(KCEDGE.getIndex()).getItem();
                Double entry$ = (Double) row.get(ENTRYDOL.getIndex()).getItem();
                Double kcQty = (kcMaxLoss) / (entry$ * (1 + kcEdge) * -100);
                updateCellValue(list2.get(counter.get()).get(KCCONTRACTNUM.getIndex()), kcQty);

                //Update Qty. Open/Close
                Double qty = (Double) row.get(QTY.getIndex()).getItem();
                updateCellValue(list2.get(counter.get()).get(QTYOPENCLOSE.getIndex()), kcQty - qty);

                counter.incrementAndGet();
            });
        });
    }
}
