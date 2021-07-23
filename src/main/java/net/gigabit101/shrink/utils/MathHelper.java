package net.gigabit101.shrink.utils;

public class MathHelper
{
    public static int hsvToRgb(float p_181758_0_, float p_181758_1_, float p_181758_2_) {
        int i = (int)(p_181758_0_ * 6.0F) % 6;
        float f = p_181758_0_ * 6.0F - (float)i;
        float f1 = p_181758_2_ * (1.0F - p_181758_1_);
        float f2 = p_181758_2_ * (1.0F - f * p_181758_1_);
        float f3 = p_181758_2_ * (1.0F - (1.0F - f) * p_181758_1_);
        float f4;
        float f5;
        float f6;
        switch(i) {
            case 0:
                f4 = p_181758_2_;
                f5 = f3;
                f6 = f1;
                break;
            case 1:
                f4 = f2;
                f5 = p_181758_2_;
                f6 = f1;
                break;
            case 2:
                f4 = f1;
                f5 = p_181758_2_;
                f6 = f3;
                break;
            case 3:
                f4 = f1;
                f5 = f2;
                f6 = p_181758_2_;
                break;
            case 4:
                f4 = f3;
                f5 = f1;
                f6 = p_181758_2_;
                break;
            case 5:
                f4 = p_181758_2_;
                f5 = f1;
                f6 = f2;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
        }

        int j = clamp((int)(f4 * 255.0F), 0, 255);
        int k = clamp((int)(f5 * 255.0F), 0, 255);
        int l = clamp((int)(f6 * 255.0F), 0, 255);
        return j << 16 | k << 8 | l;
    }

    public static int clamp(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
        if (p_76125_0_ < p_76125_1_) {
            return p_76125_1_;
        } else {
            return p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_;
        }
    }
}
