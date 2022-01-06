package mchorse.blockbuster.network;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.client.*;
import mchorse.blockbuster.network.client.audio.ClientHandlerAudio;
import mchorse.blockbuster.network.client.guns.*;
import mchorse.blockbuster.network.client.recording.ClientHandlerFrames;
import mchorse.blockbuster.network.client.recording.ClientHandlerPlayback;
import mchorse.blockbuster.network.client.recording.ClientHandlerPlayerRecording;
import mchorse.blockbuster.network.client.recording.ClientHandlerRequestedFrames;
import mchorse.blockbuster.network.client.recording.ClientHandlerSyncTick;
import mchorse.blockbuster.network.client.recording.ClientHandlerUnloadFrames;
import mchorse.blockbuster.network.client.recording.ClientHandlerUnloadRecordings;
import mchorse.blockbuster.network.client.recording.actions.ClientHandlerActionList;
import mchorse.blockbuster.network.client.recording.actions.ClientHandlerActions;
import mchorse.blockbuster.network.client.scene.ClientHandlerSceneCast;
import mchorse.blockbuster.network.client.scene.ClientHandlerSceneManage;
import mchorse.blockbuster.network.client.scene.ClientHandlerScenes;
import mchorse.blockbuster.network.common.*;
import mchorse.blockbuster.network.common.audio.PacketAudio;
import mchorse.blockbuster.network.common.guns.*;
import mchorse.blockbuster.network.common.recording.PacketFramesChunk;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketPlayerRecording;
import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.network.common.recording.PacketRequestRecording;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.network.common.recording.PacketUnloadFrames;
import mchorse.blockbuster.network.common.recording.PacketUnloadRecordings;
import mchorse.blockbuster.network.common.recording.PacketUpdatePlayerData;
import mchorse.blockbuster.network.common.recording.actions.PacketAction;
import mchorse.blockbuster.network.common.recording.actions.PacketActionList;
import mchorse.blockbuster.network.common.recording.actions.PacketActions;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestAction;
import mchorse.blockbuster.network.common.recording.actions.PacketRequestActions;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.blockbuster.network.common.scene.PacketScenePause;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.network.common.scene.PacketSceneTeleport;
import mchorse.blockbuster.network.common.scene.PacketScenes;
import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.network.common.scene.sync.PacketScenePlay;
import mchorse.blockbuster.network.common.structure.PacketStructure;
import mchorse.blockbuster.network.common.structure.PacketStructureList;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.blockbuster.network.common.structure.PacketStructureRequest;
import mchorse.blockbuster.network.server.*;
import mchorse.blockbuster.network.server.recording.ServerHandlerFramesChunk;
import mchorse.blockbuster.network.server.recording.ServerHandlerPlayback;
import mchorse.blockbuster.network.server.recording.ServerHandlerRequestFrames;
import mchorse.blockbuster.network.server.recording.ServerHandlerRequestRecording;
import mchorse.blockbuster.network.server.recording.ServerHandlerUpdatePlayerData;
import mchorse.blockbuster.network.server.recording.actions.ServerHandlerAction;
import mchorse.blockbuster.network.server.recording.actions.ServerHandlerRequestAction;
import mchorse.blockbuster.network.server.recording.actions.ServerHandlerRequestActions;
import mchorse.blockbuster.network.server.scene.ServerHandlerRequestScenes;
import mchorse.blockbuster.network.server.scene.ServerHandlerSceneCast;
import mchorse.blockbuster.network.server.scene.ServerHandlerSceneManage;
import mchorse.blockbuster.network.server.scene.ServerHandlerScenePause;
import mchorse.blockbuster.network.server.scene.ServerHandlerScenePlayback;
import mchorse.blockbuster.network.server.scene.ServerHandlerSceneRecord;
import mchorse.blockbuster.network.server.scene.ServerHandlerSceneRequestCast;
import mchorse.blockbuster.network.server.scene.ServerHandlerSceneTeleport;
import mchorse.blockbuster.network.server.scene.sync.ServerHandlerSceneGoto;
import mchorse.blockbuster.network.server.scene.sync.ServerHandlerScenePlay;
import mchorse.mclib.network.AbstractDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(Blockbuster.MOD_ID)
    {
        @Override
        public void register()
        {
            /* Update actor properties */
            register(PacketModifyActor.class, ClientHandlerModifyActor.class, Side.CLIENT);
            register(PacketModifyActor.class, ServerHandlerModifyActor.class, Side.SERVER);
            register(PacketActorPause.class, ClientHandlerActorPause.class, Side.CLIENT);
            register(PacketActorRotate.class, ServerHandlerActorRotate.class, Side.SERVER);

            /* Update model block properties */
            register(PacketModifyModelBlock.class, ClientHandlerModifyModelBlock.class, Side.CLIENT);
            register(PacketModifyModelBlock.class, ServerHandlerModifyModelBlock.class, Side.SERVER);

            /* Recording */
            register(PacketCaption.class, ClientHandlerCaption.class, Side.CLIENT);
            register(PacketPlayerRecording.class, ClientHandlerPlayerRecording.class, Side.CLIENT);

            register(PacketSyncTick.class, ClientHandlerSyncTick.class, Side.CLIENT);
            register(PacketPlayback.class, ClientHandlerPlayback.class, Side.CLIENT);
            register(PacketPlayback.class, ServerHandlerPlayback.class, Side.SERVER);

            register(PacketUnloadFrames.class, ClientHandlerUnloadFrames.class, Side.CLIENT);
            register(PacketUnloadRecordings.class, ClientHandlerUnloadRecordings.class, Side.CLIENT);

            register(PacketFramesLoad.class, ClientHandlerFrames.class, Side.CLIENT);
            register(PacketFramesChunk.class, ServerHandlerFramesChunk.class, Side.SERVER);
            register(PacketRequestedFrames.class, ClientHandlerRequestedFrames.class, Side.CLIENT);
            register(PacketRequestFrames.class, ServerHandlerRequestFrames.class, Side.SERVER);

            register(PacketAction.class, ServerHandlerAction.class, Side.SERVER);
            register(PacketActions.class, ClientHandlerActions.class, Side.CLIENT);
            register(PacketRequestAction.class, ServerHandlerRequestAction.class, Side.SERVER);
            register(PacketRequestActions.class, ServerHandlerRequestActions.class, Side.SERVER);
            register(PacketRequestRecording.class, ServerHandlerRequestRecording.class, Side.SERVER);
            register(PacketActionList.class, ClientHandlerActionList.class, Side.CLIENT);

            /* Scene management messages */
            register(PacketSceneCast.class, ClientHandlerSceneCast.class, Side.CLIENT);
            register(PacketSceneCast.class, ServerHandlerSceneCast.class, Side.SERVER);
            register(PacketSceneRequestCast.class, ServerHandlerSceneRequestCast.class, Side.SERVER);

            register(PacketScenes.class, ClientHandlerScenes.class, Side.CLIENT);
            register(PacketRequestScenes.class, ServerHandlerRequestScenes.class, Side.SERVER);
            register(PacketSceneManage.class, ClientHandlerSceneManage.class, Side.SERVER);
            register(PacketSceneManage.class, ServerHandlerSceneManage.class, Side.SERVER);

            register(PacketUpdatePlayerData.class, ServerHandlerUpdatePlayerData.class, Side.SERVER);

            /* Scene syncing */
            register(PacketSceneGoto.class, ServerHandlerSceneGoto.class, Side.SERVER);
            register(PacketScenePlay.class, ServerHandlerScenePlay.class, Side.SERVER);
            register(PacketScenePlayback.class, ServerHandlerScenePlayback.class, Side.SERVER);
            register(PacketSceneRecord.class, ServerHandlerSceneRecord.class, Side.SERVER);
            register(PacketScenePause.class, ServerHandlerScenePause.class, Side.SERVER);
            register(PacketSceneTeleport.class, ServerHandlerSceneTeleport.class, Side.SERVER);

            /* Multiplayer */
            register(PacketReloadModels.class, ServerHandlerReloadModels.class, Side.SERVER);

            /* Guns */
            register(PacketGunInfo.class, ServerHandlerGunInfo.class, Side.SERVER);
            
            register(PacketGunInfoStack.class, ServerHandlerGunInfoStack.class, Side.SERVER);
            register(PacketGunInfoStack.class, ClientHandlerGunInfoStack.class, Side.CLIENT);
            register(PacketGunInfo.class, ClientHandlerGunInfo.class, Side.CLIENT);
            register(PacketGunShot.class, ClientHandlerGunShot.class, Side.CLIENT);
            register(PacketGunProjectile.class, ClientHandlerGunProjectile.class, Side.CLIENT);
            register(PacketGunInteract.class, ServerHandlerGunInteract.class, Side.SERVER);
            register(PacketGunReloading.class, ServerHandlerGunReloading.class, Side.SERVER);
            register(PacketGunInteract.class, ClientHandlerGunInteract.class, Side.CLIENT);
            register(PacketGunProjectileVanish.class, ClientHandlerGunProjectileVanish.class, Side.CLIENT);
            register(PacketGunStuck.class, ClientHandlerGunStuck.class, Side.CLIENT);

            /* Structure morph */
            register(PacketStructure.class, ClientHandlerStructure.class, Side.CLIENT);
            register(PacketStructureRequest.class, ServerHandlerStructureRequest.class, Side.SERVER);
            register(PacketStructureList.class, ClientHandlerStructureList.class, Side.CLIENT);
            register(PacketStructureListRequest.class, ServerHandlerStructureListRequest.class, Side.SERVER);

            /* Playback button */
            register(PacketPlaybackButton.class, ServerHandlerPlaybackButton.class, Side.SERVER);
            register(PacketPlaybackButton.class, ClientHandlerPlaybackButton.class, Side.CLIENT);

            /* Audio */
            register(PacketAudio.class, ClientHandlerAudio.class, Side.CLIENT);

            CameraHandler.registerMessages();
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        DISPATCHER.sendToTracked(entity, message);
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        DISPATCHER.register();
    }
}