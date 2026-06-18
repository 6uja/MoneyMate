package service;

import dao.TransactionDao;
import model.Transaction;
import model.TransactionType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvService {

    private final TransactionDao transactionDao;

    public CsvService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void importCsv(File file) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);

                if (values.length < 5) {
                    continue;
                }

                Transaction transaction = new Transaction(
                        0,
                        values[0].trim(),
                        TransactionType.valueOf(values[1].trim()),
                        values[2].trim(),
                        Integer.parseInt(values[3].trim()),
                        values[4].trim()
                );

                transactionDao.insert(transaction);
            }

        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("CSV 가져오기 실패", e);
        }
    }

    public void exportCsv(File file) {
        List<Transaction> transactions = transactionDao.findAll();

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            bw.write("date,type,category,amount,memo");
            bw.newLine();

            for (Transaction t : transactions) {
                bw.write(String.join(",",
                        t.getDate(),
                        t.getType().name(),
                        t.getCategory(),
                        String.valueOf(t.getAmount()),
                        t.getMemo()
                ));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("CSV 내보내기 실패", e);
        }
    }
}
