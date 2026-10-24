package takanayagi;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public abstract class PropertiesGeneratorExtension {
    // Path to the input CSV file
    public abstract RegularFileProperty getCsvFile();

    // Prefix for the output properties files (e.g., "Resources_")
    public abstract Property<String> getPropertiesPrefix();

    // Path to the output directory for the generated properties files
    public abstract DirectoryProperty getOutputDir();

}
