package it.unimore.programmazioneoggetti.budget.view;

import it.unimore.programmazioneoggetti.budget.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionListener;

/**
 * Frame principale dell'applicazione Gestione Bilancio.
 * Contiene un {@link JTable} per visualizzare le transazioni e un form per
 * aggiungerle, modificarle e ricercarle. Espone metodi per registrare
 * gli {@link ActionListener} relativi ai bottoni e alle voci di menu.
 */
public class BudgetFrame extends JFrame {

    private final TransactionTableModel tableModel;
    private final JTable table;

    private final JTextField dateField;
    private final JTextField descriptionField;
    private final JTextField amountField;
    private final JComboBox<String> typeCombo;

    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;

    private final JLabel balanceLabel;

    private final JTextField searchField;
    private final JButton searchButton;

    private final JMenuItem saveCsvItem;
    private final JMenuItem loadCsvItem;
    private final JMenuItem loadTxtItem;
    private final JMenuItem exportTxtItem;

    /**
     * Costruisce la finestra principale, crea la barra dei menu “File” per
     * salvataggio e caricamento, il form, la tabella e i bottoni in basso.
     */
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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1) Tabella delle transazioni (CENTER)
        tableModel = new TransactionTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 2) Form in alto (NORTH): data, descrizione, importo, tipo, ricerca
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Data
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Data (gg/MM/aaaa):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        formPanel.add(dateField, gbc);

        // Descrizione
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);

        // Importo
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Importo:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);

        // Tipo (Income/Expense)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        formPanel.add(typeCombo, gbc);

        // Ricerca per descrizione
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Cerca (descrizione):"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(15);
        formPanel.add(searchField, gbc);
        gbc.gridx = 2;
        searchButton = new JButton("Cerca");
        formPanel.add(searchButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // 3) Bottoni in basso (SOUTH): Aggiungi, Modifica, Elimina, Saldo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addButton = new JButton("Aggiungi");
        editButton = new JButton("Modifica");
        deleteButton = new JButton("Elimina");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        balanceLabel = new JLabel("Saldo: 0.00");
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD, 14f));
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(balanceLabel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Imposta la data, la descrizione, l’importo e il tipo nel form. */
    public void setDefaultFormData(String date, String description, String amount, String type) {
        dateField.setText(date);
        descriptionField.setText(description);
        amountField.setText(amount);
        typeCombo.setSelectedItem(type);
    }

    /** @return il testo del campo data (dd/MM/yyyy). */
    public String getDateFieldText() {
        return dateField.getText();
    }

    /** @return il testo del campo descrizione. */
    public String getDescriptionFieldText() {
        return descriptionField.getText();
    }

    /** @return il testo del campo importo. */
    public String getAmountFieldText() {
        return amountField.getText();
    }

    /** @return il tipo selezionato ("Income" o "Expense"). */
    public String getSelectedType() {
        return (String) typeCombo.getSelectedItem();
    }

    /** Pulisce tutti i campi del form. */
    public void clearForm() {
        dateField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        typeCombo.setSelectedIndex(0);
    }

    /** Aggiorna la tabella con la lista di transazioni fornita. */
    public void refreshTable(List<Transaction> transactions) {
        tableModel.setTransactions(transactions);
    }

    /** @return l’indice della riga attualmente selezionata nella tabella. */
    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    /** @return la {@link Transaction} corrispondente alla riga indicata. */
    public Transaction getTransactionAt(int rowIndex) {
        return tableModel.getTransactionAt(rowIndex);
    }

    /** Imposta il testo dell’etichetta del saldo totale. */
    public void updateBalanceLabel(String text) {
        balanceLabel.setText(text);
    }

    // ================= Metodi per registrare gli ActionListener =================

    /** Registra l’ActionListener del pulsante “Aggiungi”. */
    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    /** Registra l’ActionListener del pulsante “Modifica”. */
    public void addEditButtonListener(ActionListener listener) {
        editButton.addActionListener(listener);
    }

    /** Registra l’ActionListener del pulsante “Elimina”. */
    public void addDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    /** Registra l’ActionListener del pulsante “Cerca”. */
    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    /** @return il testo inserito nel campo di ricerca. */
    public String getSearchFieldText() {
        return searchField.getText();
    }

    /** Registra l’ActionListener della voce di menu “Salva CSV”. */
    public void addSaveCsvMenuListener(ActionListener listener) {
        saveCsvItem.addActionListener(listener);
    }

    /** Registra l’ActionListener della voce di menu “Carica CSV”. */
    public void addLoadCsvMenuListener(ActionListener listener) {
        loadCsvItem.addActionListener(listener);
    }

    /** Registra l’ActionListener della voce di menu “Carica TXT”. */
    public void addLoadTxtMenuListener(ActionListener listener) {
        loadTxtItem.addActionListener(listener);
    }

    /** Registra l’ActionListener della voce di menu “Esporta TXT”. */
    public void addExportTxtMenuListener(ActionListener listener) {
        exportTxtItem.addActionListener(listener);
    }
}
