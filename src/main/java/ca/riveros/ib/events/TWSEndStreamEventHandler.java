package ca.riveros.ib.events;

import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.PERPL;

/**
 * Created by admin on 11/28/16.
 */
public class TWSEndStreamEventHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        SpreadsheetCell cell = (SpreadsheetCell) event.getTarget();
        ObservableList<SpreadsheetCell> rowList = Mediator.INSTANCE.getSpreadSheetCells().get(cell.getRow());
        Platform.runLater(() -> {
            updatePercentPL((Double) cell.getItem(), rowList);
            //TODO: More formula calculations to follow
        });
    }

    private void updatePercentPL(Double mid, ObservableList<SpreadsheetCell> rowList) {
        Double entry$ = (Double) rowList.get(ENTRYDOL.getIndex()).getItem();
        Double perpl = 0.0;
        if(entry$ < 0)
            perpl = (entry$ - mid) / entry$;
        else
            perpl = (mid - entry$) / entry$;
        updateCellValue(rowList.get(PERPL.getIndex()), perpl);
    }

}
