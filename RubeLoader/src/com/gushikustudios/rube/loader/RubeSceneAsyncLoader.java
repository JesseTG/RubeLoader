package com.gushikustudios.rube.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;

public class RubeSceneAsyncLoader extends AsynchronousAssetLoader<RubeScene, RubeSceneAsyncLoader.RubeSceneParameters>
{
   private final RubeSceneLoader mLoader;
   private RubeScene mScene;
   
   static public class RubeSceneParameters extends AssetLoaderParameters<RubeScene>
   {
   }
   
   public RubeSceneAsyncLoader(FileHandleResolver resolver)
   {
      super(resolver);
      mLoader = new RubeSceneLoader();
   }

   @SuppressWarnings("rawtypes")
   @Override
   public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, RubeSceneParameters parameter)
   {
      return null;
   }

   @Override
   public void loadAsync(AssetManager manager, String fileName, FileHandle file, RubeSceneParameters parameter)
   {
      mScene = null;
      mScene = mLoader.addScene(file);
   }

   @Override
   public RubeScene loadSync(AssetManager manager, String fileName, FileHandle file, RubeSceneParameters parameter)
   {
      return mScene;
   }
}
