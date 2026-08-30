package kniumm.mercenaries.client;

import kniumm.mercenaries.AbstractArmedVillager;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.item.CrossbowItem;
import org.jspecify.annotations.NonNull;

public abstract class ArmedVillagerRenderer<T extends AbstractArmedVillager, S extends IllagerRenderState> extends MobRenderer<T, S, IllagerModel<S>> {
    protected ArmedVillagerRenderer(final EntityRendererProvider.Context context, final IllagerModel<S> model, final float shadow) {
        super(context, model, shadow);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
    }

    public void extractRenderState(final @NonNull T entity, final @NonNull S state, final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks);
        state.isRiding = entity.isPassenger();
        state.mainArm = entity.getMainArm();
        state.armPose = entity.getArmPose();
        state.maxCrossbowChargeDuration = state.armPose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE ? CrossbowItem.getChargeDuration(entity.getUseItem(), entity) : 0;
        state.ticksUsingItem = entity.getTicksUsingItem(partialTicks);
        state.attackAnim = entity.getAttackAnim(partialTicks);
        state.isAggressive = entity.isAggressive();
    }
}
