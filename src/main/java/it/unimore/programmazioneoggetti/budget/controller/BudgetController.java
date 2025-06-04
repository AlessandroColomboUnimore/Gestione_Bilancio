package it.unimore.programmazioneoggetti.budget.controller;

import it.unimore.programmazioneoggetti.budget.model.BudgetManager;
import it.unimore.programmazioneoggetti.budget.model.Income;
import it.unimore.programmazioneoggetti.budget.model.Transaction;
import it.unimore.programmazioneoggetti.budget.model.Expense;
import it.unimore.programmazioneoggetti.budget.view.BudgetFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import it.unimore.programmazioneoggetti.budget.util.FileUtil;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controller per l'applicazione Gestione Bilancio.
 * Si occupa di collegare la view (BudgetFrame) con il model (BudgetManager),
 * gestendo gli eventi dell'interfaccia grafica e aggiornando la logica di business.
 */
public class BudgetController {

    private final BudgetManager model;
    private final BudgetFrame view;

    /** Formatter per parsing e formattazione di date (gg/MM/aaaa). */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Costruisce il controller, inizializza i dati di default nella view
     * e registra tutti gli ActionListener per i bottoni e le voci di menu.
     *
     * @param model istanza di {@link BudgetManager} che tiene traccia delle transazioni
     * @param view  istanza di {@link BudgetFrame} sulla quale si registrano gli eventi
     */
    public BudgetController(BudgetManager model, BudgetFrame view) {
        this.model = model;
        this.view = view;

        // Imposta la data odierna nel form e aggiorna la vista
        String todayStr = LocalDate.now().format(dateFormatter);
        view.setDefaultFormData(todayStr, "", "", "Income");
        refreshView();

        // Registra gli ActionListener
        view.addAddButtonListener(new AddButtonListener());
        view.addEditButtonListener(new EditButtonListener());
        view.addDeleteButtonListener(new DeleteButtonListener());
        view.addSearchButtonListener(new SearchButtonListener());
        view.addSaveCsvMenuListener(new SaveButtonListener());
        view.addLoadCsvMenuListener(new LoadButtonListener());
        view.addLoadTxtMenuListener(new LoadTxtButtonListener());
        view.addExportTxtMenuListener(new ExportTextButtonListener());
    }

    /**
     * Rilegge tutte le transazioni dal model, aggiorna la tabella della view
     * e ricalcola il saldo totale visualizzato.
     */
    private void refreshView() {
        List<Transaction> all = model.getAllTransactions();
        view.refreshTable(all);
        BigDecimal saldo = model.calculateTotalBalance();
        view.updateBalanceLabel("Saldo: " + saldo.toString());
    }

    /**
     * Valida e converte i dati presenti nei campi del form in un oggetto {@link Transaction}.
     * Mostra un dialogo di errore in caso di input non valido.
     *
     * @return l’istanza di {@code Transaction} creata, oppure {@code null} se l’input è invalido
     */
    private Transaction parseFormInput() {
        String dateStr = view.getDateFieldText();
        String description = view.getDescriptionFieldText();
        String amountStr = view.getAmountFieldText();
        String type = view.getSelectedType();

        // 1) Controlla che nessun campo sia vuoto
        if (dateStr.isBlank() || description.isBlank() || amountStr.isBlank()) {
            JOptionPane.showMessageDialog(view,
                    "Tutti i campi devono essere compilati.",
                    "Errore di input",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 2) Parsing della data
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view,
                    "Formato data non valido. Usa gg/MM/aaaa",
                    "Errore di input",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 3) Parsing dell’importo (BigDecimal)
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException("Importo negativo");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view,
                    "Importo non valido. Usa un numero positivo (es. 100.50).",
                    "Errore di input",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 4) Crea l’oggetto Income o Expense in base al tipo selezionato
        if ("Income".equals(type)) {
            return new Income(date, description, amount);
        } else {
            return new Expense(date, description, amount);
        }
    }

    /**
     * Listener per il pulsante “Aggiungi”: legge i valori dal form,
     * crea una transazione e la aggiunge al model, poi aggiorna la vista.
     */
    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Transaction t = parseFormInput();
            if (t != null) {
                model.addTransaction(t);
                refreshView();
                // Ripristina il form con data odierna e campi vuoti
                String todayStr = LocalDate.now().format(dateFormatter);
                view.setDefaultFormData(todayStr, "", "", "Income");
            }
        }
    }

    /**
     * Listener per il pulsante “Modifica”: prende la riga selezionata,
     * popola il form con i dati correnti e, dopo conferma,
     * sostituisce la transazione nel model.
     */
    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getSelectedRowIndex();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(view,
                        "Seleziona prima una riga da modificare.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Recupera la transazione selezionata
            Transaction oldT = view.getTransactionAt(selectedRow);
            String dateStr = oldT.getDate().format(dateFormatter);
            view.setDefaultFormData(dateStr, oldT.getDescription(), oldT.getAmount().toString(),
                    (oldT instanceof Income) ? "Income" : "Expense");

            int response = JOptionPane.showConfirmDialog(view,
                    "Confermi la modifica dei dati nel form?",
                    "Conferma Modifica",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                model.removeTransaction(oldT);
                Transaction newT = parseFormInput();
                if (newT != null) {
                    model.addTransaction(newT);
                } else {
                    // Se input non valido, reinserisce l’oggetto originale
                    model.addTransaction(oldT);
                }
                refreshView();
                String todayStr = LocalDate.now().format(dateFormatter);
                view.setDefaultFormData(todayStr, "", "", "Income");
            }
        }
    }

    /**
     * Listener per il pulsante “Elimina”: rimuove la transazione selezionata
     * dopo richiesta di conferma, poi aggiorna la vista.
     */
    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getSelectedRowIndex();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(view,
                        "Seleziona prima una riga da eliminare.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Transaction toRemove = view.getTransactionAt(selectedRow);
            int response = JOptionPane.showConfirmDialog(view,
                    "Sei sicuro di voler eliminare: " + toRemove.getDescription() + "?",
                    "Conferma Eliminazione",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                model.removeTransaction(toRemove);
                refreshView();
            }
        }
    }

    /**
     * Listener per la voce di menu “Salva CSV”: apre un {@link JFileChooser} per
     * selezionare il file di destinazione, poi salva le transazioni correnti in CSV.
     *
     * @brief Se il file esiste già, chiede conferma all’utente prima di sovrascrivere.
     */
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salva file CSV");
            chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
            int userSelection = chooser.showSaveDialog(view);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                // Assicurati che termini con .csv
                if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
                }

                // Se il file esiste già, chiedi conferma prima di sovrascrivere
                if (fileToSave.exists()) {
                    int overwrite = JOptionPane.showConfirmDialog(
                            view,
                            "Il file \"" + fileToSave.getName() + "\" esiste già.\nVuoi sovrascriverlo?",
                            "Conferma Sovrascrittura",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (overwrite != JOptionPane.YES_OPTION) {
                        // L’utente ha scelto di non sovrascrivere: esci subito
                        return;
                    }
                }

                try {
                    FileUtil.saveAsCSV(model.getAllTransactions(), fileToSave);
                    JOptionPane.showMessageDialog(view, "Salvataggio CSV completato!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            view,
                            "Errore durante il salvataggio: " + ex.getMessage(),
                            "Errore IO",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Listener per la voce di menu “Carica CSV”: apre un {@link JFileChooser} per
     * selezionare un file CSV, lo legge e rimpiazza tutte le transazioni correnti nel model.
     */
    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Apri file CSV");
            chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
            int userSelection = chooser.showOpenDialog(view);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = chooser.getSelectedFile();
                try {
                    List<Transaction> loaded = FileUtil.loadFromCSV(fileToOpen);
                    model.getAllTransactions().forEach(model::removeTransaction);
                    for (Transaction t : loaded) {
                        model.addTransaction(t);
                    }
                    refreshView();
                    JOptionPane.showMessageDialog(view, "Caricamento CSV completato!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view, "Errore durante il caricamento: " + ex.getMessage(),
                            "Errore IO", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Listener per la voce di menu “Esporta TXT”: apre un {@link JFileChooser} per
     * selezionare il file di destinazione, poi salva le transazioni correnti in
     * formato tab-delimited.
     */
    private class ExportTextButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Esporta file di testo");
            chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
            int userSelection = chooser.showSaveDialog(view);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
                }
                try {
                    FileUtil.saveAsText(model.getAllTransactions(), fileToSave);
                    JOptionPane.showMessageDialog(view, "Esportazione testo completata!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view, "Errore durante l'esportazione: " + ex.getMessage(),
                            "Errore IO", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Listener per il pulsante “Cerca”: filtra le transazioni correnti in base
     * al testo inserito nel campo di ricerca e aggiorna la tabella.
     */
    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String raw = view.getSearchFieldText();
            if (raw == null) raw = "";
            String searchKey = raw.trim().toLowerCase();

            List<Transaction> filtered = model.getAllTransactions().stream()
                    .filter(t -> t.getDescription().toLowerCase().contains(searchKey))
                    .collect(java.util.stream.Collectors.toList());

            view.refreshTable(filtered);
        }
    }

    /**
     * Listener per la voce di menu “Carica TXT”: apre un {@link JFileChooser} per
     * selezionare un file di testo tab-delimited, lo legge e rimpiazza tutte le
     * transazioni correnti nel model.
     */
    private class LoadTxtButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Apri file di testo");
            chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
            int userSelection = chooser.showOpenDialog(view);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = chooser.getSelectedFile();
                try {
                    List<Transaction> loaded = FileUtil.loadFromText(fileToOpen);
                    model.getAllTransactions().forEach(model::removeTransaction);
                    for (Transaction t : loaded) {
                        model.addTransaction(t);
                    }
                    refreshView();
                    JOptionPane.showMessageDialog(view, "Caricamento TXT completato!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view,
                            "Errore durante il caricamento: " + ex.getMessage(),
                            "Errore IO", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
