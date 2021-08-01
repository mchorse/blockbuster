In this folder, you can place your own models in order to "pack" them into 
Blockbuster mod's jar. Automatically it won't work though, you'll need to 
edit the user.json file, and add there the models you're going to use, 
specify if OBJ, MTL, OBJ shapes or VOX files paths relative to users folder
and also change the paths to point into Blockbuster's jar path.

The JSON format for user.json is something like this:

{
    "siren_head": {
        "obj": "Siren_head.obj"
    },
    "shrek": {
        "obj": "model.obj",
        "mtl": "model.mtl",
        "shapes": [
            "shapes/smile.obj"
        ]
    },
    "lego": {
        "obj": "lego.obj",
        "mtl": "lego.mtl"
    },
    "book": {
        "vox": "book.vox"
    }
}

It's pretty hard to explain the entire thing, but if you'll stumble upon this 
file and will be have issues with packing your models into the mod's jar, feel 
free to join my Discord server and ask me about this: https://discord.gg/qfxrqUF