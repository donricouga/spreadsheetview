package ca.riveros.ib.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

/**
 * Created by ricardo on 1/18/17.
 */
public class FlashingAnimation {

    public static String NEGATIVE = "CB5A5A";

    private Timeline kcTakeProfit$Animation;

    private static FlashingAnimation INSTANCE;

    private FlashingAnimation() {}

    public static FlashingAnimation getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FlashingAnimation();
        }
        return INSTANCE;
    }

    public void playKcTakeProfit$Animation(SpreadsheetCell cell, String color) {
        if(kcTakeProfit$Animation == null) {
            kcTakeProfit$Animation = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), e ->
                        cell.setStyle("-fx-background-color: #" + color)
                    ),

                    new KeyFrame(Duration.seconds(1.0), e ->
                        cell.setStyle("-fx-background-color: white")
                    )
            );
            kcTakeProfit$Animation.setCycleCount(Animation.INDEFINITE);
        }
        kcTakeProfit$Animation.playFromStart();
    }

    public void stopKcTakeProfit$Animation(SpreadsheetCell cell) {
        if(kcTakeProfit$Animation != null && kcTakeProfit$Animation.getStatus() == Animation.Status.RUNNING) {
            kcTakeProfit$Animation.stop();
            cell.setStyle("-fx-background-color: white");
        }
    }
}
