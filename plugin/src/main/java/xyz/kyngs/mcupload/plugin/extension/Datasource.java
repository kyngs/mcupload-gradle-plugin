package xyz.kyngs.mcupload.plugin.extension;

import org.gradle.api.Project;

public interface Datasource {

    String getVersionName();

    String getChangelog();

    String getName();

    String getReadMe();

    default String getVersion(Project project) {
        return project.getVersion().toString();
    }

    void fetch() throws Exception;

}
