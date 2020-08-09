package main.java.worker;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.renderer.Camera;

import jme3tools.optimize.GeometryBatchFactory;
import main.java.entity.Actor;
import main.java.entity.Egg;
import main.java.entity.Entity;
import main.java.entity.Meat;
import main.java.entity.Resource;
import main.java.entity.Tree;
import main.java.entity.Water;
import main.java.entity.Wheat;
import main.java.entity.WheatGrain;
import main.java.entity.Wood;
import main.java.entity.WoodTool;
import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

public class Main extends SimpleApplication {

//	Material mat;
    Spatial waterPlane;
//    Geometry lightSphere;
    SimpleWaterProcessor waterProcessor;
    Node sceneNode;
    Node floorNode;
    boolean useWater = true;
    Vector3f lightPos =  new Vector3f(-.5f,-.5f,-.5f).normalizeLocal();
    
    Node[][] entityChunks;
    
    ArrayList<Resource> resourceList;
    ArrayList<Actor> actorList;
    
    HashMap<Entity,Spatial> entityMap;
    ArrayList<Entity> toRemove;
    
    Environment env;
    Properties props;
    Interpreter interpreter;
    
    Material baseActorMat;
    Material baseWheatMat;
    Material baseWheatGrainMat;
    Material baseTreeMat;
    Material baseWoodMat;
    Material baseWoodToolMat;
    Material baseEggMat;
    Material baseMeatMat;
    Material baseEntityMat;

    Spatial baseEntityGeom;
    Spatial baseActorModel;
    
    int chunkSize;
    int renderDistance;
    
    boolean paused = false;
    
    boolean stepping = false;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

	@Override
	public void simpleInitApp() {
		try {
			props = PropertyFileReader.getProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String userHome = System.getProperty("user.home");
//	    assetManager.registerLocator(userHome, FileLocator.class);
//	    
//	    baseActorModel = assetManager.loadModel("Models/OrangeBOT.obj");
//		
//		File file = new File(userHome+"/Models/"+"OrangeBOT.j3o");
//		
//		BinaryExporter exp = BinaryExporter.getInstance();
//		
//		
//		try {
//	        exp.save(baseActorModel, file);
//	    } catch (IOException ex) {
//	        System.out.println(file.getAbsolutePath());
//	    }
		
		chunkSize = Integer.parseInt(props.getProperty("CHUNK_SIZE"));
		renderDistance = Integer.parseInt(props.getProperty("RENDER_DISTANCE"));
		
		
		interpreter = new Interpreter(props);
		env = new Environment(props, interpreter, new Random());
		

		flyCam.setMoveSpeed(50);
		cam.setLocation(new Vector3f(0,10,0));
		
		sceneNode = new Node();
		floorNode = new Node();
		
		DirectionalLight sun = new DirectionalLight();
        sun.setDirection(lightPos);
        rootNode.addLight(sun);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.05f));
        rootNode.addLight(al);
        
        Spatial sky = SkyFactory.createSky(getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);
        sceneNode.attachChild(sky);
        rootNode.attachChild(sceneNode);
		
        //create processor
        waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneNode);
        viewPort.addProcessor(waterProcessor);

        waterProcessor.setLightPosition(lightPos);
        waterProcessor.setWaveSpeed(0.05f);
        waterProcessor.setDistortionScale(0.05f);

        waterPlane = (Spatial)assetManager.loadModel("Models/WaterTest/WaterTest.mesh.xml");
        waterPlane.setMaterial(waterProcessor.getMaterial());
        waterPlane.setLocalScale(env.getWorldWidth()/2);
        waterPlane.setLocalTranslation(0, -0.5f, 0);

        rootNode.attachChild(waterPlane);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/splat/grass.jpg"));
        mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg"));
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse",ColorRGBA.White);
        mat.setColor("Specular",ColorRGBA.White);
        mat.setColor("Ambient",ColorRGBA.White);
        mat.setFloat("Shininess", 16f);  // [0,128]
//        mat.setColor("Color", ColorRGBA.Blue);
        Box box = new Box(0.5f,0.5f, 0.5f);
        Geometry geo = new Geometry("Box", box);
        geo.setMaterial(mat);
        Entity e;
        int w = env.getWorldWidth();
        int h = env.getWorldHeight();
        
        Node floorChunkNode = new Node();
        int numProcessed = 0;
        
        for (int x=0; x<w; x++) {
        	for (int y=0; y<h; y++) {
        		e = env.getEntity(x, y);
        		if (!(e instanceof Water)) {
	        		geo = geo.clone();
	        		geo.setLocalTranslation(x - w/2, -0.5f, y - h/2);
	        		floorChunkNode.attachChild(geo);
        		}
        		numProcessed += 1;
        		if (numProcessed % (chunkSize*chunkSize) == 0) {
        			GeometryBatchFactory.optimize(floorChunkNode);
        			floorNode.attachChild(floorChunkNode);
        			floorChunkNode = new Node();
        		}
            }
        }
        
        GeometryBatchFactory.optimize(floorChunkNode);
		floorNode.attachChild(floorChunkNode);
        
//        GeometryBatchFactory.optimize(floorNode);
        rootNode.attachChild(floorNode);
        
        entityMap = new HashMap<>();
        toRemove = new ArrayList<>();
        
        entityChunks = new Node[w/chunkSize][h/chunkSize];
        
        for (int x=0; x<entityChunks.length; x++) {
        	for (int y=0; y<entityChunks[x].length; y++) {
        		entityChunks[x][y] = new Node();
        		entityChunks[x][y].setUserData("center", new Vector3f((float)((x+0.5)*chunkSize - env.getWorldWidth()/2),0,(float)((y+0.5)*chunkSize - env.getWorldHeight()/2)));
        		entityChunks[x][y].setUserData("visible", false);
        		rootNode.attachChild(entityChunks[x][y]);
        	}
        }
        
        baseActorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
////        baseActorMat.setTexture("DiffuseMap",
////                assetManager.loadTexture("Textures/Actor/octopusBody.jpg"));
        baseActorMat.setBoolean("UseMaterialColors",true);
        baseActorMat.setColor("Diffuse",ColorRGBA.White);
        baseActorMat.setColor("Specular",ColorRGBA.White);
        baseActorMat.setColor("Ambient",ColorRGBA.White);
        baseActorMat.setFloat("Shininess", 8f);
        
        baseWheatMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseWheatMat.setBoolean("UseMaterialColors",true);
        baseWheatMat.setColor("Diffuse",ColorRGBA.Yellow);
        baseWheatMat.setColor("Specular",ColorRGBA.Yellow);
        baseWheatMat.setColor("Ambient",ColorRGBA.Yellow);
        baseWheatMat.setFloat("Shininess", 32f);
        
        baseWheatGrainMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseWheatGrainMat.setBoolean("UseMaterialColors",true);
        baseWheatGrainMat.setColor("Diffuse",ColorRGBA.Orange);
        baseWheatGrainMat.setColor("Specular",ColorRGBA.Orange);
        baseWheatGrainMat.setColor("Ambient",ColorRGBA.Orange);
        baseWheatGrainMat.setFloat("Shininess", 32f);
        
        baseMeatMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseMeatMat.setBoolean("UseMaterialColors",true);
        baseMeatMat.setColor("Diffuse",ColorRGBA.Brown);
        baseMeatMat.setColor("Specular",ColorRGBA.Brown);
        baseMeatMat.setColor("Ambient",ColorRGBA.Brown);
        baseMeatMat.setFloat("Shininess", 32f);
        
        baseWoodMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseWoodMat.setBoolean("UseMaterialColors",true);
        baseWoodMat.setColor("Diffuse",ColorRGBA.Gray);
        baseWoodMat.setColor("Specular",ColorRGBA.Gray);
        baseWoodMat.setColor("Ambient",ColorRGBA.Gray);
        baseWoodMat.setFloat("Shininess", 32f);
        
        baseWoodToolMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseWoodToolMat.setBoolean("UseMaterialColors",true);
        baseWoodToolMat.setColor("Diffuse",ColorRGBA.DarkGray);
        baseWoodToolMat.setColor("Specular",ColorRGBA.DarkGray);
        baseWoodToolMat.setColor("Ambient",ColorRGBA.DarkGray);
        baseWoodToolMat.setFloat("Shininess", 32f);
        
        baseEggMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseEggMat.setBoolean("UseMaterialColors",true);
        baseEggMat.setColor("Diffuse",ColorRGBA.White);
        baseEggMat.setColor("Specular",ColorRGBA.White);
        baseEggMat.setColor("Ambient",ColorRGBA.White);
        baseEggMat.setFloat("Shininess", 32f);
        
        baseTreeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseTreeMat.setBoolean("UseMaterialColors",true);
        baseTreeMat.setColor("Diffuse",ColorRGBA.Green);
        baseTreeMat.setColor("Specular",ColorRGBA.Green);
        baseTreeMat.setColor("Ambient",ColorRGBA.Green);
        baseTreeMat.setFloat("Shininess", 32f);
        
        baseEntityMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        baseEntityMat.setBoolean("UseMaterialColors",true);
        baseEntityMat.setColor("Diffuse",ColorRGBA.Black);
        baseEntityMat.setColor("Specular",ColorRGBA.Black);
        baseEntityMat.setColor("Ambient",ColorRGBA.Black);
        baseEntityMat.setFloat("Shininess", 32f);
        
        
        inputManager.addMapping("Toggle Sim", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Toggle Renderer", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Toggle Stepping", new KeyTrigger(KeyInput.KEY_K));
        
        inputManager.addListener(actionListener,new String[]{"Toggle Sim","Toggle Renderer","Toggle Stepping"});
//        assetManager.
//        System.out.println(getAssetManager().toString());
//        baseActorGeom = loadedNode;//getAssetManager().loadModel("Models/Doughnut.obj");
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		
		
		if (!paused) {
			toRemove.clear();
			Node chunk;
			Vector3f camPos = cam.getLocation();
			Entity e;
			Spatial geom;
			
			for (int xChunk=0; xChunk<entityChunks.length; xChunk++) {
				for (int yChunk=0; yChunk<entityChunks[xChunk].length; yChunk++) {
					chunk = entityChunks[xChunk][yChunk];
					
					float dist = camPos.distance(chunk.getUserData("center"));
//					boolean offScreen = false;
//					Vector3f chunkScreenPos = cam.getScreenCoordinates(chunk.getUserData("center"));
//					if (chunkScreenPos.x < 0 || chunkScreenPos.y < 0 || chunkScreenPos.z < 0) {
//						offScreen = true;
//					}
					
					if (dist < renderDistance) {
						if (!(boolean) chunk.getUserData("visible")) {
							chunk.setUserData("visible", true);
							chunk.setCullHint(CullHint.Dynamic);
						}
							
						for (int x=xChunk*chunkSize; x<(1+xChunk)*chunkSize; x++) {
							for (int y=yChunk*chunkSize; y<(1+yChunk)*chunkSize; y++) {
								if (env.isValidPosition(x, y)) {
									e = env.getEntity(x, y);
									if (e != null && !(e instanceof Water)) {
										
										if (entityMap.containsKey(e)) {
											geom = entityMap.get(e);
											geom.setUserData("updated", true);
//											if (!((int)geom.getUserData("x") == x && (int)geom.getUserData("y") == y)) {
//												geom.setUserData("x", x);
//												geom.setUserData("y", y);
//												geom.setLocalTranslation(x - env.getWorldWidth()/2, geom.getUserData("z"), y - env.getWorldHeight()/2);
//												
////												geom.setLocalRotation(new Quaternion().fromAngleAxis(1.5f , new Vector3f(0, 1, 0)));
//											}
											geom.setUserData("x", x);
											geom.setUserData("y", y);
											geom.setUserData("updated", true);
											
//											geom.setLocalTranslation(x - env.getWorldWidth()/2, geom.getUserData("z"), y - env.getWorldHeight()/2);
										} else {
	//										
											geom = getEntityGeometry(e);
//											geom.setUserData("x", x);
//											geom.setUserData("y", y);
//											geom.setUserData("updated", true);
											entityMap.put(e, geom);
//											geom.setLocalTranslation(x - env.getWorldWidth()/2, geom.getUserData("z"), y - env.getWorldHeight()/2);
//											if (e instanceof Actor) {
//												geom.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI*(((Actor)e).getDirection()+1), new Vector3f(0, 1, 0)));
//											}
											
											geom.setUserData("x", x);
											geom.setUserData("y", y);
											geom.setUserData("updated", true);
											geom.setLocalTranslation(x - env.getWorldWidth()/2, geom.getUserData("z"), y - env.getWorldHeight()/2);
											
											if (e instanceof Actor) {
												geom.setLocalScale(FastMath.sqrt(((Actor)e).size)*0.2f);
											}
											
											chunk.attachChild(geom);
										}
										
										
										
										

										
										if (e instanceof Actor) {
											
											Actor a = (Actor)e;
											
											geom.setLocalTranslation(geom.getLocalTranslation().interpolateLocal(new Vector3f(x - env.getWorldWidth()/2, geom.getUserData("z"), y - env.getWorldHeight()/2), 5f*tpf));
											float newAngle = 0;
											if (a.getDirection() == 0) {
												newAngle = FastMath.HALF_PI;
											} else if (a.getDirection() == 1) {
												newAngle = 0;
											} else if (a.getDirection() == 2) {
												newAngle = FastMath.HALF_PI*3;
											} else if (a.getDirection() == 3) {
												newAngle = FastMath.PI;
											}
											geom.getLocalRotation().slerp(new Quaternion().fromAngleAxis(newAngle, new Vector3f(0, 1, 0)), 5f*tpf);
										}
									}
								}
							}
						}
						
					} else {
						if ((boolean) chunk.getUserData("visible")) {
							chunk.setUserData("visible", false);
							chunk.setCullHint(CullHint.Always);
							
							chunk.detachAllChildren();
							
							for (int x=xChunk*chunkSize; x<(1+xChunk)*chunkSize; x++) {
								for (int y=yChunk*chunkSize; y<(1+yChunk)*chunkSize; y++) {
									if (env.isValidPosition(x, y)) {
										e = env.getEntity(x, y);
										
										if (entityMap.containsKey(e)) {
											
//											chunk.detachChild(entityMap.get(e));
											entityMap.remove(e);
										}
									}
									
								}
							}
							
						}
					}
					
				}
			}
			
			for (Entity entity : entityMap.keySet()) {

				if (!(boolean)entityMap.get(entity).getUserData("updated") || entity.destroyed || (entity instanceof Resource && ((Resource)entity).pickedUp)) {
					toRemove.add(entity);
				}
				entityMap.get(entity).setUserData("updated", false);
				
				
			}
			
			for (Entity entity : toRemove) {
				try {
						entityMap.get(entity).getParent().detachChild(entityMap.get(entity));
				} catch (NullPointerException except) {
				}
				
//				entityChunks[(int)entityMap.get(entity).getUserData("x")/chunkSize][(int)entityMap.get(entity).getUserData("y")/chunkSize].detachChild(entityMap.get(entity));
				entityMap.remove(entity);
			}
		}
		
	}
	
	public void fixMaterials( Spatial s ) {
	    s.depthFirstTraversal(new SceneGraphVisitorAdapter() {
	            public void visit(Geometry geom) {
	                geom.getMaterial().setColor("Ambient", geom.getMaterial().getParamValue("Diffuse"));
	            }
	        });
	}
	
	
	public Spatial getEntityGeometry(Entity e) {
		
		Spatial geom = null;
        
		
		if (e instanceof Actor) {
			geom = assetManager.loadModel("Models/Octopus.obj");
			geom.setLocalScale(0.3f);
//			geom.rotate(0,FastMath.HALF_PI-0.1f,0);
//			geom = new Geometry("Box", b);
			geom.setUserData("z", 0f);
//			geom..setColor("Ambient",ColorRGBA.White);
			fixMaterials(geom);
		} else {
			Box b = new Box(0.2f,0.2f,0.2f);
//			geom = assetManager.loadModel("Models/Teapot/Teapot.obj");
			geom = new Geometry("Box", b);
			geom.setUserData("z", 0.2f);
		}
		
		if (e instanceof Actor) {
//			geom.setMaterial(baseActorMat);
		} else if (e instanceof Wheat) {
			geom.setMaterial(baseWheatMat);
		} else if (e instanceof Egg) {
			geom.setMaterial(baseEggMat);
		} else if (e instanceof WheatGrain) {
			geom.setMaterial(baseWheatGrainMat);
		} else if (e instanceof Meat) {
			geom.setMaterial(baseMeatMat);
		} else if (e instanceof Wood) {
			geom.setMaterial(baseWoodMat);
		} else if (e instanceof WoodTool) {
			geom.setMaterial(baseWoodToolMat);
		} else if (e instanceof Tree) {
			geom.setMaterial(baseTreeMat);
		} else {
			geom.setMaterial(baseEntityMat);
		}
		return geom;
		
	}
	
	@Override
	public void destroy() {
		env.finish();
		super.destroy();
	}
	
	private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
//            System.out.println(name + keyPressed);
        	if (keyPressed) {
	        	if (name.equals("Toggle Renderer")) {
	                paused = !paused;
	            } else if (name.equals("Toggle Sim")) {
	            	if (stepping) {
//	            		stepping = !stepping;
	            		env.ready = true;
	            	} else {
	            		env.ready = !env.ready;
	            	}
	            } else if (name.equals("Toggle Stepping")) {
	            	env.stepping = !env.stepping;
	            	stepping = !stepping;
	            } 
        	}
        }
    };

}
