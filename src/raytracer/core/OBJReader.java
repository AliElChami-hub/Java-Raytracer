package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import raytracer.geom.GeomFactory;

import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.geom.Primitive;
import raytracer.math.Point;
import raytracer.math.Vec3;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *                    The file to read the data from
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the filename is null or the empty string,
	 *                                  the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *                    The InputStream of the data to be read.
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the InputStream is null, the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		if (in == null || accelerator == null) {
			throw new IllegalArgumentException("The input stream is null or the accelerator is null ");
		}

		if (shader == null) {
			throw new IllegalArgumentException("The shader is null");
		}
		if (translate == null || translate.isInfinity()) {
			throw new IllegalArgumentException("The translate vector is null");
		}

		if (!Float.isFinite(scale)) {
			throw new IllegalArgumentException("The scale is not a (finite) floating-point number");
		}
		// the scanner object that we need as followsss:
		Scanner scan = new Scanner(in);
		scan.useLocale(Locale.ENGLISH);
		// the array lists where we store the vertices and the faces, do not mind the
		// names :D:
		List<Point> verticesLOL = new ArrayList<>();
		List<int[]> faces = new ArrayList<>();

		while (scan.hasNextLine()) {
			// it trims the lines and takes the line alone:
			String line = scan.nextLine().trim();

			// Skip comment lines
			if (line.startsWith("#")) {
				continue;
			}
			// it separates the line into tokens and then every token is stored in the array
			// of tokens for each line:
			String[] tokeyyy = line.split("\\s+");
			// if the length of the line is 0 then it is empty so we skip this line and go
			// to the next one:
			if (tokeyyy.length == 0) {
				continue;
			}
			// we take the type of the token to check if it is a vertex or a face as
			// followw:
			String type = tokeyyy[0];

			if (type.equals("v") && tokeyyy.length >= 4) {
				// Vertex definition:
				float xxx = Float.parseFloat(tokeyyy[1]) * scale + translate.x();
				float yyy = Float.parseFloat(tokeyyy[2]) * scale + translate.y();
				float zzz = Float.parseFloat(tokeyyy[3]) * scale + translate.z();
				Point vertex = new Point(xxx, yyy, zzz);
				verticesLOL.add(vertex);
			} else if (type.equals("f") && tokeyyy.length >= 4) {
				// Face definition:
				int[] faceIndices = new int[tokeyyy.length - 1];
				for (int i = 1; i < tokeyyy.length; i++) {
					faceIndices[i - 1] = Integer.parseInt(tokeyyy[i]);
				}
				faces.add(faceIndices);
			}
		}

		scan.close();

		// Create triangles using the collected data and insert them into the
		// accelerator
		for (int[] faceIndices : faces) {
			if (faceIndices.length >= 3) {

				int ind1, ind2, ind3;
				ind1 = faceIndices[0] - 1; // Subtract 1 since OBJ indices start from 1
				ind2 = faceIndices[1] - 1;// Subtract 1 since OBJ indices start from 1
				ind3 = faceIndices[2] - 1;// Subtract 1 since OBJ indices start from 1

				if (ind1 >= 0 && ind1 < verticesLOL.size() &&
						ind2 >= 0 && ind2 < verticesLOL.size() &&
						ind3 >= 0 && ind3 < verticesLOL.size()) {

					Point vert1;
					vert1 = verticesLOL.get(ind1);
					Point vert2;
					vert2 = verticesLOL.get(ind2);
					Point vert3;
					vert3 = verticesLOL.get(ind3);

					Primitive triangle = GeomFactory.createTriangle(vert1, vert2, vert3);
					accelerator.add(new StandardObj(triangle, shader));

				}
			}
		}

	}
}
