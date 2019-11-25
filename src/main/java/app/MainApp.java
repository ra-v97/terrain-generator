package app;

import model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import processor.MapProcessingUtil;
import transformation.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class MainApp {

    private static Logger log = Logger.getLogger(MainApp.class.getName());

    private TerrainMap loadData() {
        File file = new File(this.getClass().getResource("/poland1000.data").getPath());
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        int dimension = (int) Math.floor(Math.sqrt(bytes.length / 2f));

        ShortBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        TerrainMap map = new TerrainMap(dimension, dimension);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                map.addPoint(j, i, new Point3d(j, i, buffer.get(i * dimension + j)));
            }
        }
        return map;
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        MainApp app = new MainApp();
        TerrainMap map = app.loadData();
        Runtime.getRuntime().gc();
        ModelGraph graph = MapProcessingUtil.spanGraphOverTerrain(map);
        graph.addAttribute("ui.stylesheet", "url('file:src/main/resources/styles.css')");
        graph.display();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        List<Transformation> transformations = Arrays.asList(new TransformationP1(), new TransformationP2(), new TransformationP3(),
                new TransformationP4(), new TransformationP5(), new TransformationP6(), new TransformationP7(), new TransformationP8(), new TransformationP9());

        double error = 0.0001;
        for (List<InteriorNode> trianglesToRefinement = MapProcessingUtil.markTrianglesForRefinement(graph, map, error);
             trianglesToRefinement.size() > 0;
             trianglesToRefinement = MapProcessingUtil.markTrianglesForRefinement(graph, map, error)) {

            trianglesToRefinement.forEach(interiorNode ->
            {
                for (Transformation p : transformations) {
                    if (p.isConditionCompleted(graph, interiorNode)) {
                        log.info("Executing transformation: " + p.getClass().getSimpleName() + " on interior" + interiorNode.getId());
                        p.transformGraph(graph, interiorNode);
                        break;
                    }
                }
            });
        }
    }
}