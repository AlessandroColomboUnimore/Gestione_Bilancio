package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Classe astratta che rappresenta una voce generica di bilancio.
 * Ogni transazione ha data, descrizione e importo (positivo).
 * Le sottoclassi {@link Income} e {@link Expense} definiscono
 * come calcolare {@link #signedAmount()} (segno positivo o negativo).
 */
public abstract class Transaction {

    private final LocalDate date;
    private final String description;
    private final BigDecimal amount;

    /**
     * Costruisce una nuova transazione.
     *
     * @param date        data della transazione (non null)
     * @param description descrizione (non nulla, non vuota)
     * @param amount      importo (BigDecimal, positivo)
     * @throws IllegalArgumentException se uno dei parametri non è valido
     */
    public Transaction(LocalDate date, String description, BigDecimal amount) {
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

    /** @return la data della transazione */
    public LocalDate getDate() {
        return date;
    }

    /** @return la descrizione della transazione */
    public String getDescription() {
        return description;
    }

    /** @return l'importo (valore positivo) */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Definisce il valore “con segno” della transazione:
     * sottoclassi implementano logiche diverse (entrate positive, uscite negative).
     *
     * @return importo con segno
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
