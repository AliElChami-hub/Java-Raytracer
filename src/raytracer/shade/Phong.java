package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec3;
import raytracer.core.LightSource;
import raytracer.math.Point;
import raytracer.math.Ray;

public class Phong implements Shader {

    private final Shader innerShader;
    private final Color ambientColor;
    private final Color diffuseColor;
    private final Color specularColor;
    private final float shininess;

    public Phong(Shader innerShader, Color ambientColor, Color diffuseColor, Color specularColor, float shininess) {
        this.innerShader = innerShader;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        // Calculate the normalized surface normal vector
        Vec3 N = hit.getNormal().normalized();

        // Calculate the normalized view direction vector
        Vec3 V = trace.getRay().dir().normalized();

        // Calculate the ambient color contribution
        Color ambient = ambientColor.mul(trace.getScene().getBackground());

        // Initialize diffuse and specular colors
        Color diffuse = Color.BLACK;
        Color specular = Color.BLACK;

        // Iterate over each light source in the scene
        for (LightSource lightSource : trace.getScene().getLightSources()) {
            // Get the position of the light source
            Point lightPosition = lightSource.getLocation();

            // Calculate the direction from the hit point to the light source and normalize
            // it
            Vec3 L = lightPosition.sub(hit.getPoint()).normalized();

            // Create the shadow ray with an offset origin and the light direction
            Ray shadowRay = new Ray(hit.getPoint().add(N.scale(0.0001f)), L);

            // Check if the shadow ray is obstructed by other objects
            boolean Obsray = trace.getScene().hit(shadowRay).hits();

            if (!Obsray) {
                // Calculate the diffuse reflection factor
                float diffuseFactor = Math.max(0, N.dot(L));

                // Get the color of the light source
                Color lightColor = lightSource.getColor();

                // Calculate the diffuse contribution based on the diffuse color, light color,
                // and diffuse factor
                Color diffuseContribution = diffuseColor.mul(lightColor).scale(diffuseFactor);

                // Add the diffuse contribution to the overall diffuse color
                diffuse = diffuse.add(diffuseContribution);

                // Calculate the reflection vector
                Vec3 R = L.reflect(N);

                // Calculate the specular reflection factor
                float specularFactor = (float) Math.pow(Math.max(0, R.dot(V)), shininess);

                // Calculate the specular contribution based on the specular color and specular
                // factor
                Color specularContribution = specularColor.scale(specularFactor);

                // Add the specular contribution to the overall specular color
                specular = specular.add(specularContribution);
            }
        }

        // Apply the inner shader's contribution only to the diffuse component
        Color innerShading = innerShader.shade(hit, trace);
        diffuse = diffuse.mul(innerShading);

        // Combine the ambient, diffuse, and specular colors to obtain the final shading
        // result
        return ambient.add(diffuse).add(specular);
    }

}
