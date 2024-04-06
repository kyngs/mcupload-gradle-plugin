package xyz.kyngs.mcupload.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;
import xyz.kyngs.mcupload.plugin.extension.Platform;

public class MCReadmeSyncTask extends MCTask {
    @TaskAction
    public void run() throws Exception {
        var datasource = prepDatasource();

        if (datasource == null) {
            return;
        }

        for (Platform platform : extension.getPlatformHandler().getPlatforms()) {
            getLogger().lifecycle("Syncing Readme to " + platform.getName());
            try {
                platform.syncReadme(extension, this, datasource, getProject());
            } catch (Exception e) {
                if (extension.swallowErrors) {
                    getLogger().error("Error while syncing Readme to {}", platform.getName(), e);
                } else {
                    throw e;
                }
            }
        }
    }
}
