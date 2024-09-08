package auxiliary;

import io.github.classgraph.ClassGraph;
import io.github.sakurawald.Fuji;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtility {

    public static ClassGraph makeBaseClassGraph() {
        return new ClassGraph()
            .acceptPackages(Fuji.class.getPackageName());
    }

}
