package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rappresenta una spesa nel bilancio.
 */
public class Expense extends Transaction {

    /**
     * Costruttore di Expense.
     *
     * @param date        Data della spesa
     * @param description Descrizione della spesa
     * @param amount      Ammontare (positivo) ma tornerà come negativo in signedAmount()
     */
    public Expense(LocalDate date, String description, BigDecimal amount) {
        super(date, description, amount);
    }

    /**
     * Per una spesa, il valore "signed" deve essere negativo
     * (es: -100.00).
     */
    @Override
    public BigDecimal signedAmount() {
        return getAmount().negate();
    }
}
