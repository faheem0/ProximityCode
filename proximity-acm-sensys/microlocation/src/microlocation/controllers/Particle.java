package microlocation.controllers;

import java.util.Random;
import java.util.Scanner;

import microlocation.controllers.MathX;
import microlocation.controllers.Point;

/**
 *
 * @author Faheem Zafari
 */
public class Particle {
    
    public float forwardNoise, turnNoise, senseNoise;
    public double x, y,z, orientation;
    public double worldWidth;
    public double worldHeight;
    public double worldLength; //z
    public double probability = 0;
    public Point[] landmarks;
    Random random;
    
    /**
     * Default constructor for a particle
     * 
     * @param landmarks Point array of landmark points for the particle
     * @param width width of the particle's world in pixels
     * @param height height of the particle's world in pixels
     */
    public Particle(Point[] landmarks,double width, double height, double length) {
        this.landmarks = landmarks;
        this.worldWidth = width;
        this.worldHeight = height;
        this.worldLength = length;
        random = new Random();
        x = random.nextFloat() * width; // randomly distribute the particles
        y = random.nextFloat() * height; // randomly distribute the particles
        z = random.nextFloat() * length;
        orientation = random.nextFloat() * 2f * ((float)Math.PI);
        forwardNoise = 0f;
        turnNoise = 0f;
        senseNoise = 0f;        
    }
    
    /**
     * Sets the position of the particle and its relative probability
     * 
     * @param x new x position of the particle
     * @param y new y position of the particle
     * @param orientation new orientation of the particle, in radians
     * @param prob new probability of the particle between 0 and 1
     * @throws Exception 
     */
    public void set(double x, double y,double z, double orientation, double prob) throws Exception {
        if(x < 0 || x >= worldWidth) {
            throw new Exception("X coordinate out of bounds");
        }
        if(y < 0 || y >= worldHeight) {
            throw new Exception("Y coordinate out of bounds");
        }
        if(z < 0 || z >= worldLength) {
            throw new Exception("Z coordinate out of bounds");
        }
        if(orientation < 0 || orientation >= 2 * Math.PI) {
            throw new Exception("X coordinate out of bounds");
        }
        this.x = x;
       // System.out.println(x);
        this.y = y;
        this.z = z;
      //  System.out.println("y "+y);
        this.orientation = orientation;
        this.probability = prob;
    }
    
    /**
     * Sets the noise of the particles measurements and movements
     * 
     * @param Fnoise noise of particle in forward movement
     * @param Tnoise noise of particle in turning
     * @param Snoise noise of particle in sensing position
     */
    public void setNoise(float Fnoise, float Tnoise, float Snoise) {
        this.forwardNoise = Fnoise;
        this.turnNoise = Tnoise;
        this.senseNoise = Snoise;
    }
    
    /**
     * Senses the distance of the particle to each of its landmarks
     * 
     * @return a float array of distances to landmarks
     */
   // public float[] sense(String[] result)
    public double[] sense(String[] result)
    {
       // float[] ret = new float[landmarks.length];
    	double[] ret = new double[landmarks.length];
        //this is where we should use the RSSI values and convert them to distance
        // we can get the RSSI values from the beacons and calculate the distance
        //indeed we should first use RSSI values to estimate position of the robot
        //and use that only
        
        for(int i=0;i<landmarks.length;i++){
        	double e=Double.parseDouble(result[i]);
        	double dist=calculateAccuracy(-55,e);
        /*	 int e = Integer.parseInt(result[i]);
        //	double e=Double.parseDouble(result[i]);
  		    double re = 0.0003351 * Math.exp(0.1103220*-e);
      //  	 double re = 0.0003351 * Math.exp(0.00003220*-e);
  		    re *= 1.75;
  	//	float d= (float) ((e+61.0)/(-26.4432));
  			//	float d= (float) ((e+55.0)/(-26.4432)); //this is the one
  	//	float d= (float) ((e+61.0)/(-30.4432));
  		//    float re=(float) Math.pow(10,d); //ths is the one
       //  Scanner in=new Scanner(System.in);
        ///  System.out.println("Enter distance");
         // float dist=in.nextFloat();
       double dist=(float)re;*/
        	
      // System.out.println("The measurement in distance is "+dist);
  	     //   x = circle((float)x, (float)worldWidth);
  	      //  y = circle((float)y,(float) worldHeight); 
        //	  float dist = (float) MathX.distance(x, y, landmarks[i].x, landmarks[i].y);
            ret[i] = dist + (float)random.nextGaussian() * senseNoise; //may be change this
    //        System.out.println("The measurement in ret[i] is "+ret[i]);
       //     System.out.println(ret[i]);
        }      
        
        return ret;
    }  
    
	protected static double calculateAccuracy(int txPower, double rssi) {
		  if (rssi == 0) {
		    return -1.0; // if we cannot determine accuracy, return -1.
		  }

		  double ratio = rssi*1.0/txPower;
		  if (ratio < 1.0) {
		    return Math.pow(ratio,10);
		  }
		  else {
		    double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;    
		    return accuracy;
		  }
		}
    
    /**
     * Moves the particle's position
     * 
     * @param turn turn value, in degrees
     * @param forward move value, must be >= 0
     */
   /* public void move(float turn, float forward) throws Exception {
        if(forward < 0) {
            throw new Exception("Robot cannot move backwards");
        }
        //in our case movement will b on its own
        orientation = orientation + turn + (float)random.nextGaussian() * turnNoise;
        orientation = circle(orientation, 2f * (float)Math.PI);
        
        double dist = forward + random.nextGaussian() * forwardNoise;
        
        x += Math.cos(orientation) * dist;
        y += Math.sin(orientation) * dist;
        x = circle(x, worldWidth);
        y = circle(y, worldHeight); 
    }*/
    
    /**
     * Calculates the probability of particle based on another particle's sense()
     * 
     * @param measurement distance measurements from another particle's sense()
     * @return the probability of the particle being correct, between 0 and 1
     */
   // public double measurementProb(float[] measurement)
    public double measurementProb(double[] measurement)
    {
        double prob = 1.0;
        for(int i=0;i<landmarks.length;i++) {
           float dist = (float) MathX.distance(x, y, z, landmarks[i].x, landmarks[i].y, landmarks[i].z);     
        
            prob *= MathX.Gaussian(dist, senseNoise, measurement[i]);            
        }      
        
        probability = prob;
        
        return prob;
    }

    private float circle(float num, float length) {         
        while(num > length - 1) num -= length;
        while(num < 0) num += length;
        return num;       
    }
    
    @Override
    public String toString() {
    //    return "[x=" + x + " y=" + y + " orient=" + Math.toDegrees(orientation) + " prob=" +probability +  "]";
    return "["+ x+" "+y+ " "+ z+" ]";
    }
    
}