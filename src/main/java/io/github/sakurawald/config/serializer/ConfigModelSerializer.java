package io.github.sakurawald.config.serializer;

import com.google.gson.*;
import io.github.sakurawald.config.model.ConfigModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ConfigModelSerializer implements JsonSerializer<ConfigModel> {
    @Override
    public JsonObject serialize(ConfigModel config, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(config).getAsJsonObject();

        Field[] fields = ConfigModel.class.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Comment.class) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(config);
                        String fieldName = field.getName();
                        jsonObject.addProperty(fieldName + "_comment", ((Comment) annotation).value());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return jsonObject;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ConfigModel.class, new ConfigModelSerializer())
                .create();

        ConfigModel config = new ConfigModel();
        // 设置ConfigModel的字段值

        String json = gson.toJson(config);
        System.out.println(json);
    }
}