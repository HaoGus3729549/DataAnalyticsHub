package com.haogus.backend;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;

public class CSVImporter {
    public CSVImporter(String csvFilePath, Posts posts) {
        try (FileReader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord record : csvParser) {
//            	[ID ,  Author ,  Content,  Likes ,  Shares ,  Date] The head
                posts.addPost(new Post(
                        Integer.parseInt(record.get("ID")),
                        record.get("Author"),
                        record.get("Content"),
                        Integer.parseInt(record.get("Likes")),
                        Integer.parseInt(record.get("Shares")), record.get("Date")));
            }
        } catch (IOException e) {
//            e.printStackTrace();

        }

    }
}
