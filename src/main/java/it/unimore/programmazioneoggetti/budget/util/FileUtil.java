package it.unimore.programmazioneoggetti.budget.util;

import it.unimore.programmazioneoggetti.budget.model.Transaction;
import it.unimore.programmazioneoggetti.budget.model.Income;
import it.unimore.programmazioneoggetti.budget.model.Expense;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe di utilità per il salvataggio e il caricamento delle transazioni.
 * Supporta formati CSV e testo (tab-delimited).
 */
public class FileUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Esporta la lista di transazioni in formato CSV.
     * Ogni riga contiene: data, descrizione (con eventuali virgolette scappate), importo, tipo.
     *
     * @param transactions lista di {@link Transaction} da salvare
     * @param file         file di destinazione (estensione .csv consigliata)
     * @throws IOException se si verifica un errore di I/O
     */
    public static void saveAsCSV(List<Transaction> transactions, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Data,Descrizione,Ammontare,Tipo");
            for (Transaction t : transactions) {
                String tipo = (t instanceof Income) ? "Income" : "Expense";
                String descEscaped = t.getDescription().replace("\"", "\\\"");
                writer.printf("%s,%s,%s,%s%n",
                        t.getDate().format(DATE_FORMATTER),
                        descEscaped,
                        t.getAmount().toString(),
                        tipo);
            }
        }
    }

    /**
     * Carica le transazioni da un file CSV precedentemente salvato con {@code saveAsCSV}.
     *
     * @param file file CSV di origine
     * @return lista di {@link Transaction} lette dal file
     * @throws IOException se si verifica un errore di I/O
     */
    public static List<Transaction> loadFromCSV(File file) throws IOException {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // salta l’intestazione
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length < 4) continue;
                LocalDate date = LocalDate.parse(tokens[0], DATE_FORMATTER);
                String desc = tokens[1].replace("\\\"", "\"");
                BigDecimal amount = new BigDecimal(tokens[2]);
                String tipo = tokens[3];
                if ("Income".equals(tipo)) {
                    list.add(new Income(date, desc, amount));
                } else {
                    list.add(new Expense(date, desc, amount));
                }
            }
        }
        return list;
    }

    /**
     * Esporta la lista di transazioni in formato testo (campi separati da tab).
     *
     * @param transactions lista di {@link Transaction} da salvare
     * @param file         file di destinazione (estensione .txt consigliata)
     * @throws IOException se si verifica un errore di I/O
     */
    public static void saveAsText(List<Transaction> transactions, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Transaction t : transactions) {
                String tipo = (t instanceof Income) ? "Income" : "Expense";
                writer.printf("%s\t%s\t%s\t%s%n",
                        t.getDate().format(DATE_FORMATTER),
                        t.getDescription(),
                        t.getAmount().toString(),
                        tipo);
            }
        }
    }

    /**
     * Carica le transazioni da un file di testo tab-delimited.
     * Ogni riga deve contenere quattro campi: data, descrizione, importo, tipo.
     *
     * @param file file di origine (testo tab-delimited)
     * @return lista di {@link Transaction} lette dal file
     * @throws IOException se si verifica un errore di I/O
     */
    public static List<Transaction> loadFromText(File file) throws IOException {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length < 4) continue;
                LocalDate date = LocalDate.parse(tokens[0], DATE_FORMATTER);
                String desc = tokens[1];
                BigDecimal amount = new BigDecimal(tokens[2]);
                String tipo = tokens[3];
                if ("Income".equals(tipo)) {
                    list.add(new Income(date, desc, amount));
                } else {
                    list.add(new Expense(date, desc, amount));
                }
            }
        }
        return list;
    }
}
