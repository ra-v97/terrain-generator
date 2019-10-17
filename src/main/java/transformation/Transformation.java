package transformation;

import model.ModelGraph;

public interface Transformation {

    default boolean isConditionCompleted(ModelGraph graph){
        return false;
    }

    ModelGraph transformGraph(ModelGraph graph);
}
