package takanayagi;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public abstract class GeneratePropertiesTask extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getCsvFile();

    @Input
    public abstract Property<String> getPropertiesPrefix();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void generate() throws IOException {
        Path csvPath = getCsvFile().get().getAsFile().toPath();
        String prefix = getPropertiesPrefix().get();
        String suffix = ".properties";

        Path outputDir = getOutputDir().get().getAsFile().toPath();
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // Read all CSV data
        List<List<String>> csvData = readCsv(csvPath);
        if (csvData.isEmpty() || csvData.get(0).size() < 2) {
            getLogger().error("Error: The CSV file is empty or missing language columns.");
            return;
        }

        // Extract column headers (languages)
        List<String> columnHeaders = csvData.get(0);
        // Start from index 1 because index 0 is "key"
        List<String> languages = columnHeaders.subList(1, columnHeaders.size());

        // Prepare writers for each language
        Map<String, List<String>> propertiesLines = new HashMap<>();
        for (String lang : languages) {
            propertiesLines.put(lang, new ArrayList<>());
        }

        // Process each row
        for (int i = 1; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);
            if (row.isEmpty()) {
                continue;
            }

            String key = row.get(0).trim();

            for (int colIndex = 1; colIndex < columnHeaders.size(); colIndex++) {
                if (colIndex >= row.size()) {
                    break;
                }
                String lang = columnHeaders.get(colIndex);
                String value = row.get(colIndex);
                if (key.startsWith("#COMMENT")) {
                    if (value != null && !value.isEmpty()) {
                        // Handle comment lines
                        propertiesLines.get(lang).add(value);
                    } // else skip comment lines with no value
                } else if (key.isEmpty() || key.startsWith("#") || key.startsWith("!")) {
                    // Empty line or comment line, add as is.
                    propertiesLines.get(lang).add(key);
                } else {
                    // Only write if the translation exists
                    if (lang.equals("en") || (value != null && !value.isEmpty())) {
                        propertiesLines.get(lang).add(key + "=" + value);
                    }
                }
            }
        }

        // Write out the properties files
        for (String lang : languages) {
            String fileName = prefix + lang + suffix;
            Path outputPath = outputDir.resolve(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE)) {
                for (String line : propertiesLines.get(lang)) {
                    writer.write(line);
                    writer.write('\n'); // Use '\n' for line breaks to maintain consistency across platforms
                }
            }
            getLogger().lifecycle("Generated: " + outputPath.toAbsolutePath() + " (" + propertiesLines.get(lang).size() + " keys)");
        }
    }

    private List<List<String>> readCsv(Path path) throws IOException {
        List<List<String>> records = new ArrayList<>();
        List<String> currentRecord = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int c;
            boolean isFirstChar = true;

            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                // Strip BOM (Byte Order Mark) if present at the very beginning
                if (isFirstChar) {
                    isFirstChar = false;
                    if (ch == '\ufeff') continue;
                }

                if (inQuotes) {
                    if (ch == '"') {
                        reader.mark(1);
                        int next = reader.read();
                        if (next == '"') {
                            currentField.append('"');
                        } else {
                            inQuotes = false;
                            if (next != -1) reader.reset();
                        }
                    } else {
                        currentField.append(ch);
                    }
                } else {
                    if (ch == '"') {
                        inQuotes = true;
                    } else if (ch == ',') {
                        currentRecord.add(currentField.toString());
                        currentField.setLength(0);
                    } else if (ch == '\n' || ch == '\r') {
                        if (ch == '\r') {
                            reader.mark(1);
                            int next = reader.read();
                            if (next != '\n' && next != -1) reader.reset();
                        }
                        currentRecord.add(currentField.toString());
                        records.add(currentRecord);
                        currentRecord = new ArrayList<>();
                        currentField.setLength(0);
                    } else {
                        currentField.append(ch);
                    }
                }
            }
        }
        // Add the last field/record if the file doesn't end with a newline
        if (currentField.length() > 0 || !currentRecord.isEmpty()) {
            currentRecord.add(currentField.toString());
            records.add(currentRecord);
        }
        return records;
    }
}
