package com.haogus.backend;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    public CSVExporter(List<Post> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Loop through the data array and write each element as a CSV record
            String elm = "ID,"
                    + "Author,"
                    + "Content,"
                    + "Likes,"
                    + "Shares,"
                    + "Date\n";
            writer.append(elm);
            data.forEach(element -> {
                String elem = element.getId() + ","
                        + element.getAuthor() + ","
                        + element.getContent() + ","
                        + element.getLikes() + ","
                        + element.getShares() + ","
                        + element.getDateTime() + "\n";
                try {
                    writer.append(elem);
                } catch (IOException e) {
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
