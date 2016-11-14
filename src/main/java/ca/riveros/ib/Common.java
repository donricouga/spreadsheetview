package ca.riveros.ib;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by admin on 11/13/16.
 */
public class Common {

    public static Predicate<List<?>> hasElements = (list) -> list != null && list.size() > 0;

}
