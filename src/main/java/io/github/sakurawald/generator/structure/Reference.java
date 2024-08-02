package io.github.sakurawald.generator.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Data
@AllArgsConstructor
public class Reference {
    String definition;
    List<String> reference;

    public static @NotNull List<Reference> reduce(@NotNull List<Reference> references) {

        // merge
        Map<String, Reference> map = new HashMap<>();
        for (Reference reference : references) {
            map.putIfAbsent(reference.definition, reference);
            map.get(reference.definition).getReference().addAll(reference.reference);
        }

        //reduce
        for (Reference reference : map.values()) {
            Set<String> set = new HashSet<>(reference.reference);
            reference.reference.clear();
            reference.reference.addAll(set);
        }

        return map.values().stream().toList();
    }
}
