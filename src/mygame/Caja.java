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
import com.jme3.scene.shape.Box;

/**
 *
 * @author Fernandez
 */

public class Caja {

    Coche obj;
    Geometry balaG;
    Spatial bala;
    Vector3f[] posIniC;    
    RigidBodyControl balaFisica;
    int id;    
    boolean usar = false;    
    boolean lanzar=false;
    
//caja
    public Caja(String name,int i) {                
        Box bal = new Box(0.7f,0.7f,0.7f);
        balaG = new Geometry(name, bal);
        balaFisica = new RigidBodyControl(10f);
        id = i;
        posIniC = new Vector3f[]{new Vector3f(100f, -4f, 0),new Vector3f(120,-4,0)};
        
    }
    
    public void aplicarFisicaC() {        
        balaG.setCullHint(Spatial.CullHint.Always);        
    }
    
    public void defensa(float s1,float s2, Vector3f posCS) {        
       if(s1<8f && usar || s2<8f && usar){                       
            balaG.setCullHint(Spatial.CullHint.Inherit);
            balaFisica.setLinearVelocity(Vector3f.ZERO);
            balaFisica.setPhysicsLocation(posCS);
            usar=false;
        }  
    }
    
    public void posOrigen() {
        lanzar = false;     
        if(bala!=null){
            bala.setCullHint(Spatial.CullHint.Always);
        }else{
            balaG.setCullHint(Spatial.CullHint.Always);
        }                        
        balaFisica.setPhysicsLocation(posIniC[id]);        
    }
    
    public Vector3f orientarCaja(Vector3f posCoche,int id){
        Vector3f v;
        float dist=3f;
        float altura=0.5f;
        switch(id){
            case 0:
                v=new Vector3f(posCoche.x,posCoche.y+altura,posCoche.z-dist);
                break;
            case 1:
                v=new Vector3f(posCoche.x-dist,posCoche.y+altura,posCoche.z);
                break;
            case 2:
                v=new Vector3f(posCoche.x,posCoche.y+altura,posCoche.z+dist);
                break;            
            case 4:
                v=new Vector3f(posCoche.x,posCoche.y+altura,posCoche.z+dist);
                break;
            default:
                v=new Vector3f(posCoche.x+dist,posCoche.y+altura,posCoche.z);
                break;                
        }
        return v;
    }
}
