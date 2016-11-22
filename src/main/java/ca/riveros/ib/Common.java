package ca.riveros.ib;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by admin on 11/13/16.
 */
public class Common {

    public static Predicate<List<?>> hasElements = (list) -> list != null && list.size() > 0;

    public static void updateCellValue(SpreadsheetCell cell, Double value) {
        cell.setEditable(true);
        cell.setItem(value);
        cell.setEditable(false);
    }

}
