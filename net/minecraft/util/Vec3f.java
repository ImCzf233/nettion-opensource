package net.minecraft.util;

public final class Vec3f
{
    private double x;
    private double y;
    private double z;
    
    public Vec3f() {
        this(0.0, 0.0, 0.0);
    }
    
    public Vec3f(final Vec3f vec3f) {
        this(vec3f.x, vec3f.y, vec3f.z);
    }
    
    public Vec3f(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3f setX(final double x) {
        this.x = x;
        return this;
    }
    
    public Vec3f setY(final double y) {
        this.y = y;
        return this;
    }
    
    public Vec3f setZ(final double z) {
        this.z = z;
        return this;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public Vec3f add(final Vec3f vec3f) {
        return this.add(vec3f.x, vec3f.y, vec3f.z);
    }
    
    public Vec3f add(final double n, final double n2, final double n3) {
        return new Vec3f(this.x + n, this.y + n2, this.z + n3);
    }
    
    public Vec3f sub(final Vec3f vec3f) {
        return new Vec3f(this.x - vec3f.x, this.y - vec3f.y, this.z - vec3f.z);
    }
    
    public Vec3f scale(final float n) {
        return new Vec3f(this.x * n, this.y * n, this.z * n);
    }
    
    public Vec3f copy() {
        return new Vec3f(this);
    }
    
    @Override
    public String toString() {
        return "Vec3{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}
