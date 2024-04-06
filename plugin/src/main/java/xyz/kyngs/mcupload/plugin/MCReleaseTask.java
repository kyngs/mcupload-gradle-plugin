package xyz.kyngs.mcupload.plugin;

import org.gradle.api.tasks.TaskAction;
import xyz.kyngs.mcupload.plugin.extension.Platform;

public class MCReleaseTask extends MCTask {
    @TaskAction
    public void run() throws Exception {
        var datasource = prepDatasource();

        if (datasource == null) {
            return;
        }
        var file = extension.resolveFile();
        if (file == null) {
            throw new IllegalStateException("File is not set!");
        }
        for (Platform platform : extension.getPlatformHandler().getPlatforms()) {
            getLogger().lifecycle("Uploading to " + platform.getName());
            try {
                platform.upload(extension, this, datasource, getProject(), file);
            } catch (Exception e) {
                if (extension.swallowErrors) {
                    getLogger().error("Error while uploading to {}", platform.getName(), e);
                } else {
                    throw e;
                }
            }
        }
    }

}
