package xyz.kyngs.mcupload.plugin.extension;

import org.gradle.api.Action;
import xyz.kyngs.mcupload.plugin.extension.platforms.GithubPlatform;
import xyz.kyngs.mcupload.plugin.extension.platforms.ModrinthPlatform;
import xyz.kyngs.mcupload.plugin.extension.platforms.PolymartPlatform;

import java.util.ArrayList;
import java.util.List;

public class PlatformHandler {
    private List<Platform> platforms;

    public PlatformHandler() {
        this.platforms = new ArrayList<>();
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    /**
     * Adds the Modrinth platform.
     *
     * @param action The action to execute.
     */
    public void modrinth(Action<ModrinthPlatform> action) {
        var modrinth = new ModrinthPlatform();
        action.execute(modrinth);
        platforms.add(modrinth);
    }

    /**
     * Adds the Polymart platform.
     *
     * @param action The action to execute.
     */
    public void polymart(Action<PolymartPlatform> action) {
        var polymart = new PolymartPlatform();
        action.execute(polymart);
        platforms.add(polymart);
    }

    /**
     * Adds the GitHub platform.
     *
     * @param action The action to execute.
     */
    public void github(Action<GithubPlatform> action) {
        var github = new GithubPlatform();
        action.execute(github);
        platforms.add(github);
    }
}
