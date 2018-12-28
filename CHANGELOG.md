# Change Log

Blockbuster's change log.

## Version 1.5.3

This patch update is quite massive in comparison to two previous patches. This update adds body part system, image morphs, a new Aperture integration, URL textures, few miscellaneous tweaks and lots of bug/crash fixes.

**Compatible** with McLib `1.0`, Metamorph `1.1.7` and Aperture `1.2`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/sWlh0LUvvMc"><img src="https://img.youtube.com/vi/sWlh0LUvvMc/0.jpg"></a> 

* Added `mclib` library mod as a dependency
* Added body part system to Blockbuster custom model system, which allows adding sub models on a model
* Added custom model and image morph editors
* Added **Image morph** which simply shows an image
* Added `Wheel` and `Wing` animation checkboxes to model editor
* Added **chat action prefix** config option
* Added `/model export_obj <model> [pose]` subcommand which allows to export Blockbuster custom models into OBJ
* Added `/model convert <fred|steve> <skin>` which allows converting `64x64` skins into `64x32` and vice versa
* Added `/spectate <target>` command
* Added `map_Kd_path` instruction to MTL parser which allows to specify default texture for the material (suggested by AzureZhen)
* Added `mounted` property to `/record clean`
* Added support of elytra layer to the slot mechanism
* Added a button to rename replay Recording ID prefixes in director block
* Added a button to copy pose properties from another pose and a button to duplicate a limb in model editor
* Added blending to default cubic limbs in custom model, which allows using semi-transparent skins
* Added URL support to resource locations
* Added a keybind which allows toggle playback of currently edited director block in dashboard GUI (suggested by Olrik&Flynn)
* Added support for recording editor within camera editor
* Fixed crash and disabled ability to parent limb to itself in the model editor (reported by El_Redstoniano)
* Fixed crash caused by malformed model morph with no model (reported by KazerLight)
* Fixed crash caused by model manager releasing memory on the integrated server (GL context on wrong thread)
* Fixed crash with empty slot (reported by Qsycho)
* Fixed crash with item playback and body actors (reported by Olrik&Flynn)
* Fixed issue with bow animation not recognizing children limbs (reported by MineLol and ZeNoob)
* Fixed morphs not appearing correctly when scrubbing through in camera editor (reported by GroupM)
* Fixed an issue with 3D extruded limbs and normals (reported by ItzCactus)
* Removed custom model morph builder
* Removed the mob model export button from the main menu model editor (reported by NlL5)
* Updated **Chinese** translation strings (thanks to ycwei982)
* Updated **Russian** translation strings (thanks to Andruxioid)

## Version 1.5.2

This patch update adds more colored blocks for chroma keying and fixes lots of things (including pose editor that I broke in `1.5.1`).

* Added more colored blocks for green screen block
* Added button in director block GUI to update player data (used by fake players option)
* Added display titles instead of action IDs in player recording editor GUI
* Added `active_hands`, `fall_distance`, `sprinting` and `sneaking` properties to `/record clean`
* Added `/model texture <location> [linear] [mipmap]` command
* Fixed pose editor which was broken in `1.5.1` (whoops, my bad)
* Fixed some cascading transformation issues custom head blocks, held items and model blocks rendering
* Fixed issue with no-material OBJ part not having selected texture of the model
* Fixed crash related to clicking on an empty cell when dragging an action (reported by Chunk7)
* Fixed some syncing issues when duplicating a replay in director block GUI (enabled, fake player and health fields)
* Fixed constant swiping in bed as fake players
* Fixed resetting of action list in player recording editor
* Fixed crash with my commands having client side code, `I18n` specifically (reported by LatvianModder)
* Optimized 3D layers to avoid creating empty display lists when there are no cells 
* Renamed **Green block** to **Chroma block**
* Rewritten action system to use a registry

## Version 1.5.1

This patch update fixes minor bugs introduced in `1.5`, as well as fixing some of the older bugs. Also, this patch contains a little surprise for `1.10.2` and `1.11.2`, allowing them enjoy `1.12.2` exclusive item model feature introduced in `1.5`.

**Compatible** with Metamorph `1.1.6` and Aperture `1.1`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added item model rendering from `1.12.2` to `1.11.2` and `1.10.2` builds
* Added a procedure to clean up GL resources after models are getting removed
* Fixed NPE in `ClientHandlerActorPause` (reported by KazerLight)
* Fixed `ResourceLocation` to `TextureLocation` in 1.11.2+ branches (reported by AzureZhen)
* Fixed issues with mixed MTL and cubic limbs
* Fixed last edited action not saved when switching between files
* Fixed `blockbuster.gui.record_editor.actions.equip.leggings`
* Fixed issue with riding an actor
* Fixed shadow configuration option doesn't get saved
* Fixed add limb with same name bug (reported by \_TroloTroll\_)
* Made pose editor clone a morph into selected morph slot (instead of modifying current)
* Made `invert` property use Z axis instead of Y for yaw when `looking` is enabled
* Made playback button and confirm break director block GUIs not pause the game
* Made `null` director block and model block if they aren't accessible in the world
* Made the model rebuild on `holding` button click

## Version 1.5 (GUIs and custom models)

Blockbuster `1.5` is a massive update which improves almost every aspect of the mod. Main features of `1.5` are: improved support for OBJ, revamped GUIs, improved damage control and green screen features.

**Compatible** with Metamorph `1.1.6` and Aperture `1.1`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/g5zrqiI_Udk"><img src="https://img.youtube.com/vi/g5zrqiI_Udk/0.jpg"></a> 

#### General

* Added green block which helps setting up a green screen
* Added green sky option which allows making the sky fully green (works nicely with green screen block)
* Added custom morph preservation, if the model wasn't found, it will still create a morph but without a model
* Added Russian localization (thanks to Andruxioid)
* Fixed another dedicated server crashes (reported by AzureZhen)
* Fixed dedicated server not persisting morph information (due not loaded custom morphs)
* Removed support for playing back and registering actors spawned from a spawn egg (obsolete feature)
* Removed Globgabgalab
* Updated Chinese localization (thanks to ycwei982)

#### GUIs (user interfaces)

* Added player recording editor GUI which allows editing player recording's actions
* Added pose editor to morph picker
* Reworked most of GUIs into a dashboard panel based GUIs
* Reworked model block GUI
* Reworked director block GUI
* Reworked model editor GUI
    * Added a button which allows creating a model from a mob

#### Director block

* Added better duplication mechanic (suggested by Olrik&Flynn)
* Added director block configuration options: start and stop commands, looping, disabling redstone state change, hiding block on playback and display name
* Added new replay properties: starting health, enabled playback and use a fake player instead of an actor.

#### Custom models

* Added back custom model code back from Metamorph to Blockbuster
* Added support for quad faces in OBJ code
* Added support for C4D exported OBJ files (suggested by Andruxioid)
* Added support for OBJ material files (must be manually enabled with `providesMtl` model property)
    * Added support for non-standard `map_Kd_linear` instruction which indicates that this texture should be linearly interpolated
    * Added mipmap support for material textures
    * Added special NBT tag which allows specifying custom texture for given material
* Added `shading` limb property which allow to disable default MC shading
* Added `lighting` limb property which allow to disable lightmap shading (glow in the dark)
* Added `slot` limb property which allows to assign an armor slot to be coated by the armor model
* Added `is3D` limb property which allows to make the limb look extruded as seen in MPM/CNPCs mods (suggested by snifferish)
* Added `scaleGui` model property which allows to set scale of the model within GUI
* Added `skins` model property which allows the model to specify an additional model from which it can reference its skins, along its own skins (suggested by Andruxioid)
* Added a feature to blacklist a model by simply adding `__` to model's folder name
* Added more poses to default models
* Improved auto-load OBJ feature which allows generating limbs out of objects found within OBJ file
* Improved default models `steve`, `alex` and `fred` by adding an anchor limb which allows posing much easier
* Optimized model reloading code which reloads models only if model files has changed

#### Commands 

* Added `/on_head` command which allows placing currently held item in the head slot
* Added several `/record` sub-commands for more extensive player recording editing:
    * `/record origin` – allows to change the initial position and rotation around that point based on player's position or given coordinates (thanks to Olrik&Flynn)
    * `/record dupe` – allows to duplicate a player recording (thanks to Agirres)
    * `/record prolong` – allows to add delays before and after the actual playback of the player recording content starts
    * `/record tp` – allows to tp to given player recording at given tick (thanks to Olrik&Flynn)
    * `/record clean` – allows to clean/set frame property within given range (thanks to Olrik&Flynn)
* Added `[path]` argument to `/model clear` command which allows to clear out only skins for specific model
* Added `[force]` argument to `/model reload` command which allows to force reload models, and also `/model reload` now reloads models on the client too
* Fixed and slightly improved `/model export` command

#### Damage control

* Restore tile entities which were removed during damage control session
* Remove entities which were created during damage control session

#### Model block

* Added a feature to render model blocks in inventory/as held items (1.12.2 feature only)
* Added global rendering of model blocks
* Added rotation order, uniform slider scale, optional entity-like shadow (suggested by _TroloTroll_) and item stacks configuration to model block
* Fixed model block connecting to nearby fences (reported by _TroloTroll_)
* Removed destruction particles

#### Recording

* Fixed item use actions not taking in account of actor's held items
* Fixed items disappear from actor's hand when executing some item action
* Switched from item ID to `ItemStack` equality for item tracking

## Version 1.4.10 (model block)

This patch adds two nice features and also cleans up the code base a little bit (for next updates, basically, it's only the beginning).

**Compatible** with Metamorph `1.1.5` and Aperture `1.1`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions probably are incompatible.

<a href="https://youtu.be/r3BpwMBDxwk"><img src="https://img.youtube.com/vi/r3BpwMBDxwk/0.jpg"></a> 

* Added **model block** which allows to place morphs as blocks with some transformation configuration (basically replacement for freeze actor feature)
* Added a feature which allows loading OBJ models without a JSON feature (see wiki for more information)
* Added `item_use_block` action which allows to record firework placing, usage of bone meal, flint & steel, and etc.
* Added `/model reload` command which allows to reload models on the server side
* Added `/director loops` command which allows making director block loopable
* Added in-game model editor button to in-game pause menu (press ESC in the world)
* Blockbuster GUIs won't pause the game anymore
* Command `/model clear` now removes all textures from texture cache which are related to Blockbuster, instead of only the missing ones (purple checkered)
* Removed freeze actor checkbox feature
* Removed hacks for 1.3 compatibility
* Removed code related to model  and `/model request` command
* Updated 1.12 build to 1.12.2

## Version 1.4.9 (OBJ and camera update)

This patch that mainly aims at providing integration with my new [Aperture](https://minecraft.curseforge.com/projects/aperture) mod (a camera mod which camera creation process using GUI). This means that all old camera code in Blockbuster was removed.

For exception of camera features, there are also some little tweaks made to other components of the mod. Mostly tweaks that fix some annoying stuff. Oh, and also OBJ support for custom models.

**Compatible** with Metamorph `1.1.4` and Aperture `1.0.1`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions probably are incompatible.

<a href="https://youtu.be/R-g6fIUBtR4"><img src="https://img.youtube.com/vi/R-g6fIUBtR4/0.jpg"></a> 

#### General

* Added support for Aperture mod
    * If you hold playback button attached to director block, you would be able to preview actors playback by scrubbing the timeline (bottom bar) or pressing play/pause button
    * Added support for camera launching for playback buttons
    * Added Blockbuster's camera editor options panel
* Dropped Minecraft `1.9.4` support and added Minecraft `1.12` support
* Toggle button teleport teleports you back to the place you teleported to director block in the first place (thanks to badr)
* More sophisticated teleport to director block (searches for a free block pos and aligns player's look toward director block)

#### Actors

* Added `.obj` model support 
* Added support for sitting on 3rd-party mods that provide sittable chairs for custom model based morphs only
* Added actor `freeze` checkbox #34 
* Remove entity shadows for invisible actors
* Remove stuck actors after exiting during director block's playback
* When actors spawn, align body with head

#### Commands

* Add `/action record` command to the history when pressing `Record` button in director block GUI (thanks to Tom Soel)
* Removed `spawn` sub-command from `/director` command
* Removed `/camera` command
* Removed `/load_chunks` command

#### GUI

* Add confirmation modal to the director block GUI (thanks to Sanchan, badr and others)
* Add label for morphs in morph pickers (thanks to Minebox)
* Add the support for displaying OBJ model parts in model editor
* Add GUI elements to modify `origin` and `providesObj` in model editor

#### Recording

* Add block placing sounds to `place_block` action (thanks to MadDreamer)
* Fixed attack action on the server (requires Easy difficulty, in order to hit player)
* Make actors make weak hit sound when swiping (configurable, by default disabled)

## Version 1.4.8 (integrated model editor)

Patch update. This update is focused on bringing [McME](https://mchorse.github.io/mcme/) into the mod itself. Why integrating model editor in the mod? Because this will increase productivity of making custom models. When you edit models in the game, you'll have instant feedback on how the model looks, meanwhile with McME, ther might be some bugs related to wrong angles between how custom model looks in the game and in the editor, how does the character would look with items in hand, etc.

**Important**: there's a new way of recording actions was implemented. Player recordings recorded in `1.4.8`, **can't** be used in 1.4.7 and below. However, you can use actions from `1.4.7` in `1.4.8`.

<a href="https://youtu.be/CoJ_6Byh6LA"><img src="https://img.youtube.com/vi/CoJ_6Byh6LA/0.jpg"></a> 

#### Camera

* Added a config option for default path fixture interpolation
* Added smooth cubic interpolated camera, like cinematic vanilla but Minema-friendly, see `Options > Controls > Blockbuster Misc.`
* Added accelerated linear interpolation for roll and FOV when smooth camera enabled
* Added `hermite` interpolation type for path fixture (`/camera edit 0 hermite`)

#### Commands

* Added sub-commands:
    * `/model clear` – clears texture map cache from purple checkered textures
    * `/model replace_texture` – replaces texture in the texture map to another already loaded texture
    * `/load_chunks` – force loads all chunks in the render distance (affects only chunks which were already generated by the world)
* Updated `/record` sub-commands to multiple action per frame feature
    * Added `/record add` and `/record remove`
    * Removed `/record set`
* Updated `/camera duration` sub-command to take in account relative values (like with `/camera step` and `/camera rotate`)

#### Director block

* Make director block's sides highlight on play or on stop
* Fix crash when actor uses playback button (thanks to Badr)

#### General

* Added a config option for custom model and skins refresh
* Added a config option for enabling rendering nametags for actors always
* Added a friendly reminder chat message when the player enters a world. Blockbuster lets the player know that (s)he's using not recommended version of Metamorph
* Added configurable damage control (by default disabled, see mod options)
* Compatible with Metamorph `1.1.3`
* Fixed grammar and phrasing in config comments (thanks to reck829)

#### GUI

* Added a button in main menu and a keybind to access the model editor
* Added a **model editor** GUI with following features:
    * Saving and loading models
    * Add, edit and remove limbs 
    * Add, edit and remove poses 
    * Edit general model properties (name, texture size, etc.)
    * Rotate and scale model in GUI
    * Limb swinging and swiping, hit box rendering, and item holding buttons
    * Texture picker
* Added current camera playback tick in F3 screen
* Added sliders in actor configuration GUI to rotate an actor (reck829's suggestion)
* When you duplicate a replay in director block GUI, recording ID field will get incremented (`record -> record_1`, `horse_5 -> horse_6`, etc.) (thanks to sanchan)

#### Recording

* Added recording of breaking block animation (`break_animation`)
* Added recording of item usage (`use_item`)
* Added `Drop` (boolean) property for `place_block` action which is responsible for dropping an according block
* Fixed `mounting` action mounting again and over again
* Switched to recording multiple actions per a frame

## Version 1.4.7 (Metamorph integration)

Another patch update. This patch update is what I waited for a long time! This update integrates Blockbuster with Metamorph. This makes Blockbuster able to perform Metamorph's abilities, attacks and abilities as well as to use Metamorph's morphs for actor morphing. From now and on, Blockbuster isn't a standalone mod. It requires [Metamorph](https://github.com/mchorse/metamorph).

**Important**: due to integration, the format of custom models and skins was changed, so before trying out this update, make sure to back up your world as it may make all your actors morphless (invisible actors with shadows).

Special thanks to **[The Minebox](https://www.youtube.com/user/TheMinebox)**, **Badr**, **[Tom Soel](https://twitter.com/TomSoel)** and **[Vasily12345](https://www.youtube.com/user/MinecraftLifeSeries)** for beta-testing and suggesting features for this update!

<a href="https://youtu.be/EY8pvphu724"><img src="https://img.youtube.com/vi/EY8pvphu724/0.jpg"></a> 

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
* Works with [Metamorph](https://minecraft.curseforge.com/projects/metamorph) `1.1.1`

#### Actors

* Added default `fred` custom model (4px wide arms with overlays)
* Flipped texture of **actor configuration** item (thanks to Tom Soel)
* Increased item pick-up delay (from `10` to `40`)
* Implemented item pick-up animation (item magnet-like animation)
* Made actors rideable (sneak + right click is to start record)

#### Camera

* Added `cubic` path fixture interpolation
* Added keys for more precise player position and angle adjustments (under *Blockbuster Camera Control* category)

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
<a href="https://youtu.be/EiNlOLCzc_s?list=PL6UPd2Tj65nEwg2bfY-NduLihPy6fgnvK"><img src="https://img.youtube.com/vi/EiNlOLCzc_s/0.jpg"></a> 

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
<a href="https://youtu.be/WXrBEQZrQ7Q?list=PL6UPd2Tj65nGxteZIdEE_fIga7_HoZJ9w"><img src="https://img.youtube.com/vi/WXrBEQZrQ7Q/0.jpg"></a> 

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
<a href="https://youtu.be/mDCYX1oRKYk?list=PL6UPd2Tj65nHvEH-_F_brz6LQDdlsCIXJ"><img src="https://img.youtube.com/vi/mDCYX1oRKYk/0.jpg"></a> 

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
<a href="https://youtu.be/gq7sg-njyUk?list=PL6UPd2Tj65nHjnaQqL3gscufRcVDBezPm"><img src="https://img.youtube.com/vi/gq7sg-njyUk/0.jpg"></a> 

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
<a href="https://youtu.be/mjvWD9rIO0U"><img src="https://img.youtube.com/vi/mjvWD9rIO0U/0.jpg"></a> 

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
<a href="https://youtu.be/LPJb49VUUqk"><img src="https://img.youtube.com/vi/LPJb49VUUqk/0.jpg"></a> 

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