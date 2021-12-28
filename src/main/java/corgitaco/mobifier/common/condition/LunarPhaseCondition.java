package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class LunarPhaseCondition implements Condition {

    public static final Codec<LunarPhaseCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.INT.listOf().fieldOf("is_valid_moon_phase").forGetter(lunarPhaseCondition -> new ArrayList<>(lunarPhaseCondition.validMoonPhases))).apply(builder, LunarPhaseCondition::new);
    });

    private final IntSet validMoonPhases = new IntArraySet();

    public LunarPhaseCondition(Collection<Integer> validMoonPhases) {
        this.validMoonPhases.addAll(validMoonPhases);
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying) {
        return this.validMoonPhases.contains(world.getMoonPhase());
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
