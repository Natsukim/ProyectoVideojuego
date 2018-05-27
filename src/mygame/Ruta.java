/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Fernandez
 */
public class Ruta {
   Vector3f[][] pos = new Vector3f[][]{{
        //ruta_0
       new Vector3f(-46, -4.5f, 400f)
     
           
       },{ 
       //ruta_1
       new Vector3f(-47, -4.5f, 400)
               
       }};  
   int ruta;
   int id; 
   Geometry objetivoGeom;   
   RigidBodyControl objFisico;
   
   public Ruta(int r){
       ruta=r;
       Sphere malla = new Sphere(32,32,0.4f,true,false);
       objetivoGeom=new Geometry("Bola", malla);       
       objFisico = new RigidBodyControl(0f);
       id=0;
   }
   
   public void aplicarFisica(){
       //objetivoGeom.setCullHint(Spatial.CullHint.Always);
      // objFisico.setGravity(Vector3f.ZERO);
       
   }
 
   public Vector3f posicionActual(){
       return pos[0][0];
   }
   
   public void cambiarPos(float dist){
       if(dist<5f){
            id++;
            Vector3f[] v=pos[ruta];
            if(id==v.length){               
                id=0;
            }
            objFisico.setPhysicsLocation(posicionActual());
           
            
       }      
   }   
   
   public void actualizarPos(int i){
       id=i;
       objFisico.setPhysicsLocation(posicionActual());
   }

    
}
