package raytracer.core.def;

import java.util.ArrayList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {
    private BBox boundingB;
    private List<Obj> objects;
    private BVHBase leftBVH;
    private BVHBase rightBVH;

    // the constructor for the class:

    public BVH() {
        boundingB = null;
        objects = new ArrayList<>();
    }

    // the bounding bix that is just returned as it is:
    @Override
    public BBox bbox() {
        return boundingB;
    }

    /**
     * Adds an object to the acceleration structure
     *
     * @param prim The object to add
     */
    @Override
    public void add(final Obj prim) {
        objects.add(prim);

        // Update the bounding box as follows:
        if (boundingB == null) {
            boundingB = prim.bbox();
        } else {
            boundingB = calculateBoundingBox(objects);
        }
    }

    /**
     * Builds the actual bounding volume hierarchy
     */
    @Override
    public void buildBVH() {
        // if the objects is empty we set the bounding to null and return nothing:
        if (objects.isEmpty()) {
            boundingB = null;
            return;
        }
        // calculate the bounding box i guess :D :
        boundingB = calculateBoundingBox(objects);
        // check if the size does not exceed the threshold:

        if (objects.size() <= BVHBase.THRESHOLD) {
            return;
        }

        Point maxofmiiiinpoints;

        int splitdim;
        // calculate the max of the mun points:

        maxofmiiiinpoints = calculateMaxOfMinPoints();

        // calculating the split dimension if it is x, y, or z:
        splitdim = calculateSplitDimension(boundingB.getMax().sub(boundingB.getMin()));

        float splitPos = boundingB.getMin().get(splitdim)
                + (maxofmiiiinpoints.sub(boundingB.getMin()).get(splitdim) * 0.5f);

        leftBVH = createSubBVH();

        rightBVH = createSubBVH();
        // distribute the objects >_> :
        distributeObjects(leftBVH, rightBVH, splitdim, splitPos);

        leftBVH.buildBVH();
        rightBVH.buildBVH();
    }

    @Override
    public Point calculateMaxOfMinPoints() {
        Point maxminpoints = new Point(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        // calculation of the maximum of the minimum points as follows:
        for (Obj obj : objects) {
            BBox objBoundingBox;
            objBoundingBox = obj.bbox();
            maxminpoints = maxminpoints.max(objBoundingBox.getMin());
        }

        return maxminpoints;
    }

    @Override
    public int calculateSplitDimension(final Vec3 extent) {
        // if 0 then split in x dimension, if 1 split in Y dimension , if 2 split in Z
        // dimension:
        return (extent.x() > extent.y() && extent.x() > extent.z()) ? 0 : (extent.y() > extent.z()) ? 1 : 2;
    }

    @Override
    public void distributeObjects(final BVHBase leftSubBVH, final BVHBase rightSubBVH,
            final int splitDim, final float splitPos) {
        for (Obj obj : objects) {
            BBox objBoundingBox;

            objBoundingBox = obj.bbox();

            float objMinPos;

            objMinPos = objBoundingBox.getMin().get(splitDim);

            (objMinPos <= splitPos ? leftSubBVH : rightSubBVH).add(obj);
        }
    }

    @Override
    public final Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {

        Boolean ishit = boundingB.intersects(obj.bbox());

        if (!ishit) {
            return Hit.No.get();
        }

        Hit closestHit = Hit.No.get();
        float closestT;
        closestT = tMax;

        for (Obj childObj : objects) {
            Hit hit = childObj.hit(ray, this, tMin, closestT);
            if (hit.hits() && hit != Hit.No.get()) {
                closestHit = hit;
                closestT = closestHit.getParameter();
            }
        }

        return closestHit;
    }

    // returning the objects as it is:
    @Override
    public List<Obj> getObjects() {
        return objects;
    }

    // the bounds of the box being calculated:
    private BBox calculateBoundingBox(List<Obj> objects) {
        if (objects.isEmpty()) {
            return null;
        }
        // minimum and maximum points of the objects:
        Point minPoint, maxPoint;
        minPoint = objects.get(0).bbox().getMin();
        maxPoint = objects.get(0).bbox().getMax();
        // calculating the bounds of the box from the points as follows:
        for (int i = 1; i < objects.size(); i++) {

            BBox objBb = objects.get(i).bbox();
            Point objminp, objmaxp;
            objminp = objBb.getMin();
            objmaxp = objBb.getMax();
            float minx = Math.min(minPoint.x(), objminp.x());
            float miny = Math.min(minPoint.y(), objminp.y());
            float minz = Math.min(minPoint.z(), objminp.z());
            minPoint = new Point(minx, miny, minz);
            float maxx = Math.max(maxPoint.x(), objmaxp.x());
            float maxy = Math.max(maxPoint.y(), objmaxp.y());
            float maxz = Math.max(maxPoint.z(), objmaxp.z());
            maxPoint = new Point(maxx, maxy, maxz);
        }
        // just creating the bounding box at the end:
        return BBox.create(minPoint, maxPoint);
    }

    // create a sub bounding box as follows:
    private BVHBase createSubBVH() {
        return new BVH();
    }
}
