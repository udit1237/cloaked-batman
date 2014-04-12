
package com.cs.clemson.cloaked.batman;

/**
 *
 * @author emmanueljohn
 */
class AirportNode {

        int id; // good coding practice would have this as private
        String airportCode;
        
        public AirportNode(String airportCode) {
            //this.id = nodeCount++;
            this.airportCode = airportCode;
        }

        public String toString() { // Always a good idea for debuging
            return "V" + airportCode; // JUNG2 makes good use of these.
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + (this.airportCode != null ? this.airportCode.hashCode() : 0);
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
            final AirportNode other = (AirportNode) obj;
            if ((this.airportCode == null) ? (other.airportCode != null) : !this.airportCode.equals(other.airportCode)) {
                return false;
            }
            return true;
        }
        
        
    }