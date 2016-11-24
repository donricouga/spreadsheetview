package ca.riveros.ib.data;

import java.io.*;
import java.util.Properties;

public final class PersistentFields {

    private static Properties properties = new Properties();
    private static OutputStream out;
    private static File file;

    static {
        try {
            file = new File("keyvalue.properties");
            file.createNewFile();
            FileInputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
            System.out.println("Loaded keyvalue.properties");
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
            System.exit(-1);
        } catch(IOException ioe) {
            System.out.println(ioe);
            System.exit(-1);
        }
    }

    public static Double getValue(String account, int contractId, int col) {
        String key = account + "." + contractId + "." + col;
        Object o = properties.get(key);
        if(o == null)
            return null;
        else
            return Double.valueOf((String) o);
    }

    public static Double getValue(String account, int contractId, int col, double defaultValue) {
        String key = account + "." + contractId + "." + col;
        Object o = properties.get(key);
        if(o == null)
            return defaultValue;
        else
            return Double.valueOf((String) o);
    }

    public static void setValue(String account, int contractId, int col, Double value) {
        properties.setProperty(account + "." + contractId + "." + col, value.toString());
        try {
            out = new FileOutputStream(file);
            properties.store(out, "");
            out.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
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
}