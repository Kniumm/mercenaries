package kniumm.mercenaries.client;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class ArmedVillagerArmorModel<S extends IllagerRenderState> extends HumanoidModel<S> {
    public ArmedVillagerArmorModel(ModelPart root) {
        super(root);
    }

    @Contract(" -> new")
    public static @NonNull ArmorModelSet<LayerDefinition> createArmorModelSet() {
        CubeDeformation inner = new CubeDeformation(0.5F);
        CubeDeformation outer = new CubeDeformation(1.0F);

        return createArmorLayerSet(inner, outer);
    }

    @Contract("_, _ -> new")
    public static @NonNull ArmorModelSet<LayerDefinition> createArmorLayerSet(final CubeDeformation innerDeformation, final CubeDeformation outerDeformation) {
        return createArmorMeshSet(ArmedVillagerArmorModel::createBaseArmorMesh, ADULT_ARMOR_PARTS_PER_SLOT, innerDeformation, outerDeformation).map((mesh) -> LayerDefinition.create(mesh, 64, 32));
    }

    private static @NonNull MeshDefinition createBaseArmorMesh(CubeDeformation deformation) {
        MeshDefinition mesh = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition root = mesh.getRoot();

        return mesh;
    }

    @Override
    public void setupAnim(final S state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * ((float)Math.PI / 180F);
        this.head.xRot = state.xRot * ((float)Math.PI / 180F);
        if (state.isRiding) {
            this.rightArm.xRot = (-(float)Math.PI / 5F);
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = (-(float)Math.PI / 5F);
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = ((float)Math.PI / 10F);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (-(float)Math.PI / 10F);
            this.leftLeg.zRot = -0.07853982F;
        } else {
            float animationSpeed = state.walkAnimationSpeed;
            float animationPos = state.walkAnimationPos;
            this.rightArm.xRot = Mth.cos((double)(animationPos * 0.6662F + (float)Math.PI)) * 2.0F * animationSpeed * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 2.0F * animationSpeed * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
            this.leftLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + (float)Math.PI)) * 1.4F * animationSpeed * 0.5F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
        }
        AbstractIllager.IllagerArmPose pose = state.armPose;
        if (pose == AbstractIllager.IllagerArmPose.ATTACKING) {
            if (state.getMainHandItemState().isEmpty()) {
                AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, state);
            } else {
                AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, state.mainArm, state.attackAnim, state.ageInTicks);
            }
        } else if (pose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos((double)(state.ageInTicks * 0.6662F)) * 0.25F;
            this.leftArm.xRot = Mth.cos((double)(state.ageInTicks * 0.6662F)) * 0.25F;
            this.rightArm.zRot = 2.3561945F;
            this.leftArm.zRot = -2.3561945F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
        } else if (pose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
            this.leftArm.xRot = -0.9424779F + this.head.xRot;
            this.leftArm.yRot = this.head.yRot - 0.4F;
            this.leftArm.zRot = ((float)Math.PI / 2F);
        } else if (pose == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
        } else if (pose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, (float)state.maxCrossbowChargeDuration, state.ticksUsingItem, true);
        } else if (pose == AbstractIllager.IllagerArmPose.CELEBRATING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.rightArm.xRot = Mth.cos((double)(state.ageInTicks * 0.6662F)) * 0.05F;
            this.rightArm.zRot = 2.670354F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.leftArm.xRot = Mth.cos((double)(state.ageInTicks * 0.6662F)) * 0.05F;
            this.leftArm.zRot = -2.3561945F;
            this.leftArm.yRot = 0.0F;
        }
        boolean crossedArms = pose == AbstractIllager.IllagerArmPose.CROSSED;
        // this.arms.visible = crossedArms;
        this.leftArm.visible = !crossedArms;
        this.rightArm.visible = !crossedArms;
    }
}