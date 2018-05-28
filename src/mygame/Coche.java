/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Matrix3f;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Fernandez
 */

public class Coche{
    int segundos;
    class Contador extends TimerTask{
        public void run(){
            segundos++;                        
        }
    }            
    
    enum tipoArma{Caja};        
    Ruta objetivo;
    RigidBodyControl cocheFisico;
    RigidBodyControl bolaFisica;      
    Geometry geomBola;    
    Spatial coche;    
    Vector3f[] posIniC = new Vector3f[]{new Vector3f(-46, -4.5f, -60f),new Vector3f(-50, -4.5f,-69f),new Vector3f(0,4.5f,0),new Vector3f(0,4.5f,10f)};
    Timer tiempoParado=new Timer();    
    float velocidad=3f;        
    tipoArma tipoA;    
    boolean cam,esquivar;  
    
                                   
    public Coche(Spatial c,String name,Ruta obj){        
        objetivo = obj;
        coche = c;               
        coche.setName(name);
        coche.scale(0.4f);
        cocheFisico= new RigidBodyControl(1f);                        
        Sphere malla = new Sphere(32,32,0.25f,true,false);
        geomBola = new Geometry("Bola_Esquivo", malla);       
        bolaFisica = new RigidBodyControl(0f);                                
        cam = false;
        esquivar = false;
        segundos = 6;
      
    }
        
    public void aplicarFisica(){           
        geomBola.setCullHint(Spatial.CullHint.Always);
        //bolaFisica.setGravity(Vector3f.ZERO);        
        //cocheFisico.setFriction(0.85f);
        //cocheFisico.setLinearDamping(0.5f);
    }
         
    public void penalizacion(){
        segundos=0;
        tiempoParado=new Timer();
        tiempoParado.schedule(new Contador(),0,1000);
    }    
    
    public void avanzar(float ds1,float ds2,Vector3f pos1,Vector3f pos2){                                                
        if(segundos>5){                                               
            
            if(ds1<8f){
                coche.lookAt(pos1, Vector3f.UNIT_Y);
            }else if(ds2<8f){
                coche.lookAt(pos2, Vector3f.UNIT_Y);
            }else if(esquivar){
                coche.lookAt(geomBola.getLocalTranslation(), Vector3f.UNIT_Y);
            }else{
                coche.lookAt(objetivo.objetivoGeom.getLocalTranslation(), Vector3f.UNIT_Y);                                
            }
            
            cocheFisico.setPhysicsRotation(coche.getLocalRotation());
            Vector3f dirFrente= cocheFisico.getPhysicsRotation().getRotationColumn(2);            
            cocheFisico.setLinearVelocity(new Vector3f(-velocidad*dirFrente.normalize().x,-velocidad*dirFrente.normalize().y,-velocidad*dirFrente.normalize().z));                  
        }
    }
        
    public void esquivo(CollisionResults detecC,CollisionResults detecCC,CollisionResults detecCM,Vector3f posC,Vector3f posCC,Vector3f posCM,float dI,float dD){                              
       Vector3f v=null;
       if(detecC.size() > 0){                      
           v = posC;          
       }else if(detecCC.size() > 0){
           v = posCC;
       }else if(detecCM.size() > 0){
           v = posCM;
       }
       if(v != null){
           esquivar=true;           
           if(dI > dD){
               v = orientarBola(v, false, dI/2f);
           }else{
               v = orientarBola(v, true, dD/2f);
           }
           bolaFisica.setLinearVelocity(Vector3f.ZERO);
           bolaFisica.setPhysicsLocation(v);
       }       
    }
            
    public Ray rayoFrente(){        
        Vector3f direccion=cocheFisico.getPhysicsRotation().getRotationColumn(2);        
        Ray rayo= new Ray (new Vector3f (cocheFisico.getPhysicsLocation().x, cocheFisico.getPhysicsLocation().y+0.1f, cocheFisico.getPhysicsLocation().z),direccion);                    
        return rayo;    
    }
    
    public Ray rayoObstaculo(){        
        Vector3f direccion=cocheFisico.getPhysicsRotation().getRotationColumn(2);
        Ray rayo= new Ray (new Vector3f (cocheFisico.getPhysicsLocation().x, cocheFisico.getPhysicsLocation().y+0.1f, cocheFisico.getPhysicsLocation().z),direccion);            
        rayo.setLimit(8f);
        return rayo;
    }

    
    public Ray rayosIzq(){        
        Matrix3f m = new Matrix3f();
        Vector3f direccion=cocheFisico.getPhysicsRotation().getRotationColumn(2);
        m.fromAngleAxis((float) (Math.PI/2),Vector3f.UNIT_Y);
        direccion=m.mult(direccion);
        Ray rayo= new Ray (new Vector3f (cocheFisico.getPhysicsLocation().x, cocheFisico.getPhysicsLocation().y-0.5f, cocheFisico.getPhysicsLocation().z),direccion);                                                   
        return rayo;
    }
    
    public Ray rayosDer(){        
        Matrix3f m = new Matrix3f();
        Vector3f direccion=cocheFisico.getPhysicsRotation().getRotationColumn(2);
        m.fromAngleAxis((float) -(Math.PI/2),Vector3f.UNIT_Y);
        direccion=m.mult(direccion);
        Ray rayo= new Ray (new Vector3f (cocheFisico.getPhysicsLocation().x, cocheFisico.getPhysicsLocation().y-0.5f, cocheFisico.getPhysicsLocation().z),direccion);                   
        return rayo;
    }
    
    public Vector3f orientarBola(Vector3f pos, boolean der, float dist){
        Vector3f v;        
        switch(objetivo.id){
            case 0:
                if(der){
                    v=new Vector3f(pos.x-dist,pos.y,pos.z);
                }else{
                    v=new Vector3f(pos.x+dist,pos.y,pos.z);
                }
                break;
            case 1:
                if(der){
                    v=new Vector3f(pos.x,pos.y,pos.z+dist);
                }else{
                    v=new Vector3f(pos.x,pos.y,pos.z-dist);
                }                
                break;
            case 2:
                if(der){
                    v=new Vector3f(pos.x+dist,pos.y,pos.z);
                }else{
                    v=new Vector3f(pos.x-dist,pos.y,pos.z);
                }                
                break;            
            case 4:
                if(der){
                    v=new Vector3f(pos.x+dist,pos.y,pos.z);
                }else{
                    v=new Vector3f(pos.x-dist,pos.y,pos.z);
                }                
                break;
            default:
                if(der){
                    v=new Vector3f(pos.x,pos.y,pos.z-dist);
                }else{
                    v=new Vector3f(pos.x,pos.y,pos.z+dist);
                }                
                break;                
        }
        return v;
    }
    
    
}

