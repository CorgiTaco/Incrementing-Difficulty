package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.MobifierRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface Condition {
    Codec<Condition> CODEC = MobifierRegistry.CONDITION.byNameCodec().dispatchStable(Condition::codec, Function.identity());

    boolean passes(ConditionContext conditionContext);

    Codec<? extends Condition> codec();

    static void register() {
        register("biome", BiomeCondition.CODEC);
        register("biome_tag", BiomeTagCondition.CODEC);
        register("difficulty", DifficultyCondition.CODEC);
        register("has_equipped", HasEquippedCondition.CODEC);
        register("attribute", AttributeCondition.CODEC);
        register("dimension", InDimensionCondition.CODEC);
        register("is_dead_or_dying", IsDeadOrDyingCondition.CODEC);
        register("last_injurer_has", LastInjurerHasCondition.CODEC);
        register("last_injurer_by_type_has", LastInjurerByTypeHasCondition.CODEC);
        register("y_range", YRangeCondition.CODEC);
        register("inside_structure", InsideStructureTagCondition.CODEC);
        register("player_inventory_has", PlayerInventoryCondition.CODEC);
        register("blockstates_are", BlockStatesAreCondition.CODEC);
        register("blocks_are", BlocksAreCondition.CODEC);
        register("precipitation_at", PrecipitationAtCondition.CODEC);
        register("see_sky_at", SeeSkyAtCondition.CODEC);
        register("chance", ChanceCondition.CODEC);
        register("lunar_phase", LunarPhaseCondition.CODEC);
        register("time_of_day", TimeOfDayCondition.CODEC);
        register("every_amount_of_days", EveryAmountOfDaysCondition.CODEC);
        register("mobifiers_passed", MobifiersPassed.CODEC);
        register("is_baby", IsBabyCondition.CODEC);
        register("is_swimming", IsSwimmingCondition.CODEC);
        register("flipped", FlipCondition.CODEC);
        register("has_effect", HasEffectCondition.CODEC);
    }

    static void register(String id, Codec<? extends Condition> codec) {
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, id), codec);
    }
}
