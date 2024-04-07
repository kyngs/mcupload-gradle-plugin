package xyz.kyngs.mcupload.plugin.extension.platforms;

import masecla.modrinth4j.client.agent.UserAgent;
import masecla.modrinth4j.endpoints.project.ModifyProject;
import masecla.modrinth4j.endpoints.version.CreateVersion;
import masecla.modrinth4j.main.ModrinthAPI;
import masecla.modrinth4j.model.version.ProjectVersion;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import xyz.kyngs.mcupload.plugin.MCReadmeSyncTask;
import xyz.kyngs.mcupload.plugin.MCReleaseTask;
import xyz.kyngs.mcupload.plugin.extension.Datasource;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;
import xyz.kyngs.mcupload.plugin.extension.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModrinthPlatform implements Platform {

    /**
     * The token to use to authenticate with the API.
     */
    public String token;
    /**
     * The project id to upload to.
     */
    public String projectId;
    /**
     * The supported game versions.
     */
    public List<String> gameVersions;
    /**
     * The supported loaders.
     */
    public List<String> loaders;
    /**
     * The API URL to use.
     */
    public String apiURL;
    private DependencyHandler dependencies;

    public DependencyHandler getDependencies() {
        return dependencies;
    }

    /**
     * Allows adding dependencies to the project.
     * @param action The action to execute.
     */
    public void dependencies(Action<DependencyHandler> action) {
        dependencies = new DependencyHandler();
        action.execute(dependencies);
    }

    private ModrinthAPI api(Project project) {
        var userAgent = UserAgent.builder()
                .authorUsername("kyngs")
                .projectName("mcupload")
                .projectVersion(getClass().getPackage().getImplementationVersion())
                .contact(projectId + "/" + project.getVersion())
                .build();

        return ModrinthAPI.rateLimited(userAgent, apiURL, token);
    }

    private void preflight() {
        Objects.requireNonNull(token, "Token cannot be null!");
        Objects.requireNonNull(projectId, "Project id cannot be null!");

        if (apiURL == null) {
            apiURL = "https://api.modrinth.com/v2";
        }
    }

    @Override
    public void upload(MCUploadExtension extension, MCReleaseTask task, Datasource datasource, Project project, File file) {
        preflight();
        if (gameVersions == null) {
            gameVersions = new ArrayList<>();
        }
        if (loaders == null) {
            loaders = new ArrayList<>();
        }
        if (gameVersions.isEmpty()) {
            throw new GradleException("No game versions specified!");
        }
        if (loaders.isEmpty()) {
            throw new GradleException("No loaders specified!");
        }
        if (dependencies == null) {
            dependencies = new DependencyHandler();
        }

        var api = api(project);

        var data = CreateVersion.CreateVersionRequest.builder()
                .projectId(projectId)
                .versionNumber(datasource.getVersion(project))
                .name(datasource.getVersionName().substring(0, Math.min(datasource.getVersionName().length(), 64)))
                .changelog(datasource.getChangelog())
                .versionType(ProjectVersion.VersionType.RELEASE)
                .gameVersions(gameVersions)
                .loaders(loaders)
                .dependencies(dependencies.getDependencies())
                .files(file)
                .build();

        var uploaded = api.versions().createProjectVersion(data)
                .join();

        task.getLogger().lifecycle("Uploaded version " + uploaded.getVersionNumber() + " to Modrinth!");
    }

    @Override
    public void syncReadme(MCUploadExtension extension, MCReadmeSyncTask task, Datasource datasource, Project project) throws Exception {
        preflight();
        var api = api(project);

        var data = ModifyProject.ProjectModifications.builder()
                .body(datasource.getReadMe())
                .build();

        api.projects().modify(projectId, data)
                .join();

        task.getLogger().lifecycle("Synced ReadMe to Modrinth!");
    }

    @Override
    public String getName() {
        return "Modrinth";
    }

    public static class DependencyHandler {
        private final List<ProjectVersion.ProjectDependency> dependencies;

        public DependencyHandler() {
            this.dependencies = new ArrayList<>();
        }

        public List<ProjectVersion.ProjectDependency> getDependencies() {
            return dependencies;
        }

        /**
         * Adds a required dependency.
         * @param projectId The project id to add.
         */
        public void required(String projectId) {
            dependencies.add(dependency(ProjectVersion.ProjectDependencyType.REQUIRED, projectId));
        }

        /**
         * Adds an optional dependency.
         * @param projectId The project id to add.
         */
        public void optional(String projectId) {
            dependencies.add(dependency(ProjectVersion.ProjectDependencyType.OPTIONAL, projectId));
        }

        /**
         * Adds a embedded dependency.
         * @param projectId The project id to add.
         */
        public void embedded(String projectId) {
            dependencies.add(dependency(ProjectVersion.ProjectDependencyType.EMBEDDED, projectId));
        }

        /**
         * Adds an incompatible (=> conflicting) dependency.
         * @param projectId The project id to add.
         */
        public void incompatible(String projectId) {
            dependencies.add(dependency(ProjectVersion.ProjectDependencyType.INCOMPATIBLE, projectId));
        }

        private ProjectVersion.ProjectDependency dependency(ProjectVersion.ProjectDependencyType type, String projectId) {
            return new ProjectVersion.ProjectDependency(null, projectId, null, type);
        }
    }

}
