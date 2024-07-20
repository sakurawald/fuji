package io.github.sakurawald.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.annotation.Documentation;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonDocsGenerator {

    @Getter
    private static final JsonDocsGenerator instance = new JsonDocsGenerator();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final String CLASS_DOCUMENTATION = "@class-documentation";
    public static final String FIELD_DOCUMENTATION = "@field-documentation";
    public static final String SKIP_WALK = "@skip-walk";

    private JsonDocsGenerator() {
    }

    private JsonObject toJsonObject(Object javaObject) {
        JsonObject root = new JsonObject();
        walk(javaObject, root);
        return root;
    }

    private void walk(Object obj, JsonObject root) {
        Class<?> clazz = obj.getClass();

        // for class
        if (clazz.isAnnotationPresent(Documentation.class)) {
            root.addProperty(clazz.getSimpleName() + CLASS_DOCUMENTATION, clazz.getAnnotation(Documentation.class).value());
        }

        // for fields
        for (Field field : clazz.getFields()) {
            try {
                String fieldName = field.getName();
                Object value = field.get(obj);

                /* insert related comment property */
                if (field.isAnnotationPresent(Documentation.class)) {
                    root.addProperty(fieldName + FIELD_DOCUMENTATION, field.getAnnotation(Documentation.class).value());
                }

                /* switch type */
                if (value != null) {

                    if (isPrimitiveOrString(field.getType())) {
                        //noinspection IfCanBeSwitch
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
                        root.addProperty(fieldName + SKIP_WALK, "list type");
                        JsonArray jsonArray = new JsonArray();
                        for (Object elt : (List<?>) value) {
                            if (elt != null) {
                                jsonArray.add(gson.toJsonTree(elt));
                            }
                        }
                        root.add(fieldName, jsonArray);
                    } else {
                        if (Map.class.isAssignableFrom(field.getType())) {
                            root.addProperty(fieldName + SKIP_WALK, "map type");
                            JsonObject jsonObject = new JsonObject();
                            Map<?, ?> map = (Map<?, ?>) value;
                            for (Map.Entry<?, ?> entry : map.entrySet()) {
                                String mapKey = entry.getKey().toString();
                                Object mapValue = entry.getValue();
                                jsonObject.add(mapKey, gson.toJsonTree(mapValue));
                            }
                            root.add(fieldName, jsonObject);
                        } else if (Set.class.isAssignableFrom(field.getType())) {
                            root.addProperty(fieldName + SKIP_WALK, "set type");
                            JsonArray jsonArray = new JsonArray();
                            Set<?> set = (Set<?>) value;
                            for (Object elt : set) {
                                jsonArray.add(gson.toJsonTree(elt));
                            }
                            root.add(fieldName, jsonArray);
                        } else {
                            JsonObject jsonObject = new JsonObject();
                            walk(value, jsonObject);
                            root.add(fieldName, jsonObject);
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                Fuji.LOGGER.warn("failed to get the value of a field, {}", e.toString());
            }
        }
    }

    private boolean isPrimitiveOrString(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class;
    }

    public JsonObject generate(Object javaObject) {
        return toJsonObject(javaObject);
    }
}
