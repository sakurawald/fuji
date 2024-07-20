package io.github.sakurawald.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.annotation.Documentation;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonDocsGenerator {

    @Getter
    private static final JsonDocsGenerator instance = new JsonDocsGenerator();

    private JsonDocsGenerator() {
    }

    private JsonObject toJsonObject(Object javaObject) {
        JsonObject root = new JsonObject();
        toJsonObject(javaObject, root);
        return root;
    }

    private void toJsonObject(Object obj, JsonObject root) {
        Class<?> clazz = obj.getClass();

        if (clazz.isAnnotationPresent(Documentation.class)) {
            root.addProperty(clazz.getSimpleName() + "@documentation", clazz.getAnnotation(Documentation.class).value());
        }

        for (Field field : clazz.getFields()) {
            try {
                String fieldName = field.getName();
                Object value = field.get(obj);

                /* insert related comment property */
                if (field.isAnnotationPresent(Documentation.class)) {
                    root.addProperty(fieldName + "@documentation", field.getAnnotation(Documentation.class).value());
                }

                /* switch type */
                if (value != null) {

                    if (isPrimitiveOrWrapper(field.getType())) {

                        if (value.getClass().equals(Boolean.class)) {
                            root.addProperty(fieldName, (Boolean) value);
                        } else if (value.getClass().equals(Integer.class)) {
                            root.addProperty(fieldName, (Integer) value);
                        } else if (value.getClass().equals(Float.class)) {
                            root.addProperty(fieldName, (Float) value);
                        } else if (value.getClass().equals(Double.class)) {
                            root.addProperty(fieldName, (Double) value);
                        } else if (value.getClass().equals(String.class)) {
                            root.addProperty(fieldName, (String) value);
                        } else if (value.getClass().equals(Character.class)) {
                            root.addProperty(fieldName, (Character) value);
                        } else {
                            root.addProperty(fieldName, value.toString());
                        }


                    } else if (List.class.isAssignableFrom(field.getType())) {
                        root.addProperty(fieldName + "@skip", "list type");

                        JsonArray jsonArray = new JsonArray();
                        for (Object listItem : (List<?>) value) {
                            if (listItem != null) {
                                jsonArray.add(new Gson().toJsonTree(listItem));
                            }
                        }
                        root.add(fieldName, jsonArray);
                    } else if (Map.class.isAssignableFrom(field.getType())) {
                        root.addProperty(fieldName + "@skip", "map type");

                        JsonObject mapJsonObject = new JsonObject();
                        Map<?, ?> map = (Map<?, ?>) value;
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            String mapKey = entry.getKey().toString();
                            Object mapValue = entry.getValue();
                            mapJsonObject.add(mapKey, new Gson().toJsonTree(mapValue));
                        }
                        root.add(fieldName, mapJsonObject);
                    } else if (Set.class.isAssignableFrom(field.getType())) {
                        root.addProperty(fieldName + "@skip", "map type");

                        JsonArray jsonArray = new JsonArray();
                        Set<?> set = (Set<?>) value;
                        for (Object o : set) {
                            jsonArray.add(new Gson().toJsonTree(o));
                        }
                        root.add(fieldName, jsonArray);
                    } else {
                        JsonObject nestedJsonObject = new JsonObject();
                        toJsonObject(value, nestedJsonObject);
                        root.add(fieldName, nestedJsonObject);
                    }
                }

            } catch (IllegalAccessException e) {
                Fuji.LOGGER.warn("failed to get the value of a field, {}", e.toString());
            }
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class ||
                Number.class.isAssignableFrom(clazz) || clazz == String.class;
    }

    @SneakyThrows
    private JsonObject writeToFile(Path path, JsonObject json) {
        path.toFile().getParentFile().mkdirs();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtils.writeStringToFile(path.toFile(), gson.toJson(json), Charset.defaultCharset());

        return json;
    }

    public JsonObject generate(Path path, Object javaObject) {
        return writeToFile(path, toJsonObject(javaObject));
    }
}
