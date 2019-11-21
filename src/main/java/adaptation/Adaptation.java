package adaptation;

import model.InteriorNode;
import model.ModelGraph;
import transformation.Transformation;

import java.util.Collection;

public class Adaptation {
    public static Boolean transform(ModelGraph graph, Transformation transformation, Collection<InteriorNode> interiors){
        if(isConditionFulfilledForAnyInterior(graph, transformation, interiors)){
            for(InteriorNode i : interiors){
                if(transformation.isConditionCompleted(graph, i)){
                    transformation.transformGraph(graph, i);
                }
            }
            return true;
        }
        return false;
    }

    private static Boolean isConditionFulfilledForAnyInterior(ModelGraph graph, Transformation transformation, Collection<InteriorNode> interiors){
        return interiors.stream().map(interior -> transformation.isConditionCompleted(graph, interior))
                .reduce(false, (acc, e) -> acc || e);
    }
}
