package com.gushikustudios.rube.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.serializers.RubeWorldSerializer;

/**
 * Loads a json file and returns a {@link RubeScene}.
 * @author clement.vayer
 *
 */
public class RubeSceneLoader 
{
	private final Json json;
	private final RubeWorldSerializer mRubeWorldSerializer;
	
	public RubeSceneLoader()
	{
		json = new Json();
		json.setTypeName(null);
		json.setUsePrototypes(false);
		
		json.setSerializer(RubeScene.class, mRubeWorldSerializer = new RubeWorldSerializer(json));
	}
	
	/**
	 * Use this to load in an individual .json scene.  Any previously loaded scene
	 * data will be lost (including Box2D objects!)
	 * 
	 * @param _file File to read.
	 * @return The scene represented by the RUBE JSON file.
	 */
	public RubeScene loadScene(FileHandle _file)
	{
	   if (mRubeWorldSerializer != null)
	   {
	      mRubeWorldSerializer.resetScene();
	   }
		RubeScene scene = null;
		try 
		{
			scene = json.fromJson(RubeScene.class, _file);	
		} 
		catch (SerializationException ex) 
		{
			throw new SerializationException("Error reading file: " + _file, ex);
		}
		return scene;
	}
	
	/**
	 * This method accumulates objects defined in a scene, allowing several separate
	 * RUBE .json files to be combined.  Objects are added to the scene's data, as
	 * well as within the Box2D world that is ultimately returned.
	 * 
	 * @param _file The JSON file to parse
	 * @return The cumulative scene
	 */
	public RubeScene addScene(FileHandle _file)
	{
	     RubeScene scene = null;
	     try 
	     {
	        scene = json.fromJson(RubeScene.class, _file);  
	     } 
	     catch (SerializationException ex) 
	     {
	        throw new SerializationException("Error reading file: " + _file, ex);
	     }
	     return scene;
	}
	
}
