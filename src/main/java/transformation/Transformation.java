package transformation;

import model.InteriorNode;
import model.ModelGraph;

public interface Transformation {

    default boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode){
        return false;
    }

    ModelGraph transformGraph(ModelGraph graph);
}
