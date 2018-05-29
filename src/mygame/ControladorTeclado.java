/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.controls.AnalogListener;


/**
 *
 * @author Fernandez
 */
public class ControladorTeclado {    
    Coche coche1;
    Coche coche2;    
    Audio audio;
    boolean izquierda = false;
    
    public ControladorTeclado(Coche c1,Coche c2,Audio a){
        coche1 = c1;
        coche2 = c2;
        audio = a;
    }
    
    AnalogListener analogListener = new AnalogListener(){
        @Override
        public void onAnalog(String name, float value, float tpf) {
                       
            if (name.equals("MuteOFF")){
                    audio.audio_base.setVolume(3);
                    
            }
            if (name.equals("MuteON")){
                    audio.audio_base.setVolume(0);
                    
            }   
        }
    };   

    
}
