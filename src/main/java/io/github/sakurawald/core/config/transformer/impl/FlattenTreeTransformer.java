package io.github.sakurawald.core.config.transformer.impl;

import com.google.gson.JsonObject;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class FlattenTreeTransformer extends ConfigurationTransformer {

    private final String subtreeIdentifier;
    private final String jsonPath;
    private final String topLevel;

    private boolean overrideTheOriginalFileWithSkeletonTree;

    public FlattenTreeTransformer(String jsonPath, String topLevel, String subtreeIdentifier) {
        this.subtreeIdentifier = subtreeIdentifier;
        this.jsonPath = jsonPath;
        this.topLevel = topLevel;
    }

    @SneakyThrows
    private void flatten(JsonObject parent, String level) {
        Path currentTreeOutPath = Fuji.CONFIG_PATH.resolve(level + ".json");
        if (Files.exists(currentTreeOutPath)) return;

        /* find and walk submodule */
        parent.keySet().stream()
            .filter(key -> parent.get(key).isJsonObject() && parent.getAsJsonObject(key).has(subtreeIdentifier))
            .forEach(key -> flatten(parent.getAsJsonObject(key), level + "." + key));

        /* create top-level file to store the tree */
        parent.remove(subtreeIdentifier);

        // remove empty subtree
        parent.keySet().stream().toList().stream()
            .filter(key -> parent.get(key).isJsonObject() && parent.getAsJsonObject(key).isEmpty())
            .forEach(parent::remove);

        // if the tree is not empty, migrate it to a standalone file
        if (!parent.isEmpty()) {
            LogUtil.debug("parent.members = {}",parent.entrySet());

            logConsole("flatten tree `{}` into `{}`", level, currentTreeOutPath);
            String json = BaseConfigurationHandler.getGson().toJson(parent);
            Files.writeString(currentTreeOutPath, json);

            this.overrideTheOriginalFileWithSkeletonTree = true;
        }

        /* remove all keys on leave */
        parent.keySet().stream().toList()
            .forEach(parent::remove);
    }

    private void deleteKeys(JsonObject parent)  {
        parent.keySet().stream().toList().stream()
            .filter(key-> !key.equals(subtreeIdentifier))
            .forEach(key -> {
                if (parent.get(key).isJsonObject()) {
                    deleteKeys(parent.getAsJsonObject(key));

                    // remove the subtree if empty. (the subtree will not be empty, if it is a sub-module
                    if (parent.getAsJsonObject(key).isEmpty()) parent.remove(key);
                } else parent.remove(key);
            });

    }

    private JsonObject makeSkeletonTree()  {
        JsonObject root = (JsonObject) read(this.jsonPath);
        deleteKeys(root);
        return root;
    }

    @Override
    public void apply() {
        JsonObject root = (JsonObject) read(this.jsonPath);
        this.flatten(root, this.topLevel);

        if (overrideTheOriginalFileWithSkeletonTree) {
            set(this.jsonPath, makeSkeletonTree());
            writeStorage();
        }
    }

}
