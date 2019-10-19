package transformation;

import model.InteriorNode;
import model.ModelGraph;

public interface Transformation {

    default boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode){
        return false;
    }

    /**
     * This operation is NOT idempotent. It only returns the {@link ModelGraph} to enable transformation chaining
     * @param graph graph to be transformed
     * @param interiorNode interior node of the triangle
     * @return the same graph after transformation
     */
    ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode);
}
