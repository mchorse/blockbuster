## Version 2.3.1

This patch update fixes a couple of bugs.

**Compatible** with McLib `2.3.1`, Metamorph `1.2.7` and Aperture `1.6`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added a feature when duplicating a scene, all player recordings will be renamed using rename prefix algorithm to avoid overwriting player recordings from previous scene
* Added a feature to pack manually models into Blockbuster's jar (see `assets/blockbuster/models/user/` folder)
* Added anchor point option to `structure` morph
* Added support for loading n-gons in OBJs models
* Added shadow option to `image` morph
* Removed eye height animation to being laggy

## Version 2.3

This update doesn't have any aim, but rather has a lot of random features, nothing groundbreaking though.

**Compatible** with McLib `2.3`, Metamorph `1.2.7` and Aperture `1.6`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/vO1tAgNsCUo"><img src="https://img.youtube.com/vi/vO1tAgNsCUo/0.jpg"></a> 

### General

* Added limiting of editing Blockbuster data on the server to OP only
* Added Emoticons version check for those who forget to update
* Added search bar to texture manager panel (suggested by ALL.Creator)
* Added audio bar playback time display option (suggested by gewenzsko)
* Added reset transformation to pose transformations
* Added a replay option to render actor last (suggested by gewenzsko)
* Added depth sorting of always rendered actors
* Added canceling of player recording by pressing pause scene keybind (suggested by Chunk7)
* Added truly random option to `sequencer` morph
* Added outline and background to selected replay in the scene menu (suggested by Chunk7)
* Changed model block to be uneditable in adventure mode (suggested by The Darvin Studio)
* Fixed `sequencer` morph entry doesn't get copied when it should be
* Fixed break block animation not working in first person playback (reported by Silent)
* Fixed jittery body yaw rotation in first person playback (reported by zoombie)
* Fixed custom model blocks are dropped as default Steve model blocks in survival (reported by The Minebox)
* Fixed scene's loops option (reported by Centryfuga)
* Fixed concurrent modification crash when a scene launches another scene (reported by gewenzsko)
* Fixed ignored option wasn't being handled properly in better preview
* Fixed crash with first person playback on dedicated server (reported by gewenzsko)
* Fixed floating point precision with long coordinates for player recordings
* Fixed interpolation list overflown by pick model button

### BB gun

* Added ammo item stack option to BB gun
* Added knockback factor option to BB gun (suggested by Centryfuga)
* Added on vaniash and on entity impact command options to BB gun (suggested by Centryfuga)
* Added ignore blocks, ignore entities (suggested by Centryfuga) and vertical knockback
* Changed BB gun's scatter to radial model, which allows creating 360 degree guns (suggested by Joziah2)
* Fixed BB gun projectiles bounce off of non opaque blocks (reported by Centryfuga)
* Fixed collision and clipping of BB gun projectiles (reported by Chryfi and Centryfuga)
* Fixed BB gun with one projectile with sequencer and random enabled not being truly random (reported by Chunk7)
* Fixed firing command not working with 0 projectiles

### Commands

* Added `/record apply <target> <source> <properties> [relative] [from] [to]` command, which applies certain properties from one player recording's frames on top of another
* Added `/mount <target> [destination]` command (suggested by Jvonlins)
* Added `/action cancel` command to cancel recording (suggested by El_Redstoniano)
* Fixed `/record append` player recording not working with `0` countdown (reported by gewenzsko)
* Fixed `/record origin` not rotating body yaw (reported by RunnyHero)
* Fixed `/scene loop` resetting the scene

### Models

* Added NBT presets to custom models
* Added legacy OBJ loading option to allow disabling an old way of loading models which are flipped on X axis
* Added ability to edit shape keys per pose
* Added rename pose context menu to model editor
* Added more default models:
    * `eyes/3.0` which is eyes rig 3.0 (suggested by Jvonlins)
    * `eyes/3.0_1px` which is eyes rig 3.0 but 1 pixel high
    * `eyes/3.1` which is eyes rig 3.1 (3.0 but with with bends)
    * `eyes/3.1_simple` which is eyes rig 3.1 (3.1 but with less bends)
    * `eyes/alex` which is `alex` but with 3D head for covering eyes holes
    * `eyes/fred` which is `fred` but with 3D head for covering eyes holes
    * `eyes/steve` which is `steve` but with 3D head for covering eyes holes
    * `eyes/head` which is just `fred` model's 3D head for using with Emoticons morphs for covering eye holes
    * `eyes/head_3D` which is just `fred` model's 3D (but also outer layer 3D) head for using with Emoticons morphs for covering eye holes
    * `mchorse/head` which is McHorse's head model
* Added McHorse morph in Blockbuster extra
* Added factory textures that can be accessed in texture picker:
    * `blockbuster/textures/entity/eyes/` are default eye skins for Steve and Alex
    * `blockbuster/textures/entity/eye_masks/` are eyes masks that allow erasing 2 and 1 pixel high eye holes with multi-skin
    * `blockbuster/textures/entity/skin_masks/` are skin masks (by Silverx) that allow erasing body parts on Minecraft player skins
    * `blockbuster/textures/entity/mchorse/` are McHorse's eyes, head and skin textures
    * `blockbuster/textures/entity/pixel.png` is a white pixel that can be used with image morph for glow effects (use the color filter to change the color of the pixel)
* Changed shape keys' relative option to be enabled by default
* Fixed shape keys incorrectly calculating after morph merging
* Fixed OBJ files not being copied when duplicating a model (reported by SergiDPlay)
* Fixed shape keys normal interpolation
* Fixed `steve` armor shoes swinging
* Fixed shape keys merging NPE crashing
* Fixed shape keys not reloading

### Snowstorm particles

* Added saving of collapsed tabs in particle editor (developed by Chryfi)
* Added inertia option (developed by Chryfi)
* Added realistic collision drag (developed by Chryfi)
* Added tangential velocity (developed by Chryfi)
* Added local velocity (developed by Chryfi)
* Added local scale (developed by Chryfi)
* Added scale texture (developed by Chryfi)
* Changed duplication of particle effects also saves it immediately (suggested by El_Redstoniano)
* Fixed Snowstorm particle effects not loading upon entering the world (reported by Jvonlins)
* Fixed NPE crash when particle doesn't exist anymore on the disk but was still present in the list (reported by zoombie)
* Fixed NPE crash in Snowstorm's morph editor when particle doesn't exist anymore (reported by Chryfi)
* Fixed once lifetime emitter component working incorrectly

### Structure morph

* Added animation and transformations options to `structure` morph (suggested by Chunk7)
* Added `structure` morph's name display (suggested by Guider)
* Added biome picker for `structure` morph (developed by NyaNLI)
* Added lighting option for `structure` morph (developed by NyaNLI)
* Fixed `structure` morph not rendering without disabling cached rendering (fixed by NyaNLI)
* Removed Cached structure rendering

## Version 2.2.2

This patch update was made to complement BB guns tutorial video.

**Compatible** with McLib `2.2.2`, Metamorph `1.2.5` and Aperture `1.5.2`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added **Cycle between panels** keybind to configure BB gun GUI
* Added **Particle expression** in Initialization expression section of particle editor (implemented by Chryfi)
* Added new functions to MoLang according to 1.16 spec: `math.acos(value)`, `math.asin(value)`, `math.atan(value)`, `math.atan2(y, x)`, `math.random_integer(min, max, seed)`, `math.die_roll(num, low, high)`, `math.die_roll_integer(num, low, high)` and `math.hermite_blend(x)`
* Changed layout of BB gun panels
* Fixed entity motion and preserve energy toggles not updating when changing values (reported by Chryfi)
* Fixed character limit in some fields within particle editor (reported by Chryfi)
* Fixed sequencer morph editor removing first similar morph instead of currently selected (reported by Chunk7)
* Fixed BB gun's default morph not updating in hand and gun transformations preview
* Fixed BB gun's vanish option bugging, not respecting sticks option and prematurely vanishing on the client side

## Version 2.2.1

This patch update adds a couple of neat features and fixes.

**Compatible** with McLib `2.2.1`, Metamorph `1.2.5` and Aperture `1.5.2`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added `body_yaw` and `roll` properties to `/record clean` and `/record process` commands
* Added support for nested animated morphs for morph action's animation duration marker
* Added **Update scene after saving** option (suggested by gewenzsko)
* Added **Item scale** and **Cape** options for model limbs
* Added new default `cape` model
* Fix crash with playback button GUI (reported by 1Deni)
* Fix record button not working in the standalone player recording editor (reported by gewenzsko)

## Version 2.2

This huge update features lots of new cool features like first person player playback, easier animation of body parts and sequencers and lots of quality of life features and tweaks, and bug fixes.

**Compatible** with McLib `2.2`, Metamorph `1.2.5` and Aperture `1.5.1`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/ioHMMEhxgkE"><img src="https://img.youtube.com/vi/ioHMMEhxgkE/0.jpg"></a> 

* Added shape key feature to OBJ model loading (it allows creating animations based on different states of OBJ models in model's `shapes` folder)
* Added support for local variables in particle system
* Added **use target** option to BB gun, to be able to use particle morph inside of BB gun's default and firing morphs (suggested by Centryfuga)
* Added **save texture** (suggested by edgyhumanzombieninja) and **copy path** buttons to texture manager panel
* Added `/modelblock` command which allows to edit model blocks (suggested by Crazy)
    * `/modelblock morph <x> <y> <z> [morph_nbt]` changes morph of the model block at XYZ
    * `/modelblock property <x> <y> <z> <property:enabled> <value>` changes a property of the model block at XYZ
* Added **Cached structure rendering** mod option
* Added animation preview into sequencer's morph editor (suggested by MaiZhi)
* Added a feature to set colorful name tags for actors (suggested by Mopolo)
* Added `/record rename <filename> <new_filename>` command
* Added **Copy pose** and **Paste pose** context menu to pose editor's limb list and pose list in model editor's pose panel (suggested by Joziah2)
* Added **Target** option to scene's replay, which allows playing back replays with a real actor
* Added a feature that automatically reloads a scene if a scene gets edited when it's already playing
* Added configuration of playback button upon sneak + right click (suggested by Mopolo)
* Added `/model combine <paths...>` command which allows creating permutations of skins in given folders relative to `config/blockbuster/models/` (suggested by Tossler)
* Added `/model report` command which allows creating a report about models and skins
* Added **Roll** option to limbs, which allows rolling the limb based on actor's or player's roll 
* Added `roll` property to player recording (suggested by Chryfi)
* Added `/record camera <filename> <camera_profile> [x] [y] [z]` command which updates, or generates a new, player recording based on motion in `<camera_profile>`
* Added `variable.particle_bounces` variable to particles of particle system (helped by Chryfi)
* Added actor path rendering in F3 screen (suggested by noob and Andruxioid)
* Added `/action append <filename> <offset> [scene]` command, which allows to start recording at desired `<offset>` tick
* Added **Teleport to tick** (`Ctrl + T` key combo) and **Record at tick** (`Ctrl + R` key combo) buttons to player recording editor
* Added **BB gun sync distance** mod option
* Added `/damage <entity> <amount>` command (suggested by Centryfuga)
* Changed sequencer morph's **set duration** option to be off by default
* Changed `/action play`'s command syntax to `/action play <filename> [invincibility] [morph_nbt]` (suggested by Centryfuga)
* Changed the way player recordings are getting saved upon modifying them with commands and player recordings to being able restore previous versions (reported by Ethobot)
* Change the wording of director block outdated messages (and send video link in the chat)
* Fixed incorrectly calculated bottom face in UV editor
* Fixed structure morph's lighting bug (glowing in the dark)
* Fixed crash when putting `/scene play` command into command on stop playback of another scene (reported by Crazy)
* Fixed **Edit camera** and **Teleport** buttons going off screen with large GUI scale
* Fixed audio gets resumed after pausing it when it was already paused
* Fixed extruded layers disappearing at random (reported by Warkanam)
* Fixed structure morphs not correctly appearing after Minecraft resources reload (reported by Joziah2)
* Fixed actors not having more than 20 health when setting custom health value above 20 (reported by Amin)
* Fixed new `"x"`, `"y"` and `"z"` values for disc shape's normal breaking the particle effect for particle system (reported by agirres)
* Fixed GIF texture not resetting playback upon model block update 
* Fixed audio file is not being scrolled upon picking a different scene
* Fixed `/model convert`'s help message (reported by Andruxioid)
* Fixed `steve` and `steve_3d` not having chestplate and boots (reported by Miscodes)
* Fixed inconsistency with vertical and horizontal image morph's cropping (reported by TimeShadow Studios)
* Removed **Recording frame skip** mod option

### Chryfi's particle system extension

Besides all that cool stuff in the list above, [Chryfi](https://www.youtube.com/Chryfi) worked for more than 3 months adding a lot of mind blowing features to the particle system. All of these additions below are Chryfi's outstanding work:

* Added variables `variable.particle_speed.length`, `variable.particle_speed.x`, `variable.particle_speed.y` and `variable.particle_speed.z`. Could be used for motionblur like effect.
* Added Сamera facing mode button to Appearance section
* Added more options to Local space section:
    * Relative direction: when enabled, it rotates the direction vector when spawning according to the rotation of the emitter/body part. Example: make particles shoot out of a body part like blood.
    * Relative acceleration: when enabled, it rotates the acceleration vector throughout the whole lifetime of a particle according to the body part's local rotation.
    * Gravity: when enabled, it vertically accelerates every particle by `-9.81` which won't be affected by the local acceleration.
* Added more options to Collision section:
    * Realistic collision: the direction vector will be mirrored on collision. Example: together with gravity particles can now bounce realistically like a bouncy rubber ball.
    * Random direction: this randomizes the direction on collision. It can be used with realistic collision on or off. It doesn't affect the speed, it only changes the direction. When bounciness is `0` this still changes the direction just without reflecting on the surface. This could be used for force fields.
        * Preserve energy: when bounciness is `0` and random bounciness is not `0`, an enabled preserve energy would ignore that the vector component responsible for colliding will be `0`. Enabled preserve energy will make the particles fast.
    * Split particles: this splits the particles into a given amount on impact. Their speed will change according to the number of splits e.g. `4` splits => `speed / 4`.
    * Split particles speed threshold: this is the speed threshold for activating the split process.
    * Damping: reduce the velocity of the particles on impact. Valid range `0..1`, `1` being the highest damping (`1` reduces the velocity completely).
    * Random damping: randomize the damping. Values are `0..1`. Example: damping of `1.0` and random damping of `0.5` means the damping will vary between `0.5` and `1.5`.
    * Change texture/appearance on collision.
    * Change tint and lighting on collision.
    * Expire on impact delay: would be also useful for texture on impact. It can be used with Molang expressions like `math.random()`, negative values will be turned into positive values.
    * Collision with entity's hitboxes.
    * Collision with entity pseudo momentum.

## Version 2.1.1

This small patch update features texture coordinates preview in the model editor, a bug fix for extruded models and updated the tutorial link to the new version of tutorial series.

**Compatible** with McLib `2.1.1`, Metamorph `1.2.3` and Aperture `1.5`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added texture coordinates preview to the model editor panel
* Changed the tutorial link to new Blockbuster academy (2020) playlist
* Fixed extruded layers not working from time to time with asynchronous multi-skin

## Version 2.1

This update is focused on simplifying some aspects of recording actors, and editing the player recordings. This update features a game changer feature called in-game audio synchronization. Director blocks' functionality was removed.

**Compatible** with McLib `2.1`, Metamorph `1.2.3` and Aperture `1.5`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/NmGz3SVs6Gs"><img src="https://img.youtube.com/vi/NmGz3SVs6Gs/0.jpg"></a> 

* Added an option to specify custom model folder on the disk (implemented by Maxi)
* Added `/item_nbt <generate_give_command:true|false>` command which allows to copying currently held item's NBT and optionally generate a `/give` command of this item (suggested by Tossler)
* Added extrusion max and extrusion factors options to model editor, which allows having HD skins to have smaller HD bits (suggested by ZyphoxFilms)
* Added **in-game audio syncing** feature to scenes, which allows attaching a `.wav` audio track which plays and syncs when you playback a scene, record an actor and edit camera profile
* Added **better preview** mechanism to sync animated poses, image animations, etc. while previewing the scene in the camera editor (it's not 100% accurate yet still better than what we had)
* Added a new `body_yaw` property to player recording's frames (which represents horizontal body rotation)
* Added `/record calculate_body_yaw` subcommand that allows to calculate the `body_yaw` property for old player recordings
* Added **Set duration** option to sequencer morph, which allows to overwrite the duration of the animated morph with sequence's duration (suggested by Chunk7)
* Added options to **Recording** category:
    * Added **Playback body yaw** option to Actor category which allows to disable recorded playback of body's horizontal rotation (it's enabled by default)
    * Added **Better preview** option to toggle **better preview** (it's enabled by default)
* Added **Snowstorm** option category with one option to enabled particle depth sorting
* Added **Audio** option category with new options:
    * Audio bars visible — whether preview waveform audio bars should be visible
    * Waveform density — how many pixels per second for waveform
    * Audio bar width — how wide is a single audio bar (in `0..1` percentage)
    * Audio bar height — how high is a single audio bar (in pixels)
    * Show audio bar's filename — whether filename of a played audio should be shown
* Added **Reset on playback** option to **Model block** category which allows resetting model blocks upon playback of the scene (suggested by Herr Bergmann)
* Added a simpler skin loading mechanism that allows to drop any Minecraft player skins into `minecraft/config/blockbuster/skins/`, and they will be transferred into correct folder, depending on the format of the skin (`1:1` skins, like `64x64`, will go to `fred/skins`, while `2:1` skins, like `64x32`, will to go `steve/skins`, HD skins supported as well, inspired by Chunk7's suggestion)
* Added animated and sequencer morph indicators to display to morph action block in the player recording editor
* Added camera editor's timeline cursor to the action editor
* Changed the horizontal zoom reset after selecting another player recording to edit (suggested by Andruxioid)
* Changed the way action editor is being synced with camera editor's timeline when scrubbing and playing
* Fixed hovering actors bug (after 3 years, reported by Badr, KazerLight and El_Redstoniano)
* Fixed **Always render actors** option not working in culled chunks (reported by many in the past)
* Fixed enchanted armor not working on custom models (reported by VillagerFilms)
* Removed the functionality of **Director blocks** (however data in director blocks will be converted to scenes upon loading this update)

## Version 2.0.2

This is a patch update that features a lot of fixes made to Snowstorm particle system, and to other parts of the mod. A couple of GUI keybinds were added, and a couple of useful /record sub-commands were added.

**Compatible** with McLib `2.0.2`, Metamorph `1.2` and Aperture `1.4`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added restore model blocks option which allows to restore a previous edited model block in the session if you accidentally broke it (disabled by default, suggested by Chunk7)
* Added Edit camera keybind (`C` key) to scenes menu
* Added Toggle scene list keybind (`N` key) to scenes menu
* Added Teleport button and keybind (`T` key) to scenes menu
* Added Detach scene keybind (`Shift + D` combo) to camera editor
* Added Reload scene keybind (`Shift + R` combo) to camera editor
* Added Apply pose icon button in model editor's poses modal, which allows to apply current limb's pose on other poses
* Added BB gun penetration factor option
* Added `/record erase <filename> <count> <from>` which removes `<count>` frames after (and including) `<from>` tick
* Added `/record process <filename> <property> <from> <to> <math>` which allows processing frame data in player recording using math expressions
* Added variable editor panel in Snowstorm's morph editor
* Added a copy of Metamorph's `block` morph to Blockbuster Extra category
* Changed how local position and local rotation properties work with Snowstorm particle system
* Fixed BB gun held item preview not working (reported by Terry)
* Fixed Snowstorm 0 radius/size of any shape mode causing particles to disappear 
* Fixed Snowstorm default values for spehere radius and initial speed component
* Fixed Snowstorm emitter lifetime components not setting `variable.emitter_lifetime`
* Fixed Snowstorm curves not working as intended (thanks to Jannis)
* Fixed Snowstorm gradient tint component not loading correctly
* Fixed player recording's with non-zero pre-delay not correctly syncing within player recording editor (reported by Lucatim)
* Fixed start and stop command not working when launching scene's playback
* Fixed size offset property of a limb not exporting correctly with `/model export_obj`
* Fixed AABB shape component not being implemented
* Fixed jittery animation with BB gun and model block rendering in the hand
* Fixed Snowstorm's half-dimensions size of box component were actually being quarter size
* Fixed "Ignored" option of animated poses not working correctly with player recording editor (reported by Tossler)

## Version 2.0.1

This is a quick patch that fixes a couple of GUI issues and adds pick texture and skin keybinds.

**Compatible** with McLib `2.0.1`, Metamorph `1.2` and Aperture `1.4`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added back Steve/Alex sequencer morph
* Added player recording's filename label in player recording editor
* Added keybinds to pick texture in image and custom model morph pickers (Shift + P)
* Added copies of `item` and `label` morphs (suggested by Andruxioid)
* Fixed duplication modal of model editor not working correctly with nested model names
* Fixed equip action panel's slot field not being filled correctly
* Updated Chinese strings for 2.0 (thanks to Chunk7, KuenYo_ and H2SO4GepaoAX)

## Version 2.0

This huge update adds a couple of awesome major feature, but mostly focuses on GUI improvements and quality of life features. This update also fixes dozens of bugs.

**Compatible** with McLib `2.0`, Metamorph `1.2` and Aperture `1.4`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/JghXifbHi-k"><img src="https://img.youtube.com/vi/JghXifbHi-k/0.jpg"></a> 

* Added `snowstorm` morph which allows to create custom particle effects based on Bedrock particle JSON specification (sponsored feature by Spark Squared)
    * Added particle effect editor in dashboard GUI
* Added `/scene <play|toggle|stop|loop> <name> [flag]` command to work with scenes
* Added new config options:
    * Added utility blocks option which adds barrier, structure and command blocks to Blockbuster creative tabs when it's enabled
    * Added disable riding option which disables riding on actors, by default actors are ridable (suggested by Stealth)
* Added new camera editor to scene (or director block) synchronization by using "Edit camera" button in scene/director block GUI
* Added `interact_entity` action to player recording system (suggested by Lucatim)
* Added "Vanish delay" BB gun option
* Added compatibility with actors opening Little Tiles' (requires version `1.5.0-pre199_34` or above) doors upon right click on block action
* Added dim chroma blocks which don't emit lighting
* Added "Local rotation" option to `particle` morph (suggested by Centryfuga)
* Added `/record fill <filename> <count> [tick]` command which allows to insert placeholder `<count>` frames at given `[tick]`
* Added keying option to `image` and custom model morphs
* Changed shadow option to be enabled by default, and global option to be disabled by default in model block
* Changed BB gun firing to allow to fire BB guns in both hands (suggested by SergiDPlay)
* Changed **Configure BB gun keybind**'s default key to `End` key
* Changed layout of items in Blockbuster's creative tab
* Changed playback of GIF textures based on entity's tick counter (suggested by Jetpack Rescue)
* Improved GUI screens:
    * Added previews to `sequencer` morph editor GUI (suggested by Jetpack Rescue)
    * Added previews of held gun item and projectile transformations in BB gun GUI
    * Added keybinds to camera editor to toggle visibility of player recording timeline (Ctrl + E) and player recording list (Ctrl + L)
    * Added copy, paste and cut (suggested by Lucatim) buttons to player recording editor GUI
    * Added Ctrl + M keybind to player recording editor GUI to add morph action
    * Added replay sorting to scenes/director block GUI (suggested by Lucatim)
    * Added alt + mouse wheel to horizontally zoom in/out and shift + mouse wheel to vertically scroll
    * Changed layout of model block panel GUI
    * Changed layout of BB gun GUI
    * Changed layout of `particle` morph GUI
    * Changed layout of `blockbuster.*` custom model morph GUI
    * Changed layout of `image` morph GUI
    * Changed layout of actor configuration item GUI
    * Changed "Pick morph" buttons everywhere to "Pick | Edit" for faster morph access
    * Fixed model blocks not being selected in quick access list menu
* Improved `image` morph:
    * Added color filtering property (including opacity)
    * Added animations (similar to animated poses)
    * Added resize crop option (which enables fitting the cropped region into 1 block space)
    * Added UV coordinate shift options
    * Added UV rotation option
    * Added pose (translate, scale and rotate)
    * Removed scale option (backward compatible though)
* Improved drastically model loading and reloading
* Fixed crash with image morphs on the server side (reported by ItsKylos)
* Fixed `/director` command working only in overworld dimension (reported by AceGaming)
* Fixed mob export model feature in the model editor (reported by Mr Wolf)
* Fixed `/record origin` command requiring only a player to use the command (reported by Joziah2)
* Fixed BB gun firing from the default head position rather from current poses's hitbox (reported by Reunion Studio)
* Fixed BB gun's projectile leaning to the left side when firing (reported by Reunion Studio)
* Fixed crash when trying deleting non loaded OBJ model (reported by ItsKylos)
* Fixed item placement when Blockbuster models hold items (reported by HumbleDoctor)
* Fixed crash when attaching a playback button and Aperture is not installed
* Fixed not being able to pick a limb when specific body parts are present
* Fixed wheels spinning in the model editor
* Fixed `image` morph's transparency cutting off at values below `25` (out of 255)
* Fixed big (128+ voxels in any dimension) voxel chunks not loading (reported by Afegor)
* Fixed kicking out of server with big structures (requires tweaking of McLib's Max. packet size)
* Fixed scene names couldn't have `.`s in it (reported by OrokinPlays)
* Fixed instant crash when McLib isn't present
* Removed old YikeFilms easter egg and extra wubs config option
* Removed config options from Mod Options (and moved to McLib's configuration)

## Version 1.6.6

This is another patch update which fixes lots of bugs. Beside bug fixes, there are also new BB gun options which allows for a better projectile configuration. Andruxioid also introduces Ukranian translation in this update!

**Compatible** with McLib `1.0.4`, Metamorph `1.1.10` and Aperture `1.3.5`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added `ua_UA` (Ukranian) language localization (translated by Andruxioid)
* Added an ability to use decimals for countdown
* Added BB gun sticking option
* Added BB gun bounce factor option
* Added default variant of image morph
* Added pausing keybind to playing keybind (suggested by SillySheep)
* Added pick skin button in the model editor
* Changed recording countdown to `1.5` seconds by default
* Changed places pick skin and pick model (suggested by Tossler)
* Fixed a crash with unknown action type (reported by zoombie)
* Fixed model files not being copied over (reported by Sbriser)
* Fixed player recordings being unloaded during camera editor (reported by Koki)
* Fixed materials not supporting GIF textures (reported by SillySheep)
* Fixed OBJ meshes with the same name not getting merged into single mesh
* Fixed sequencer morph merging when they're the same (doesn't work in some cases due to Metamorph's code)
* Fixed lying pose has incorrect Y position for \*wears (reported by Tossler)
* Fixed old `vox` models that don't have the scene graph to get loaded (reported by Mayd and Murply)
* Fixed director block's block morph not appearing correctly
* Removed old commands (`/model texture` and `/model replace_texture`)

## Version 1.6.5

This is a little update features two neat features, and mostly bug fixes. A couple of things to note about this update:

1. MagicaVoxel code was rewritten, and now the new code shows models differently, so if you MagicaVoxel models before in `1.6.3` or `1.6.4`, it will break poses and general position and orientation of these models. You'll have to reconfigure your morphs, **so don't update until you finish current project**.
2. MorePlayerModels-like models (`alex_3d`, `fred_3d` and `steve_3d`) were added. Please **remove your 3D (`alex_3d`, `fred_3d` and `steve_3d`) models** if you have installed it before.
3. I fixed some bug, which caused countdown and some other features to run twice as fast, so apparently the countdown wasn't 3 seconds all that time, but rather 1.5 seconds... My life have been a lie...

**Compatible** with McLib `1.0.4`, Metamorph `1.1.10` and Aperture `1.3.5`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added multiple object parsing to vox reader to be able to use multiple limbs
* Added `offset` scale option to custom model code
* Added 3D model variants to Blockbuster
* Changed the color of model blocks in F3 state when they're not enabled (to something like yellow, because green and red are already occupied by director blocks)
* Changed particle morph spawning particles on the client side rather than on the server side
* Fixed `this.model` being `null` (reported by \_Paddi)
* Fixed sequencer and custom morph can merge interactions are wrong...
* Fixed post and pre delays are not applying first/last frames on the actor during delay periods
* Fixed prefix generation when the scene name has *prefix*, i.e. scene name is `tia_6`, but the first replay becomes `tia_1` instead of `tia_6_1`
* Fixed vanilla morph not working with BB gun properly (reported by Centryfuga)
* Fixed fake player being twice as fast with scenes (reported by SillySheep)

## Version 1.6.4

This is a quick hot patch update which fixes a couple of major issues I found after releasing 1.6.3, my bad guys...

**Compatible** with McLib `1.0.4`, Metamorph `1.1.10` and Aperture `1.3.5`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added a config option for green sky color
* Added negative pre- and post-delays (suggested by Agirres)
* Fixed vanilla particle morphs to work with body parts
* Fixed limb pose and body part list after switching a model
* Fixed scenes not saving when switching between scenes
* Fixed scenes getting removed when being edited (stupid copy paste)
* Fixed animated pose feature not resetting correctly

## Version 1.6.3

This update introduces native support for MagicaVoxel (`*.vox`) models, Scenes (remote director blocks which are stored in files and managed in GUI), many new options, a couple of nice tweaks and workflow enhancements and, as always, bug and crash fixes.

<a href="https://youtu.be/4n5p83KAG4k"><img src="https://img.youtube.com/vi/4n5p83KAG4k/0.jpg"></a> 

**Compatible** with McLib `1.0.4`, Metamorph `1.1.10` and Aperture `1.3.5`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added support to `.vox` models
* Added scenes (remote alternatives to director blocks) (reminder by Agirres)
* Added "First time?" modal (it shows relevant tutorial information about the mod)
* Added player recording filename rendering of an actor to F3 screen
* Added `image` morph cropping options (left, top, right, bottom)
* Added `record` morph random skip option (suggested by Andruxioid)
* Added BB gun projectile fade in/out options (suggested by Joziah2)
* Added a button in main panel to open `models` folder
* Added a button to custom model morph editor that allows to change model without NBT editing
* Added a replay option to teleport back to where you started recording
* Added `/record reverse <filename>` command to reverse playback of actors (suggested by STH)
* Added `/record flip <filename> <axis:X|Z> <coordinate> [center]` to allow an ability to create mirrored scenes
* Changed URL from my main to secondary channel in the main panel
* Changed model loading system to allow use **any filename** for OBJs, MTLs or vox models files
* Changed `Dashboard` keybind to allow holding Control key to open the main panel
* Fixed incorrect config name for chat prefix (thanks to Lucatim)
* Fixed a crash related to poor morph seeking
* Fixed `/model convert` not auto-completing filenames
* Fixed digging, mining and other stuff actually destroy blocks in case it was too fast
* Fixed actors disappearing (after their playback finished) in Aperture's preview when playing the camera profile
* Fixed body parts not completely in sync with `Idle` checkbox (`setRotationAndAngles` issue)
* Fixed custom model and sequencer morphs no transition in between each other
* Fixed translucent pixels in textures appear opaque (reported by Chunk7)
* Fixed `billboard` image morph option not working with body parts correctly
* Fixed custom model animated poses being not correctly merged when used with looping sequencer (reported by SillySheep)

## Version 1.6.2

This update introduces particle morph, a couple of QoL GUI features, minor visual tweaks and lots of bug fixes.

<a href="https://youtu.be/aToxS732NfE"><img src="https://img.youtube.com/vi/aToxS732NfE/0.jpg"></a> 

**Compatible** with McLib `1.0.4`, Metamorph `1.1.10` and Aperture `1.3.3`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

* Added picking limbs by left clicking them while holding Ctrl/Command key in model editor, pose editor or body part menus
* Added more `/record` sub-commands:
    * `/record fade <filename> <fade_out>` to cross fade last `<fade_out>` frames with beginning
    * `/record cut <filename> <before> <after>` cuts out all frames before `<before>` and after `<after>`
    * `/record restore <filename> <iteration>` allows to restore (by swapping files) player recording with given `<iteration>` version
* Added model block `Global` and `Enabled` model block options
* Added an ability to use wings on Z axis (`swiping`)
* Added `Hold` checkbox in model editor to disable holding item angle correction
* Added an ability to leash the actor (right click with lead)
* Added `particle` morph which emites vanilla and morph configured particles
* Changed `/model convert` to be more flexible
* Changed `/record prolong` to output the resulted prolongation of the player recording
* Changed model block Y rotation to be assigned on placing when sneaking (and add a button to orient toward you instead)
* Changed director and model block F3 rendering to quads instead of lines (suggested by Andruxioid)
* Changed the recording ID field to disallow using spaces
* Fix issue with glitching Aperture slider (reported by Andruxioid) which also fixes double clicking events
* Fix issue with resetting morphs in camera editor (reported by KazerLight)
* Fix and investigate `3D` feature's missing voxels... (issue was using wrong dimension value, i.e. width instead of depth, for left and right sides) 
* Fix `3D`'s edge geometry construction to correctly texture map (implement bit flags)
* Fix BB gun to allow 0 bounces
* Fix a crash related to discarded director block when it doesn't exist anymore
* Fix recording ID getting overlapped by morphs (reported by Kanguste)
* Fix global scale not affecting vanilla entity shadows (reported by Joziah2)
* Fix entity rotation when entering Aperture (reported by Reunion Studio)
* Fix desynchronized actors who are not disappearing due to MrCrayfish's furniture (reported by Dracay and LadyMania)
* Fix crisis of broken action in player recordings and fix NPE with URL skins (reported by Jubb)
* Fix minor issue with sequencer morph where it skips the first sequence when initializing
* Fix textures not appearing if they have upper case file extension (reported by Chunk7)

## Version 1.6.1

This is a small update which introduces animated poses, a couple of neat config options here and thered, some quality of life tweaks, and plenty of crash/bug fixes.

**Compatible** with McLib `1.0.3`, Metamorph `1.1.9` and Aperture `1.3.2`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/CI0WGNFLV4k"><img src="https://img.youtube.com/vi/CI0WGNFLV4k/0.jpg"></a> 

* Added random order option for `sequencer` morph
* Added loop option for `record` morph
* Added "Launch up the player" option for BB gun
* Added a config option to disable model block rendering
* Added animated poses feature for custom model morphs
* Added `empty` default model
* Added a keybind to record currently selected replay in director block menu
* Added saving of 5 last versions of player recording
* Added default keys for record and play/pause director block keybinds (right alt and right control respectively)
* Changed limbs that have `0` opacity to not render at all
* Changed damage/attack action not affecting character's health in camera editor preview mode (suggested by Ray from Slightly Insulted)
* Changed director block GUI to prevent selecting first replay every time
* Changed recordings list to display director block related player recordings
* Changed the caption in the top left corner to display: which player recording is about to start recording, and the tick of currently recording player recording.
* Fixed NPE crash with `null` morph replay (reported by Braigar)
* Fixed pre delay not properly playback on the client
* Fixed GIF not working with custom model morphs
* Fixed GIF having one blank frame
* Fixed Aperture to work with new changes from 1.3.2
* Fixed `/model export_obj` exporting a mirrored detached (in terms of vertices) version of model
* Fixed index out of bounds exception when duping in director block with no replays (reported by Afegor)
* Fixed a crash related to actors using playback button, which was reported by Olrik&Flynn, but apparently I didn't fix it (reported by Serene Studios)
* Fixed minor rendering issue with limb highlight with body part being the same model (reported by Andruxioid)

## Version 1.6

This big massive update probably isn't as big as `1.5`, however, it saturated with so many awesome features that expand new horizons of machinima creation! Beside that, this update also makes Blockbuster much more stable than `1.5.3`.

This update adds three new types of special Blockbuster exclusive morphs, in addition to `image` morph: Sequencer, Record and Structure morphs. It also adds another several major features: multiskin, improved texture picker, custom model smooth shading, hide all director blocks (if hide on playback is enabled) upon playback, item slots support in body parts, several new image and custom model morph properties and dozens of bug fixes!

**Compatible** with McLib `1.0.1`, Metamorph `1.1.8` and Aperture `1.3.1`. It doesn't mean that future versions of Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

<a href="https://youtu.be/wpfiLTrzTLs"><img src="https://img.youtube.com/vi/wpfiLTrzTLs/0.jpg"></a> 

#### General

* Added following config options:
    * Added `Actors > Fix Y` config option, which uses actual Y value recorded on the client side, instead of the interpolated from server, which in turn fixes hovering when jumping around, but at the cost of sharp vertical movement
    * Added `Actors > Always render` which makes body actor entities render always no matter hitbox size (suggested by Andruxioid)
* Added multi-skin feature to texture picker which allows constructing a single texture out of multiple textures
* Added texture manager panel where you can explore textures loaded by Minecraft
* Added animated GIF support (with reference work from [MrCrayfish's furniture mod](https://github.com/MrCrayfish/MrCrayfishFurnitureMod) and [DhyanB](https://github.com/DhyanB/Open-Imaging/blob/master/src/main/java/at/dhyan/open_imaging/GifDecoder.java))
* Added **BB gun** item, which allows to setup a firearm-like weapon which can shoot projectiles with lots of different configuration options (suggested by STH and Andruxioid)
* Add hierarchical model loading and sorting in `config/blockbuster/models` (suggested by Jubb)
* Changed the location of ticks in player recording editor grid
* Changed the texture picker to capable of browsing folders
* Fixed crash related to body actors using playback button (reported by ChrissyPixy)
* Fixed issue with duplicated player recording in the list
* Fixed `@VERSION` in blockbuster_core mod (reported by Andruxioid, I think)
* Fixed issue with URL skins doesn't work with extruded layers and /model texture (reported by Jubb)
* Fixed clickable item slots through the morph picker
* Fixed crash related to tick out of bounds (I assume with pre delay) (reported by STH)
* Fixed NPE with image morphs when stupid png URLs lead to those stupid websites instead of an actual PNG
* Fixed 1.11.2 chroma blocks appear all as green (reported by SlySuptic)
* Fixed item transformations on the head for model block
* Fixed crash when picking up a texture in texture manager (reported by Andruxioid)
* Fixed inventory GUI desync after player's death
* Fixed config comments display (reported by GroupM)
* Fixed shadow property of model block not getting synced
* Remove model editor button from main menu

#### Aperture integration

* Changed the player recording list in camera editor to not fully overlay the player recording editor
* Fixed crash camera editor initiation (reported by ycwei982 and zoombie)
* Fixed packets in Aperture's integration which kicks players (found by FairFox)
* Fixed Play and Load camera mode doesn't work on dedicated server (reported by Olrik&Flynn)
* Fixed some weird desync when recording editor elements are in camera editor (reported by Andruxioid and Agirres)
* Fixed pre/post delays set with /record prolong command not working properly with camera editor

#### Custom models

* Added `smooth` shading property to custom models and model editor which makes the triangular shading much smoother
* Fixed issue with default model not being completely loaded on start up
* Fixed small space with export mob model and add search bar (suggested by Joziah2)
* Fixed `body` limb when generating JSON model from auto OBJ
* Fixed writing materials to `model.json`
* Fixed extruded layers to support mirroring

#### Director block

* Added rendering of director block in F3 view (same way as model block) (suggested by ChatpKSK)
* Changed all director blocks to invisible when one starts playing (and hide on playback is enabled) (suggested by GroupM)
* Changed hidden block to have no collision when walking through them (suggested by Jubb)
* Changed the way rename prefix works (replace everything until last `_` and a number)
* Changed width of `Record` button to be the same width as other buttons
* Fixed issue with fake players on the server side in `Director#collectActors()` (reported by FairFox and Cactuz)
* Fixed issue with illegal property shit when tile entity director tries to check for hidden blocks (reported by Andruxioid and terbin)
* Fixed director block's loop mode cloning actors (reported by HerrBergmann and Lycoon)
* Fix/prohibit inserting invisible characters into Recording ID

#### Morphs

* Added new types of morphs:
    * Added `sequencer` morph, which takes in any number of morphs and switches between them based on given delay between every sequence (with possible randromizer), which allows creating animated morphs
    * Added `record` morph, which takes a morph and given player recording and plays it in a loop within the morph, this way it's possible to add alive passengers within models through body part system and etc. (loosely based on ideas of HerrBergmann)
    * Added `structure` morph, which allows using saved `nbt` structures via the structure block (suggested by El\_Redstoniano)
* Added support for item slots in body part system
* Added a couple of properties to `image` morph:
    * `lighting` option which allows to disable light map on the image morph (suggested by Morris)
    * `billboard` (Look at player) option which allows orient the image morph to always look at the player
* Added a couple of properties to custom model morph:
    * `scale` option which allows scaling the model (like the global scale option in the model editor)
    * `scaleGui` option which allows scaling the model in GUI cells
* Changed body part's default rotation by X axis to `180.0` by default
* Changed morph picker to allow to nullify selected morph by clicking elsewhere in the morph picker
* Fixed NPE crash with `getPose()` (reported by STH)
* Fixed NPE crash related to custom (reported by Andruxioid)
* Fixed body part crash on the server (reported by Skorpion_G)
* Fixed shading when scaling of image morphs (i.e. enable normal rescale)
* Fixed body part system in morph editor GUI not showing up

## Version 1.5.3

This patch update is quite massive in comparison to two previous patches. This update adds body part system, image morphs, a new Aperture integration, URL textures, few miscellaneous tweaks and lots of bug/crash fixes.

**Compatible** with McLib `1.0`, Metamorph `1.1.7` and Aperture `1.2`. It doesn't mean that future versions of McLib, Metamorph and Aperture would be incompatible, but older versions are most likely incompatible.

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

* Added `/action record` command to the history when pressing `Record` button in director block GUI (thanks to Tom Soel)
* Removed `spawn` sub-command from `/director` command
* Removed `/camera` command
* Removed `/load_chunks` command

#### GUI

* Added confirmation modal to the director block GUI (thanks to Sanchan, badr and others)
* Added label for morphs in morph pickers (thanks to Minebox)
* Added the support for displaying OBJ model parts in model editor
* Added GUI elements to modify `origin` and `providesObj` in model editor

#### Recording

* Added block placing sounds to `place_block` action (thanks to MadDreamer)
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
* Fixed crash when actor uses playback button (thanks to Badr)

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
* Works with [Metamorph](https://www.curseforge.com/minecraft/mc-mods/metamorph) `1.1.1`

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
* Fixed (for 1.11 version) holding hand status (reported by Juan Lopez on YT)

## Version 1.4.3

Another patch update. This patch is aimed at fixing few things and maybe some enhancements. 

* Changed the recording command message to `To record $name, click here.` (for understanding which actor to record)
* Fixed `steve` and `alex` armor desynchronization
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