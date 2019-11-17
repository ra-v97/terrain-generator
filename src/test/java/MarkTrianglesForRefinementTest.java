import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.TerrainMap;
import org.junit.Test;
import processor.MapProcessingUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkTrianglesForRefinementTest {

    private static TerrainMap flatMap(int sizeX, int sizeY, double z) {
        TerrainMap map = new TerrainMap(sizeX, sizeY);
        Point3d[][] points = map.getPoints();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                points[i][j] = new Point3d(i, j, z);
            }
        }
        return map;
    }

    private static TerrainMap angledFlatMap(int sizeX, int sizeY, double z1, double z2, double z3) {
        TerrainMap map = new TerrainMap(sizeX, sizeY);
        Point3d[][] points = map.getPoints();
        Point3d p1 = points[0][0] = new Point3d(0, 0, z1);
        Point3d p2 = points[0][points.length - 1] = new Point3d(0, points.length - 1, z2);
        Point3d p3 = points[points.length - 1][0] = new Point3d(points.length - 1, 0, z3);
        MapProcessingUtil.Plane plane = new MapProcessingUtil.Plane(p1, p2, p3);
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                points[i][j] = new Point3d(i, j, plane.getZCoordinate(i, j));
            }
        }
        return map;
    }

    private static void addRandomInterior(TerrainMap map, double edgeZ) {
        Point3d[][] points = map.getPoints();
        for (int i = 1; i < map.getSizeX() - 1; i++) {
            for (int j = 1; j < map.getSizeY() - 1; j++) {
                points[i][j] = new Point3d(i, j, Math.random() * edgeZ);
            }
        }
    }

    private static List<InteriorNode> markTrianglesForRefinement(TerrainMap map, double errorEps) {
        ModelGraph graph = MapProcessingUtil.spanGraphOverTerrain(map);
        return MapProcessingUtil.markTrianglesForRefinement(graph, map, errorEps);
    }

    @Test
    public void When_FlatMapWithPositiveHeight_Expect_NoTrianglesMarkedForRefinement() {
        TerrainMap testMap = flatMap(10, 10, 1.0);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(0, triangles.size());
    }

    @Test
    public void When_FlatMapWithNegativeHeight_Expect_NoTrianglesMarkedForRefinement() {
        TerrainMap testMap = flatMap(10, 10, -1.0);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(0, triangles.size());
    }

    @Test
    public void When_MapWithFlatBorderEdgesAndRandomInterior_Expect_AllTrianglesMarkedForRefinement() {
        TerrainMap testMap = flatMap(10, 10, 1.0);
        addRandomInterior(testMap, 1.0);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(2, triangles.size());
    }

    @Test
    public void When_SmallestPossibleMap_Expect_NoTrianglesMarkedForRefinement() {
        TerrainMap testMap = flatMap(2, 2, 1.0);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(0, triangles.size());
    }

    @Test
    public void When_AlmostFlatMap_Expect_SomeTrianglesMarkedForRefinement() {
        TerrainMap testMap = flatMap(100, 100, 1.0);
        testMap.getPoints()[0][99] = new Point3d(0, 0, 1.01);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(1, triangles.size());
    }

    @Test
    public void When_OutliersOnADiagonalEdge_Expect_NoTrianglesMarkedForRefinement() {
        TerrainMap testMap = flatMap(10, 10, 1.0);
        testMap.getPoints()[1][1] = new Point3d(1, 1, -5.0);
        testMap.getPoints()[3][3] = new Point3d(3, 3, -120.0);
        testMap.getPoints()[8][8] = new Point3d(8, 8, 10.0);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(0, triangles.size());
    }

    @Test
    public void When_AngledFlatMap_Expect_NoTrianglesMarkedForRefinement() {
        TerrainMap testMap = angledFlatMap(1000, 1000, 0, 4, 8);
        List<InteriorNode> triangles = markTrianglesForRefinement(testMap, 0.0);
        assertEquals(0, triangles.size());
    }
}
