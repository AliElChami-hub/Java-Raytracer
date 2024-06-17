package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Plane extends BBoxedPrimitive {
    private final Vec3 norm; // The normal variable represents the normal vector of the plane.
    private final Point supp; // The support variable represents a point on the plane that provides support
                              // for defining the plane.

    // Constructor using three span points
    public Plane(Point a, Point b, Point c) {
        super(BBox.INF);
        this.norm = computeNormal(a, b, c);
        this.supp = a;
    }

    // Constructor using normal vector and support point
    public Plane(Vec3 normal, Point support) {
        super(BBox.INF);
        this.norm = normal.normalized();
        this.supp = support;
    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        return new LazyHitTest(obj) {
            private Point point = null;
            private Vec3 planeNorm = null;
            private float parameter = Constants.EPS;

            // The getParameter method returns the intersection parameter.
            @Override
            public float getParameter() {
                return parameter;
            }

            // The getPoint method calculates and returns the intersection point by
            // evaluating the ray equation ray.eval(parameter) if it hasn't been calculated
            // yet.
            @Override
            public Point getPoint() {
                if (point == null) {
                    point = ray.eval(parameter);
                }
                return point;
            }

            // The getUV method calculates and returns the UV coordinates on the plane's
            // surface based on the normal, support point, and intersection point by calling
            // the computePlaneUV method from the Util class.
            @Override
            public Vec2 getUV() {
                return Util.computePlaneUV(norm, supp, point);

            }

            // The getNormal method returns the surface normal, which is the same as the
            // plane's normal.
            @Override
            public Vec3 getNormal() {
                if (planeNorm == null) {
                    planeNorm = norm;
                }
                return planeNorm;
            }

            @Override
            protected boolean calculateHit() {
                float den;
                den = ray.dir().dot(norm);
                // If denom is zero, it means that the ray is parallel to the plane, so there is
                // no intersection. In this case, the method returns false.
                if (den == 0) {
                    return false; // Ray is parallel to the plane
                }
                // If denom is not zero, it calculates the intersection parameter t by taking
                // the dot product of the difference between the ray's base point and the
                // support point with the plane's normal and dividing it by denom.
                float bro = (supp.sub(ray.base())).dot(norm) / den;

                // It then checks if t is within the valid range defined by tmin and tmax. If
                // it's outside the range, there is no valid intersection, so the method returns
                // false.
                if (bro < tmin || bro > tmax) {
                    return false; // Hit point is outside the valid range
                }

                // If there is a valid intersection, it assigns the intersection parameter,
                // calculates the intersection point by evaluating the ray equation
                // ray.eval(parameter), and returns true to indicate a successful hit.
                parameter = bro;
                point = ray.eval(parameter);
                return true;
            }
        };
    }

    @Override
    public int hashCode() {
        return norm.hashCode() ^ supp.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Plane) {
            Plane plane = (Plane) other;
            return plane.norm.equals(norm) && plane.supp.equals(supp);
        }
        return false;
    }

    // Helper method to compute the plane's normal vector from three span points
    private Vec3 computeNormal(Point a, Point b, Point c) {
        Vec3 u = b.sub(a);
        Vec3 v = c.sub(a);
        return u.cross(v).normalized();
    }
}
