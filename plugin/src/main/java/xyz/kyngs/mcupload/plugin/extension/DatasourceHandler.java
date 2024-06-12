package xyz.kyngs.mcupload.plugin.extension;

import org.gradle.api.Action;
import org.gradle.api.Project;
import xyz.kyngs.mcupload.plugin.extension.datasources.FileDatasource;
import xyz.kyngs.mcupload.plugin.extension.datasources.ManualDatasource;

public class DatasourceHandler {

    private final Project project;

    private Datasource datasource;

    public DatasourceHandler(Project project) {
        this.project = project;
    }

    public void manual(Action<ManualDatasource> action) {
        var manual = new ManualDatasource();
        action.execute(manual);
        datasource = manual;
    }

    public void file(Action<FileDatasource> action) {
        var file = new FileDatasource(project);
        action.execute(file);
        datasource = file;
    }

    public Datasource getDatasource() {
        return datasource;
    }
}
