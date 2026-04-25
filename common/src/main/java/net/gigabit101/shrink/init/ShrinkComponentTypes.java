package net.gigabit101.shrink.init;

import com.mojang.serialization.Codec;
import net.creeperhost.polylib.registry.PolyRegistry;
import net.gigabit101.shrink.ShrinkCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public class ShrinkComponentTypes
{
    public static final PolyRegistry<DataComponentType<?>> COMPONENTS = PolyRegistry.create(Registries.DATA_COMPONENT_TYPE, ShrinkCommon.MOD_ID);

    public static final Supplier<DataComponentType<Double>> SHRINKING_DEVICE = COMPONENTS.register("scale", () -> DataComponentType.<Double>builder().
            persistent(Codec.DOUBLE.orElse(0D)).networkSynchronized(ByteBufCodecs.DOUBLE).build());

    @Deprecated(forRemoval = true)
    public static final Supplier<DataComponentType<String>> ENTITY = COMPONENTS.register("entity", () -> DataComponentType.<String>builder().
            persistent(Codec.STRING.orElse("")).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final Supplier<DataComponentType<Component>> ENTITY_NAME = COMPONENTS.register("entity_name", () -> DataComponentType.<Component>builder().
            persistent(ComponentSerialization.CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).build());

    public static void init(){
        COMPONENTS.init();
    }
}
