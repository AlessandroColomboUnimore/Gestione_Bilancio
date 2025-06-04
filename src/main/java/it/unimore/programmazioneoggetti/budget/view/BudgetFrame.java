package it.unimore.programmazioneoggetti.budget.view;

import it.unimore.programmazioneoggetti.budget.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Frame principale dell'applicazione Gestione Bilancio.
 * Contiene un JTable per visualizzare le transazioni, un form per aggiungerle/modificarle,
 * campi “Da/A data” per il filtro, e un menu “File” per Salva/Carica/Esporta.
 */
public class BudgetFrame extends JFrame {

    // TableModel personalizzato
    private final TransactionTableModel tableModel;
    private final JTable             table;

    // Campi del form (inserimento/modifica)
    private final JTextField      dateField;        // formato: dd/MM/yyyy
    private final JTextField      descriptionField;
    private final JTextField      amountField;      // formato: "1234.56"
    private final JComboBox<String> typeCombo;     // "Income" o "Expense"

    // Pulsanti di azione principali
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;

    // Label per saldo totale
    private final JLabel balanceLabel;

    // Campo di ricerca (descrizione)
    private final JTextField searchField;
    private final JButton    searchButton;

    // Nuovi campi per filtro “Da/A data”
    private final JTextField fromDateField;
    private final JTextField toDateField;
    private final JButton    filterDateButton;

    // Voci di Menu per Salva/Carica/Esporta
    private final JMenuItem saveCsvItem;
    private final JMenuItem loadCsvItem;
    private final JMenuItem loadTxtItem;
    private final JMenuItem exportTxtItem;

    public BudgetFrame() {
        super("Gestione Bilancio - Programmazione a Oggetti (UNIMORE)");

        // ---- crea JMenuBar con un menu “File” ----
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        saveCsvItem   = new JMenuItem("Salva CSV");
        loadCsvItem   = new JMenuItem("Carica CSV");
        loadTxtItem   = new JMenuItem("Carica TXT");
        exportTxtItem = new JMenuItem("Esporta TXT");

        fileMenu.add(saveCsvItem);
        fileMenu.add(loadCsvItem);
        fileMenu.add(loadTxtItem);
        fileMenu.add(exportTxtItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        // -----------------------------------------

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // -------------------------------
        // 1) CREAZIONE DELLA TABELLA
        // -------------------------------
        tableModel = new TransactionTableModel();
        table      = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // -------------------------------
        // 2) CREAZIONE DEL FORM IN ALTO
        // -------------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // RIGA 0: Data
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Data (gg/MM/aaaa):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        formPanel.add(dateField, gbc);

        // RIGA 1: Descrizione
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);

        // RIGA 2: Importo
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Importo:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);

        // RIGA 3: Tipo
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        formPanel.add(typeCombo, gbc);

        // RIGA 4: Cerca (descrizione)
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Cerca (descrizione):"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(15);
        formPanel.add(searchField, gbc);
        gbc.gridx = 2;
        searchButton = new JButton("Cerca");
        formPanel.add(searchButton, gbc);

        // RIGA 5: “Da data”
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Da data (gg/MM/aaaa):"), gbc);
        gbc.gridx = 1;
        fromDateField = new JTextField(10);
        formPanel.add(fromDateField, gbc);

        // RIGA 6: “A data”
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("A data (gg/MM/aaaa):"), gbc);
        gbc.gridx = 1;
        toDateField = new JTextField(10);
        formPanel.add(toDateField, gbc);

        // Pulsante “Filtra” alla destra di “A data”
        gbc.gridx = 2;
        filterDateButton = new JButton("Filtra");
        formPanel.add(filterDateButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // -------------------------------
        // 3) CREAZIONE DEI PULSANTI IN BASSO
        // -------------------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addButton    = new JButton("Aggiungi");
        editButton   = new JButton("Modifica");
        deleteButton = new JButton("Elimina");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Etichetta per saldo totale
        balanceLabel = new JLabel("Saldo: 0.00");
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD, 14f));
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(balanceLabel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Inizializza i campi del form con valori di default (es. data odierna).
     *
     * @param date        stringa data in formato dd/MM/yyyy
     * @param description testo descrizione
     * @param amount      testo importo
     * @param type        stringa “Income” o “Expense”
     */
    public void setDefaultFormData(String date, String description, String amount, String type) {
        dateField.setText(date);
        descriptionField.setText(description);
        amountField.setText(amount);
        typeCombo.setSelectedItem(type);
    }

    /**
     * Ritorna il testo immesso nell'input 'dateField'.
     */
    public String getDateFieldText() {
        return dateField.getText();
    }

    public String getDescriptionFieldText() {
        return descriptionField.getText();
    }

    public String getAmountFieldText() {
        return amountField.getText();
    }

    public String getSelectedType() {
        return (String) typeCombo.getSelectedItem();
    }

    /**
     * Pulisce completamente il form (campi vuoti).
     */
    public void clearForm() {
        dateField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        typeCombo.setSelectedIndex(0);
    }

    /**
     * Aggiorna la JTable con una nuova lista di transazioni.
     *
     * @param transactions lista di {@link Transaction} da mostrare
     */
    public void refreshTable(List<Transaction> transactions) {
        tableModel.setTransactions(transactions);
    }

    /**
     * Ritorna l'indice di riga selezionato nella tabella.
     */
    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    /**
     * Restituisce la Transaction corrispondente a quella riga (o null se non valida).
     *
     * @param rowIndex indice riga
     * @return {@link Transaction} o null se rowIndex invalido
     */
    public Transaction getTransactionAt(int rowIndex) {
        return tableModel.getTransactionAt(rowIndex);
    }

    /**
     * Aggiorna l'etichetta del saldo totale.
     *
     * @param text nuovo testo da mostrare
     */
    public void updateBalanceLabel(String text) {
        balanceLabel.setText(text);
    }

    // Metodi per registrare gli ActionListener (controller)

    public void addAddButtonListener(java.awt.event.ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addEditButtonListener(java.awt.event.ActionListener listener) {
        editButton.addActionListener(listener);
    }

    public void addDeleteButtonListener(java.awt.event.ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addFilterDateButtonListener(ActionListener listener) {
        filterDateButton.addActionListener(listener);
    }

    public String getSearchFieldText() {
        return searchField.getText();
    }

    public String getFromDateFieldText() {
        return fromDateField.getText();
    }

    public String getToDateFieldText() {
        return toDateField.getText();
    }

    /**
     * Registra l'ActionListener della voce di menu “Salva CSV”.
     */
    public void addSaveCsvMenuListener(ActionListener listener) {
        saveCsvItem.addActionListener(listener);
    }

    /**
     * Registra l'ActionListener della voce di menu “Carica CSV”.
     */
    public void addLoadCsvMenuListener(ActionListener listener) {
        loadCsvItem.addActionListener(listener);
    }

    /**
     * Registra l'ActionListener della voce di menu “Carica TXT”.
     */
    public void addLoadTxtMenuListener(ActionListener listener) {
        loadTxtItem.addActionListener(listener);
    }

    /**
     * Registra l'ActionListener della voce di menu “Esporta TXT”.
     */
    public void addExportTxtMenuListener(ActionListener listener) {
        exportTxtItem.addActionListener(listener);
    }
}
