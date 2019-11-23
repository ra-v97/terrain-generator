package adaptation;

import app.MainApp;
import model.InteriorNode;
import model.ModelGraph;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import transformation.Transformation;

import java.util.Collection;

public class Adaptation {
    private static Logger log = Logger.getLogger(Adaptation.class.getName());

    public static Pair<ModelGraph, Boolean> transform(ModelGraph graph, Transformation transformation){
        return transform(graph, transformation, false);
    }

    private static Pair<ModelGraph, Boolean> transform(ModelGraph graph, Transformation transformation, Boolean prevResult){
        boolean transformationResult = prevResult;
        ModelGraph model = graph;
        Collection<InteriorNode> interiors = graph.getInteriors();
        for(InteriorNode i : interiors){
            try {
                if(transformation.isConditionCompleted(graph, i)){
                    model = transformation.transformGraph(model, i);
                    log.info("Executing transformation: " + transformation.getClass().getSimpleName() + " on interior" + i.getId());
                    Thread.sleep(1000);
                    return transform(model, transformation, true);
                }
            } catch (Exception e) {
                log.error("Transformation " + transformation.getClass().getSimpleName() + " returned an error: " + e.toString() + " for interior: " + i.toString());
            }
        }

        return new Pair<>(model,transformationResult);
    }
}
