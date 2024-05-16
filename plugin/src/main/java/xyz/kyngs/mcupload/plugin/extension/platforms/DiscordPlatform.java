package xyz.kyngs.mcupload.plugin.extension.platforms;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.gradle.api.Project;
import xyz.kyngs.mcupload.plugin.MCReadmeSyncTask;
import xyz.kyngs.mcupload.plugin.MCReleaseTask;
import xyz.kyngs.mcupload.plugin.extension.Datasource;
import xyz.kyngs.mcupload.plugin.extension.MCUploadExtension;
import xyz.kyngs.mcupload.plugin.extension.Platform;

import java.io.File;
import java.util.Objects;

public class DiscordPlatform implements Platform {

    /**
     * The webhook URL to use to authenticate with the API.
     */
    public String webhookUrl;

    @Override
    public void upload(MCUploadExtension extension, MCReleaseTask task, Datasource datasource, Project project, File file) throws Exception {
        Objects.requireNonNull(webhookUrl, "Webhook URL cannot be null!");

        var embed = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(datasource.getVersionName(), null))
                .setDescription(datasource.getChangelog())
                .build();

        var message = new WebhookMessageBuilder()
                .addFile(file)
                .addEmbeds(embed)
                .build();

        try (var client = WebhookClient.withUrl(webhookUrl)) {
            client.send(message);
        }
    }

    @Override
    public void syncReadme(MCUploadExtension extension, MCReadmeSyncTask task, Datasource datasource, Project project) throws Exception {
        project.getLogger().lifecycle("Syncing Readme to Discord is not supported yet.");
    }

    @Override
    public String getName() {
        return "Discord";
    }
}
