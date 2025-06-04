package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rappresenta una spesa nel bilancio. Estende {@link Transaction} e
 * sovrascrive il metodo {@code signedAmount()} per restituire l’importo
 * con segno negativo.
 */
public class Expense extends Transaction {

    /**
     * Costruisce una nuova spesa.
     *
     * @param date        data della spesa
     * @param description descrizione della spesa
     * @param amount      importo (valore positivo, verrà reso negativo in {@code signedAmount()})
     */
    public Expense(LocalDate date, String description, BigDecimal amount) {
        super(date, description, amount);
    }

    /**
     * Ritorna l’importo negativo per indicare una spesa (uscita).
     *
     * @return valore negativo di {@code getAmount()}
     */
    @Override
    public BigDecimal signedAmount() {
        return getAmount().negate();
    }
}
