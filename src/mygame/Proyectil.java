/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


/**
 *
 * @author Migue
 */
public class Proyectil extends RigidBodyControl implements PhysicsCollisionListener{
    public Geometry gProyectil;
    public Vector3f posIni = null;
    public boolean posicionado = false;
    public Vector3f Vo;
    public Vector3f posFinal;
    
    public Proyectil(String nombre){
        gProyectil = new Geometry(nombre,new Box(1.25f,1.25f,1.25f));
        posIni = new Vector3f(-47.5f, -2.5f, 120f);   
    }
    
    public void aplicarFisicaC(){        
        gProyectil.setCullHint(Geometry.CullHint.Always);      
    }
    
    public void desaplicarFisicaC(){
        gProyectil.setCullHint(Geometry.CullHint.Never);
        posicionado= true;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       
}
