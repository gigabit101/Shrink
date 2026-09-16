package net.gigabit101.shrink.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.gigabit101.shrink.ShrinkCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.TagValueInput;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ShrinkBottleRenderer implements SpecialModelRenderer<ShrinkBottleRenderer.Preview>
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ShrinkCommon.MOD_ID, "bottled_mob");
    private final CameraRenderState camera = new CameraRenderState();
    // Weak references avoid retaining a disconnected world through its preview entities.
    private final Map<TypedEntityData<EntityType<?>>, WeakReference<LivingEntity>> entities = new LinkedHashMap<>(16, 0.75F, true);
    private boolean extracting;

    @Override
    public Preview extractArgument(ItemStack stack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        TypedEntityData<EntityType<?>> data = stack.get(DataComponents.ENTITY_DATA);
        if (level == null || data == null || extracting || minecraft.getEntityRenderDispatcher().camera == null) return null;

        // A captured mob may itself be holding a bottle. Do not recursively extract previews.
        extracting = true;
        try
        {
            WeakReference<LivingEntity> cached = entities.get(data);
            LivingEntity entity = cached == null ? null : cached.get();
            if (entity == null || entity.level() != level)
            {
                if (!(data.type().create(level, EntitySpawnReason.LOAD) instanceof LivingEntity living)) return null;
                // Client-created entities need an explicit ID, just like spawner display mobs.
                living.setId(-1);
                ProblemReporter.Collector problems = new ProblemReporter.Collector();
                living.load(TagValueInput.create(problems, level.registryAccess(), data.copyTagWithoutId()));
                if (!problems.isEmpty())
                {
                    ShrinkCommon.LOG.warn("Unable to load bottled mob preview {}: {}", data.type(), problems.getReport());
                    return null;
                }
                // This detached entity is for rendering only; it never joins or ticks in the world.
                living.snapTo(0, 0, 0, 0, 0);
                living.yBodyRot = living.yBodyRotO = 0;
                living.yHeadRot = living.yHeadRotO = 0;
                living.hurtTime = living.deathTime = 0;
                entity = living;
                entities.put(data, new WeakReference<>(entity));
                if (entities.size() > 64) entities.remove(entities.keySet().iterator().next());
            }

            float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            EntityRenderState state = minecraft.getEntityRenderDispatcher().extractEntity(entity, partialTick);
            state.nameTag = null;
            state.scoreText = null;
            state.leashStates = null;
            state.passengerOffset = null;
            state.shadowPieces.clear();
            state.displayFireAnimation = false;
            state.isInvisible = false;
            state.outlineColor = 0;
            if (state instanceof LivingEntityRenderState livingState) livingState.isInvisibleToPlayer = false;

            // Fit tall and wide mobs, compensating for Shrink without enlarging baby models.
            float size = Math.max(entity.getBbHeight(), entity.getBbWidth() * 1.6F);
            float ageScale = entity.getAgeScale();
            if (ageScale > 0 && ageScale < 1) size /= ageScale;
            float scale = 0.32F / Math.max(size, 0.01F);
            float spin = ((level.getGameTime() % 80) + partialTick) * 4.5F;
            return new Preview(state, scale, spin);
        }
        catch (RuntimeException exception)
        {
            ShrinkCommon.LOG.warn("Unable to render bottled mob {}", data.type(), exception);
            return null;
        }
        finally
        {
            extracting = false;
        }
    }

    @Override
    public void submit(Preview preview, PoseStack poseStack, SubmitNodeCollector collector,
                       int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (preview == null) return;
        preview.entity.lightCoords = lightCoords;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.13F, 0.5F);
        poseStack.rotateDegrees(Axis.YP, preview.spin);
        poseStack.scale(preview.scale, preview.scale, preview.scale);
        Minecraft.getInstance().getEntityRenderDispatcher().submit(preview.entity, camera, 0, 0, 0, poseStack, collector);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output)
    {
        output.accept(new Vector3f(0.25F, 0.13F, 0.25F));
        output.accept(new Vector3f(0.75F, 0.5F, 0.75F));
    }

    public record Preview(EntityRenderState entity, float scale, float spin) {}

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Preview>
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<Preview> bake(SpecialModelRenderer.BakingContext context)
        {
            return new ShrinkBottleRenderer();
        }

        @Override
        public MapCodec<Unbaked> type()
        {
            return MAP_CODEC;
        }
    }
}
