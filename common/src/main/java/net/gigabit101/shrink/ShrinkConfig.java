package net.gigabit101.shrink;

import net.creeperhost.polylib.blue.endless.jankson.Comment;
import net.creeperhost.polylib.config.ConfigData;

public class ShrinkConfig extends ConfigData
{
    @Comment("Shrinking Device capacity")
    public int shrinkingDeviceCapacity = 100000;

    @Comment("Shrinking Device cost")
    public int shrinkingDeviceCost = 100;

    @Comment("Shrink Max Size")
    public double maxSize = 10.0D;

    @Comment("Shrink Min Size")
    public double minSize = 0.20D;
}
