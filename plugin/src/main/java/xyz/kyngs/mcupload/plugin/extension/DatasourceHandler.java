package xyz.kyngs.mcupload.plugin.extension;

import org.gradle.api.Action;
import xyz.kyngs.mcupload.plugin.extension.datasources.FileDatasource;
import xyz.kyngs.mcupload.plugin.extension.datasources.ManualDatasource;

public class DatasourceHandler {

    private Datasource datasource;

    public void manual(Action<ManualDatasource> action) {
        var manual = new ManualDatasource();
        action.execute(manual);
        datasource = manual;
    }

    public void file(Action<FileDatasource> action) {
        var file = new FileDatasource();
        action.execute(file);
        datasource = file;
    }

    public Datasource getDatasource() {
        return datasource;
    }
}
