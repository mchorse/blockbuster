# Change Log

Blockbuster's change log.

## Version 1.3.1 (Slow-Mo update)

This is a big patch update that improves the quality of recording. Now you can use [Minema mod](https://github.com/daipenger/minema/releases) to record high quality videos of your machinimas.

The player recording code was almost rewritten from scratch and camera code was altered enough to support frame-based playback and synchronization with actor playback. In simple words, yey, you can capture smooth high-quality machinimas (with shaders) and !

**Tutorial video** for 1.3.1:  
Nope yet

* Added actor spawn egg to Blockbuster tab
* Added camera tracking (server will remember which camera profile you had)
* Added command `/model` with two sub-commands:
    * `/model export` – previously known as `/export-model`
    * `/model request` – request models and skins from server
* Added configuration (see Mod Options in main/in game menu)
    * Recording delay (recording FPS)
    * Camera default duration and duration step
    * Send models and skins on log in
* Added more detailed tooltips to items and blocks
* Added support for camera's *Field-Of-View* (`fov`) and *roll* (rotation across Z-axis) parameters
* Added sub-commands for `/camera`:
    * `/camera default` – reset camera's roll and FOV
    * `/camera fov` – set FOV of the camera
    * `/camera roll` – set roll of the camera
* Added sub-commands for `/action`:
    * `/action clear` – clears cached records on the client
    * `/action request` – requests server to send a record to current client
* Added support for `ResourceLocation` skins (you can reference Minecraft or other mod resources using `domain:path` format)
* Changed default scale of the actors to correspond to real players (@NlL5)
* Converted camera profile format to JSON
* Fixed actor detaching (@NlL5)
* Improved player recording (smooth as silk)
* Improved camera playback (synchronized with actor playback)
* Improved formatting of commands (colors)
* Switched camera duration to tick units (`20` ticks per second)
* Removed shadows for invisible actors (@NlL5)
* Removed camera duration formatting

**Important**: camera and recording files from previous versions not supported by this update (I'm sorry, I'm really really bad at supporting formats...). 

## Version 1.3

This is a big update that brings custom models and morphing into the game. This update adds support for custom models and player morphing. Your machinimas will be much interesting, because now you can record not only players, but also `mobs`, `blocks`, or even your own custom creatures. All thanks to custom models!

**Tutorial video** for 1.3:  
<a href="https://youtu.be/WXrBEQZrQ7Q?list=PL6UPd2Tj65nGxteZIdEE_fIga7_HoZJ9w">
    <img src="https://img.youtube.com/vi/WXrBEQZrQ7Q/0.jpg">
</a>

* Added support for JSON custom models for actors
* Added support for player morphing based on JSON custom models
* Added multiplayer support (models and skins can be saved in world saves, and loaded by clients)
* Added client-side `morph` command that allows player to morph into custom models in multiplayer
* Added client-side `export-model` command that allows users to export in-game models into JSON format (basic non-optimal export, better than nothing, though)
* Added three optional arguments to `/action record`
* Added support for placing multiblock blocks (like bed or door)
* Fixed crash on dedicated server (previously known to be fixed)
* Improved a little bit `look` and `follow` camera fixtures
* Merged functionality of director map block with director block
* Renamed top-level package from `noname` to `mchorse`
* Removed director map block
* Updated actor GUI for using custom models
* Updated director block GUI for manipulating replays

See McME [repository](https://github.com/mchorse/mcme) 
and [web page](https://mchorse.github.io/mcme/) for more information about the model 
editor.

**Important**: This update breaks save data compatability with previous versions. So if you're going to use this update with previously created worlds, all director blocks are going to be cleared (unintentionally), and actors will lose their `Filename` fields.

## Version 1.2.1

This is a small patch that enhances existing elements of the mod. Nothing new, just enhancements and fixes.

**Tutorial video** for 1.2.1:  
<a href="https://youtu.be/mDCYX1oRKYk?list=PL6UPd2Tj65nHvEH-_F_brz6LQDdlsCIXJ">
    <img src="https://img.youtube.com/vi/mDCYX1oRKYk/0.jpg">
</a>

* Added `/entitydata` skin client synchronization
* Added `/action stop` sub-command
* Added time formatting for argument `<duration>` (seconds `s`, minutes `m`, and hours `h` were added)
* Added feedback messages to `/director` command
* Added lots of camera shortcuts for better camera control
* Added colors for profile rendering
* Changed registering item behavior – now it ties to director block instead of actor which allows register actors without running here and there many times
* Changed playback button behavior – now it toggles playback instead of play only
* Changed playback button behavior on sneak and use – now you teleport to director block
* Changed path fixture behavior when you `/camera edit` it, instead of adding/removing, you pass it index of point, and edit the point's position
* Fixed director block GUIs – now scroll bar is draggable, and buttons are clickable
* Improved skin picker in actor GUI – now it supports tab completition and much easier to use when you have tons of skins

## Version 1.2

Third version of Blockbuster mod. This release focuses on improving cameras. 
Old entity-like camera was removed, and more flexible and complex cameras were 
added. Now, instead of riding cameras every time when you doing screen recording, 
you can just press play button and sit down, because you setup once, play camera 
how many times you want.

**Tutorial video** for 1.2:  
<a href="https://youtu.be/gq7sg-njyUk?list=PL6UPd2Tj65nHjnaQqL3gscufRcVDBezPm">
    <img src="https://img.youtube.com/vi/gq7sg-njyUk/0.jpg">
</a>

* Added camera profile. Camera profile is a list of camera fixtures that can be 
  imported/exported and played
* Added camera fixtures. Camera fixture is the definition of how camera should 
  behave. Following fixtures were added:
  	* Idle fixture – static camera shot 
    * Path fixture – linear-interpolated camera path way
    * Follow fixture – camera follows given entity from specified angle 
      (specified angle is determined when the fixture is being added) 
    * Look fixture – camera looks at given entity from specified point of view
      (specified point of view is determined when the fixture is being added)
    * Circular fixture – camera circles around given point for specified amount 
      of circles in degrees
* Added `camera` command which allows players to manage profiles and fixtures
* Added camera profile rendering
* Added camera key bindings for:
	* Removing last fixture
	* Adding idle, look or follow fixture to current camera profile
	* Toggle camera profile rendering
	* Start or stop camera profile
* Added lava and water support to place block action
* Added playback button GUI and lores
* Fixed actor's rotation when he is spawned with `/action play` command
* Merged `play-director` and `stop-director` into `director` command
* Merged `record` and `play` into `action` command
* Removed camera item, camera entity, camera jump key bindings, and camera 
  entities support in director block

## Version 1.1

Second version of Blockbuster mod. This release removes the "Name Tag" mechanism 
(redundant running here and there with intention to register/give recording name 
to the actor/director map block) and substituted this with _nice looking_ GUIs.

Basically, this release mostly focuses on enhancing GUI and the look of the mod.

**Tutorial video** for 1.1:  
<a href="https://www.youtube.com/watch?v=mjvWD9rIO0U">
    <img src="https://img.youtube.com/vi/mjvWD9rIO0U/0.jpg">
</a>

* Update to Minecraft 1.9.4 and Forge 12.17.0.1976 (thanks to [Lightwave](http://www.minecraftforge.net/forum/index.php?action=profile;u=36902))
* Added GUIs for director and director map blocks
* Added HUD overlay while player recording (red circle with caption in top-left 
  corner of the screen)
* Added arm pose while holding a bow
* Added `stop-director` command which allows you to stop playback of director at 
  specified position
* Renamed _skin manager device_ to _actor manager device_
* Changed actor behavior regarding name of the recording, now file name of the 
  recording is not depends on the actor's name tag, but stored in separate 
  field in actor's class. Use _actor configuration device_ to change to which 
  file actor is being recorded (see Recording ID field)
* Changed (improved) camera and actor configuration GUIs
* Changed camera's model and texture
* Fixed place block action, now actors can place any block (before, they couldn't place redstone wire) 
* Fixed crash on dedicated server
* Removed "Name Tag" mechanism
* Reduced the amount of console messages
* Recording format was broken, so old recordings won't work

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
