package com.extralent.client.sounds;

import com.extralent.common.misc.ModMisc;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundHandler {
        public static final SoundEvent FUSE;

        static {
            FUSE = addSoundsToRegistry("fuse");
        }

        private static SoundEvent addSoundsToRegistry(String soundId) {
            ResourceLocation soundLocation = new ResourceLocation(ModMisc.MODID, soundId);
            SoundEvent soundEvent = new SoundEvent(soundLocation);
            soundEvent.setRegistryName(soundLocation);
            return soundEvent;
        }
}

