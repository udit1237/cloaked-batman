package com.cs.clemson.cloaked.batman;

/**
 *
 * @author emmanueljohn
 */
class AirportLink {

    double weight; // should be private for good practice
    int id;

    public AirportLink(double weight, int edgeId) {
        this.id = edgeId; // This is defined in the outer class.
        this.weight = weight;
    }

    public String toString() { // Always good for debugging
        return "E" + id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AirportLink other = (AirportLink) obj;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
}
