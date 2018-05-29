package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Line;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */

public class Main extends SimpleApplication {
    
    private BulletAppState estadosFisicos;
    private Geometry meta;   
    private RigidBodyControl metaFisica;
    private Coche coche2,coche1;
    private Ruta nav,nav2;
    private Regalo regalo1,regalo2;
    private Spatial suelo;        
    private Audio audio;
    private RigidBodyControl sueloFisico;    
    private Colision colision;
    private ControladorTeclado cntT;    
    float tiempo = 0; 
    Diana diana;
    boolean cocheParado = false;
    
    public static Main app = new Main();
     
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);                
        app.start();
        
    }

    @Override
    public void simpleInitApp() {
        estadosFisicos = new BulletAppState();
        stateManager.attach(estadosFisicos);

        //Cambiar pantalla a 640*480
        settings.setWidth(640);
        settings.setHeight(480);
        app.setSettings(settings);
        app.restart();
        
        
        setDisplayFps(false);
        setDisplayStatView(false);                
        flyCam.setEnabled(false);                        
    
        
    //Crear diana
    diana = new Diana(0f, estadosFisicos, rootNode, assetManager);    
        
        
    //crear Audio        
        AudioNode audio_base = new AudioNode(assetManager, "Sounds/base2.wav", AudioData.DataType.Stream);
        audio=new Audio(audio_base);
        rootNode.attachChild(audio_base);     
                
        
    //luz direccional
        DirectionalLight sun1 = new DirectionalLight();
        DirectionalLight sun2 = new DirectionalLight();
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        
        sun1.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun2.setDirection((new Vector3f(0.5f, -0.5f, 0.5f)).normalizeLocal());
        
        sun1.setColor(ColorRGBA.White);
        sun2.setColor(ColorRGBA.Gray);
        
        rootNode.addLight(sun1);
        rootNode.addLight(sun2);

    //carga esqueleto mapa y da propiedades
        suelo = assetManager.loadModel("Scenes/colision.j3o");
        suelo.setName("Suelo");
        sueloFisico = new RigidBodyControl(0.0f);                
        integrarObjeto(suelo, sueloFisico, estadosFisicos, new Vector3f(0f, -4.1f,0f), 0);
        sueloFisico.setRestitution(0.9f);
        sueloFisico.setFriction(0.5f);
        
    //crear ruta para coches
        nav = new Ruta(0);
        integrarObjeto(nav.objetivoGeom, nav.objFisico, estadosFisicos, nav.posicionActual(), "");
        nav.aplicarFisica();
        
    //crear coche1        
        coche1 = new Coche(assetManager.loadModel("Models/Buggy/Buggy.j3o"),"Coche1",nav);
        integrarObjeto(coche1.coche, coche1.cocheFisico, estadosFisicos, coche1.posIniC[0], 0);
        integrarObjeto(coche1.geomBola, coche1.bolaFisica, estadosFisicos, coche1.posIniC[2], "");
        coche1.aplicarFisica();        
        coche1.cam = true;

    //crear regalos        
        regalo1 = new Regalo(assetManager.loadModel("Models/Teapot/Teapot.obj"),"Regalo1");                                                
        integrarObjeto(regalo1.regalo, regalo1.regaloFisico, estadosFisicos, regalo1.posicionActual(),0);
        regalo1.propiedades();

        regalo2 = new Regalo(assetManager.loadModel("Models/Teapot/Teapot.obj"),"Regalo2");              
        integrarObjeto(regalo2.regalo, regalo2.regaloFisico, estadosFisicos, regalo2.posicionActual(), 0);            
        regalo2.cambiarPos(0);
        regalo2.propiedades();
                
    //crear colision    
        colision = new Colision(coche1,coche2,regalo1,regalo2);
        estadosFisicos.getPhysicsSpace().addCollisionListener(colision);                           
        
    //cargar Teclado
        cntT= new ControladorTeclado(coche1,coche2,audio);
        inicTeclado();
   }

    @Override
    public void simpleUpdate(float tpf) {
        tiempo = tiempo + tpf;
        
       
    //posiciones de los coches
//        Vector3f posCoche2 = coche2.cocheFisico.getPhysicsLocation();        
        Vector3f posCoche1 = coche1.cocheFisico.getPhysicsLocation();        
        
   //posicion de la camara______________________________________________________       
       if(!cocheParado){
           Vector3f camP = posCoche1;        
           Vector3f parteTrasera = posicionCamara(camP, nav.id);                                                
           cam.setLocation( parteTrasera );
           cam.lookAt( camP, Vector3f.UNIT_Y);
       } else {
           this.flyCam.setEnabled(true);
           cam.setLocation(new Vector3f(-47.5f, 0f, 110));
           this.flyCam.setMoveSpeed(10);
           this.flyCam.setRotationSpeed(2.5f);
           
       }
        
    
       
        
        audio.inicio();
        
        
        
//Rayos coche Coche1+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++    

    //Detecta distancia con pared del coche enemigo
        CollisionResults rayoI_Coche1 = new CollisionResults();
        suelo.collideWith(coche1.rayosIzq(),rayoI_Coche1);
        float distancia_Coche1_Izq=0;        
        if (rayoI_Coche1.getClosestCollision() != null){
            distancia_Coche1_Izq=rayoI_Coche1.getClosestCollision().getDistance();
        }
        CollisionResults rayoD_Coche1 = new CollisionResults();
        suelo.collideWith(coche1.rayosDer(),rayoD_Coche1);
        float distancia_Coche1_Der=0;
        if(rayoD_Coche1.getClosestCollision()!=null){
            distancia_Coche1_Der=rayoD_Coche1.getClosestCollision().getDistance();        
        }                
  
        
//--------------------------------------------------------------------------------------------------------------------------------------

//AQUI PARA LA CAJA-----

    //Lanzar caja para defensa Coche1
        float regalo1_Coche1 = coche1.cocheFisico.getPhysicsLocation().distance(regalo1.regaloFisico.getPhysicsLocation());
        float regalo2_Coche1 = coche1.cocheFisico.getPhysicsLocation().distance(regalo2.regaloFisico.getPhysicsLocation());        
       // Vector3f pos_CCoche1 = cajaCoche1.orientarCaja(posCoche1, nav.id);        
      //  cajaCoche1.defensa(regalo1_Coche1,regalo2_Coche1,pos_CCoche1);        

//HASTA AQUI PARA TIRAR LA CAJA        


            
    //Navegacion Jugador    
            float distancia_NavCoche1 = coche1.cocheFisico.getPhysicsLocation().distance(nav.objFisico.getPhysicsLocation());
            nav.cambiarPos(distancia_NavCoche1);
            coche1.avanzar(regalo1_Coche1, regalo2_Coche1,regalo1.regalo.getLocalTranslation(),regalo2.regalo.getLocalTranslation());
            
            //PARA PARAR EL COCHE
            if(coche1.cocheFisico.getPhysicsLocation().z >= 120){               
                coche1.cocheFisico.setLinearVelocity(new Vector3f(0, 0, 0));
                coche1.cocheFisico.setPhysicsLocation(new Vector3f(-47.5f, -4.5f, 120));
                cocheParado = true;
                
            }
            
    //actualizador semaforo para Colisiones
        if(!colision.cambio){            
            colision.cambio=true;
        }        
                
           
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render cod  e
    }
    
    private void inicTeclado() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S)); 
        inputManager.addMapping("MuteOFF", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("MuteON", new KeyTrigger(KeyInput.KEY_M));                
        inputManager.addListener(cntT.analogListener, "Left","Right","Up","Down","MuteOFF","MuteON");
    }
    
    public void integrarObjeto(Spatial objetoVisual, RigidBodyControl objetoFisico, BulletAppState estadosFisicos, Vector3f posicion, int giro) {
        rootNode.attachChild(objetoVisual);                                               //integración en el mundo visual 
        objetoVisual.addControl(objetoFisico);                                          //Asociación  objeto visual-fisico
        estadosFisicos.getPhysicsSpace().add(objetoFisico);                  //integración en el mundo físico        
        objetoFisico.setPhysicsLocation(posicion);
    }

    public  void  integrarObjeto (Geometry objetoVisual, RigidBodyControl objetoFisico, BulletAppState estadosFisicos, Vector3f posicion, String textura){ 
        Material material;
        if (textura.equals("caja")){
            material = assetManager.loadMaterial("Materials/Generated/caja.j3m");            
        }else{  
            material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");  
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Diffuse", new ColorRGBA(0.7f, (float) Math.random(), 0.5f, 1f));  
        }        
        objetoVisual.setMaterial(material);    
        rootNode.attachChild(objetoVisual);                                               //integración en el mundo visual 
        objetoVisual.addControl(objetoFisico );                                          //Asociación  objeto visual-fisico
        estadosFisicos.getPhysicsSpace().add( objetoFisico );                  //integración en el mundo físico        
        if(posicion==null){
            posicion=new Vector3f(-40,-4,14);
            Matrix3f mat = new Matrix3f();
            mat.fromAngleAxis((float) -(Math.PI/2),Vector3f.UNIT_Y);
            objetoFisico.setPhysicsRotation(mat);
        }
       objetoFisico.setPhysicsLocation(posicion);
    }
    
    public Vector3f posicionCamara(Vector3f pos,int id){    
        Vector3f v=pos;
        float dist=20f;
        float altura=4.5f;
        switch(id){
            case 0:
                
                v=new Vector3f(v.x,v.y+altura,v.z-dist);
                break;
            case 1:
                
                v=new Vector3f(v.x-dist,v.y+altura,v.z);
                break;
            case 2:
                
                v=new Vector3f(v.x,v.y+altura,v.z+dist);
                break;            
            case 4:
                
                v=new Vector3f(v.x,v.y+altura,v.z+dist);
                break;
            default:
                
                v=new Vector3f(v.x+dist,v.y+altura,v.z);
                break;                
        }
        return v;
    }
 
    public class Diana extends RigidBodyControl implements PhysicsCollisionListener {
        public boolean activarFisicas;
        public BulletAppState estadosFisicos;
        
        Geometry[] gDiana = new Geometry[7];
        Geometry[] gLinea = new Geometry[20];
        Geometry[] gNumeros = new Geometry[20];
        public Node nDiana = new Node();
        public Node nLineas = new Node();
        public Node nNumeros = new Node();
        public Node nDianaEntera = new Node();
        
        public Diana(float masa, BulletAppState estadosFisicos, Node rootNode, AssetManager assetManager){
            super(masa);
            //--- Diana
            for(int i=0; i<7; i++){
                String nombre = "gDiana"+i;
                gDiana[i] = new Geometry(nombre+i,new Cylinder(30, 40, 1f, 0.001f, true));
                nDiana.attachChild(gDiana[i]);
            }
            nDianaEntera.attachChild(nDiana);
            nDianaEntera.attachChild(nLineas);
            nDianaEntera.attachChild(nNumeros);
            rootNode.attachChild(nDianaEntera);
            crearDiana();
        }

        @Override
        public void collision(PhysicsCollisionEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        private void crearDiana(){
            //------------------------ Geometrys
            
            //--- Lineas
            for(int i=0; i<20; i++){
                String nombre = "gDiana"+i;
                gLinea[i] = new Geometry(nombre,new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,32f,0f)));
                nLineas.attachChild(gLinea[i]);
            }

            //--- Numeros
            for(int i=0; i<20; i++){
                String nombre = "gNumeros"+(i+1);
                gNumeros[i] = new Geometry(nombre,new Quad(5f,5f));
                nNumeros.attachChild(gNumeros[i]);
            }
            
            //------------------------ Materiales
            Material mDianaRojo = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            Material mDianaVerde = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            Material mDianaNegro = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            Material mNumeros[] = new Material[20];
            for(int i=0; i<20; i++){
                mNumeros[i] = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            }
            
            //------------------------- Texturas
            Texture tNumeros[] = new Texture[20];
            for(int i=1; i<=20; i++){
                String dir = "Textures/MisTexturas/"+i+".jpg";
                tNumeros[i-1] = assetManager.loadTexture(dir);
            }
            
            for(int i=0; i<20; i++){
                mNumeros[i].setTexture("ColorMap",tNumeros[i]);
            }
            
            mDianaRojo.setColor("Color", ColorRGBA.Red);
            mDianaVerde.setColor("Color",new ColorRGBA(1f, 1f, 1f, 0f));
            mDianaNegro.setColor("Color", ColorRGBA.Black);
            
            gDiana[0].setMaterial(mDianaRojo);
            gDiana[1].setMaterial(mDianaVerde);
            gDiana[2].setMaterial(mDianaRojo);
            gDiana[3].setMaterial(mDianaVerde);
            gDiana[4].setMaterial(mDianaRojo);
            gDiana[5].setMaterial(mDianaVerde);
            gDiana[6].setMaterial(mDianaNegro);

            for(int i=0; i<20; i++){
                gLinea[i].setMaterial(mDianaNegro);
            }

            for(int i=0; i<20; i++){
                gNumeros[i].setMaterial(mNumeros[i]);
            }
            
            //--------------------- Transformaciones Locales
            //------ Diana
            //--- Circulos
            gDiana[0].setLocalTranslation(new Vector3f(-47.5f,40f,268.95f));
            gDiana[1].scale(2f);
            gDiana[1].setLocalTranslation(new Vector3f(-47.5f,40f,269.15f));
            gDiana[2].scale(15f);
            gDiana[2].setLocalTranslation(new Vector3f(-47.5f,40f,269.35f));
            gDiana[3].scale(17f);
            gDiana[3].setLocalTranslation(new Vector3f(-47.5f,40f,269.55f));
            gDiana[4].scale(28f);
            gDiana[4].setLocalTranslation(new Vector3f(-47.5f,40f, 269.75f));
            gDiana[5].scale(30f);
            gDiana[5].setLocalTranslation(new Vector3f(-47.5f,40f, 269.95f));
            gDiana[6].scale(37.5f);
            gDiana[6].setLocalTranslation(new Vector3f(-47.5f, 40f, 270));


            //--- Lineas
            gLinea[0].setLocalRotation(new Quaternion().fromAngles(0f, 0f, (float)Math.PI/20));
            for(int i=1; i<20; i++){
                gLinea[i].setLocalRotation(new Quaternion().fromAngles(0f, 0f, ((float)Math.PI/20)+ i*(float)Math.PI/10));
            }
            //posicion lineas
            for(int i=0; i<20; i++){
                gLinea[i].setLocalTranslation(new Vector3f(-47.5f, 40 ,268f));                
            }
            
            
            //Rotar los numeros, porque estaban al reves al trasladarlo a este proyecto
            for(int i = 0; i < 20 ; i++){
                gNumeros[i].setLocalRotation(new Quaternion().fromAngles(0f, 84.8f, 0));
            }
                 

            //--- Numeros
            gNumeros[19].setLocalTranslation(new Vector3f(-45f,70.8f,268f));                        
            gNumeros[0].setLocalTranslation(new Vector3f(-53.9f,69.5f,268));
            gNumeros[17].setLocalTranslation(new Vector3f(-64.6f,64.3f,268));
            
            gNumeros[3].setLocalTranslation(new Vector3f(-70.5f,58f,268));
            gNumeros[12].setLocalTranslation(new Vector3f(-76.5f,47.5f,268));
            gNumeros[5].setLocalTranslation(new Vector3f(-78f,38f,268)); //----PI/2
            
            gNumeros[9].setLocalTranslation(new Vector3f(-77f,28f,268));
            gNumeros[14].setLocalTranslation(new Vector3f(-73.2f,19.4f,268));
            gNumeros[1].setLocalTranslation(new Vector3f(-65f,9.9f,268));
            
            gNumeros[16].setLocalTranslation(new Vector3f(-56.5f,5.5f,268));
            gNumeros[2].setLocalTranslation(new Vector3f(-45.7f,3.4f,268)); //--- PI
            gNumeros[18].setLocalTranslation(new Vector3f(-36f,5f,268));
            
            gNumeros[6].setLocalTranslation(new Vector3f(-27f,10f,268));
            gNumeros[15].setLocalTranslation(new Vector3f(-18.5f,18f,268));
            gNumeros[7].setLocalTranslation(new Vector3f(-12.5f,27.7f,268));  
            
            gNumeros[10].setLocalTranslation(new Vector3f(-12f,38f,268)); //----3PI/2
            gNumeros[13].setLocalTranslation(new Vector3f(-13.5f,47f,268));
            gNumeros[8].setLocalTranslation(new Vector3f(-17.2f,57f,268));
            
            gNumeros[11].setLocalTranslation(new Vector3f(-24.8f,64.7f,268));
            gNumeros[4].setLocalTranslation(new Vector3f(-34.7f,69.6f,268));
        }
    }
    
    
}
