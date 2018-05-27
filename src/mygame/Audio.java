/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;


import com.jme3.audio.AudioNode;

/**
 *
 * @author Fernandez
 */
public class Audio {
    AudioNode audio_base;
    
    public Audio(AudioNode b){
                   
       audio_base = b;
       audio_base.setPositional(false);
       audio_base.setLooping(true);  
       audio_base.setVolume(3);           
    }
    
    public void inicio(){
        audio_base.play();
    }
    
}
