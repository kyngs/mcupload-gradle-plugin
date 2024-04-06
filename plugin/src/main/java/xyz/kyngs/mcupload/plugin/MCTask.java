package xyz.kyngs.mcupload.plugin;

import org.gradle.api.DefaultTask;
import xyz.kyngs.mcupload.plugin.extension.Datasource;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;

public class MCTask extends DefaultTask {

    protected final MCUploadExtension extension = getProject().getExtensions().getByType(MCUploadExtension.class);

    protected Datasource prepDatasource() throws Exception {
        var datasource = extension.getDatasourceHandler().getDatasource();
        if (datasource == null) {
            throw new IllegalStateException("Datasource is not set!");
        }
        try {
            datasource.fetch();
        } catch (Exception e) {
            if (extension.swallowErrors) {
                getLogger().error("Error while fetching version data from the {} datasource.", datasource.getName(), e);
                return null;
            } else {
                throw e;
            }
        }
        return datasource;
    }
}
