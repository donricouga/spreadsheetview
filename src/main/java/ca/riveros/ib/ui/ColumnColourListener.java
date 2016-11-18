package ca.riveros.ib.ui;

import javafx.collections.ListChangeListener;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;

import java.util.List;

/**
 * Created by admin on 11/17/16.
 */
public class ColumnColourListener implements ListChangeListener<SpreadsheetColumn> {

    @Override
    public void onChanged(Change<? extends SpreadsheetColumn> c) {
        while(c.next()) {
            if(c.wasUpdated()) {

            }
            else {
                for(SpreadsheetColumn removedSpreadSheetColumn: c.getRemoved()) {

                }
            }
        }
    }
}
