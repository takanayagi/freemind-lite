package takanayagi;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PropertiesGeneratorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Register the extension to allow users to configure the plugin in build.gradle
        PropertiesGeneratorExtension extension = project.getExtensions()
            .create("propertiesGenerator", PropertiesGeneratorExtension.class);

        extension.getOutputDir().convention(
            project.getLayout().getBuildDirectory().dir("generated/properties"));

        // Register the custom task and map the extension properties to the task inputs
        project.getTasks().register("generateProperties", GeneratePropertiesTask.class, task -> {
            task.setGroup("build");
            task.setDescription("Generates .properties files from a CSV file.");
            task.getCsvFile().set(extension.getCsvFile());
            task.getPropertiesPrefix().set(extension.getPropertiesPrefix());
            task.getOutputDir().set(extension.getOutputDir());
        });
    }
}
