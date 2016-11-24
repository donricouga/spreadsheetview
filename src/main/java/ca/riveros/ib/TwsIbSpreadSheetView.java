/**
 * Copyright (c) 2013, 2015 ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ca.riveros.ib;

import ca.riveros.ib.events.*;
import ca.riveros.ib.model.SpreadsheetModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.riveros.ib.TableColumnIndexes.*;
import static ca.riveros.ib.data.PersistentFields.getValue;

/**
 * Build the UI and launch the Application
 */
public class TwsIbSpreadSheetView extends Application {

    //Mediator - Using mediator Pattern
    Mediator mediator;

    SpreadsheetView spreadSheetView;
    private BorderPane borderPane;
    private final CheckBox rowHeader = new CheckBox();
    private final CheckBox columnHeader = new CheckBox();
    private final CheckBox selectionMode = new CheckBox();
    private final CheckBox editable = new CheckBox();

    //Combo Box to Allow user to select to filter by accounts
    ComboBox<String> accountComboBox = new ComboBox<>();

    //Button to show and hide logs
    Button logButton = new Button("Show Logs");

    //Top of BorderPane TextFields
    TextField totalNetLiqTextField = new TextField("0.0");
    TextField accountNetLiqTextField = new TextField("0.0");
    TextField totalInitMarginTextField = new TextField("0.0");
    TextField perCapitalToTradeTextField = new TextField("00.00");

    //Logger tabs
    TextArea inLoggerText = new TextArea("");
    TextArea outLoggerText = new TextArea("");
    TextArea messagesLoggerText = new TextArea("");

    private Boolean logsDisplayed = false;

    public TwsIbSpreadSheetView(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(getSampleName());

        Scene scene = new Scene((Parent) getPanel(), 1400, 768);
        scene.getStylesheets().add(Thread.currentThread()
                .getContextClassLoader().getResource("fxsampler.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private final List<String> columnData = Arrays.asList("Contract", "Qty", "KC-Qty", "Qty. Open/Close", "Entry $", "Mid",
            "Market $", "Unreal P/L", "Real P/L", "% of Port", "% P/L", "Margin", "Prob. Profit", "KC % Port", "Profit %", "Loss %",
            "KC Edge", "KC Profit %", "KC Loss %", "KC Take Profit $", "KC Take Loss $", "KC Net Profit $", "KC Net Loss", "KC Max Loss",
            "Notional", "Delta", "ImpVol %", "Bid", "Ask", "Contract Id", "Symbol", "Account");

    //@Override
    public String getSampleName() {
        return "Custom Data Table";
    }


    public Node getPanel() {
        borderPane = new BorderPane();

        int rowCount = 100;
        int columnCount = columnData.size();
        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1," "));
            }
            rows.add(list);
        }
        grid.setRows(rows);

        //Set Column Headers
        grid.getColumnHeaders().setAll(columnData);

        //Create Spreadsheet
        spreadSheetView = new SpreadsheetView(grid);
        spreadSheetView.setShowRowHeader(false);
        spreadSheetView.setShowColumnHeader(true);
        spreadSheetView.setEditable(true);
        spreadSheetView.getSelectionModel().setSelectionMode(selectionMode.isSelected() ? SelectionMode.MULTIPLE : SelectionMode.SINGLE);
        spreadSheetView.getColumns().forEach(c -> {
            c.setMinWidth(100);
            c.fitColumn();
        });

        //set the default column configuration
        spreadSheetView.getColumns().get(0).setFixed(true);
        hideUnecessaryColumns();

        borderPane.setCenter(spreadSheetView);

        spreadSheetView.getStylesheets().add(Thread.currentThread()
                .getContextClassLoader().getResource("spreadsheetSample.css").toExternalForm());


        borderPane.setTop(createTopOfBorderPane());

        //Bottom Pane
        logButton.setOnAction(e -> {
            if(logsDisplayed) {
                Platform.runLater(() -> {
                    borderPane.setBottom(null);
                    logButton.setText("Show Logs");
                    logsDisplayed = false;
                });
            }
            else {
                Platform.runLater(() -> {
                    borderPane.setBottom(createLogViewTabs());
                    logButton.setText("Hide Logs");
                    logsDisplayed = true;
                });
            }
        });
        return borderPane;
    }

    /**
     * generate a grid pane that represents the top portion of the borderPane
     */
    private GridPane createTopOfBorderPane() {
        GridPane gridPane = new GridPane();
        //gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setStyle("-fx-background-color: DAE6F3;");

        accountComboBox.valueProperty()
                .addListener(((observable, oldValue, newValue) -> mediator.requestAccountUpdate(newValue)));

        //Create Text Boxes
        Label totalNetLiqLabel = new Label("Total Net Liq");
        totalNetLiqTextField.setEditable(false);
        totalNetLiqTextField.getStyleClass().add("disabled");
        totalNetLiqTextField.setMaxSize(100,10);
        Label acctNetLiqLabel = new Label("Account Net Liq");
        accountNetLiqTextField.setEditable(false);
        accountNetLiqTextField.getStyleClass().add("disabled");
        accountNetLiqTextField.setMaxSize(100,10);
        Label totalInitMarginLabel = new Label("Total Initial Margin");
        totalInitMarginTextField.setEditable(false);
        totalInitMarginTextField.getStyleClass().add("disabled");
        totalInitMarginTextField.setMaxSize(75,10);
        Label perCapToTradeLabel = new Label("Percentage Capital To Trade");
        perCapitalToTradeTextField.setMaxSize(75,10);

        gridPane.add(accountComboBox, 0, 0, 1, 1);
        gridPane.add(totalNetLiqLabel, 2, 0, 1, 1);
        gridPane.add(totalNetLiqTextField, 3,0, 1, 1);
        gridPane.add(acctNetLiqLabel, 4, 0, 1, 1);
        gridPane.add(accountNetLiqTextField,5, 0, 1, 1);
        gridPane.add(totalInitMarginLabel, 6, 0, 1, 1);
        gridPane.add(totalInitMarginTextField, 7, 0, 1, 1);
        gridPane.add(perCapToTradeLabel, 8, 0, 1, 1);
        gridPane.add(perCapitalToTradeTextField, 9, 0, 1, 1);
        gridPane.add(logButton, 10, 0, 1, 1);

        return gridPane;
    }

    private TabPane createLogViewTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab inTab = new Tab();
        inTab.setText("Received from TWS");
        inTab.setContent(inLoggerText);

        Tab outTab = new Tab();
        outTab.setText("Sent to TWS");
        outTab.setContent(outLoggerText);

        Tab messagesTab = new Tab();
        messagesTab.setText("Messages");
        messagesTab.setContent(messagesLoggerText);

        tabPane.getTabs().addAll(inTab,outTab, messagesTab);
        return tabPane;
    }

    private void hideUnecessaryColumns() {
        spreadSheetView.getColumns().get(BID.getIndex()).setMaxWidth(0);
        spreadSheetView.getColumns().get(BID.getIndex()).setPrefWidth(0);
        spreadSheetView.getColumns().get(BID.getIndex()).setMinWidth(0);
        spreadSheetView.getColumns().get(ASK.getIndex()).setMaxWidth(0);
        spreadSheetView.getColumns().get(ASK.getIndex()).setPrefWidth(0);
        spreadSheetView.getColumns().get(ASK.getIndex()).setMinWidth(0);
        spreadSheetView.getColumns().get(CONTRACTID.getIndex()).setMaxWidth(0);
        spreadSheetView.getColumns().get(CONTRACTID.getIndex()).setPrefWidth(0);
        spreadSheetView.getColumns().get(CONTRACTID.getIndex()).setMinWidth(0);
        spreadSheetView.getColumns().get(ACCOUNT.getIndex()).setMaxWidth(0);
        spreadSheetView.getColumns().get(ACCOUNT.getIndex()).setPrefWidth(0);
        spreadSheetView.getColumns().get(ACCOUNT.getIndex()).setMinWidth(0);
    }

    private SpreadsheetCell createCell(int row, int col, Double value, Boolean editable) {
        SpreadsheetCell cell = SpreadsheetCellType.DOUBLE.createCell(row, col, 1, 1, value);
        cell.setEditable(editable);
        return cell;
    }

    private SpreadsheetCell createCell(int row, int col, Double value, Boolean editable, String cssClass, ChangeListener cl) {
        SpreadsheetCell cell = createCell(row,col,value,editable);
        cell.getStyleClass().add(cssClass);
        cell.itemProperty().addListener(cl);
        return cell;
    }


    public void updateSpreadsheetViewGrid(List<SpreadsheetModel> list) {
        Grid g = spreadSheetView.getGrid();
        String account = accountComboBox.getValue();

        //Create a Collection<ObservableList<SpreadsheetCell>>
        ObservableList<ObservableList<SpreadsheetCell>> spreadsheetModelObservableList = FXCollections.observableArrayList();

        //Map SpreadsheetModel to Grid Observable List
        AtomicInteger counter = new AtomicInteger(0);
        list.forEach(sm -> {
            ObservableList<SpreadsheetCell> rowsList = FXCollections.observableArrayList();
            SpreadsheetCell contractCell = SpreadsheetCellType.STRING.createCell(counter.intValue(),0,1,1,sm.getContract());
            contractCell.setWrapText(true);
            rowsList.add(contractCell);
            rowsList.add(createCell(counter.intValue(),1,sm.getQty(),false));
            rowsList.add(createCell(counter.intValue(),2,sm.getKcQty(),false));
            rowsList.add(createCell(counter.intValue(),3,sm.getQtyOpenClose(),false));
            rowsList.add(createCell(counter.intValue(),4,sm.getEntry$(),false));
            rowsList.add(createCell(counter.intValue(),5,sm.getMid(),false));
            rowsList.add(createCell(counter.intValue(),6,sm.getMarket$(),false));
            SpreadsheetCell unrealPNLCell = createCell(counter.intValue(),7,sm.getUnrealPL(),false);
            if(sm.getUnrealPL() > 0)
                unrealPNLCell.getStyleClass().add("positive");
            else
                unrealPNLCell.getStyleClass().add("negative");
            rowsList.add(unrealPNLCell);
            rowsList.add(createCell(counter.intValue(),8,sm.getRealPL(),false));
            rowsList.add(createCell(counter.intValue(),9,sm.getPercentOfPort(),false));
            rowsList.add(createCell(counter.intValue(),10,sm.getPercentPL(),false));
            rowsList.add(createCell(counter.intValue(),11,getValue(account,sm.getContractId(), MARGIN.getIndex(), 0.0), true, "manualo",
                    new MarginActionEvent(spreadSheetView.getGrid().getRows(), Double.valueOf(accountNetLiqTextField.getText()))));
            rowsList.add(createCell(counter.intValue(),12,getValue(account, sm.getContractId(), PROBPROFIT.getIndex(), 0.91), true, "manualo",
                    new ProbabilityOfProfitEvent(spreadSheetView.getGrid().getRows())));
            rowsList.add(createCell(counter.intValue(),13,getValue(account, sm.getContractId(), KCPERPORT.getIndex(), 0.0075), true, "manualy",
                    new KCPercentPortEvent((spreadSheetView.getGrid().getRows()))));
            rowsList.add(createCell(counter.intValue(),14,getValue(account, sm.getContractId(), PROFITPER.getIndex(), 0.57), true, "manualy",
                    new ProfitPercentageEvent(spreadSheetView.getGrid().getRows())));
            rowsList.add(createCell(counter.intValue(),15,getValue(account, sm.getContractId(), LOSSPER.getIndex(), 2.2), true, "manualy",
                    new LossPercentageEvent(spreadSheetView.getGrid().getRows())));
            rowsList.add(createCell(counter.intValue(),16,getValue(account, sm.getContractId(), KCEDGE.getIndex(), 0.1), true, "manualy",
                    new KCEdgeEvent(spreadSheetView.getGrid().getRows())));
            rowsList.add(createCell(counter.intValue(),17,sm.getKcProfitPercentage(),false));
            rowsList.add(createCell(counter.intValue(),18,sm.getKcLossPercentage(),false));
            rowsList.add(createCell(counter.intValue(),19,sm.getKcTakeProfit$(),false));
            rowsList.add(createCell(counter.intValue(),20,sm.getKcTakeLoss$(),false));
            rowsList.add(createCell(counter.intValue(),21,sm.getKcNetProfit$(),false));
            rowsList.add(createCell(counter.intValue(),22,sm.getKcNetLoss$(),false));
            rowsList.add(createCell(counter.intValue(),23,sm.getKcMaxLoss(),false));
            rowsList.add(createCell(counter.intValue(),24,sm.getNotional(),false));
            rowsList.add(createCell(counter.intValue(),25,sm.getDelta(),false));
            rowsList.add(createCell(counter.intValue(),26,sm.getImpVolPercentage(),false));
            rowsList.add(createCell(counter.intValue(),27,sm.getBid(),false)); //BID
            rowsList.add(createCell(counter.intValue(),28,sm.getMid(),false)); //ASK
            rowsList.add(SpreadsheetCellType.INTEGER.createCell(counter.intValue(),29,1,1,sm.getContractId()));
            rowsList.add(SpreadsheetCellType.STRING.createCell(counter.intValue(),30,1,1,sm.getSymbol()));
            rowsList.add(SpreadsheetCellType.STRING.createCell(counter.intValue(),31,1,1,sm.getAccount()));
            counter.incrementAndGet();
            spreadsheetModelObservableList.add(rowsList);
        });

        //set grid and display
        g.setRows(spreadsheetModelObservableList);
        spreadSheetView.setGrid(g);
    }
}
