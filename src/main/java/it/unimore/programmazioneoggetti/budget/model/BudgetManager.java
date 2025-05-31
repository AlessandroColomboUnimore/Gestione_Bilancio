package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe che gestisce l'elenco di tutte le transazioni (entrate e spese).
 * Implementa metodi per aggiungere, rimuovere, modificare, filtrare e calcolare saldi.
 */
public class BudgetManager {

    // 1) Incapsuliamo la collezione: List<Transaction>
    private final List<Transaction> transactions;

    /**
     * Costruttore: inizializza la lista vuota.
     */
    public BudgetManager() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Aggiunge una nuova transazione (entrata o spesa).
     * @param t transazione da aggiungere (non null)
     */
    public void addTransaction(Transaction t) {
        if (t == null) {
            throw new IllegalArgumentException("La transazione non può essere nulla");
        }
        transactions.add(t);
    }

    /**
     * Rimuove una transazione esistente.
     * @param t transazione da rimuovere
     * @return true se rimossa con successo, false altrimenti
     */
    public boolean removeTransaction(Transaction t) {
        return transactions.remove(t);
    }

    /**
     * Restituisce la lista (non modificabile direttamente) di tutte le transazioni correnti.
     * Usiamo List.copyOf() per evitare che clienti del metodo modifichino la lista interna.
     */
    public List<Transaction> getAllTransactions() {
        return List.copyOf(transactions);
    }

    /**
     * Calcola il saldo complessivo (somma algebrica di tutte le transazioni).
     * Entrate positive, spese negative.
     */
    public BigDecimal calculateTotalBalance() {
        return transactions.stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Filtra le transazioni in un singolo giorno.
     * @param date giorno da filtrare
     * @return lista di transazioni con data pari a 'date'
     */
    public List<Transaction> getByDate(LocalDate date) {
        return transactions.stream()
                .filter(t -> t.getDate().isEqual(date))
                .collect(Collectors.toList());
    }

    /**
     * Filtra le transazioni in un intervallo di date (inclusivo).
     * @param from data di inizio (inclusiva)
     * @param to   data di fine (inclusiva)
     * @return lista di transazioni nell'intervallo
     */
    public List<Transaction> getByDateRange(LocalDate from, LocalDate to) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    // (Opzionale: filtri per settimana, mese, anno, ma possiamo usare getByDateRange)
}
