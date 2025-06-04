package it.unimore.programmazioneoggetti.budget.view;

import it.unimore.programmazioneoggetti.budget.model.Transaction;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel personalizzato per mostrare una lista di {@link Transaction} in una {@link JTable}.
 * Definisce tre colonne: data, descrizione e importo (con segno).
 */
public class TransactionTableModel extends AbstractTableModel {

    private final List<Transaction> transactions;
    private final String[] columnNames = {"Data", "Descrizione", "Importo"};
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Costruisce un modello vuoto, pronto per essere popolato con {@code setTransactions(...) }. */
    public TransactionTableModel() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Sostituisce la lista corrente di transazioni e notifica alla JTable
     * di rinfrescare i dati.
     *
     * @param list lista di {@link Transaction} da mostrare (può essere null)
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
     * Ritorna il valore da mostrare nella cella specificata.
     * Colonna 0 → data formattata, 1 → descrizione, 2 → importo con segno.
     *
     * @param rowIndex    indice di riga
     * @param columnIndex indice di colonna
     * @return oggetto da visualizzare nella cella
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction t = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return t.getDate().format(dateFormatter);
            case 1:
                return t.getDescription();
            case 2:
                return t.signedAmount().toString();
            default:
                return null;
        }
    }

    /**
     * Restituisce la transazione corrispondente a una riga specifica,
     * utile per operazioni di modifica o eliminazione.
     *
     * @param rowIndex indice di riga
     * @return {@link Transaction} alla riga specificata, oppure null se l’indice non è valido
     */
    public Transaction getTransactionAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= transactions.size()) {
            return null;
        }
        return transactions.get(rowIndex);
    }
}
