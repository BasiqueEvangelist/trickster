package dev.enjarai.trickster.compat.pehkui;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TricksterPehkui implements ModInitializer {
    public static final String MOD_ID = "trickster_pehkui";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
