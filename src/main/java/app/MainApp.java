package app;

import model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import processor.MapProcessingUtil;
import transformation.Transformation;
import transformation.TransformationP1;
import transformation.TransformationP2;

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
        File file = new File(this.getClass().getResource("/poland100.data").getPath());
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
            if (i % 10 == 0)
                System.out.println("Row " + i);
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
        System.out.println(MapProcessingUtil.markTrianglesForRefinement(graph, map, 0.1));
        List<Transformation> transformations = Arrays.asList(new TransformationP1(), new TransformationP2());
        MapProcessingUtil.markTrianglesForRefinement(graph, map, 0.1).forEach(interiorNode ->
        {
            for (Transformation p : transformations) {
                try {
                    if (p.isConditionCompleted(graph, interiorNode)) {
                        System.out.println(p.getClass());
                        p.transformGraph(graph, interiorNode);
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        });

    }
}