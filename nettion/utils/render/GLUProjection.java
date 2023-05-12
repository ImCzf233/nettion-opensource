/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.vector.Matrix
 *  org.lwjgl.util.vector.Matrix4f
 */
package nettion.utils.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;


public final class GLUProjection {
    private static GLUProjection instance;
    private final FloatBuffer coords = BufferUtils.createFloatBuffer((int)3);
    private IntBuffer viewport;
    private FloatBuffer modelview;
    private FloatBuffer projection;
    private Vector3D frustumPos;
    private Vector3D[] frustum;
    private Vector3D[] invFrustum;
    private Vector3D viewVec;
    private double displayWidth;
    private double displayHeight;
    private double widthScale;
    private double heightScale;
    private double bra;
    private double bla;
    private double tra;
    private double tla;
    private Line tb;
    private Line bb;
    private Line lb;
    private Line rb;


    public static GLUProjection getInstance() {
        if (instance == null) {
            instance = new GLUProjection();
        }
        return instance;
    }

    public enum ClampMode {
    	   ORTHOGONAL,
    	   DIRECT,
    	   NONE;
    	}


    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Projection project(double x, double y, double z, ClampMode clampModeOutside, boolean extrudeInverted) {
        boolean outsideFrustum;
        if (this.viewport == null || this.modelview == null || this.projection == null) return new Projection(0.0, 0.0, Projection.Type.FAIL);
        Vector3D posVec = new Vector3D(x, y, z);
        boolean[] frustum = this.doFrustumCheck(this.frustum, this.frustumPos, x, y, z);
        boolean bl = outsideFrustum = frustum[0] || frustum[1] || frustum[2] || frustum[3];
        if (outsideFrustum) {
            boolean outsideInvertedFrustum;
            boolean opposite = posVec.sub(this.frustumPos).dot(this.viewVec) <= 0.0;
            boolean[] invFrustum = this.doFrustumCheck(this.invFrustum, this.frustumPos, x, y, z);
            boolean bl2 = outsideInvertedFrustum = invFrustum[0] || invFrustum[1] || invFrustum[2] || invFrustum[3];
            if (extrudeInverted && (!outsideInvertedFrustum || outsideInvertedFrustum && clampModeOutside != ClampMode.NONE)) {
                if (extrudeInverted && !outsideInvertedFrustum || clampModeOutside == ClampMode.DIRECT && outsideInvertedFrustum) {
                    double vecX = 0.0;
                    double vecY = 0.0;
                    if (!GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)this.modelview, (FloatBuffer)this.projection, (IntBuffer)this.viewport, (FloatBuffer)this.coords)) return new Projection(0.0, 0.0, Projection.Type.FAIL);
                    if (opposite) {
                        vecX = this.displayWidth * this.widthScale - (double)this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0;
                        vecY = this.displayHeight * this.heightScale - (this.displayHeight - (double)this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0;
                    } else {
                        vecX = (double)this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0;
                        vecY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0;
                    }
                    Vector3D vec = new Vector3D(vecX, vecY, 0.0).snormalize();
                    vecX = vec.x;
                    vecY = vec.y;
                    Line vectorLine = new Line(this.displayWidth * this.widthScale / 2.0, this.displayHeight * this.heightScale / 2.0, 0.0, vecX, vecY, 0.0);
                    double angle = Math.toDegrees(Math.acos(vec.y / Math.sqrt(vec.x * vec.x + vec.y * vec.y)));
                    if (vecX < 0.0) {
                        angle = 360.0 - angle;
                    }
                    Vector3D intersect = new Vector3D(0.0, 0.0, 0.0);
                    intersect = angle >= this.bra && angle < this.tra ? this.rb.intersect(vectorLine) : (angle >= this.tra && angle < this.tla ? this.tb.intersect(vectorLine) : (angle >= this.tla && angle < this.bla ? this.lb.intersect(vectorLine) : this.bb.intersect(vectorLine)));
                    return new Projection(intersect.x, intersect.y, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
                }
                if (clampModeOutside != ClampMode.ORTHOGONAL || !outsideInvertedFrustum) return new Projection(0.0, 0.0, Projection.Type.FAIL);
                if (!GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)this.modelview, (FloatBuffer)this.projection, (IntBuffer)this.viewport, (FloatBuffer)this.coords)) return new Projection(0.0, 0.0, Projection.Type.FAIL);
                double guiX = (double)this.coords.get(0) * this.widthScale;
                double guiY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale;
                if (opposite) {
                    guiX = this.displayWidth * this.widthScale - guiX;
                    guiY = this.displayHeight * this.heightScale - guiY;
                }
                if (guiX < 0.0) {
                    guiX = 0.0;
                } else if (guiX > this.displayWidth * this.widthScale) {
                    guiX = this.displayWidth * this.widthScale;
                }
                if (guiY < 0.0) {
                    guiY = 0.0;
                    return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
                } else {
                    if (guiY <= this.displayHeight * this.heightScale) return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
                    guiY = this.displayHeight * this.heightScale;
                }
                return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
            }
            if (!GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)this.modelview, (FloatBuffer)this.projection, (IntBuffer)this.viewport, (FloatBuffer)this.coords)) return new Projection(0.0, 0.0, Projection.Type.FAIL);
            double guiX = (double)this.coords.get(0) * this.widthScale;
            double guiY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale;
            if (!opposite) return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
            guiX = this.displayWidth * this.widthScale - guiX;
            guiY = this.displayHeight * this.heightScale - guiY;
            return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
        }
        if (!GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)this.modelview, (FloatBuffer)this.projection, (IntBuffer)this.viewport, (FloatBuffer)this.coords)) return new Projection(0.0, 0.0, Projection.Type.FAIL);
        double guiX = (double)this.coords.get(0) * this.widthScale;
        double guiY = (this.displayHeight - (double)this.coords.get(1)) * this.heightScale;
        return new Projection(guiX, guiY, Projection.Type.INSIDE);
    }

    public boolean[] doFrustumCheck(Vector3D[] frustumCorners, Vector3D frustumPos, double x, double y, double z) {
        Vector3D point = new Vector3D(x, y, z);
        boolean c1 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[3], frustumCorners[0]}, point);
        boolean c2 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[0], frustumCorners[1]}, point);
        boolean c3 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[1], frustumCorners[2]}, point);
        boolean c4 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[2], frustumCorners[3]}, point);
        return new boolean[]{c1, c2, c3, c4};
    }

    public boolean crossPlane(Vector3D[] plane, Vector3D point) {
        Vector3D z = new Vector3D(0.0, 0.0, 0.0);
        Vector3D e0 = plane[1].sub(plane[0]);
        Vector3D e1 = plane[2].sub(plane[0]);
        Vector3D normal = e0.cross(e1).snormalize();
        double D = z.sub(normal).dot(plane[2]);
        double dist = normal.dot(point) + D;
        if (dist >= 0.0) {
            return true;
        }
        return false;
    }

    public Vector3D[] getFrustum() {
        return this.frustum;
    }

    public static class Line {
        public Vector3D sourcePoint = new Vector3D(0.0, 0.0, 0.0);
        public Vector3D direction = new Vector3D(0.0, 0.0, 0.0);

        public Line(double sx, double sy, double sz, double dx, double dy, double dz) {
            this.sourcePoint.x = sx;
            this.sourcePoint.y = sy;
            this.sourcePoint.z = sz;
            this.direction.x = dx;
            this.direction.y = dy;
            this.direction.z = dz;
        }

        public Vector3D intersect(Line line) {
            double a = this.sourcePoint.x;
            double b = this.direction.x;
            double c = line.sourcePoint.x;
            double d = line.direction.x;
            double e = this.sourcePoint.y;
            double f = this.direction.y;
            double g = line.sourcePoint.y;
            double h = line.direction.y;
            double te = - a * h - c * h - d * (e - g);
            double be = b * h - d * f;
            if (be == 0.0) {
                return this.intersectXZ(line);
            }
            double t = te / be;
            Vector3D result = new Vector3D(0.0, 0.0, 0.0);
            result.x = this.sourcePoint.x + this.direction.x * t;
            result.y = this.sourcePoint.y + this.direction.y * t;
            result.z = this.sourcePoint.z + this.direction.z * t;
            return result;
        }

        private Vector3D intersectXZ(Line line) {
            double a = this.sourcePoint.x;
            double b = this.direction.x;
            double c = line.sourcePoint.x;
            double d = line.direction.x;
            double e = this.sourcePoint.z;
            double f = this.direction.z;
            double g = line.sourcePoint.z;
            double h = line.direction.z;
            double te = - a * h - c * h - d * (e - g);
            double be = b * h - d * f;
            if (be == 0.0) {
                return this.intersectYZ(line);
            }
            double t = te / be;
            Vector3D result = new Vector3D(0.0, 0.0, 0.0);
            result.x = this.sourcePoint.x + this.direction.x * t;
            result.y = this.sourcePoint.y + this.direction.y * t;
            result.z = this.sourcePoint.z + this.direction.z * t;
            return result;
        }

        private Vector3D intersectYZ(Line line) {
            double a = this.sourcePoint.y;
            double b = this.direction.y;
            double c = line.sourcePoint.y;
            double d = line.direction.y;
            double e = this.sourcePoint.z;
            double f = this.direction.z;
            double g = line.sourcePoint.z;
            double h = line.direction.z;
            double te = - a * h - c * h - d * (e - g);
            double be = b * h - d * f;
            if (be == 0.0) {
                return null;
            }
            double t = te / be;
            Vector3D result = new Vector3D(0.0, 0.0, 0.0);
            result.x = this.sourcePoint.x + this.direction.x * t;
            result.y = this.sourcePoint.y + this.direction.y * t;
            result.z = this.sourcePoint.z + this.direction.z * t;
            return result;
        }
    }

    public static class Projection {
        private final double x;
        private final double y;
        private final Type t;

        public Projection(double x, double y, Type t) {
            this.x = x;
            this.y = y;
            this.t = t;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public Type getType() {
            return this.t;
        }

        public enum Type {
            INSIDE,
            OUTSIDE,
            INVERTED,
            FAIL;
        }
    }

    public static class Vector3D {
        public double x;
        public double y;
        public double z;

        public Vector3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3D add(Vector3D v) {
            return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
        }

        public Vector3D add(double x, double y, double z) {
            return new Vector3D(this.x + x, this.y + y, this.z + z);
        }

        public Vector3D sub(Vector3D v) {
            return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        public double dot(Vector3D v) {
            return this.x * v.x + this.y * v.y + this.z * v.z;
        }

        public Vector3D cross(Vector3D v) {
            return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        }

        public double length() {
            return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        }

        public Vector3D snormalize() {
            double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
            this.x /= len;
            this.y /= len;
            this.z /= len;
            return this;
        }

        public String toString() {
            return "(X: " + this.x + " Y: " + this.y + " Z: " + this.z + ")";
        }
    }
}

