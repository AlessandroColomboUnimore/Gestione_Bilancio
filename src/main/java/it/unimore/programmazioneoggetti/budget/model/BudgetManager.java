package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestisce tutte le transazioni del bilancio: aggiunta, rimozione, ricerca
 * e calcolo del saldo totale. Mantiene una lista interna immutabile di
 * {@link Transaction}.
 */
public class BudgetManager {

    private final List<Transaction> transactions;

    /**
     * Costruisce un nuovo BudgetManager con lista vuota di transazioni.
     */
    public BudgetManager() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Aggiunge una nuova transazione. Se il parametro è {@code null}, lancia
     * IllegalArgumentException.
     *
     * @param t transazione da aggiungere (non null)
     * @throws IllegalArgumentException se {@code t} è null
     */
    public void addTransaction(Transaction t) {
        if (t == null) throw new IllegalArgumentException("La transazione non può essere nulla");
        transactions.add(t);
    }

    /**
     * Rimuove una transazione esistente. Restituisce true se l’ha rimossa,
     * false se non era presente.
     *
     * @param t transazione da rimuovere
     * @return {@code true} se rimossa con successo, {@code false} altrimenti
     */
    public boolean removeTransaction(Transaction t) {
        return transactions.remove(t);
    }

    /**
     * Ritorna una copia della lista di tutte le transazioni correnti.
     * In questo modo la lista interna rimane protetta.
     *
     * @return lista immutabile di {@link Transaction}
     */
    public List<Transaction> getAllTransactions() {
        return List.copyOf(transactions);
    }

    /**
     * Calcola il saldo totale delle transazioni: somma degli importi con segno
     * (le uscite sono negative, le entrate positive).
     *
     * @return saldo totale come BigDecimal
     */
    public BigDecimal calculateTotalBalance() {
        return transactions.stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Filtra e restituisce tutte le transazioni avvenute in una data specifica.
     *
     * @param date data di ricerca
     * @return lista di transazioni con {@code t.getDate().isEqual(date)}
     */
    public List<Transaction> getByDate(LocalDate date) {
        return transactions.stream()
                .filter(t -> t.getDate().isEqual(date))
                .collect(Collectors.toList());
    }

    /**
     * Filtra e restituisce le transazioni comprese nell’intervallo [from, to].
     *
     * @param from data di inizio (inclusa)
     * @param to   data di fine (inclusa)
     * @return lista di transazioni che cadono nel range specificato
     */
    public List<Transaction> getByDateRange(LocalDate from, LocalDate to) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                .collect(Collectors.toList());
    }
}
