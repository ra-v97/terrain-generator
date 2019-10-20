package model;

import java.util.Objects;

public final class Point3d {

    private final double x;

    private final double y;

    private final double z;

    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public static double distance(Point3d p1, Point3d p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY()- p2.getY();
        double dz = p1.getZ() - p2.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Point3d middlePoint(Point3d p1, Point3d p2) {
        double xs = p1.getX() + p2.getX();
        double ys = p1.getY() + p2.getY();
        double zs = p1.getZ() + p2.getZ();
        return new Point3d(xs / 2d, ys / 2d, zs / 2d);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3d point3d = (Point3d) o;
        return Double.compare(point3d.x, x) == 0 &&
                Double.compare(point3d.y, y) == 0 &&
                Double.compare(point3d.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
