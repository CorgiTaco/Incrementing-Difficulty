package corgitaco.mobifier.common;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.condition.*;
import corgitaco.mobifier.common.util.CodecUtil;
import corgitaco.mobifier.common.util.DoubleComparator;
import corgitaco.mobifier.common.util.DoubleModifier;
import corgitaco.mobifier.common.util.ItemStackCheck;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MobifierConfig {

    public static final Codec<MobifierConfig> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.unboundedMap(CodecUtil.ENTITY_TYPE_CODEC, MobMobifier.CODEC.listOf()).fieldOf("mobifier").forGetter(mobifierConfig -> mobifierConfig.mobMobifierMap)
        ).apply(builder, MobifierConfig::new);
    });

    public static MobifierConfig INSTANCE = null;

    public static final MobifierConfig DEFAULT = new MobifierConfig(Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.put(EntityType.HUSK, Util.make(new ArrayList<>(), list -> {
            list.add(new MobMobifier(new DoubleModifier("*2"), Util.make(new Object2ObjectOpenHashMap<>(), map1 -> {
                for (Attribute attribute : Registry.ATTRIBUTE) {
                    map1.put(attribute, new DoubleModifier("*5"));
                }

            }), false, new ArrayList<>(), Util.make(new ArrayList<>(), (list1) -> {
                list1.add(new BiomeCategoryCondition(ImmutableList.of(Biome.Category.DESERT, Biome.Category.JUNGLE)));
                list1.add(new BiomeCondition(ImmutableList.of(Biomes.JUNGLE, Biomes.JUNGLE_EDGE)));
                list1.add(new AttributeCondition(Util.make(new Object2ObjectOpenHashMap<>(), map1 -> {
                    map1.put(Attributes.MAX_HEALTH, new DoubleComparator(">=5.0"));
                })));
                list1.add(new WearingCondition(Util.make(new ArrayList<>(), list2 -> {
                    list2.add(new ItemStackCheck(Items.NETHERITE_BOOTS, Optional.of(new DoubleComparator(">100")), Optional.empty(), Optional.of(Util.make(new Object2ObjectOpenHashMap<>(), map2 -> {
                        map2.put(Enchantments.FALL_PROTECTION, new DoubleComparator(">=3"));
                    }))));
                })));
                list1.add(new InDimensionCondition(ImmutableList.of(World.OVERWORLD)));

                list1.add(new HasInHandCondition(Util.make(new Object2ObjectOpenHashMap<>(), map1 -> {
                    map1.put(Hand.MAIN_HAND, new ItemStackCheck(Items.WOODEN_SWORD, Optional.empty(), Optional.empty(), Optional.empty()));
                })));
            })));
        }));
    }));
    private final Map<EntityType<?>, List<MobMobifier>> mobMobifierMap;

    public MobifierConfig(Map<EntityType<?>, List<MobMobifier>> mobMobifierMap) {
        this.mobMobifierMap = mobMobifierMap;
    }

    public Map<EntityType<?>, List<MobMobifier>> getMobMobifierMap() {
        return mobMobifierMap;
    }

    public static MobifierConfig getConfig() {
        return getConfig(false);
    }

    public static MobifierConfig getConfig(boolean serialize) {
        if (INSTANCE == null || serialize) {
            INSTANCE = readConfig();
        }

        return INSTANCE;
    }


    private static MobifierConfig readConfig() {
        final Path path = FMLPaths.CONFIGDIR.get().resolve(Mobifier.MOD_ID + ".json");

        if (!path.toFile().exists()) {
            JsonElement jsonElement = CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT).result().get();

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(jsonElement).getBytes());
            } catch (IOException e) {
                Mobifier.LOGGER.error(e.toString());
            }
        }

        try {
            return CODEC.decode(JsonOps.INSTANCE, new JsonParser().parse(new FileReader(path.toFile()))).result().orElseThrow(RuntimeException::new).getFirst();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return DEFAULT;
    }
}