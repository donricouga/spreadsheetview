package ca.riveros.ib.events;

import ca.riveros.ib.Common;
import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.Common.calcKcMaxLoss;
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

    }
}

//CONSOLIDATE NETLIQ LISTENER AND HANDLER
