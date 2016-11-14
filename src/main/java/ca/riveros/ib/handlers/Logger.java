package ca.riveros.ib.handlers;

import com.ib.controller.ApiConnection;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Logger implements ApiConnection.ILogger {

    private TextArea textArea;

    public Logger(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void log(String valueOf) {
        System.out.println(valueOf + "\n");
        Platform.runLater(() -> textArea.appendText(valueOf + "\n"));
    }


}