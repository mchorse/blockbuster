# Change Log

Blockbuster's change log.

## Version 1.4.7 (Metamorph integration)

Another patch update. This patch update is what I waited for a long time! This update integrates Blockbuster with Metamorph. This makes Blockbuster able to perform Metamorph's abilities, attacks and abilities as well as to use Metamorph's morphs for actor morphing. From now and on, Blockbuster isn't a standalone mod. It requires [Metamorph](https://github.com/mchorse/metamorph).

**Important**: due to integration, the format of custom models and skins was changed, so before trying out this update, make sure to back up your world as it may make all your actors morphless (invisible actors with shadows).

Special thanks to **[The Minebox](https://www.youtube.com/user/TheMinebox)**, **Badr**, **[Tom Soel](https://twitter.com/TomSoel)** and **[Vasily12345](https://www.youtube.com/user/MinecraftLifeSeries)** for beta-testing and suggesting features for this update!

#### General

* Added config options:
    * Recording countdown (suggested by The Minebox)
    * Disable teleport to director block with playback (suggested by badr)
    * Enable command action recording
    * Camera step and rotation factors for keys
directions
* Adapted `MORPH` action to Metamorph's integration
* Adapted actor and director GUIs to Metamorph's integration
* Removed `/morph` command and morphing capability from Blockbuster
* Replaced `Custom Model` and `Skin` fields with morph picker
* Works with Metamorph `1.1`

#### Actors

* Added default `fred` custom model (4px wide arms with overlays)
* Flipped texture of **actor configuration** item (thanks to Tom Soel)
* Increased item pick-up delay (from `10` to `40`)
* Implemented item pick-up animation (item magnet-like animation)
* Made actors rideable (sneak + right click is to start record)

#### Camera

* Added `cubic` path fixture interpolation
* Added keys for more precise player position and angle adjustments (under *Blockbuster Camera Control* category)
* Added markers for path fixture points

#### Commands

* Added `/camera path` subcommands:
    * `/camera path add` – add a point in a path fixture
    * `/camera path edit` – edit a point in a path fixture
    * `/camera path remove` – remove a point from path fixture
    * `/camera path move` – move a point to another index in a path fixture
    * `/camera path goto` – go to a point in a path fixture (like `/camera goto`, but specific for path fixtures)
* Added `/camera step` which moves player absolutely or relatively
* Added `/camera rotate` which rotates player absolutely or relatively
* Added `/director spawn` subcommand (spawns actors in director block at given tick in pause mode)
* Added `/record` command with four sub-commands:
    * `/record get` – output the data tag of the action in given player recording and tick
    * `/record set` – set an action in given player recording at given tick
    * `/record info` – output the information about given player recording
    * `/record search` – find actions of specified type in given player recording

#### Director block

* Added a replay thumbnail in **director block** GUI (thanks to badr)
* Added director block break confirmation (thanks to Tom Soel)
* Added duplicate button in **director block** GUI (thanks to badr)
* Added error messages and red labels when `Recording ID` field is empty (thanks to The Minebox)
* Fixed toggle replay bug in **director block** GUI (thanks to badr)
* When *registering device* item is attached to director block, using the item will bring up **director block** GUI

#### Recording

* Added `COMMAND` action (which executes command based on actor)
* Added `MORPH_ACTION` action (which basically executes morph's action)
* Corrected `ATTACK` action's direction vector
* Fixed NPE crash at the end of playback with mounts (thanks to Tom Soel)

There were also few bugs during testing which lead to game crash, but they're not listed here, since they were fixed during the release (thus it will be inappropriate add them since they weren't present in previous update).

## Version 1.4.6

Another patch update. This patch is quite useful and contains one bug fix and two very useful config options. This patch fixes annoying scrolling in **director block** GUI and adds two config options for controlling render and tracking range of the actors, allowing players to render actors from much further distance (up to 1024 blocks away).

* Added `actor_rendering_range` config option (client related)
* Added `actor_tracking_range` config option (server related)
* Added a label on top of text field in **director block** GUI
* Added `DAMAGE` action which does self-damage to the entity actor
* Added the ability to *commit suicide* during recording (simply use `/kill` command during recording)
* Fixed reset scrolling bug in **director block** GUI

## Version 1.4.5

Another patch update. This patch adds an attack action, fixes hand held items position slightly, and adds two options.

* Added `ATTACK` action (now you can hit entities, yay! Unfortunately, it's always `2.0` damage)
* Added two mod options:
    * Apply fall damage (by default is `true`)
    * Record attack action on hand swipe (by default is `false`)
* Fixed hand held items positions (they were a little bit off)
* Fixed X-axis aligned first person display of arms

## Version 1.4.4

Another patch update. This patch fixes bugs which were found by users. Those are mostly actor bugs.

* Apply fall damage on actors (reported by AlpesH312)
* Make actors trigger pressure plates (reported by Tilairgan, long time ago)
* Fix (for 1.11 version) holding hand status (reported by Juan Lopez on YT)

## Version 1.4.3

Another patch update. This patch is aimed at fixing few things and maybe some enhancements. 

* Change the recording command message to `To record $name, click here.` (for understanding which actor to record)
* Fix `steve` and `alex` armor desynchronization
* When invoked `/action record` with director block coordinates and if director block is playing, then stop director block playback

## Version 1.4.2

This is a small patch that adds few user requested features. Somebody might be excited for this.

* Added legacy compatibility for pre-`1.3` director blocks (requested by NlL5)
* Added support for arm postures and item animations (suggested by RedComet2000)
    * Comes in effect after making a new player recordings (since some new data needs to be recorded)
* Added `Hands` field in player recording frame data structure

## Version 1.4.1

This is a small patch that fixes several things, and adds one configuration option.

* Added `Switch to Spectator` configuration option in `Camera` category
* Added creation of `alex` and `steve` skins folders (thanks to badr and to people who didn't knew where to put their skins)
* Fixed `Error executing task` while saving of player recordings

## Version 1.4 (Slow-Mo update)

This is a big update that improves the quality of recording. Now you can use [Minema mod](https://github.com/daipenger/minema/releases) to record high quality videos of your machinimas (with shaders... **HYPE mode activated**).

The player recording code was almost rewritten from scratch and camera code was altered enough to support frame-based playback and synchronization with actor playback. In simple words, now you can capture smooth high-quality machinimas with Minema!

**Showcase video** for 1.4: 
<a href="https://youtu.be/EiNlOLCzc_s?list=PL6UPd2Tj65nEwg2bfY-NduLihPy6fgnvK"> 
    <img src="https://img.youtube.com/vi/EiNlOLCzc_s/0.jpg">
</a>

* Added actor spawn egg to Blockbuster tab
* Added button "Record" which simplifies recording of ghost actors
* Added camera tracking (server will remember which camera profile you had)
* Added command `/model` with two sub-commands:
    * `/model export` – previously known as `/export-model`
    * `/model request` – request models and skins from server
* Added configuration (see Mod Options in main/in-game menu)
    * General
        * Send models and skins when player logs in?
        * Clean downloads folder when exiting from server
    * Recording
        * Recording frame delay
        * Automatic record unload time
        * Turn on automatic record unloading?
        * Record tick synchronization rate
    * Camera
        * Default fixture duration
        * Duration increase/decrease step
        * Interpolate target fixtures?
        * Target interpolation value
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
* Converted player recording format to NBT
* Fixed actor detaching (@NlL5)
* Improved player recording (smooth as silk)
* Improved camera playback (synchronized with actors ticks)
* Improved formatting of commands
* Switched camera duration to tick units (`20` ticks per second)
* Removed shadows for invisible actors (@NlL5)
* Removed camera duration formatting (`s`, `m` and `h` suffixes are gone)
* Renamed the "Undocumented Feature" to "Camera Marker."

**Important**: camera and recording files from previous versions not supported by this update (I'm sorry, I'm really really bad at supporting formats). Next version will be less likely to break the format. Sorry... 

## Version 1.3

This is a big update that brings custom models and morphing into the game. This update adds support for custom models and player morphing. Your machinimas will be much interesting, because now you can record not only players, but also `mobs`, `blocks`, or even your own custom creatures. All thanks to custom models!

**Showcase video** for 1.3:  
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

**Showcase video** for 1.2.1:  
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

**Showcase video** for 1.2:  
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
to the actor/director map block) and substituted this with *nice looking* GUIs.

Basically, this release mostly focuses on enhancing GUI and the look of the mod.

**Showcase video** for 1.1:  
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

P.S.: where "*nice looking* GUIs" is a subjective statement

## Version 1.0

First version of Blockbuster mod. This release provides really basic features 
that allows to capture simple Minecraft machinimas and cinematics for 
adventure maps.

**Showcase video** for 1.0:  
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