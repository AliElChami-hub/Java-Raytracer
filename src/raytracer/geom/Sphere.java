package raytracer.geom;

//import java.lang.invoke.ConstantCallSite;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Sphere extends BBoxedPrimitive {
    private final Point cent; // we need an atribute for the center whihc is a point
    private final float rad; // we need also a radius which can be a float

    // making the constructor needed for the testing in the factory:
    public Sphere(Point center, float radius) {
        super(BBox.create(center.sub(new Vec3(radius, radius, radius)), center.add(new Vec3(radius, radius, radius))));
        this.cent = center;
        this.rad = radius;
    }

    // the fun begins here :D :
    // The hitTest method is overridden from the superclass BBoxedPrimitive to
    // perform the actual ray-sphere intersection test
    // It returns a Hit object that contains information about the intersection.

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        return new LazyHitTest(obj) {
            private Point intpoint = null; // just initialisation of the intersection point
            private Vec3 norm = null; // same here, initialisation of the surface normal
            private float para = Constants.EPS; // initialise a small constant value

            @Override
            public float getParameter() {
                return para;
            }

            // method calculates and returns the intersection point by evaluating the ray
            // equation:
            @Override
            public Point getPoint() {
                if (intpoint == null) {
                    intpoint = ray.eval(para);
                }
                return intpoint;
            }

            // method calculates and returns the UV coordinates on the sphere's surface
            // based on the radial vector from the center to the intersection point.
            @Override
            public Vec2 getUV() {
                Vec3 radial = intpoint.sub(cent);
                return Util.computeSphereUV(radial);
            }

            // calculates and returns the surface normal vector at the intersection point.
            // If it hasn't been calculated yet, it subtracts the center from the
            // intersection point and normalizes the resulting vector.
            @Override
            public Vec3 getNormal() {
                if (norm == null) {
                    norm = intpoint.sub(cent).normalized();
                }
                return norm;
            }

            @Override
            protected boolean calculateHit() {

                float a = Constants.EPS;
                float b = Constants.EPS;
                float c = Constants.EPS;

                a = ray.dir().dot(ray.dir());

                b = 2.0f * ray.base().sub(cent).dot(ray.dir());
                c = ray.base().sub(cent).dot(ray.base().sub(cent)) - rad * rad;

                float disc;
                disc = b * b - 4 * a * c;

                // It calculates the discriminant and checks if it's negative, indicating no
                // intersection.
                if (disc < 0) {
                    return false;
                }

                float sqrtDisc = (float) Math.sqrt(disc);
                float denom = 2 * a;
                float rootL = (-b - sqrtDisc) / denom;

                // If there is an intersection, it calculates the closest root and checks if
                // it's within the valid range defined by tmin and tmax. If not, it calculates
                // the other root and performs the range check again.

                if (rootL < tmin || rootL > tmax) {

                    rootL = (-b + sqrtDisc) / denom;

                    if (rootL < tmin || rootL > tmax) {
                        return false;
                    }
                }
                // Finally, it assigns the intersection parameter, intersection point, and
                // surface normal to the corresponding variables and returns true to indicate a
                // valid intersection.
                para = rootL;
                intpoint = ray.eval(para);
                norm = intpoint.sub(cent).normalized();
                return true;
            }
        };
    }

    @Override
    public int hashCode() {
        return cent.hashCode() ^ Float.hashCode(rad);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Sphere) {
            Sphere sphere = (Sphere) other;
            return sphere.cent.equals(cent) && sphere.rad == rad;
        }
        return false;
    }
}
