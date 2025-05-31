package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rappresenta un'entrata nel bilancio.
 */
public class Income extends Transaction {

    /**
     * Costruttore di Income.
     *
     * @param date        Data dell'entrata
     * @param description Descrizione dell'entrata
     * @param amount      Ammontare (positivo)
     */
    public Income(LocalDate date, String description, BigDecimal amount) {
        super(date, description, amount);
    }

    /**
     * Per un'entrata, il valore "signed" è positivo.
     */
    @Override
    public BigDecimal signedAmount() {
        return getAmount();
    }
}
