package xyz.kyngs.mcupload.plugin.extension;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;

public class MCUploadExtension {
    private final Project project;
    /**
     * If true, errors will be logged and the build will continue. The build will fail if otherwise.
     */
    public boolean swallowErrors = false;
    public Object file;
    private PlatformHandler platformHandler;
    private DatasourceHandler datasourceHandler;

    public MCUploadExtension(Project project) {
        this.project = project;
    }

    /**
     * @return The platform handler.
     */
    public PlatformHandler getPlatformHandler() {
        return platformHandler;
    }

    /**
     * @return The datasource handler.
     */
    public DatasourceHandler getDatasourceHandler() {
        return datasourceHandler;
    }

    /**
     * Allows adding platforms to upload to.
     *
     * @param action The action to execute.
     */
    public void platforms(Action<PlatformHandler> action) {
        platformHandler = new PlatformHandler();
        action.execute(platformHandler);
    }

    /**
     * Allows adding a datasource to read version data from.
     *
     * @param action The action to execute.
     */
    public void datasource(Action<DatasourceHandler> action) {
        datasourceHandler = new DatasourceHandler(project);
        action.execute(datasourceHandler);
    }

    public File resolveFile() {
        if (file == null) {
            return null;
        }

        if (file instanceof File casted) {
            return casted;
        } else if (file instanceof AbstractArchiveTask task) {
            return task.getArchiveFile().get().getAsFile();
        } else if (file instanceof TaskProvider<?> provider) {
            Object provided = provider.get();

            return provider.flatMap(x -> {
                if (provided instanceof AbstractArchiveTask task) {
                    return task.getArchiveFile();
                }
                return project.getLayout().file(project.provider(() -> project.file(provider)));
            }).get().getAsFile();
        }

        return project.getLayout().file(project.provider(() -> project.file(file))).get().getAsFile();
    }

}
