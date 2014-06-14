RubeLoader
==========
This is a RUBE scene JSON loader for libGdx.  It reads JSON data output by RUBE and creates and populates
a Box2D world with the bodies, joints, and fixtures defined therein.  It includes support for custom properties
and images.   

This repo contains a fully self-contained Libgdx test for reference.  It also contains a reference
for Box2DControllers (https://github.com/tescott/box2dcontrollers).

[Trello Dashboard](https://trello.com/b/JSYQjGY6/rubeloader)

About RUBE
==========
From https://www.iforce2d.net/rube/:

R.U.B.E stands for Really Useful Box2D Editor. This editor allows you to graphically manipulate 
a Box2D world and save it to a file. You can then load the saved file in your game/app and run the world.

General
=======
The loader consists of several serializers to read in objects from the RUBE JSON output:

	* Body
	* Fixture
	* Image
	* Joint
	* World
	* RubeWorld
	* Vector2

Scene Prep
==========
RubeLoader cannot load a .rube file.  The data must first be exported by to a JSON format that RubeLoader understands.  To do this, open up your scene's .rube file using
RUBE and then select: File > Export Scene > Save Raw Info.  This will result in a .JSON file that RubeLoader can then interpret.

RubeLoader and libgdx Integration
==================
YMMV with however you integrate things with libgdx.  This repo contains a snapshot of libgdx libs to make it fully self-contained.  This will cause conflict issues 
if you bring the RubeLoader source into a libgdx project.  One easy way to address this is to delete the libs dir from the RubeLoader project and update it to point 
to whatever libs or source you are using for your libgdx build.

Loading a Scene
===============
Creating a physics world populated with Box2D objects only takes two lines:

		RubeSceneLoader loader = new RubeSceneLoader();
		RubeScene scene = loader.loadScene(Gdx.files.internal("data/palm.json"));
		
There are two ways to approach loading a RUBE scene, either using a blocking load (as shown above), or using asynchronous loading via Asset Manager.  By using
asynchronous loading, your app can continue to render while the scene is loaded in.  This is helpful for loading in relatively large RUBE scene files and
displaying an active "loading" indication to the user.  This is demoed by the flashing "Loading..." text in the test file.  To take advantage of this, you 
will need to divide your loading operations into two: first, kicking off the asynchronous load; second, polling the asynchronous load for completion and
then performing scene processing when it does complete.  

Example asynchronous load startup code:

	  // kick off asset manager operations...
      mAssetManager = new AssetManager();
      mAssetManager.setLoader(RubeScene.class, new RubeSceneAsyncLoader(new InternalFileHandleResolver()));
      mAssetManager.load(RUBE_SCENE_FILE, RubeScene.class);
         
Example asynchronous load polling code:

      // if the asset manager has completed...
      if (mAssetManager.update())
      {
         // get the scene and process it...
         mScene = mAssetManager.get(RUBE_SCENE_FILE, RubeScene.class);
         processScene();
      }

Several scene objects are created by the loading methods.  These objects can be used for post-processing operations:

	* scene.getWorld(): This method returns the Box2D physics world.  After loading, it is populated with the bodies, joints, and fixtures from the JSON file.
	* scene.getBodies(): This method returns an array of bodies created
	* scene.getFixtures(): This method returns an array of fixtures created
	* scene.getJoints(): This method returns an array of joints created
	* scene.getImages(): This method returns an array of RubeImages defined in the JSON file.  Note: it is up to the app to perform all rendering
	* scene.getMappedImage(): This method returns an array of all RubeImages associated with a particular Body.
	* scene.getCustom(): This method allows you to retrieve custom property info from an object.
	* scene.getNamed(): This method allows you to retrieve a scene object based on name.  Since multiple objects can have the same name, this returns an Array<> type.
	
If the scene data is no longer needed, scene.clear() can be executed to free up any references.  Note that this does not alter or delete the world!  It is up
to the underlying application to handle body, etc. deletions of the Box2D physics world.

Multiple JSON file Support
==========================
It is possible to load several separate JSON files into a single, unified scene using the addScene() method.  All objects in the separate files will be found
in the scene object returned.  Additionally, the related Box2D world will contain all bodies, etc. from across all scenes specified in this manner. 

		RubeSceneLoader loader = new RubeSceneLoader();
		RubeScene scene = loader.addScene(Gdx.files.internal("data/base.json"));
		loader.addScene(Gdx.files.internal("data/images1.json"));
		loader.addScene(Gdx.files.internal("data/bodies1.json"));
		loader.addScene(Gdx.files.internal("data/images2.json"));
		loader.addScene(Gdx.files.internal("data/bodies2.json"));
		loader.addScene(Gdx.files.internal("data/images3.json"));
		// the 'scene' object return above contains the accumulated objects from all loaded scenes

Notes:
- The non-custom world properties of the first loaded scene will be used for all scenes (gravity, etc.)  Other JSON files will have no affect.
- If two different JSON files contain the same custom world property, the last loaded JSON file's custom value will prevail

Dealing with a pre-existing Box2D World
=======================================
It is possible to load in scene data to a pre-existing Box2D world.  See the following examples that demonstrate how to define this:

		RubeSceneLoader loader = new RubeSceneLoader(new World(new Vector2(0,10),true));
		RubeScene scene = loader.loadScene(Gdx.files.internal("data/palm.json"));
		
Using the asset manager and asynchronous loader:

		mAssetManager = new AssetManager();
		mAssetManager.setLoader(RubeScene.class, new RubeSceneAsyncLoader(new World(new Vector2(0,10),true),new InternalFileHandleResolver()));
		mAssetManager.load(RUBE_SCENE_FILE, RubeScene.class);
		

RubeLoaderTest
==============
This defines test file sets that include multiple & single files, custom properties and image info.  Use the mouse to pan and zoom.  On Android touch the screen to pan.

The included rendering is for demo purposes only.  A SimpleSpatial class is used to render image data which may or may not be attached to a Box2D body.
It is by no means efficient (textures are created for each image) and requires GL20 support for non-POT.  But, at the very least, should convey
example usage.

The scenes include examples of both kinds of images - ones referenced based on a body and others referenced to the world origin. 

General Setup
-------------
1. Clone the repo to a local directory.
2. Open up Eclipse.  Set workspace to that local directory.
3. File > Import > General > Existing projects into workspace > Next > Browse > Ok > Select All > Finish
4. If you do not have the Box2dControllers repo cloned, you may wish to close the projects RubeLoaderTestWithBox2dControllers and RubeLoaderTestWithBox2dControllers-desktop to avoid errors.

Android Setup
-------------
1. You may see an error if you don't have the same SDK installed.  No worries!  Right-click RubeLoaderTest-Android > Properties > Android.  Check installed SDK.  Click ok.
2. Right-click RubleLoaderTest-Android > Run As.. > Android Application
3. If you have an Android device connected to your machine, it should automatically install and launch.

Desktop Setup
-------------
1. Right-click on RubeLoaderTest-desktop > Run As... > Java Application
2. Select "RubeLoaderTestDesktop". 

Box2dController Setup
---------------------
1. Clone https://github.com/tescott/box2dcontrollers
2. Import the Box2DControllers into your workspace.
3. Resolve any dependency issues.
4. Right-click on RubeLoaderTestWithBox2dControllers-desktop > Run As > Java Application > RubeLoaderTestDesktop

Related Software:
==================
- Eclipse
- Android SDK
- RUBE: https://www.iforce2d.net/rube/ (optional if you wish to create your own scenes)
- Box2dControllers: https://github.com/tescott/box2dcontrollers (optional)

Games that use RubeLoader
=========================
- [Dragon Swoopers](https://play.google.com/store/apps/details?id=com.gushikustudios.baublebird "Dragon Swoopers on Google Play")

Screenshot of test example
==================================
![Screenshot](https://raw.github.com/tescott/RubeLoader/master/screenshot.png)

