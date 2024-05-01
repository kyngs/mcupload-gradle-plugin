package xyz.kyngs.mcupload.plugin.extension.platforms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.gradle.api.Project;
import xyz.kyngs.mcupload.plugin.MCReadmeSyncTask;
import xyz.kyngs.mcupload.plugin.MCReleaseTask;
import xyz.kyngs.mcupload.plugin.extension.Datasource;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;
import xyz.kyngs.mcupload.plugin.extension.Platform;

import java.io.File;
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

    private static StringBody multipartString(String value) {
        return new StringBody(value, ContentType.MULTIPART_FORM_DATA);
    }

    @Override
    public void upload(MCUploadExtension extension, MCReleaseTask task, Datasource datasource, Project project, File file) throws Exception {
        Objects.requireNonNull(apiKey, "API key cannot be null!");
        Objects.requireNonNull(resourceId, "Resource id cannot be null!");

        var data = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.LEGACY)
                .addPart("resource_id", multipartString(resourceId))
                .addPart("version", multipartString(datasource.getVersion(project)))
                .addPart("title", multipartString(datasource.getVersionName()))
                .addPart("message", multipartString(datasource.getChangelog()))
                .addPart("api_key", multipartString(apiKey))
                .addPart("file", new FileBody(file));

        var post = new HttpPost("https://api.polymart.org/v1/postUpdate");

        post.setHeader("enctype", "multipart/form-data");
        post.setEntity(data.build());

        try (var client = HttpClients.createDefault()) {
            try (var res = client.execute(post)) {
                var gson = new Gson();
                var string = EntityUtils.toString(res.getEntity());

                var response = gson.fromJson(string, JsonObject.class);

                if (res.getCode() != 200) {
                    throw new RuntimeException("Failed to upload to Polymart: " + response.get("message").getAsString());
                }

                var parsedResponse = response.getAsJsonObject("response");

                if (parsedResponse.get("success").getAsBoolean()) {
                    task.getLogger().lifecycle("Uploaded version " + datasource.getVersion(project) + " to Polymart.");
                } else {
                    throw new RuntimeException("Failed to upload to Polymart: " + string);
                }
            }
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
