package xyz.kyngs.mcupload.plugin.extension.platforms;

import org.gradle.api.Project;
import org.kohsuke.github.GHReleaseBuilder;
import org.kohsuke.github.GitHubBuilder;
import xyz.kyngs.mcupload.plugin.MCReadmeSyncTask;
import xyz.kyngs.mcupload.plugin.MCReleaseTask;
import xyz.kyngs.mcupload.plugin.extension.Datasource;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;
import xyz.kyngs.mcupload.plugin.extension.Platform;

import java.io.File;
import java.util.Objects;

public class GithubPlatform implements Platform {

    /**
     * The token to use to authenticate with the API.
     */
    public String token;
    /**
     * The repository to upload to.
     */
    public String repository;

    @Override
    public void upload(MCUploadExtension extension, MCReleaseTask task, Datasource datasource, Project project, File file) throws Exception {
        Objects.requireNonNull(token, "Token cannot be null!");
        Objects.requireNonNull(repository, "Repository cannot be null!");

        var api = new GitHubBuilder()
                .withOAuthToken(token)
                .build();

        var repo = api.getRepository(repository);

        if (repo == null) {
            throw new RuntimeException("Failed to find repository: " + repository);
        }

        var release = repo.createRelease(datasource.getVersion(project))
                .name(datasource.getVersionName())
                .body(datasource.getChangelog())
                .makeLatest(GHReleaseBuilder.MakeLatest.LEGACY)
                .create();

        project.getLogger().lifecycle("Release created, uploading asset...");

        release.uploadAsset(file, "application/java-archive");
    }

    @Override
    public void syncReadme(MCUploadExtension extension, MCReadmeSyncTask task, Datasource datasource, Project project) throws Exception {
        project.getLogger().lifecycle("Syncing Readme to GitHub is not supported yet.");
    }

    @Override
    public String getName() {
        return "GitHub";
    }
}
