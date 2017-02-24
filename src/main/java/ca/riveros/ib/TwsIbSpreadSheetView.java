package ca.riveros.ib;

import ca.riveros.ib.events.*;
import ca.riveros.ib.model.SpreadsheetModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import static ca.riveros.ib.Common.createCell;
import static ca.riveros.ib.Common.decimalFormat;
import static ca.riveros.ib.Common.dollarFormat;
import static ca.riveros.ib.Common.noDecimals;
import static ca.riveros.ib.Common.percentFormat;
import static ca.riveros.ib.Common.twoDecimalFormat;
import static ca.riveros.ib.Common.twoPercentFormat;
import static ca.riveros.ib.events.EventTypes.twsEndStreamEventType;
import static ca.riveros.ib.TableColumnIndexes.*;
import static ca.riveros.ib.data.PersistentFields.getValue;

/**
 * Build the UI and launch the Application
 */
public class TwsIbSpreadSheetView extends Application {

    //Mediator - Using mediator Pattern
    Mediator mediator;
    //Tabs
    SpreadsheetView spreadsheetView;
    SpreadsheetView spreadsheetView2;
    SpreadsheetView spreadsheetView3;
    SpreadsheetView spreadsheetView4;

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

    private final List<String> columnData1 = Arrays.asList("Contract", "Qty", "Entry $", "Mid", "Unreal P/L", "Real P/L", "% of Port", "Margin",
            "Target Profit %", "Target Loss %");

    private final List<String> columnData2 = Arrays.asList("KC Prob. Profit", "KC Edge", "KC Calculate Take Loss At", "KC Credit Received", "KC Take Profit %", "KC Take Profit $", "KC Net Profit $",
            "KC Loss Level", "KC Take Loss $", "KC Net Loss $", "KC % Port.", "KC Max Loss", "KC Contract #", "Qty. Open/Close");

    private final List<String> columnData3 = Arrays.asList("Market $", "Notional",
            "Delta", "ImplVol %", "% P/L", "Bid", "Ask", "Contract ID", "Symbol", "Account");

    private final List<String> columnData4 = Arrays.asList("Acc. #", "Net Liq", "% Traded", "$Traded", "% Symbol", "$ Symbol", "Margin", "Contract");

    //@Override
    public String getSampleName() {
        return "Custom Data Table";
    }


    public Node getPanel() {
        borderPane = new BorderPane();

        spreadsheetView = createSpreadsheetViews(columnData1);
        spreadsheetView2 = createSpreadsheetViews(columnData2);
        spreadsheetView3 = createSpreadsheetViews(columnData3);
        spreadsheetView4 = createSpreadsheetViews(columnData4);


        //set the default column configuration
        spreadsheetView.getColumns().get(0).setFixed(true);
        hideUnecessaryColumns();

        //set pickers
//        for(int i = 0; i < spreadsheetView.getColumns().size(); i++) {
//            spreadsheetView.getColumnPickers().put(i, new ColumnSortPicker(spreadsheetView, i));
//        }

        borderPane.setCenter(createTabs());
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

    private SpreadsheetView createSpreadsheetViews(List<String> columnData) {
        GridBase grid = new GridBase(1, columnData.size());
        //grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight()));
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
        SpreadsheetView spv = new SpreadsheetView(grid);
        spv.setShowRowHeader(false);
        spv.setShowColumnHeader(true);
        spv.setEditable(true);
        spv.getSelectionModel().setSelectionMode(selectionMode.isSelected() ? SelectionMode.MULTIPLE : SelectionMode.SINGLE);
        spv.getColumns().forEach(c -> {
            c.setMinWidth(100);
            c.fitColumn();
        });

        //set the default column configuration
        spv.getStylesheets().add(Thread.currentThread()
                .getContextClassLoader().getResource("spreadsheetSample.css").toExternalForm());

        return spv;
    }

    /*private Map<Integer, Double> generateRowHeight() {
        Map<Integer, Double> rowHeight = new HashMap<>();
        rowHeight.put(1, 100.0);
        return rowHeight;
    }*/

    private TabPane createTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tab = new Tab();
        tab.setText("Tab 1");
        tab.setContent(spreadsheetView);

        Tab tab2 = new Tab();
        tab2.setText("Tab 2");
        tab2.setContent(spreadsheetView2);

        Tab tab3 = new Tab();
        tab3.setText("Tab 3");
        tab3.setContent(spreadsheetView3);

        Tab tab4 = new Tab();
        tab4.setText("Block Trading");
        tab4.setContent(spreadsheetView4);

        tabPane.getTabs().addAll(tab,tab2,tab3, tab4);
        return tabPane;
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
        /*spreadsheetView3.getColumns().get(BID.getIndex()).setMaxWidth(0);
        spreadsheetView3.getColumns().get(BID.getIndex()).setPrefWidth(0);
        spreadsheetView3.getColumns().get(BID.getIndex()).setMinWidth(0);
        spreadsheetView3.getColumns().get(ASK.getIndex()).setMaxWidth(0);
        spreadsheetView3.getColumns().get(ASK.getIndex()).setPrefWidth(0);
        spreadsheetView3.getColumns().get(ASK.getIndex()).setMinWidth(0);*/
        spreadsheetView3.getColumns().get(CONTRACTID.getIndex()).setMaxWidth(0);
        spreadsheetView3.getColumns().get(CONTRACTID.getIndex()).setPrefWidth(0);
        spreadsheetView3.getColumns().get(CONTRACTID.getIndex()).setMinWidth(0);
        spreadsheetView3.getColumns().get(ACCOUNT.getIndex()).setMaxWidth(0);
        spreadsheetView3.getColumns().get(ACCOUNT.getIndex()).setPrefWidth(0);
        spreadsheetView3.getColumns().get(ACCOUNT.getIndex()).setMinWidth(0);
        spreadsheetView3.getColumns().get(SYMBOL.getIndex()).setMaxWidth(0);
        spreadsheetView3.getColumns().get(SYMBOL.getIndex()).setPrefWidth(0);
        spreadsheetView3.getColumns().get(SYMBOL.getIndex()).setMinWidth(0);
    }


    public SpreadsheetView getSpreadsheetView() {
        return spreadsheetView;
    }

    public SpreadsheetView getSpreadsheetView2() {
        return spreadsheetView2;
    }

    public SpreadsheetView getSpreadsheetView3() {
        return spreadsheetView3;
    }

    public SpreadsheetView getSpreadsheetView4() {
        return spreadsheetView4;
    }
}
