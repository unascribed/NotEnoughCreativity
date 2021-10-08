package com.unascribed.notenoughcreativity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.notenoughcreativity.Ability;
import com.unascribed.notenoughcreativity.AbilityCheck;
import com.unascribed.notenoughcreativity.NEClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

	public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(at=@At("HEAD"), method="tickMovement()V", cancellable=true)
	public void tickMovement(CallbackInfo ci) {
		if (AbilityCheck.enabled(this, Ability.FREE_FLIGHT)) {
			ci.cancel();
			if (!this.getAbilities().flying) {
				this.getAbilities().flying = true;
				this.sendAbilitiesUpdate();
			}
			MinecraftClient mc = MinecraftClient.getInstance();
			Input input = ((ClientPlayerEntity)(Object)this).input;
			input.tick(false);
			Vec3d vec = new Vec3d(0, 0, 0);
			float pitch = this.getPitch();
			if (Math.abs(pitch) < 30) {
				pitch = 0;
			}
			float yaw = this.getYaw();
			Vec3d fwd = getRotationVector(pitch, yaw);
			Vec3d up = getRotationVector(pitch-90, yaw);
			Vec3d left = up.crossProduct(fwd);
			vec = vec.add(fwd.multiply(input.movementForward));
			vec = vec.add(left.multiply(input.movementSideways));
			float movementCamspaceUp = 0;
			boolean sneakIsDown = mc.options.keySneak.equals(NEClient.INSTANCE.keyDown);
			boolean jumpIsUp = mc.options.keyJump.equals(NEClient.INSTANCE.keyUp);
			if (!sneakIsDown && input.sneaking) movementCamspaceUp--;
			if (!jumpIsUp && input.jumping) movementCamspaceUp++;
			vec = vec.add(up.multiply(movementCamspaceUp));
			float movementWorldspaceUp = 0;
			if (NEClient.INSTANCE.keyDown.isPressed() || (sneakIsDown && input.sneaking)) movementWorldspaceUp--;
			if (NEClient.INSTANCE.keyUp.isPressed() || (jumpIsUp && input.jumping)) movementWorldspaceUp++;
			vec = vec.add(new Vec3d(0, 1, 0).multiply(movementWorldspaceUp));
			double speed = 0.5;
			if (mc.options.keySprint.isPressed()) {
				speed *= 3;
			}
			if (AbilityCheck.enabled(this, Ability.SUPER_SPEED)) {
				speed *= 4;
			}
			vec = vec.normalize().multiply(speed);
			setVelocity(vec);
			super.tickMovement();
		}
	}
	
}
