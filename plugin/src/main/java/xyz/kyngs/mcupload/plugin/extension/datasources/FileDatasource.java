package xyz.kyngs.mcupload.plugin.extension.datasources;

import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.mcupload.plugin.extension.Datasource;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class FileDatasource implements Datasource {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDatasource.class);

    private final Project project;

    public String readmeFile;
    public String changelogFile;

    private String readme;
    private String changelog;
    private String versionName;

    public FileDatasource(Project project) {
        this.project = project;
    }

    @Override
    public String getVersionName() {
        return versionName;
    }

    @Override
    public String getChangelog() {
        return changelog;
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getReadMe() {
        return readme;
    }

    @Override
    public void fetch() throws Exception {
        if (readmeFile == null) {
            LOGGER.warn("ReadMe file is not set!");
            this.readme = "";
        } else {
            this.readme = String.join("\n", readMarkdownFile(readmeFile, "ReadMe"));
        }

        if (changelogFile == null) {
            LOGGER.warn("Changelog file is not set!");
            this.changelog = "";
            this.versionName = "Untitled";
        } else {
            var changelog = readMarkdownFile(changelogFile, "Changelog");
            if (changelog.isEmpty()) {
                LOGGER.warn("Changelog file is empty!");
                this.changelog = "";
                this.versionName = "Untitled";
                return;
            }
            this.versionName = changelog.get(0);
            changelog.remove(0);
            this.changelog = String.join("\n", changelog);
        }
    }

    private List<String> readMarkdownFile(String path, String name) throws IOException {
        return readMarkdownFile(obtainFile(path, name), name);
    }

    private List<String> readMarkdownFile(File file, String name) throws IOException {
        if (!file.getName().endsWith(".md")) {
            throw new IllegalArgumentException(name + " file is not a markdown file!");
        }
        try (var reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    private File obtainFile(String path, String name) {
        if (path == null) {
            throw new IllegalArgumentException(name + " file path cannot be null!");
        }
        var file = new File(project.getRootDir(), path);

        if (!file.exists()) {
            throw new IllegalArgumentException(name + " file does not exist! Searched path: " + file.getAbsolutePath());
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException(name + " file is not a file!");
        }

        if (!file.canRead()) {
            throw new IllegalArgumentException(name + " file cannot be read!");
        }

        return file;
    }
}
