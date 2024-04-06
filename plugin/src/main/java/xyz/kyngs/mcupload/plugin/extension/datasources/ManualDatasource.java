package xyz.kyngs.mcupload.plugin.extension.datasources;

import xyz.kyngs.mcupload.plugin.extension.Datasource;

import java.util.Objects;

public class ManualDatasource implements Datasource {

    public String versionName;
    public String changelog;
    public String readMe;

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
        return "manual";
    }

    @Override
    public String getReadMe() {
        return readMe;
    }

    @Override
    public void fetch() throws Exception {
        Objects.requireNonNull(versionName, "Version name cannot be null!");
        Objects.requireNonNull(changelog, "Changelog cannot be null!");
        Objects.requireNonNull(readMe, "ReadMe cannot be null!");
    }
}
