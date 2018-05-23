package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    private BulletAppState estadosFisicos = new BulletAppState();
    public Coche coche;
    public Diana diana;
    public Escena1 escena1;
    public Cubo cubo;
    
    private boolean Disparar = false;
    private boolean activarFisicas= false;
    private boolean controlTeclado= false;
    
    
    // Colisiones
    private CollisionResults resultado = new CollisionResults();
    private Ray limiteSup = new Ray(new Vector3f(100,1,-155), new Vector3f(-200,0,0));
    
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        stateManager.attach(estadosFisicos);
        
        //Camara
        this.flyCam.setEnabled(true);
        cam.setLocation(new Vector3f(0f, 5f, 0f));
        //cam.lookAt(new Vector3f(0f,0f,1f), Vector3f.ZERO);
        this.setDisplayFps(false);
        
        //Metodo para dar iluminacion
        ponerIluminacion();
        
        escena1 = new Escena1(0f, assetManager.loadModel("Scenes/EscenaPista.j3o"), null, estadosFisicos, rootNode, assetManager);
        
        coche = new Coche(50f, null, "Coche", estadosFisicos, rootNode, assetManager);
        coche.inicTeclado();
        
        diana = new Diana(0f, estadosFisicos, rootNode, assetManager);
        //diana.setPhysicsLocation(new Vector3f(0f,40f,-200f));
        cubo = new Cubo(1f, null, "Cubo", estadosFisicos, rootNode, assetManager);
        
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
        coche.calcColisiones();
        //cubo.prePhysicsTick(estadosFisicos.getPhysicsSpace(), tpf);
        if(Disparar){
            if(!cubo.posicionado){
                cubo.prePhysicsTick(estadosFisicos.getPhysicsSpace(), tpf);
                cubo.posicionado = true;
            }else{
                cubo.physicsTick(estadosFisicos.getPhysicsSpace(), tpf);
            }
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void ponerIluminacion(){
        DirectionalLight sun1 = new DirectionalLight();
        DirectionalLight sun2 = new DirectionalLight();
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        
        sun1.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun2.setDirection((new Vector3f(0.5f, -0.5f, 0.5f)).normalizeLocal());
        
        sun1.setColor(ColorRGBA.White);
        sun2.setColor(ColorRGBA.Gray);
        
        rootNode.addLight(sun1);
        rootNode.addLight(sun2);
    }
    
    //------------------------- Prefabricado Escena
    public class Escena1 extends RigidBodyControl implements PhysicsCollisionListener {
        Spatial terreno= null;
        Geometry[] gCarretera;
        Geometry[] gTierra;
        Geometry[] gHierba;
        
        public Escena1(float masa, Spatial spatial, Material mat, BulletAppState estadosFisicos, Node rootNode, AssetManager assetManager){
            super(masa);
            terreno = spatial;
            //if (mat==null) geometria.setMaterial(Main.matPorDefecto);
            rootNode.attachChild(terreno);
            terreno.addControl(this);
            estadosFisicos.getPhysicsSpace().add(this);
            crearEscenario(); //Crear carretera,suelo,etc...
        }

        @Override
        public void collision(PhysicsCollisionEvent event) {
           //System.out.println(event.getNodeB().getName()+" colisionó con "+event.getNodeA().getName());
        }
        
        private void crearEscenario(){
            //--- Carretera
            gCarretera = new Geometry[20];
            for(int i=0; i<20; i++){
                String nombre = "gCarretera"+i;
                gCarretera[i] = new Geometry(nombre,new Box(4f,0.001f,4f));
            }

            //--- Tierra
            gTierra = new Geometry[40];
            for(int i=0; i<40; i++){
                String nombre = "gTierra"+i;
                gTierra[i] = new Geometry(nombre,new Box(4f,0.001f,4f));
            }

            //--- Hierba
            gHierba = new Geometry[400];
            for(int i=0; i<400; i++){
                String nombre = "gHierba"+i;
                gHierba[i]= new Geometry(nombre,new Box(4f,0.001f,4f));
            }
            
            //--- Materiales
            Material mCarretera = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            Material mTierra = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            Material mHierba = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            
            //--- Texturas
            Texture tCarretera = assetManager.loadTexture("Textures/MisTexturas/estrada.jpg");
            Texture tTierra = assetManager.loadTexture("Textures/MisTexturas/moss.JPG");
            Texture tHierba = assetManager.loadTexture("Textures/MisTexturas/grass.jpg");
            
            mCarretera.setTexture("ColorMap",tCarretera);
            mTierra.setTexture("ColorMap", tTierra);
            mHierba.setTexture("ColorMap", tHierba);
            
            for(int i=0; i<20; i++){
                gCarretera[i].setMaterial(mCarretera);
            }

            for(int i=0; i<40; i++){
                gTierra[i].setMaterial(mTierra);
            }

            for(int i=0; i<400; i++){
                gHierba[i].setMaterial(mHierba);
            }
            
            //--- Nodos
            Node nCarretera = new Node();
            for(int i=0; i<20; i++){
                nCarretera.attachChild(gCarretera[i]);
            }

            Node nTierraIzq = new Node();
            for(int i=0; i<20; i++){
                nTierraIzq.attachChild(gTierra[i]);
            }

            Node nTierraDer = new Node();
            for(int i=0; i<20; i++){
                nTierraDer.attachChild(gTierra[i+20]);
            }

            Node nTierra = new Node();
            nTierra.attachChild(nTierraIzq);
            nTierra.attachChild(nTierraDer);

            Node nHierba = new Node();
            for(int i=0; i<400; i++){
                nHierba.attachChild(gHierba[i]);
            }
            
            //--- Transformaciones
            //--- Carretera
            for(int i=0; i<20; i++){
                gCarretera[i].setLocalTranslation(new Vector3f(0f,0.001f,-(float)i*8f));
            }

            //--- Tierra
            for(int i=0; i<20; i++){
                gTierra[i].setLocalTranslation(new Vector3f(-8f,0.001f,-(float)i*8f));
            }

            for(int i=0; i<20; i++){
                gTierra[i+20].setLocalTranslation(new Vector3f(8f,0.001f,-(float)i*8f));
            }

            //--- Hierba
            for(int i=0; i<20; i++){
                //--- Hierba derecha
                gHierba[i].setLocalTranslation(new Vector3f(16f,0.001f,-(float)i*8f));
                gHierba[i+20].setLocalTranslation(new Vector3f(24f,0.001f,-(float)i*8f));
                gHierba[i+40].setLocalTranslation(new Vector3f(32f,0.001f,-(float)i*8f));
                gHierba[i+60].setLocalTranslation(new Vector3f(40f,0.001f,-(float)i*8f));
                gHierba[i+80].setLocalTranslation(new Vector3f(48f,0.001f,-(float)i*8f));
                gHierba[i+100].setLocalTranslation(new Vector3f(56f,0.001f,-(float)i*8f));
                gHierba[i+120].setLocalTranslation(new Vector3f(64f,0.001f,-(float)i*8f));
                gHierba[i+140].setLocalTranslation(new Vector3f(72f,0.001f,-(float)i*8f));
                gHierba[i+160].setLocalTranslation(new Vector3f(80f,0.001f,-(float)i*8f));
                gHierba[i+180].setLocalTranslation(new Vector3f(88f,0.001f,-(float)i*8f));

                //--- Hierba izquierda
                gHierba[i+200].setLocalTranslation(new Vector3f(-16f,0.001f,-(float)i*8f));
                gHierba[i+220].setLocalTranslation(new Vector3f(-24f,0.001f,-(float)i*8f));
                gHierba[i+240].setLocalTranslation(new Vector3f(-32f,0.001f,-(float)i*8f));
                gHierba[i+260].setLocalTranslation(new Vector3f(-40f,0.001f,-(float)i*8f));
                gHierba[i+280].setLocalTranslation(new Vector3f(-48f,0.001f,-(float)i*8f));
                gHierba[i+300].setLocalTranslation(new Vector3f(-56f,0.001f,-(float)i*8f));
                gHierba[i+320].setLocalTranslation(new Vector3f(-64f,0.001f,-(float)i*8f));
                gHierba[i+340].setLocalTranslation(new Vector3f(-72f,0.001f,-(float)i*8f));
                gHierba[i+360].setLocalTranslation(new Vector3f(-80f,0.001f,-(float)i*8f));
                gHierba[i+380].setLocalTranslation(new Vector3f(-88f,0.001f,-(float)i*8f));
            }
            
            rootNode.attachChild(nCarretera);
            rootNode.attachChild(nTierraIzq);
            rootNode.attachChild(nTierraDer);
            rootNode.attachChild(nHierba);
        }
    }
    
    //-------------------------- Prefabricado coche
    public class Coche extends RigidBodyControl implements PhysicsTickListener , PhysicsCollisionListener {
        public boolean activarFisicas, actualizaFuerzas, controlTeclado;
        public BulletAppState estadosFisicos;
        
        public Spatial coche;
        public Vector3f pos= Vector3f.ZERO;
        public String nombre;
        public Node nCoche = new Node();
        
        public float fuerza=20.5f * this.mass;
        public float velocidadDelante=0f;
        public float velocidadDetras=0f;
        
        //public boolean Disparar= false;
        
        public Coche(float masa, Material mat, String nombre, BulletAppState estadosFisicos, Node rootNode, AssetManager assetManager){
            super(masa);
            coche = assetManager.loadModel("Models/Ferrari/Car.scene"); 
            coche.setName(nombre);
            nCoche.attachChild(coche);
            rootNode.attachChild(nCoche);
            coche.addControl(this);
            estadosFisicos.getPhysicsSpace().add(this);
            estadosFisicos.getPhysicsSpace().addCollisionListener(this);
            this.estadosFisicos= estadosFisicos; 
            //this.setGravity(Vector3f.ZERO); 
        }
        
        @Override
        public void prePhysicsTick(PhysicsSpace space, float tpf) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
              
        }

        @Override
        public void physicsTick(PhysicsSpace space, float tpf) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            
        }

        @Override
        public void collision(PhysicsCollisionEvent event) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        public void calcColisiones(){
            coche.collideWith(limiteSup, resultado);
        }
        
        public void inicTeclado() {
            inputManager.addMapping("Avanzar", new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("Atras", new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("Izquierda", new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("Derecha", new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("Disparar", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addListener(analogListener,"Izquierda", "Derecha", "Avanzar", "Atras");
            inputManager.addListener(actionListener, "Disparar");
        }
    
        private ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if(name.equals("Disparar")){
                    if(isPressed){ //Si se inicia el disparo
                        if(!Disparar){
                            Disparar = true;
                        }

                    }else{ //Si se termino el disparo
                        if(Disparar){
                            Disparar = false;
                        }

                    }
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        private AnalogListener analogListener = new AnalogListener(){
            @Override
            public void onAnalog(String name, float value, float tpf) {
                nCoche.setLocalTransform(coche.getWorldTransform()); //El world del geom es World padre
                coche.setLocalTransform(new Transform()); //Se reinicia la transf. local del geom
                if (name.equals("Derecha")){
                    coche.rotate(0, - 0.001f, 0);
                }
                if (name.equals("Izquierda")){
                    coche.rotate( 0, 0.001f, 0);

                }
                if (name.equals("Avanzar")){

                    if(resultado.size()==0){
                        coche.move(0, 0, -(velocidadDelante+(tpf/10)));
                        velocidadDelante = velocidadDelante +(tpf/10);
                        velocidadDetras = 0;

                    }else{
                        //Geometry geomEncontrado = resultado.getClosestCollision().getGeometry();
                        //System.out.println ( geomEncontrado.getName()+ " está a la derecha de 0,0,0 ");
                    }
                }
                if (name.equals("Atras")){
                    coche.move(0,0, velocidadDetras+(tpf/10));
                    velocidadDetras = velocidadDetras + (tpf/10);
                    velocidadDelante = 0;
                    resultado.clear();
                }
            }
        };
    }
    
    //------------------ Prefabricado Diana
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
            gDiana[0].setLocalTranslation(new Vector3f(0f,40f,-149.9f));
            gDiana[1].scale(2f);
            gDiana[1].setLocalTranslation(new Vector3f(0f,40f,-149.95f));
            gDiana[2].scale(15f);
            gDiana[2].setLocalTranslation(new Vector3f(0f,40f,-150.01f));
            gDiana[3].scale(17f);
            gDiana[3].setLocalTranslation(new Vector3f(0f,40f,-150.015f));
            gDiana[4].scale(28f);
            gDiana[4].setLocalTranslation(new Vector3f(0f,40f,-150.025f));
            gDiana[5].scale(30f);
            gDiana[5].setLocalTranslation(new Vector3f(0f,40f,-150.03f));
            gDiana[6].scale(37.5f);
            gDiana[6].setLocalTranslation(new Vector3f(0f,40f,-150.035f));


            //--- Lineas
            gLinea[0].setLocalRotation(new Quaternion().fromAngles(0f, 0f, (float)Math.PI/20));
            for(int i=1; i<20; i++){
                gLinea[i].setLocalRotation(new Quaternion().fromAngles(0f, 0f, ((float)Math.PI/20)+ i*(float)Math.PI/10));
            }

            for(int i=0; i<20; i++){
                gLinea[i].setLocalTranslation(new Vector3f(0f,40f,-150f));
            }

            //--- Numeros
            gNumeros[19].setLocalTranslation(new Vector3f(-2.5f,71f,-150f));
            gNumeros[0].setLocalTranslation(new Vector3f(7.5f,70f,-150f));
            gNumeros[17].setLocalTranslation(new Vector3f(17.5f,65f,-150f));
            gNumeros[3].setLocalTranslation(new Vector3f(25f,57f,-150f));
            gNumeros[12].setLocalTranslation(new Vector3f(30f,48f,-150f));
            gNumeros[5].setLocalTranslation(new Vector3f(31.5f,38f,-150f)); //----PI/2
            gNumeros[9].setLocalTranslation(new Vector3f(30f,28f,-150f));
            gNumeros[14].setLocalTranslation(new Vector3f(25f,18f,-150f));
            gNumeros[1].setLocalTranslation(new Vector3f(17.5f,10.5f,-150f));
            gNumeros[16].setLocalTranslation(new Vector3f(7.5f,5.5f,-150f));
            gNumeros[2].setLocalTranslation(new Vector3f(-2.5f,4f,-150f)); //--- PI
            gNumeros[18].setLocalTranslation(new Vector3f(-12.5f,5.5f,-150f));
            gNumeros[6].setLocalTranslation(new Vector3f(-22.5f,10.5f,-150f));
            gNumeros[15].setLocalTranslation(new Vector3f(-30f,18f,-150f));
            gNumeros[7].setLocalTranslation(new Vector3f(-35f,28f,-150f));
            gNumeros[10].setLocalTranslation(new Vector3f(-36.5f,38f,-150f)); //----3PI/2
            gNumeros[13].setLocalTranslation(new Vector3f(-35f,48f,-150f));
            gNumeros[8].setLocalTranslation(new Vector3f(-30f,57f,-150f));
            gNumeros[11].setLocalTranslation(new Vector3f(-22.5f,65f,-150f));
            gNumeros[4].setLocalTranslation(new Vector3f(-12.5f,70f,-150f));
        }
    }
    
    //-------------------------- Prefabricado cubo
    public class Cubo extends RigidBodyControl implements PhysicsTickListener , PhysicsCollisionListener {
        public boolean activarFisicas, actualizaFuerzas, controlTeclado;
        public BulletAppState estadosFisicos;
        
        private boolean posicionado = false;
        private Geometry gCubo;
        Node nCubo = new Node();
        
        public Cubo(float masa, Material mat, String nombre, BulletAppState estadosFisicos, Node rootNode, AssetManager assetManager){
            super(masa);
            gCubo = new Geometry(nombre,new Box(0.5f,0.5f,0.5f));
            Material mDianaRojo = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            mDianaRojo.setColor("Color", ColorRGBA.Red);
            gCubo.setMaterial(mDianaRojo);
            //cubo.setName(nombre);
            nCubo.attachChild(gCubo);
            rootNode.attachChild(nCubo);
            gCubo.addControl(this);
            estadosFisicos.getPhysicsSpace().add(this);
            estadosFisicos.getPhysicsSpace().addCollisionListener(this);
            this.estadosFisicos= estadosFisicos;
            gCubo.setLocalTranslation(new Vector3f(0f,0.5f,-10f));
            this.setSpatial(gCubo);
        }

        @Override
        public void prePhysicsTick(PhysicsSpace space, float tpf) {
            gCubo.setLocalTranslation(coche.coche.getWorldTranslation());
            this.setSpatial(gCubo);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void physicsTick(PhysicsSpace space, float tpf) {
            System.out.println("gCubo: \n"+ gCubo.getLocalTranslation());
            this.setLinearVelocity(new Vector3f(0f,10f,0f));
            //this.setSpatial(gCubo);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void collision(PhysicsCollisionEvent event) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}