import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.config.serializer.Comment;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class BuildConfigurationDocumentTest {

    private JsonObject buildJsonObjectWithComments(Object obj) {
        JsonObject jsonObject = new JsonObject();
        processFieldsWithComments(obj, jsonObject);
        return jsonObject;
    }

    private void processFieldsWithComments(Object obj, JsonObject jsonObject) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getFields()) {
            try {
                String fieldName = field.getName();
                Object value = field.get(obj);

                /* insert related comment property */
                if (field.isAnnotationPresent(Comment.class)) {
                    Comment commentAnnotation = field.getAnnotation(Comment.class);
                    jsonObject.addProperty(fieldName + "@comment", commentAnnotation.value());
                }

                /* switch type */
                if (value != null) {
                    if (isPrimitiveOrWrapper(field.getType())) {
                        jsonObject.addProperty(fieldName, value.toString());
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        JsonArray jsonArray = new JsonArray();
                        for (Object listItem : (List<?>) value) {
                            if (listItem != null) {
                                jsonArray.add(new Gson().toJsonTree(listItem));
                            }
                        }
                        jsonObject.add(fieldName, jsonArray);
                    } else if (Map.class.isAssignableFrom(field.getType())) {
                        JsonObject mapJsonObject = new JsonObject();
                            Map<?, ?> map = (Map<?, ?>) value;
                            for (Map.Entry<?, ?> entry : map.entrySet()) {
                                String mapKey = entry.getKey().toString();
                                Object mapValue = entry.getValue();
                                mapJsonObject.add(mapKey, new Gson().toJsonTree(mapValue));
                            }
                            jsonObject.add(fieldName, mapJsonObject);
                    } else {
                        JsonObject nestedJsonObject = new JsonObject();
                        processFieldsWithComments(value, nestedJsonObject);
                        jsonObject.add(fieldName, nestedJsonObject);
                    }
                }


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class ||
                Number.class.isAssignableFrom(clazz) || clazz == String.class;
    }

    private void writeToFile(String fileName, JsonObject content) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filePath = "./build/document/" + fileName;
        new File(filePath).getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(gson.toJson(content));
            System.out.println("File " + fileName + " has been written successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing file: " + e.getMessage());
        }
    }

    @Test
    void buildConfigurationDocument() {
        writeToFile("config.json", buildJsonObjectWithComments(new ConfigModel()));
    }
}
