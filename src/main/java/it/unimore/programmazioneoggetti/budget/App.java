package it.unimore.programmazioneoggetti.budget;

import javax.swing.SwingUtilities;
import it.unimore.programmazioneoggetti.budget.model.Income;
import it.unimore.programmazioneoggetti.budget.model.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.SwingUtilities;

/**
 * Classe principale che avvia l'applicazione Gestione Bilancio.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Gestione Bilancio avviata");

            // Test di istanziazione di Income ed Expense
            Income i = new Income(LocalDate.now(), "Stipendio", new BigDecimal("1500.00"));
            Expense e = new Expense(LocalDate.now(), "Affitto", new BigDecimal("600.00"));

            System.out.println(i); // dovrebbe stampare dati di Income
            System.out.println(e); // idem per Expense

            System.out.println("Signed Income: " + i.signedAmount());
            System.out.println("Signed Expense: " + e.signedAmount());

            // Somma algebrica di un bilancio semplificato
            BigDecimal totale = i.signedAmount().add(e.signedAmount());
            System.out.println("Saldo (i - e): " + totale);
        });
}
}
