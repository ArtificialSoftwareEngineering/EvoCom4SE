/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.search;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import unalcol.io.Write;

/**
 *
 * @author Jonatan
 */
public class Solution<T> {
    protected T x;
    protected double qx;
    
    public Solution( T x, double qx ){
        this.x = x;
        this.qx = qx;
    }
    
    public Solution( T x, Goal<T> goal ){
        this.x = x;
        this.qx = goal.quality(x);
    }
    
    public double quality( Goal<T> g ){
        if( g.nonStationary() ) qx = g.quality(x);
        return qx;
    }
    
    public T value(){ return x; }
    public double quality(){ return qx; }
    
    public String toString(){
    	String txt = Write.toString(x);
    	escribirTextoArchivo(quality()+ "\r\n" + txt + "\r\n" );
    	return txt + "," + quality();
    }
    
	public void escribirTextoArchivo( String texto ) {
		String ruta = "C:/Refactor/R.txt";
		try(FileWriter fw=new FileWriter( ruta , true );
				FileReader fr=new FileReader( ruta )){
			//Escribimos en el fichero un String y un caracter 97 (a)
			fw.write( texto );
			//fw.write(97);
			//Guardamos los cambios del fichero
			fw.flush();
		}catch(IOException e){
			System.out.println("Error E/S: "+e);
		}

	}
    
}
