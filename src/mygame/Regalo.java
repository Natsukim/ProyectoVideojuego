/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Random;

/**
 *
 * @author Fernandez
 */

public class Regalo {
    Spatial regalo;    
    RigidBodyControl regaloFisico;    
    int id=0;
    Vector3f[] posRegalo = new Vector3f[]{
        new Vector3f(-48.5f, -3.7f, -35f),
        new Vector3f(-46.5f, -3.7f, 0f),
        new Vector3f(-48.5f, -3.7f, 20f),
        new Vector3f(-46.5f, -3.7f, 40f),
        new Vector3f(-48.5f, -3.7f, 60f),
        new Vector3f(-46.5f, -3.7f, 80f),
        new Vector3f(-48.5f, -3.7f, 100f),
        
        //Aqui ya ha chocado con el desti
        new Vector3f(-45.5f, -3.7f, 220f),
        new Vector3f(-48.5f, -3.7f, 280f),
        
        };
    
    public Regalo(Spatial r,String name){
        regalo = r;
        regalo.setName(name);
        regalo.setCullHint(Spatial.CullHint.Always);
        regaloFisico = new RigidBodyControl(1f);                
    }
    
    public void propiedades(){
        regaloFisico.setFriction(1f);
    }
    
    public Vector3f posicionActual(){
       return posRegalo[id];
   }
    
    //Cambiar posicion del Regalo
     public void cambiarPos(int id_otra){              
       Random r = new Random();
       int num = r.nextInt(posRegalo.length);       
       while(num==id_otra || num==id){
           num = r.nextInt(posRegalo.length);
       }       
       id++;       
       regaloFisico.setLinearVelocity(Vector3f.ZERO);
       regaloFisico.setPhysicsLocation(posicionActual());
   }
}
