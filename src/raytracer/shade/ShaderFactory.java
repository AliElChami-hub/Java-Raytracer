package raytracer.shade;

import raytracer.core.Shader;
import raytracer.math.Color;
import raytracer.math.Constants;

public final class ShaderFactory {
    private ShaderFactory() {
    }

    /**
     * Generates a checkerboard structured shader. Two shaders are "placed"
     * alternately on the whole structure.
     *
     * @param a
     *              The first shader structure (x + y even)
     * @param b
     *              The second shader structure (x + y odd)
     * @param scale
     *              The size of the tiles
     * @throws IllegalArgumentException
     *                                       If the scale value is not a valid
     *                                       number (must be non-negative,
     *                                       representing a number, not infinity).
     *                                       In addition to that the
     *                                       two shaders must be instance values
     *                                       (not null).
     * @throws UnsupportedOperationException
     *                                       If the scale factor is a non-negative
     *                                       number equal to zero (with respect to
     *                                       epsilon)
     */
    public static Shader createCheckerBoard(final Shader a, final Shader b, final float scale) {
        if (scale < 0 || Float.isNaN(scale) || Float.isInfinite(scale)) {
            throw new IllegalArgumentException("The scale is invalid bro");
        }
        if (a == null || b == null) {
            throw new IllegalArgumentException("Shaders must be not null obviously lmao");
        }

        if (Math.abs(scale) < Constants.EPS) {
            throw new UnsupportedOperationException("Scale factor must be greater than zero");
        }

        return new CheckerBoard(a, b, scale);
    }

    /**
     * Generates a Phong (http://en.wikipedia.org/wiki/Phong_shading) shader.
     *
     * @param inner
     *                  The base shader of this Phong shader
     * @param ambient
     *                  Color of the ambient light emitted to the scene
     * @param diffuse
     *                  The ratio of reflection of the diffuse term of incoming
     *                  light
     * @param specular
     *                  The ratio of reflection of the specular term of incoming
     *                  light
     * @param shininess
     *                  A shininess constant defining this material. The larger the
     *                  value the more "mirror-like" is the structure
     * @throws IllegalArgumentException
     *                                  If the diffuse, specular or shininess parts
     *                                  are not valid
     *                                  numbers (must non-negative, representing a
     *                                  number, not
     *                                  infinity). In addition to that the shader
     *                                  and the ambient
     *                                  color must be instance values (not null).
     */
    public static Shader createPhong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float shininess) {
        if (inner == null || ambient == null) {
            throw new IllegalArgumentException("Shader and ambient color must be not null");
        }
        if (diffuse < 0 || Float.isNaN(diffuse) || Float.isInfinite(diffuse)) {
            throw new IllegalArgumentException("The diffuse value is invalid :) ");
        }
        if (specular < 0 || Float.isNaN(specular) || Float.isInfinite(specular)) {
            throw new IllegalArgumentException("The specular value is invalid .-.  ");
        }
        if (shininess < 0 || Float.isNaN(shininess) || Float.isInfinite(shininess)) {
            throw new IllegalArgumentException("The shininess is not valid! :( ");
        }

        Color diffuseColor, specularColor;
        diffuseColor = new Color(diffuse, diffuse, diffuse);
        specularColor = new Color(specular, specular, specular);

        return new Phong(inner, ambient, diffuseColor, specularColor, shininess);
    }

}
