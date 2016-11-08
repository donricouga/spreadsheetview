package ca.riveros.ib.handlers;

import com.ib.controller.ApiConnection;

public class Logger implements ApiConnection.ILogger {

    @Override
    public void log(String valueOf) {
        System.out.println("LOGGING " + valueOf);
    }


}