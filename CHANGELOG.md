# Change Log

Blockbuster's change log

## Version 1.1

Second version of Blockbuster mod. This release removes the "Name Tag" mechanism 
(redundant running here and there with intention to register/give recording name 
to the actor/director map block) and substituted this with _nice looking_ GUIs.

Basically, this release mostly focuses on enhancing GUI and the look of the mod.

**Tutorial video** for 1.1:
Coming soon

* Supports Minecraft 1.9.4 and Forge whatever supports Minecraft 1.9.4, he-he (thanks to [Lightwave](http://www.minecraftforge.net/forum/index.php?action=profile;u=36902))
* Added GUIs for director and director map blocks
* Added HUD overlay while player recording (red circle with caption in top-left 
  corner of the screen)
* Added arm pose while holding a bow
* Renamed _skin manager device_ to _actor manager device_
* Changed actor behavior regarding name of the recording, now file name of the 
  recording is not depends on the actor's name tag, but stored in separate 
  field in actor's class. Use _actor configuration device_ to change to which 
  file actor is being recorded
* Changed camera and actor configuration GUIs
* (gonna be) Changed camera's model and texture 
* Removed "Name Tag" mechanism
* Reduced the amount of console messages

P.S.: where "_nice looking_ GUIs" is a subjective statement

## Version 1.0

First version of Blockbuster mod. This release provides really basic features 
that allows to capture simple Minecraft machinimas and cinematics for 
adventure maps.

**Tutorial video** for 1.0:
<a href="https://www.youtube.com/watch?v=LPJb49VUUqk">
    <img src="https://img.youtube.com/vi/LPJb49VUUqk/0.jpg">
</a>

* Supports Minecraft 1.9.0 and Forge 12.6.1.1907
* Added director and director map blocks
* Added skin manager, camera, camera configuration, register, and playback button items
* Added camera and actor entities
* Added commands `record`, `play`, and `play-director`
* Implemented player's recording (thanks to [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000))
* Implemented camera configuration and actors skin change
* Implemented camera switching
* Implemented actor skin resource pack (skins can be loaded from world's save folder or config folder) 
  (world's save folder is SP only)
* Fixed bugs which occured during development