package net.damagejr05.emma.util;

import net.damagejr05.emma.Emma;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent INVISIBILITY_TRIGGER = registerSoundEvent("invisibility_trigger");
    public static final SoundEvent INVISIBILITY_END = registerSoundEvent("invisibility_end");
    public static final SoundEvent SMOKE_BOMB_THROW = registerSoundEvent("smoke_bomb_throw");
    public static final SoundEvent SMOKE_BOMB_EXPLODE = registerSoundEvent("smoke_bomb_explode");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Emma.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.of(id);
        Registry.register(Registries.SOUND_EVENT, id, soundEvent);
        Emma.LOGGER.info("Registered sound: " + id);
        return soundEvent;
    }



    public static void registerSounds() {
        Emma.LOGGER.info("Registering Sounds for " + Emma.MOD_ID);
    }
}
