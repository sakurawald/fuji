package io.github.sakurawald.config.serializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.config.handler.ConfigHandler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CommentTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        Class<T> rawType = (Class<T>) type.getRawType();
        Field[] fields = rawType.getDeclaredFields();

        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Comment.class) {
                    Comment commentAnnotation = (Comment) annotation;
                    field.setAccessible(true);

                    System.out.println("raw type is " + rawType.getName());
                    System.out.printf("==== find @Comment `%s` in field %s\n", commentAnnotation.value(), field.getName());

                    return new TypeAdapter<>() {
                        @Override
                        public void write(JsonWriter out, T value) throws IOException {
//                            JsonObject jsonObject = new JsonObject();
//                            jsonObject.addProperty("comment", commentAnnotation.value());
//                            new Gson().getAdapter(JsonObject.class).write(out, jsonObject);

                            System.out.println("value is " + value);

//                            out.beginObject();
                            delegateAdapter.write(out, value);
//                            out.endObject();
                        }

                        @Override
                        public T read(JsonReader in) throws IOException {
                            return delegateAdapter.read(in);
                        }
                    };
                }
            }
        }

        return null;
    }
}
