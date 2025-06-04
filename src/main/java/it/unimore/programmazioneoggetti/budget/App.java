package it.unimore.programmazioneoggetti.budget;

import it.unimore.programmazioneoggetti.budget.controller.BudgetController;
import it.unimore.programmazioneoggetti.budget.model.BudgetManager;
import it.unimore.programmazioneoggetti.budget.view.BudgetFrame;

import javax.swing.SwingUtilities;

/**
 * Classe principale che avvia l'applicazione Gestione Bilancio.
 * Crea il model, la view e il controller, e lancia il tutto sul
 * thread per l'interfaccia grafica.
 */
public class App {

    /** Punto di ingresso dell’applicazione. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BudgetManager model = new BudgetManager();
            BudgetFrame view = new BudgetFrame();
            view.setVisible(true);
            new BudgetController(model, view);
        });
    }
}
