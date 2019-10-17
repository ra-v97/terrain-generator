package transformation;

import model.ModelGraph;

public class TransformationP1 implements Transformation{

    @Override
    public boolean isConditionCompleted(ModelGraph graph) {
        return false;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph) {
        return null;
    }
}
