package it.unimore.programmazioneoggetti.budget.view;
import java.math.BigDecimal;

import it.unimore.programmazioneoggetti.budget.model.Transaction;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel personalizzato per mostrare una lista di Transaction in una JTable.
 */
public class TransactionTableModel extends AbstractTableModel {

    private final List<Transaction> transactions;
    private final String[] columnNames = {"Data", "Descrizione", "Importo"};

    // Formatter per visualizzare LocalDate in formato leggibile (dd/MM/yyyy)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TransactionTableModel() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Aggiorna l'intera lista di transazioni e notifica la JTable di rinfrescare.
     *
     * @param list lista di Transaction da mostrare
     */
    public void setTransactions(List<Transaction> list) {
        transactions.clear();
        if (list != null) {
            transactions.addAll(list);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return transactions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Ritorna il valore da mostrare nella cella [rowIndex, columnIndex].
     * Col 0 = Data (formattata), Col 1 = Descrizione, Col 2 = Importo con segno.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction t = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                // format da LocalDate in dd/MM/yyyy
                return t.getDate().format(dateFormatter);
            case 1:
                return t.getDescription();
            case 2:
                // mostriamo signedAmount() come stringa, es. "-600.00" o "+1500.00"
                BigDecimal signed = t.signedAmount();
                return signed.toString();
            default:
                return null;
        }
    }

    /**
     * Restituisce la Transaction corrispondente a una riga della tabella.
     * Utile per conoscere quale oggetto rimuovere o modificare.
     *
     * @param rowIndex indice di riga selezionata
     * @return Transaction associata a rowIndex, oppure null se rowIndex invalido
     */
    public Transaction getTransactionAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= transactions.size()) {
            return null;
        }
        return transactions.get(rowIndex);
    }
}
