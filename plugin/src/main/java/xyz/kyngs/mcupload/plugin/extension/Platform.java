package xyz.kyngs.mcupload.plugin.extension;

import org.gradle.api.Project;
import xyz.kyngs.mcupload.plugin.MCReadmeSyncTask;
import xyz.kyngs.mcupload.plugin.MCReleaseTask;

import java.io.File;

public interface Platform {
    void upload(MCUploadExtension extension, MCReleaseTask task, Datasource datasource, Project project, File file) throws Exception;

    void syncReadme(MCUploadExtension extension, MCReadmeSyncTask task, Datasource datasource, Project project) throws Exception;

    String getName();
}
