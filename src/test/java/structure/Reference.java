package structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Data
@AllArgsConstructor
public class Reference {
    String definition;
    List<String> referenceList;

    public static @NotNull List<Reference> reduce(@NotNull List<Reference> referenceList) {

        // merge
        Map<String, Reference> map = new HashMap<>();
        for (Reference reference : referenceList) {
            map.putIfAbsent(reference.definition, reference);
            map.get(reference.definition).getReferenceList().addAll(reference.referenceList);
        }

        //reduce
        for (Reference reference : map.values()) {
            Set<String> set = new HashSet<>(reference.referenceList);
            reference.referenceList.clear();
            reference.referenceList.addAll(set);
        }

        return map.values().stream().toList();
    }
}
