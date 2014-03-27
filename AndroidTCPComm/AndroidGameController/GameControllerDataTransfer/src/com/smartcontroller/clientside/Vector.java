package com.smartcontroller.clientside;

public class Vector {

	private double x;
	private double y;

	private static final double TO_DEGREES = 180 / Math.PI;
	private static final double TO_RADIANS = Math.PI / 180;
	private Vector temp;

	public Vector(double x, double y) {

		this.x = x;
		this.y = y;
	}

	public Vector reset(double x, double y) {

		this.x = x;
		this.y = y;

		return this;
	}

	public Vector clone() {
		return new Vector(this.x, this.y);
	}

	public String toString(int decPlaces) {
		double scalar = Math.pow(10, decPlaces);
		return "[" + Math.round(this.x * scalar) / scalar + ", "
				+ Math.round(this.y * scalar) / scalar + "]";
	}

	public void copyFrom(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}

	public double magnitude() {
		return Math.sqrt((this.x * this.x) + (this.y * this.y));
	}

	public double magnitudeSquared() {
		return (this.x * this.x) + (this.y * this.y);
	}

	public Vector normalise() {
		double m = this.magnitude();

		this.x = this.x / m;
		this.y = this.y / m;

		return this;
	}

	public Vector reverse() {
		this.x = -this.x;
		this.y = -this.y;

		return this;
	}

	public Vector plusEq(Vector v) {
		this.x += v.x;
		this.y += v.y;

		return this;
	}

	public Vector plusNew(Vector v) {
		return new Vector(this.x + v.x, this.y + v.y);
	}

	public Vector minusEq(Vector v) {
		this.x -= v.x;
		this.y -= v.y;

		return this;
	}

	public Vector minusNew(Vector v) {
		return new Vector(this.x - v.x, this.y - v.y);
	}

	public Vector multiplyEq(double scalar) {
		this.x *= scalar;
		this.y *= scalar;

		return this;
	}

	public Vector multiplyNew(double scalar) {
		Vector returnvec = this.clone();
		return returnvec.multiplyEq(scalar);
	}

	public Vector divideEq(double scalar) {
		this.x /= scalar;
		this.y /= scalar;

		return this;
	}

	public Vector divideNew(double scalar) {
		Vector returnvec = this.clone();
		return returnvec.divideEq(scalar);
	}

	public double dot(Vector v) {
		return (this.x * v.x) + (this.y * v.y);
	}

	public double angle(boolean useRadians) {

		return Math.atan2(this.y, this.x) * (useRadians ? 1 : TO_DEGREES);
	}

	public Vector rotate(double angle, boolean useRadians) {
		double cosRY = Math.cos(angle * (useRadians ? 1 : TO_RADIANS));
		double sinRY = Math.sin(angle * (useRadians ? 1 : TO_RADIANS));

		temp.copyFrom(this);

		this.x = (temp.x * cosRY) - (temp.y * sinRY);
		this.y = (temp.x * sinRY) + (temp.y * cosRY);

		return this;
	}

	public boolean equals(Vector v) {
		return ((this.x == v.x) && (this.y == v.x));
	}

	public boolean isMagnitudeLessThan(double distance) {
		return (this.magnitudeSquared() < distance * distance);
	}

	public boolean isMagnitudeMoreThan(double distance) {
		return (this.magnitudeSquared() > distance * distance);
	}

}
