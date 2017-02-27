package ca.riveros.ib.data;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public final class PersistentFields {

    private static Properties properties = new Properties();
    private static OutputStream out;
    private static File file;

    public static final String A_TABLE = "A";
    public static final String K_TABLE = "K";
    public static final String O_TABLE = "O";
    public static final String BLOCK_TRADE_TABLE = "BT";

    static {
        try {
            String home = System.getProperty("user.home");
            file = new File(home + "/keyvalue.properties");
            file.createNewFile();
            FileInputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
            System.out.println("Loaded keyvalue.properties");
        } catch(IOException ioe) {
            System.out.println(ioe);
            System.exit(-1);
        }
    }

    public static Double getValue(String account, int contractId, String table, int col, double defaultValue) {
        String key = account + "." + contractId + "." + table + "." + col;
        Object o = properties.get(key);
        if(o == null)
            return defaultValue;
        else
            return Double.valueOf((String) o);
    }

    public static void setValue(String account, int contractId, String table, int col, Double value) {
        properties.setProperty(account + "." + contractId + "." + table + "." + col, value.toString());
        writeToFile();
    }

    public static Double getPercentTraded(String account, Double defaultValue) {
        Object o = properties.get("block.trading.percent.traded." + account);
        if(o == null) {
            return defaultValue;
        }
        return Double.valueOf((String) o);
    }

    public static void setPercentTraded(String account, Double percentTraded) {
        properties.setProperty("block.trading.percent.traded." + account, percentTraded.toString());
        writeToFile();
    }

    public static Double getPercentSymbol(String account, Double defaultValue) {
        Object o = properties.get("block.trading.percent.symbol." + account);
        if(o == null) {
            return defaultValue;
        }
        return Double.valueOf((String) o);
    }

    public static void setPercentSymbol(String account, Double percentSymbol) {
        properties.setProperty("block.trading.percent.symbol." + account, percentSymbol.toString());
        writeToFile();
    }

    public static Double getMargin(String account, Double defaultValue) {
        Object o = properties.get("block.trading.margin." + account);
        if(o == null) {
            return defaultValue;
        }
        return Double.valueOf((String) o);
    }

    public static void setMargin(String account, Double margin) {
        properties.setProperty("block.trading.margin." + account, margin.toString());
        writeToFile();
    }

    public static void clearProperties() {
        try {
            out = new FileOutputStream(file);
            properties.clear();
            out.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void writeToFile() {
        try {
            out = new FileOutputStream(file);
            properties.store(out, "");
            out.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}