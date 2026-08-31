package kniumm.mercenaries.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
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

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .addBox(
                                -4.0F, -10.0F, -4.0F,
                                8.0F, 10.0F, 8.0F,
                                new CubeDeformation(0.5F)
                        ),
                PartPose.ZERO
        );

        head.addOrReplaceChild(
                "hat",
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .addBox(
                                -4.0F, 0.0F, -3.0F,
                                8.0F, 12.0F, 6.0F,
                                new CubeDeformation(0.5F)
                        ),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .addBox(
                                -3.0F, -2.0F, -2.0F,
                                4.0F, 12.0F, 4.0F,
                                new CubeDeformation(0.5F)
                        ),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .addBox(
                                -1.0F, -2.0F, -2.0F,
                                4.0F, 12.0F, 4.0F,
                                new CubeDeformation(0.5F)
                        ),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .addBox(
                                -2.0F, 0.0F, -2.0F,
                                4.0F, 12.0F, 4.0F,
                                new CubeDeformation(0.5F)
                        ),
                PartPose.offset(-2.0F, 12.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                        .addBox(
                                -2.0F, 0.0F, -2.0F,
                                4.0F, 12.0F, 4.0F,
                                new CubeDeformation(0.5F)
                        ),
                PartPose.offset(2.0F, 12.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }
}