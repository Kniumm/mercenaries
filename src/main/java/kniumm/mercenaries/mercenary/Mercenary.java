package kniumm.mercenaries.mercenary;

import kniumm.mercenaries.AbstractArmedVillager;
import kniumm.mercenaries.DefendVillageTargetGoal;
import kniumm.mercenaries.RangedCrossbowAttackGoal;
import kniumm.mercenaries.allegiance.Allegiance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class Mercenary extends AbstractArmedVillager implements CrossbowAttackMob, InventoryCarrier, NeutralMob {
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW;
    private final SimpleContainer inventory = new SimpleContainer(5);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private long persistentAngerEndTime;
    private @Nullable EntityReference<LivingEntity> persistentAngerTarget;

    public Mercenary(final EntityType<? extends AbstractVillager> type, final Level level) {
        super(type, level);

        this.getNavigation().setCanOpenDoors(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Creaking.class, 8.0F, 1.0F, 1.2));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0F, 8.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0F, true));
        this.goalSelector.addGoal(4, new MoveBackToVillageGoal(this, 0.6, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, AbstractVillager.class)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Witch.class, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Ravager.class, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Zombie.class, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, ZombieVillager.class, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vex.class, false));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.35F).add(Attributes.MAX_HEALTH, 24.0F).add(Attributes.ATTACK_DAMAGE, 5.0F).add(Attributes.FOLLOW_RANGE, 32.0F);
    }

    @Override
    public @NonNull ItemStack getProjectile(final @NonNull ItemStack heldWeapon) {
        if (heldWeapon.getItem() instanceof ProjectileWeaponItem) {
            Predicate<ItemStack> supportedProjectiles = ((ProjectileWeaponItem)heldWeapon.getItem()).getSupportedHeldProjectiles();
            ItemStack heldProjectile = ProjectileWeaponItem.getHeldProjectile(this, supportedProjectiles);
            return heldProjectile.isEmpty() ? new ItemStack(Items.ARROW) : heldProjectile;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    @Override
    protected void rewardTradeXp(@NonNull MerchantOffer offer) {

    }

    @Override
    public boolean canUseNonMeleeWeapon(final @NonNull ItemStack item) {
        return item.getItem() == Items.CROSSBOW;
    }

    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    @Override
    public void setChargingCrossbow(final boolean isCharging) {
        this.entityData.set(IS_CHARGING_CROSSBOW, isCharging);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public TagKey<Item> getPreferredWeaponType() {
        return ItemTags.PILLAGER_PREFERRED_WEAPONS;
    }

    @Override
    protected void addAdditionalSaveData(final @NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.writeInventoryToTag(output);
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(Items.CROSSBOW)) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
        } else {
            return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
        }
    }

    @Override
    protected void readAdditionalSaveData(final @NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readInventoryFromTag(input);
        this.setCanPickUpLoot(true);
    }

    @Override
    public float getWalkTargetValue(final @NonNull BlockPos pos, final @NonNull LevelReader level) {
        return 0.0F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(final @NonNull ServerLevelAccessor level, final @NonNull DifficultyInstance difficulty, final @NonNull EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
        RandomSource random = level.getRandom();

        this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon());
        this.setItemSlot(EquipmentSlot.OFFHAND, this.createSpawnOffhand());
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(level, random, difficulty);
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NonNull ServerLevel level, @NonNull AgeableMob partner) {
        return null;
    }

    @Override
    protected void populateDefaultEquipmentSlots(final @NonNull RandomSource random, final @NonNull DifficultyInstance difficulty) {
        this.maybeWearArmor(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET), random);
        this.maybeWearArmor(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE), random);
        this.maybeWearArmor(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS), random);
        this.maybeWearArmor(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS), random);

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.maybeWearArmor(EquipmentSlot.HEAD, Allegiance.getAllegianceBannerInstance(), random);
        }
    }

    private void maybeWearArmor(final EquipmentSlot slot, final ItemStack itemStack, final @NonNull RandomSource random) {
        if (random.nextFloat() < 0.1F) {
            this.setItemSlot(slot, itemStack);
        }
    }

    private @NonNull ItemStack createSpawnWeapon() {
        return this.random.nextInt(3) == 0 ? new ItemStack(Items.CROSSBOW) : new ItemStack(this.random.nextInt(2) == 0 ? Items.IRON_AXE : Items.IRON_SWORD);
    }

    private @NonNull ItemStack createSpawnOffhand() {
        ItemStack weapon = this.getMainHandItem();

        if (weapon.is(Items.CROSSBOW)) {
            return ItemStack.EMPTY;
        }

        ItemStack head = this.getItemBySlot(EquipmentSlot.HEAD);

        return (double) this.random.nextFloat() < (double)0.5F ? ItemStack.EMPTY : head.is(Items.BANNER.red()) ? new ItemStack(Items.SHIELD) : Allegiance.getAllegianceShieldInstance();
    }

    @Override
    protected void enchantSpawnedWeapon(final @NonNull ServerLevelAccessor level, final @NonNull RandomSource random, final @NonNull DifficultyInstance difficulty) {
        super.enchantSpawnedWeapon(level, random, difficulty);
        if (random.nextInt(300) == 0) {
            ItemStack weapon = this.getMainHandItem();
            if (weapon.is(Items.CROSSBOW)) {
                EnchantmentHelper.enchantItemFromProvider(weapon, level.registryAccess(), VanillaEnchantmentProviders.PILLAGER_SPAWN_CROSSBOW, difficulty, random);
            }
        }

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(final @NonNull DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    public void performRangedAttack(final @NonNull LivingEntity target, final float power) {
        this.performCrossbowAttack(this, 1.6F);
    }

    @Override
    public @NonNull SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    protected void pickUpItem(final @NonNull ServerLevel level, final @NonNull ItemEntity entity) {
        ItemStack itemStack = entity.getItem();
        if (itemStack.getItem() instanceof BannerItem) {
            super.pickUpItem(level, entity);
        } else if (this.wantsItem(itemStack)) {
            this.onItemPickup(entity);
            ItemStack remainder = this.inventory.addItem(itemStack);
            if (remainder.isEmpty()) {
                entity.discard();
            } else {
                itemStack.setCount(remainder.getCount());
            }
        }

    }

    private boolean wantsItem(final @NonNull ItemStack itemStack) {
        return itemStack.is(Items.BANNER.red());
    }

    @Override
    public @Nullable SlotAccess getSlot(final int slot) {
        int inventorySlot = slot - 300;
        return inventorySlot >= 0 && inventorySlot < this.inventory.getContainerSize() ? this.inventory.getSlot(inventorySlot) : super.getSlot(slot);
    }

    @Override
    protected void updateTrades(@NonNull ServerLevel level) {

    }

    static {
        IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Mercenary.class, EntityDataSerializers.BOOLEAN);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setPersistentAngerEndTime(final long endTime) {
        this.persistentAngerEndTime = endTime;
    }

    @Override
    public long getPersistentAngerEndTime() {
        return this.persistentAngerEndTime;
    }

    @Override
    public void setPersistentAngerTarget(final @Nullable EntityReference<LivingEntity> persistentAngerTarget) {
        this.persistentAngerTarget = persistentAngerTarget;
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }
}
