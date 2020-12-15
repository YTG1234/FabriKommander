package me.gserv.fabrikommander.mixin;

import me.gserv.fabrikommander.commands.BackCommand;
import me.gserv.fabrikommander.data.PlayerDataManager;
import me.gserv.fabrikommander.data.spec.Pos;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntity.class, ServerPlayerEntity.class})
public abstract class PlayerEntityMixin extends LivingEntity {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("RETURN"))
    private void setBackPos(DamageSource source, CallbackInfo ci) {
        if (!world.isClient) {
            // We need a config, as some people wouldn't want this
            PlayerDataManager.INSTANCE.setBackPos(uuid, new Pos(
                    getX(),
                    getY(),
                    getZ(),

                    yaw,
                    pitch,

                    world.getRegistryKey().getValue()
            ));
            BackCommand.Utils.sendDeathMessage((PlayerEntity) (Object) this);
        }
    }
}
