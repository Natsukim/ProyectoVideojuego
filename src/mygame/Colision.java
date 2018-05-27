/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.Vector3f;


/**
 *
 * @author Fernandez
 */

public class Colision implements PhysicsCollisionListener{
    boolean cambio;
    Coche coche1,coche2;
    Caja cajaCoche1;
    Caja cajaCoche2;
    Regalo regalo1;
    Regalo regalo2;
    
    
    public Colision(Coche c1,Coche c2,Caja cc1,Caja cc2,Regalo s1,Regalo s2){        
        coche1=c1;
        coche2=c2;
        cajaCoche1=cc1;
        cajaCoche2=cc2;
        regalo1=s1;
        regalo2=s2;
        cambio=true;
    }    
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
    //si colision es diferente al suelo
        if(!event.getNodeB().getName().equals("Suelo")){                         
            
    //REGALOS__________________________________________
    
        //si alguien colisiona con una regalo1 o regalo2 tiene que activa el arma y mover la Regalo
            if(cambio && event.getNodeB().getName().substring(0,6).equals("Regalo") ){
                cambio=false;
                                                                   
                //Si el Regalo la coge Coche1
                if(event.getNodeA().getName().equals("Coche1")){
                  //  cajaCoche1.usar=true;
                   // coche1.tipoA=Coche.tipoArma.Caja;
                    
                //Si el Regalo la coge Coche2
                } else if(event.getNodeA().getName().equals("Coche2")){
                    cajaCoche2.usar=true;
                    coche2.tipoA=Coche.tipoArma.Caja;
                                        
                }               
                
                if(event.getNodeB().getName().equals("Regalo1")){                    
                    regalo1.cambiarPos(regalo2.id);
                } else {
                    regalo2.cambiarPos(regalo1.id);
                }                                        
                                                        
            }
            
    //CAJAS_____________________________________________________________
            
    //si algo choca contra la caja del COCHE1, si es coche paraliza             
            else if(cambio && event.getNodeB().getName().equals("CajaCoche1")){
                cambio=false;                
                
                if(event.getNodeA().getName().equals("Coche1")){
                    cajaCoche1.posOrigen();
                    coche1.penalizacion();
                    System.out.println(event.getNodeB().getName()+" choco contra "+event.getNodeA().getName());
                    
                }else if(event.getNodeA().getName().equals("Coche2")){
                    cajaCoche1.posOrigen();
                    coche2.penalizacion();
                    System.out.println(event.getNodeB().getName()+" choco contra "+event.getNodeA().getName());                    
                }
            }        
    //si algo choca contra la caja del COCHE2, si es coche paraliza 
            else if(cambio && event.getNodeB().getName().equals("CajaCoche2")){
                cambio=false;     
                
                if(event.getNodeA().getName().equals("Coche1")){
                    cajaCoche2.posOrigen();
                    coche1.penalizacion();
                    System.out.println(event.getNodeB().getName()+" choco contra "+event.getNodeA().getName());
                    
                } else if(event.getNodeA().getName().equals("Coche2")){
                    cajaCoche2.posOrigen();
                    coche2.penalizacion();
                    System.out.println(event.getNodeB().getName()+" choco contra "+event.getNodeA().getName());                    
                }
            }
            
            else if(cambio && event.getNodeA().getName().equals("Coche2") && event.getNodeB().getName().equals("Bola_Esquivo")){
                    cambio=false;
                    coche2.esquivar=false;            
                    coche2.bolaFisica.setPhysicsLocation(new Vector3f(0,-100f,0));
                    
            }        
            
            else if(cambio && event.getNodeA().getName().equals("Coche1") && event.getNodeB().getName().equals("Bola_Esquivo")){
                    cambio=false;
                    coche1.esquivar=false;            
                    coche1.bolaFisica.setPhysicsLocation(new Vector3f(0,-100f,0));
                    
            }
            
        }
    }
    
    
}
