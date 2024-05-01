package xyz.kyngs.mcupload.plugin.extension.platforms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.gradle.api.Project;
import xyz.kyngs.mcupload.plugin.MCReadmeSyncTask;
import xyz.kyngs.mcupload.plugin.MCReleaseTask;
import xyz.kyngs.mcupload.plugin.extension.Datasource;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;
import xyz.kyngs.mcupload.plugin.extension.Platform;
import xyz.kyngs.mcupload.plugin.util.MultiPartBodyPublisher;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class PolymartPlatform implements Platform {

    /**
     * The API key to use to authenticate with the API.
     */
    public String apiKey;
    /**
     * The resource id to upload to.
     */
    public String resourceId;

    @Override
    public void upload(MCUploadExtension extension, MCReleaseTask task, Datasource datasource, Project project, File file) throws Exception {
        Objects.requireNonNull(apiKey, "API key cannot be null!");
        Objects.requireNonNull(resourceId, "Resource id cannot be null!");

        var client = HttpClient.newHttpClient();

        var data = new MultiPartBodyPublisher()
                .addPart("resource_id", resourceId)
                .addPart("version", datasource.getVersion(project))
                .addPart("title", datasource.getVersionName())
                .addPart("message", datasource.getChangelog())
                .addPart("api_key", apiKey)
                .addPart("file", file.toPath())
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.polymart.org/v1/postUpdate"))
                .header("Content-Type", "multipart/form-data")
                .header("enctype", "multipart/form-data")
                .POST(data);

        var rawResponse = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        var gson = new Gson();

        var response = gson.fromJson(rawResponse.body(), JsonObject.class);

        if (rawResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to upload to Polymart: " + response.get("message").getAsString());
        }

        var parsedResponse = response.getAsJsonObject("response");

        if (parsedResponse.get("success").getAsBoolean()) {
            task.getLogger().lifecycle("Uploaded version " + datasource.getVersion(project) + " to Polymart.");
        } else {
            throw new RuntimeException("Failed to upload to Polymart: " + rawResponse.body());
        }

    }

    @Override
    public void syncReadme(MCUploadExtension extension, MCReadmeSyncTask task, Datasource datasource, Project project) throws Exception {
        task.getLogger().lifecycle("Polymart does not support syncing ReadMe files. :(");
    }

    @Override
    public String getName() {
        return "Polymart";
    }
}
