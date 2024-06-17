package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
//import raytracer.math.Vec2;

public class CheckerBoard implements Shader {
    // It has three class variables: shaderA, shaderB, and scale.
    // shaderA and shaderB are instances of the Shader interface, representing two
    // different shaders that will be used to shade the alternating checker pattern.
    // scale is a float value that determines the size of the checker pattern.
    private final Shader shadA;
    private final Shader shadB;
    private final float scal;

    public CheckerBoard(Shader shaderA, Shader shaderB, float scale) {
        this.shadA = shaderA;
        this.shadB = shaderB;
        this.scal = scale;
    }

    // The shade method is overridden from the Shader interface. It takes a Hit
    // object and a Trace object as parameters and returns a Color object
    // representing the shading result.
    @Override
    public Color shade(Hit hit, Trace trace) {
        // It first retrieves the UV coordinates of the hit point using hit.getUV()

        // It then calculates ix and iy by dividing the x and y components of uv by
        // scale and flooring the result to get integer values.
        int X, Y;
        X = (int) Math.floor(hit.getUV().x() / scal);
        Y = (int) Math.floor(hit.getUV().y() / scal);

        // The sign of ix or iy is flipped to alternate the checker pattern. If ix is
        // negative and iy is non-negative or vice versa, it multiplies ix by -1.
        if ((X < 0 && Y >= 0) || (X >= 0 && Y < 0)) {
            X = X * -1;
        }
        // Finally, it checks if the sum of ix and iy is even or odd. If it's even, it
        // uses shaderA to shade the hit point and returns the resulting color. If it's
        // odd, it uses shaderB to shade the hit point and returns the resulting color.

        if ((X + Y) % 2 == 0) {
            return shadA.shade(hit, trace);
        } else {
            return shadB.shade(hit, trace);
        }
    }
}
