package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Rappresenta una voce di bilancio (astratta).
 * Contiene data, descrizione e ammontare.
 */
public abstract class Transaction {

    // 1) Incapsulamento: rendiamo i campi 'private'
    private LocalDate date;
    private String description;
    private BigDecimal amount;

    /**
     * Costruttore base di Transaction.
     *
     * @param date        Data della transazione (non null)
     * @param description Descrizione (non vuota)
     * @param amount      Importo (BigDecimal, positivo)
     */
    public Transaction(LocalDate date, String description, BigDecimal amount) {
        // Validazione minima per garantire correttezza dati
        if (date == null) {
            throw new IllegalArgumentException("La data non può essere nulla");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La descrizione non può essere nulla o vuota");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("L'ammontare non può essere nullo o negativo");
        }

        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // 2) Getter per tutti i campi (nessun setter, rendiamo Transaction IMMUTABILE dopo la creazione)
    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Metodo tripolare: definisce il segno del valore nella tabella.
     * Sottoclassi (Income o Expense) potranno implementare un comportamento differente
     * se necessario (polimorfismo).
     *
     * @return valore numerico con segno (+ per entrate, - per uscite).
     */
    public abstract BigDecimal signedAmount();

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(description, that.description) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, description, amount);
    }
}
