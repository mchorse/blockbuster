package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class BedrockComponentAppearanceBillboard extends BedrockComponentBase implements IComponentParticleRender
{
    /* Options */
    public MolangExpression sizeW = MolangParser.ZERO;
    public MolangExpression sizeH = MolangParser.ZERO;
    public CameraFacing facing = CameraFacing.LOOKAT_XYZ;
    public boolean customDirection = false;
    public float directionSpeedThreshhold = 0.01F;
    public MolangExpression directionX = MolangParser.ZERO;
    public MolangExpression directionY = MolangParser.ZERO;
    public MolangExpression directionZ = MolangParser.ZERO;
    public int textureWidth = 128;
    public int textureHeight = 128;
    public MolangExpression uvX = MolangParser.ZERO;
    public MolangExpression uvY = MolangParser.ZERO;
    public MolangExpression uvW = MolangParser.ZERO;
    public MolangExpression uvH = MolangParser.ZERO;

    public boolean flipbook = false;
    public float stepX;
    public float stepY;
    public float fps;
    public MolangExpression maxFrame = MolangParser.ZERO;
    public boolean stretchFPS = false;
    public boolean loop = false;

    /* Runtime properties */
    protected float w;
    protected float h;

    protected float u1;
    protected float v1;
    protected float u2;
    protected float v2;

    protected Matrix4f transform = new Matrix4f();
    protected Matrix4f rotation = new Matrix4f();
    protected Vector4f[] vertices = new Vector4f[] {
        new Vector4f(0, 0, 0, 1),
        new Vector4f(0, 0, 0, 1),
        new Vector4f(0, 0, 0, 1),
        new Vector4f(0, 0, 0, 1)
    };
    protected Vector3f vector = new Vector3f();
    protected Vector3f direction = new Vector3f();

    public BedrockComponentAppearanceBillboard()
    {}

    @Override
    public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
    {
        if (!elem.isJsonObject()) return super.fromJson(elem, parser);

        JsonObject element = elem.getAsJsonObject();

        if (element.has("size") && element.get("size").isJsonArray())
        {
            JsonArray size = element.getAsJsonArray("size");

            if (size.size() >= 2)
            {
                this.sizeW = parser.parseJson(size.get(0));
                this.sizeH = parser.parseJson(size.get(1));
            }
        }

        if (element.has("facing_camera_mode"))
        {
            this.facing = CameraFacing.fromString(element.get("facing_camera_mode").getAsString());
        }

        if (this.facing.isDirection && element.has("direction"))
        {
            this.parseDirection(element.get("direction").getAsJsonObject(), parser);
        }

        if (element.has("uv") && element.get("uv").isJsonObject())
        {
            this.parseUv(element.get("uv").getAsJsonObject(), parser);
        }

        return super.fromJson(element, parser);
    }

    protected void parseDirection(JsonObject object, MolangParser parser) throws MolangException
    {
        this.customDirection = object.has("mode") && object.get("mode").getAsString().equals("custom");

        if (customDirection && object.has("custom_direction"))
        {
            JsonArray directionArray = object.getAsJsonArray("custom_direction");
            this.directionX = parser.parseJson(directionArray.get(0));
            this.directionY = parser.parseJson(directionArray.get(1));
            this.directionZ = parser.parseJson(directionArray.get(2));
        }
        else if (!customDirection && object.has("min_speed_threshold"))
        {
            this.directionSpeedThreshhold = object.get("min_speed_threshold").getAsFloat();
        }
    }

    protected void parseUv(JsonObject object, MolangParser parser) throws MolangException
    {
        if (object.has("texture_width")) this.textureWidth = object.get("texture_width").getAsInt();
        if (object.has("texture_height")) this.textureHeight = object.get("texture_height").getAsInt();

        if (object.has("uv") && object.get("uv").isJsonArray())
        {
            JsonArray uv = object.getAsJsonArray("uv");

            if (uv.size() >= 2)
            {
                this.uvX = parser.parseJson(uv.get(0));
                this.uvY = parser.parseJson(uv.get(1));
            }
        }

        if (object.has("uv_size") && object.get("uv_size").isJsonArray())
        {
            JsonArray uv = object.getAsJsonArray("uv_size");

            if (uv.size() >= 2)
            {
                this.uvW = parser.parseJson(uv.get(0));
                this.uvH = parser.parseJson(uv.get(1));
            }
        }

        if (object.has("flipbook") && object.get("flipbook").isJsonObject())
        {
            this.flipbook = true;
            this.parseFlipbook(object.get("flipbook").getAsJsonObject(), parser);
        }
    }

    protected void parseFlipbook(JsonObject flipbook, MolangParser parser) throws MolangException
    {
        if (flipbook.has("base_UV") && flipbook.get("base_UV").isJsonArray())
        {
            JsonArray uv = flipbook.getAsJsonArray("base_UV");

            if (uv.size() >= 2)
            {
                this.uvX = parser.parseJson(uv.get(0));
                this.uvY = parser.parseJson(uv.get(1));
            }
        }

        if (flipbook.has("size_UV") && flipbook.get("size_UV").isJsonArray())
        {
            JsonArray uv = flipbook.getAsJsonArray("size_UV");

            if (uv.size() >= 2)
            {
                this.uvW = parser.parseJson(uv.get(0));
                this.uvH = parser.parseJson(uv.get(1));
            }
        }

        if (flipbook.has("step_UV") && flipbook.get("step_UV").isJsonArray())
        {
            JsonArray uv = flipbook.getAsJsonArray("step_UV");

            if (uv.size() >= 2)
            {
                this.stepX = uv.get(0).getAsFloat();
                this.stepY = uv.get(1).getAsFloat();
            }
        }

        if (flipbook.has("frames_per_second")) this.fps = flipbook.get("frames_per_second").getAsFloat();
        if (flipbook.has("max_frame")) this.maxFrame = parser.parseJson(flipbook.get("max_frame"));
        if (flipbook.has("stretch_to_lifetime")) this.stretchFPS = flipbook.get("stretch_to_lifetime").getAsBoolean();
        if (flipbook.has("loop")) this.loop = flipbook.get("loop").getAsBoolean();
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();
        JsonArray size = new JsonArray();
        JsonObject uv = new JsonObject();

        size.add(this.sizeW.toJson());
        size.add(this.sizeH.toJson());

        /* Adding "uv" properties */
        uv.addProperty("texture_width", this.textureWidth);
        uv.addProperty("texture_height", this.textureHeight);

        if (!this.flipbook && !MolangExpression.isZero(this.uvX) || !MolangExpression.isZero(this.uvY))
        {
            JsonArray uvs = new JsonArray();
            uvs.add(this.uvX.toJson());
            uvs.add(this.uvY.toJson());

            uv.add("uv", uvs);
        }

        if (!this.flipbook && !MolangExpression.isZero(this.uvW) || !MolangExpression.isZero(this.uvH))
        {
            JsonArray uvs = new JsonArray();
            uvs.add(this.uvW.toJson());
            uvs.add(this.uvH.toJson());

            uv.add("uv_size", uvs);
        }

        /* Adding "flipbook" properties to "uv" */
        if (this.flipbook)
        {
            JsonObject flipbook = new JsonObject();

            if (!MolangExpression.isZero(this.uvX) || !MolangExpression.isZero(this.uvY))
            {
                JsonArray base = new JsonArray();
                base.add(this.uvX.toJson());
                base.add(this.uvY.toJson());

                flipbook.add("base_UV", base);
            }

            if (!MolangExpression.isZero(this.uvW) || !MolangExpression.isZero(this.uvH))
            {
                JsonArray uvSize = new JsonArray();
                uvSize.add(this.uvW.toJson());
                uvSize.add(this.uvH.toJson());

                flipbook.add("size_UV", uvSize);
            }

            if (this.stepX != 0 || this.stepY != 0)
            {
                JsonArray step = new JsonArray();
                step.add(this.stepX);
                step.add(this.stepY);

                flipbook.add("step_UV", step);
            }

            if (this.fps != 0) flipbook.addProperty("frames_per_second", this.fps);
            if (!MolangExpression.isZero(this.maxFrame)) flipbook.add("max_frame", this.maxFrame.toJson());
            if (this.stretchFPS) flipbook.addProperty("stretch_to_lifetime", true);
            if (this.loop) flipbook.addProperty("loop", true);

            uv.add("flipbook", flipbook);
        }

        if (this.facing.isDirection)
        {
            JsonObject directionObj = new JsonObject();

            if (this.customDirection)
            {
                directionObj.addProperty("mode", "custom");

                if (this.directionX != MolangParser.ZERO || this.directionY != MolangParser.ZERO || this.directionZ != MolangParser.ZERO)
                {
                    JsonArray directionArray = new JsonArray();
                    directionArray.add(this.directionX.toJson());
                    directionArray.add(this.directionY.toJson());
                    directionArray.add(this.directionZ.toJson());

                    directionObj.add("custom_direction", directionArray);
                }

                object.add("direction", directionObj);
            }
            else if (this.directionSpeedThreshhold != 0.01f)
            {
                directionObj.addProperty("mode", "derive_from_velocity");
                directionObj.addProperty("min_speed_threshold", this.directionSpeedThreshhold);

                object.add("direction", directionObj);
            }
        }

        /* Add main properties */
        object.add("size", size);
        object.addProperty("facing_camera_mode", this.facing.id);
        object.add("uv", uv);

        return object;
    }

    @Override
    public void preRender(BedrockEmitter emitter, float partialTicks)
    {}

    @Override
    public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
    {
        this.calculateUVs(particle, partialTicks);

        /* Render the particle */
        double px = Interpolations.lerp(particle.prevPosition.x, particle.position.x, partialTicks);
        double py = Interpolations.lerp(particle.prevPosition.y, particle.position.y, partialTicks);
        double pz = Interpolations.lerp(particle.prevPosition.z, particle.position.z, partialTicks);
        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);

        Vector3d pos = this.calculatePosition(emitter, particle, px, py, pz);
        px = pos.x;
        py = pos.y;
        pz = pos.z;

        /* Calculate the geometry for billboards using cool matrix math */
        int light = emitter.getBrightnessForRender(partialTicks, px, py, pz);
        int lightX = light >> 16 & 65535;
        int lightY = light & 65535;

        this.calculateFacing(emitter, particle, px, py, pz);

        this.rotation.rotZ(angle / 180 * (float) Math.PI);
        this.transform.mul(this.rotation);
        this.transform.setTranslation(new Vector3f((float) px, (float) py, (float) pz));

        for (Vector4f vertex : this.vertices)
        {
            this.transform.transform(vertex);
        }

        float u1 = this.u1 / (float) this.textureWidth;
        float u2 = this.u2 / (float) this.textureWidth;
        float v1 = this.v1 / (float) this.textureHeight;
        float v2 = this.v2 / (float) this.textureHeight;

        builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
        builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
        builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
        builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
    }

    protected void calculateFacing(BedrockEmitter emitter, BedrockParticle particle, double px, double py, double pz)
    {
        /* Calculate yaw and pitch based on the facing mode */
        float entityYaw = emitter.cYaw;
        float entityPitch = emitter.cPitch;
        double entityX = emitter.cX;
        double entityY = emitter.cY;
        double entityZ = emitter.cZ;

        /* Flip width when frontal perspective mode */
        if (emitter.perspective == 2)
        {
            this.w = -this.w;
        }
        /* In GUI renderer */
        else if (emitter.perspective == 100 && !this.facing.isLookAt)
        {
            entityYaw = 180 - entityYaw;

            this.w = -this.w;
            this.h = -this.h;
        }

        if (this.facing.isLookAt && !this.facing.isDirection)
        {
            double dX = entityX - px;
            double dY = entityY - py;
            double dZ = entityZ - pz;

            double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

            entityYaw = 180 - (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
            entityPitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI))) + 180;
        }

        this.calculateVertices(emitter, particle);

        if (this.facing == CameraFacing.ROTATE_XYZ || this.facing == CameraFacing.LOOKAT_XYZ)
        {
            this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
            this.transform.mul(this.rotation);
            this.rotation.rotX(entityPitch / 180 * (float) Math.PI);
            this.transform.mul(this.rotation);
        }
        else if (this.facing == CameraFacing.ROTATE_Y || this.facing == CameraFacing.LOOKAT_Y) {
            this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
            this.transform.mul(this.rotation);
        }
        else if (this.facing == CameraFacing.EMITTER_YZ)
        {
            if (!GuiModelRenderer.isRendering())
            {
                this.rotation.rotZ((float) Math.toRadians(180));
                this.transform.mul(this.rotation);
                this.rotation.rotY((float) Math.toRadians(90));
                this.transform.mul(this.rotation);
            }
            else
            {
                this.rotation.rotY((float) Math.toRadians(-90));
                this.transform.mul(this.rotation);
            }
        }
        else if (this.facing == CameraFacing.EMITTER_XZ)
        {
            if (!GuiModelRenderer.isRendering())
            {
                this.rotation.rotX((float) Math.toRadians(90));
                this.transform.mul(this.rotation);
            }
            else
            {
                this.rotation.rotZ((float) Math.toRadians(180));
                this.transform.mul(this.rotation);
                this.rotation.rotX((float) Math.toRadians(-90));
                this.transform.mul(this.rotation);
            }
        }
        else if (this.facing == CameraFacing.EMITTER_XY)
        {
            if (!GuiModelRenderer.isRendering())
            {
                this.rotation.rotX((float) Math.toRadians(180));
                this.transform.mul(this.rotation);
            }
            else
            {
                this.rotation.rotY((float) Math.toRadians(180));
                this.transform.mul(this.rotation);
            }
        }
        else if (this.facing.isDirection)
        {
            if (this.customDirection)
            {
                direction.x = (float) this.directionX.get();
                direction.y = (float) this.directionY.get();
                direction.z = (float) this.directionZ.get();
            }
            else // default use particle motion as its direction
            {
                // TODO: keep previous direction when particle is not moving
                direction.set(particle.speed);
            }

            double lengthSq = direction.lengthSquared();
            if (lengthSq < 0.0001)
            {
                direction.set(1, 0, 0);
            }
            else if (Math.abs(lengthSq - 1) > 0.0001)
            {
                direction.normalize();
            }

            // extract yaw and pitch from direction
            entityYaw = getYaw();
            entityPitch = getPitch();

            if (this.facing == CameraFacing.DIRECTION_X)
            {
                this.rotation.rotY((float) (entityYaw / 180 * Math.PI));
                this.transform.mul(this.rotation);
                this.rotation.rotX((float) (entityPitch / 180 * Math.PI));
                this.transform.mul(this.rotation);
                this.rotation.rotY((float) Math.toRadians(90));
                this.transform.mul(this.rotation);
            }
            else if (facing == CameraFacing.DIRECTION_Y)
            {
                this.rotation.rotY((float) (entityYaw / 180 * Math.PI));
                this.transform.mul(this.rotation);
                this.rotation.rotX((float) (entityPitch / 180 * Math.PI));
                this.transform.mul(this.rotation);
                this.rotation.rotX((float) Math.toRadians(90));
                this.transform.mul(this.rotation);
            }
            else if (facing == CameraFacing.DIRECTION_Z)
            {
                this.rotation.rotY((float) (entityYaw / 180 * Math.PI));
                this.transform.mul(this.rotation);
                this.rotation.rotX((float) (entityPitch / 180 * Math.PI));
                this.transform.mul(this.rotation);
            }
            else if (facing == CameraFacing.LOOKAT_DIRECTION)
            {
                Vector3f cameraDir = new Vector3f(
                        (float) (entityX - particle.origin.x),
                        (float) (entityY - particle.origin.y),
                        (float) (entityZ - particle.origin.z));

                Vector3f particleDir = new Vector3f(
                        (float) (px - particle.origin.x),
                        (float) (py - particle.origin.y),
                        (float) (pz - particle.origin.z));

                cameraDir.normalize();
                particleDir.normalize();

                Vector3f particleD_ = new Vector3f(particleDir);
                particleD_.scale(cameraDir.dot(particleDir));
                direction.sub(cameraDir, particleD_);

                Vector3f up = new Vector3f(0, 1, 0);
                Vector3f right = new Vector3f();
                right.cross(particleDir, up);
                float angle = direction.angle(right);

                if (direction.dot(up) < 0)
                    angle = -angle;

                rotation.rotY((float) (entityYaw / 180 * Math.PI));
                transform.mul(rotation);
                rotation.rotX((float) (entityPitch / 180 * Math.PI));
                transform.mul(rotation);
                rotation.rotY((float) Math.PI * 0.5f);
                transform.mul(rotation);
                rotation.rotX(angle);
                transform.mul(rotation);
            }
        }
    }

    private float getYaw()
    {
        double yaw = Math.atan2(-direction.x, direction.z);
        yaw = Math.toDegrees(yaw);
        if (yaw < -180) {
            yaw += 360;
        } else if (yaw > 180) {
            yaw -= 360;
        }
        return (float) -yaw;
    }

    private float getPitch()
    {
        double pitch = Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
        return (float) -Math.toDegrees(pitch);
    }

    protected void calculateVertices(BedrockEmitter emitter, BedrockParticle particle)
    {
        this.transform.setIdentity();

        float hw = this.w * 0.5f;
        float hh = this.h * 0.5f;

        if (particle.relativeScaleBillboard)
        {
            hw *= emitter.scale[0];
            hh *= emitter.scale[1];
        }

        this.vertices[0].set(-hw, -hh, 0, 1);
        this.vertices[1].set( hw, -hh, 0, 1);
        this.vertices[2].set( hw,  hh, 0, 1);
        this.vertices[3].set(-hw,  hh, 0, 1);
    }

    protected Vector3d calculatePosition(BedrockEmitter emitter, BedrockParticle particle, double px, double py, double pz)
    {
        if (particle.relativePosition && particle.relativeRotation)
        {
            this.vector.set((float) px, (float) py, (float) pz);

            if (particle.relativeScale)
            {
                Vector3d pos = new Vector3d(px, py, pz);

                Matrix3d scale = new Matrix3d(emitter.scale[0], 0, 0,
                        0, emitter.scale[1], 0,
                        0, 0, emitter.scale[2]);

                scale.transform(pos);

                this.vector.x = (float) pos.x;
                this.vector.y = (float) pos.y;
                this.vector.z = (float) pos.z;
            }

            emitter.rotation.transform(this.vector);

            px = this.vector.x;
            py = this.vector.y;
            pz = this.vector.z;

            px += emitter.lastGlobal.x;
            py += emitter.lastGlobal.y;
            pz += emitter.lastGlobal.z;
        }
        else if (particle.relativeScale)
        {
            Vector3d pos = new Vector3d(px, py, pz);

            Matrix3d scale = new Matrix3d(emitter.scale[0], 0, 0,
                                    0, emitter.scale[1], 0,
                                    0, 0, emitter.scale[2]);

            pos.sub(emitter.lastGlobal); //transform back to local
            scale.transform(pos);
            pos.add(emitter.lastGlobal); //transform back to global

            px = pos.x;
            py = pos.y;
            pz = pos.z;
        }

        return new Vector3d(px, py, pz);
    }

    @Override
    public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
    {
        this.calculateUVs(particle, partialTicks);

        this.w = this.h = 0.5F;
        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);

        /* Calculate the geometry for billboards using cool matrix math */
        float hw = this.w * 0.5f;
        float hh = this.h * 0.5f;
        this.vertices[0].set(-hw, -hh, 0, 1);
        this.vertices[1].set( hw, -hh, 0, 1);
        this.vertices[2].set( hw,  hh, 0, 1);
        this.vertices[3].set(-hw,  hh, 0, 1);

        this.transform.setIdentity();
        this.transform.setScale(scale * 2.75F);
        this.transform.setTranslation(new Vector3f(x, y - scale / 2, 0));

        this.rotation.rotZ(angle / 180 * (float) Math.PI);
        this.transform.mul(this.rotation);

        for (Vector4f vertex : this.vertices)
        {
            this.transform.transform(vertex);
        }

        float u1 = this.u1 / (float) this.textureWidth;
        float u2 = this.u2 / (float) this.textureWidth;
        float v1 = this.v1 / (float) this.textureHeight;
        float v2 = this.v2 / (float) this.textureHeight;

        BufferBuilder builder = Tessellator.getInstance().getBuffer();

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
        builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
        builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();
        builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();

        Tessellator.getInstance().draw();
    }

    public void calculateUVs(BedrockParticle particle, float partialTicks)
    {
        /* Update particle's UVs and size */
        this.w = (float) this.sizeW.get() * 2.25F;
        this.h = (float) this.sizeH.get() * 2.25F;

        float u = (float) this.uvX.get();
        float v = (float) this.uvY.get();
        float w = (float) this.uvW.get();
        float h = (float) this.uvH.get();

        if (this.flipbook)
        {
            int index = (int) (particle.getAge(partialTicks) * this.fps);
            int max = (int) this.maxFrame.get();

            if (this.stretchFPS)
            {
                float lifetime = (particle.lifetime <= 0) ? 0 : (particle.age + partialTicks) / (particle.lifetime);

                //for particles with expiration - stretch differently since lifetime changed
                if (particle.getExpireAge() != -1)
                {
                    lifetime = (particle.lifetime <= 0) ? 0 : (particle.age + partialTicks) / (particle.getExpirationDelay());
                }

                index = (int) (lifetime * max);
            }

            if (this.loop && max != 0)
            {
                index = index % max;
            }

            if (index > max)
            {
                index = max;
            }

            u += this.stepX * index;
            v += this.stepY * index;
        }

        this.u1 = u;
        this.v1 = v;
        this.u2 = u + w;
        this.v2 = v + h;
    }

    @Override
    public void postRender(BedrockEmitter emitter, float partialTicks)
    {}
}