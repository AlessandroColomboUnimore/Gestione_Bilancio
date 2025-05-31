package it.unimore.programmazioneoggetti.budget;

import it.unimore.programmazioneoggetti.budget.model.BudgetManager;
import it.unimore.programmazioneoggetti.budget.model.Income;
import it.unimore.programmazioneoggetti.budget.model.Expense;
import it.unimore.programmazioneoggetti.budget.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Classe principale che avvia l'applicazione Gestione Bilancio.
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Gestione Bilancio avviata");

            // 1) Creiamo il BudgetManager
            BudgetManager manager = new BudgetManager();

            // 2) Aggiungiamo un paio di transazioni di test
            manager.addTransaction(new Income(LocalDate.now(), "Stipendio", new BigDecimal("1500.00")));
            manager.addTransaction(new Expense(LocalDate.now(), "Affitto", new BigDecimal("600.00")));
            manager.addTransaction(new Expense(LocalDate.now(), "Spesa supermercato", new BigDecimal("100.50")));

            // 3) Stampiamo tutte le transazioni
            System.out.println("--- Tutte le transazioni ---");
            manager.getAllTransactions().forEach(System.out::println);

            // 4) Stampiamo il saldo totale
            System.out.println("Saldo totale: " + manager.calculateTotalBalance());

            // 5) Filtriamo per data odierna
            List<Transaction> today = manager.getByDate(LocalDate.now());
            System.out.println("--- Transazioni di oggi ---");
            today.forEach(System.out::println);

            // 6) Test di rimozione
            if (today.size() > 1) {
                Transaction toRemove = today.get(1); // rimuoviamo la seconda transazione
                manager.removeTransaction(toRemove);
                System.out.println("Dopo rimozione di: " + toRemove);
                System.out.println("Saldo aggiornato: " + manager.calculateTotalBalance());
            }
        });
    }
}
