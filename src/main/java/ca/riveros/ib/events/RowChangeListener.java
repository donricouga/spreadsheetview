package ca.riveros.ib.events;

import ca.riveros.ib.Common;
import ca.riveros.ib.Mediator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import java.util.ArrayList;
import java.util.List;

import static ca.riveros.ib.Common.calcKCTakeProfit$;
import static ca.riveros.ib.Common.calcKcCalculateTakeLossAt;
import static ca.riveros.ib.Common.calcKcContractNum;
import static ca.riveros.ib.Common.calcKcLossLevel;
import static ca.riveros.ib.Common.calcKcMaxLoss;
import static ca.riveros.ib.Common.calcKcNetLoss$;
import static ca.riveros.ib.Common.calcKcNetProfit$;
import static ca.riveros.ib.Common.calcKcTakeLoss$;
import static ca.riveros.ib.Common.calcMid;
import static ca.riveros.ib.Common.calcPerPL;
import static ca.riveros.ib.Common.calcQtyOpenClose;
import static ca.riveros.ib.Common.updateCellValue;
import static ca.riveros.ib.TableColumnIndexes.ACCOUNT;
import static ca.riveros.ib.TableColumnIndexes.ASK;
import static ca.riveros.ib.TableColumnIndexes.BID;
import static ca.riveros.ib.TableColumnIndexes.ENTRYDOL;
import static ca.riveros.ib.TableColumnIndexes.KCCALCTAKELOSSAT;
import static ca.riveros.ib.TableColumnIndexes.KCCONTRACTNUM;
import static ca.riveros.ib.TableColumnIndexes.KCCREDITREC;
import static ca.riveros.ib.TableColumnIndexes.KCEDGE;
import static ca.riveros.ib.TableColumnIndexes.KCLOSSLEVEL;
import static ca.riveros.ib.TableColumnIndexes.KCMAXLOSS;
import static ca.riveros.ib.TableColumnIndexes.KCNETLOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCNETPROFITDOL;
import static ca.riveros.ib.TableColumnIndexes.KCPERPORT;
import static ca.riveros.ib.TableColumnIndexes.KCPROBPROFIT;
import static ca.riveros.ib.TableColumnIndexes.KCTAKELOSSDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITDOL;
import static ca.riveros.ib.TableColumnIndexes.KCTAKEPROFITPER;
import static ca.riveros.ib.TableColumnIndexes.LOSSPER;
import static ca.riveros.ib.TableColumnIndexes.MID;
import static ca.riveros.ib.TableColumnIndexes.PERPL;
import static ca.riveros.ib.TableColumnIndexes.PROFITPER;
import static ca.riveros.ib.TableColumnIndexes.QTY;
import static ca.riveros.ib.TableColumnIndexes.QTYOPENCLOSE;

/**
 * Created by ricardo on 2/11/17.
 */
public class RowChangeListener implements ListChangeListener<SpreadsheetCell> {

    private Integer index;

    public RowChangeListener(Integer index) {
        this.index = index;
    }

    @Override
    public void onChanged(ListChangeListener.Change<? extends SpreadsheetCell> c) {
        List<SpreadsheetCell> row = Mediator.INSTANCE.getSpreadSheetCells().get(index);
        List<SpreadsheetCell> row2 = Mediator.INSTANCE.getSpreadSheetCells2().get(index);
        List<SpreadsheetCell> row3 = Mediator.INSTANCE.getSpreadSheetCells3().get(index);

        //Now simply update the cells without trigerring the RowChangeListener. % of Port already calculated by summary handler

        //Get Data that is update from Interactive Brokers (TWS)
        Double bid = (Double) row3.get(BID.getIndex()).getItem();
        Double ask = (Double) row3.get(ASK.getIndex()).getItem();
        Double entry$ = (Double) row.get(ENTRYDOL.getIndex()).getItem();
        Double qty = (Double) row.get(QTY.getIndex()).getItem();
        Double mid = calcMid(bid,ask);
        Double perPL = calcPerPL(entry$, mid);
        Double kcCreditReceived = (Double) row2.get(KCCREDITREC.getIndex()).getItem();

        //Get Manual Fields
        Double kcProbProfit = (Double) row2.get(KCPROBPROFIT.getIndex()).getItem();
        Double kcEdge = (Double) row2.get(KCEDGE.getIndex()).getItem();
        Double kcTakeProfitPer = (Double) row2.get(KCTAKEPROFITPER.getIndex()).getItem();
        Double kcPerPort = (Double) row2.get(KCPERPORT.getIndex()).getItem();

        String account = (String) row3.get(ACCOUNT.getIndex()).getItem();
        Double perCapitalTrade = Mediator.INSTANCE.getPercentCapitalToTradeByAccountNumber(account);

        //Calculate Data
        Double kcTakeProfit$ = calcKCTakeProfit$(entry$, kcTakeProfitPer);
        Double kcNetProfit$ = calcKcNetProfit$(entry$, kcTakeProfit$);
        Double kcLossLevel = calcKcLossLevel(kcTakeProfitPer, kcProbProfit, kcEdge);
        Double kcTakeLoss$ = calcKcTakeLoss$(entry$, kcLossLevel);
        Double kcNetLoss$ = calcKcNetLoss$(entry$, kcTakeLoss$);
        Double kcMaxLoss = calcKcMaxLoss(Mediator.INSTANCE.getAccountNetLiq().doubleValue(), perCapitalTrade, kcPerPort);
        Double kcContractNum = calcKcContractNum(kcMaxLoss, kcNetLoss$);
        Double qtyOpenClose = calcQtyOpenClose(kcContractNum, qty);
        Double kcTakeLossAt = calcKcCalculateTakeLossAt(kcCreditReceived, kcProbProfit, kcTakeLoss$);

        //Update table. Do need to do % of Port since the account summary handler does that already.
        Platform.runLater(() -> {
            updateCellValue(row.get(MID.getIndex()), mid);
            updateCellValue(row.get(PERPL.getIndex()), perPL);
            updateCellValue(row2.get(KCTAKEPROFITDOL.getIndex()), kcTakeProfit$);
            updateCellValue(row2.get(KCNETPROFITDOL.getIndex()), kcNetProfit$);
            updateCellValue(row2.get(KCLOSSLEVEL.getIndex()), kcLossLevel);
            updateCellValue(row2.get(KCTAKELOSSDOL.getIndex()), kcTakeLoss$);
            updateCellValue(row2.get(KCNETLOSSDOL.getIndex()), kcNetLoss$);
            updateCellValue(row2.get(KCMAXLOSS.getIndex()), kcMaxLoss);
            updateCellValue(row2.get(KCCONTRACTNUM.getIndex()), kcContractNum);
            updateCellValue(row2.get(QTYOPENCLOSE.getIndex()), qtyOpenClose);
            updateCellValue(row2.get(KCCALCTAKELOSSAT.getIndex()), kcTakeLossAt);
        });

    }

}
