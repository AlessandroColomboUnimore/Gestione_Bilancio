package it.unimore.programmazioneoggetti.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rappresenta un’entrata nel bilancio. Estende {@link Transaction} e
 * sovrascrive il metodo {@code signedAmount()} per restituire l’importo
 * con segno positivo.
 */
public class Income extends Transaction {

    /**
     * Costruisce una nuova entrata.
     *
     * @param date        data dell’entrata
     * @param description descrizione dell’entrata
     * @param amount      importo (valore positivo)
     */
    public Income(LocalDate date, String description, BigDecimal amount) {
        super(date, description, amount);
    }

    /**
     * Ritorna l’importo positivo per indicare un’entrata.
     *
     * @return {@code getAmount()} (valore positivo)
     */
    @Override
    public BigDecimal signedAmount() {
        return getAmount();
    }
}
