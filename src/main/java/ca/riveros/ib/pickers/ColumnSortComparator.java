package ca.riveros.ib.pickers;

import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import java.util.Comparator;

/**
 * Created by admin on 11/27/16.
 */
public class ColumnSortComparator implements Comparator<ObservableList<SpreadsheetCell>> {

    private int index;

    public ColumnSortComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(ObservableList<SpreadsheetCell> o1, ObservableList<SpreadsheetCell> o2) {
        Object cell1 = o1.get(index).getItem();
        Object cell2 = o2.get(index).getItem();
        if(cell1 instanceof String) {
            String item1 = (String) cell1;
            String item2 = (String) cell2;
            return item1.compareTo(item2);
        } else {
            Double item1 = (Double) cell1;
            Double item2 = (Double) cell2;
            if(item1 < item2)
                return -1;
            else if(item1.equals(item2))
                return 0;
            else
                return 1;
        }
    }
}
