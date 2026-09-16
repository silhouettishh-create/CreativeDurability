package com.creativedurability;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creative Durability
 *
 * Membuat item kehilangan durability di Creative Mode, persis seperti di
 * Survival, saat fitur ini diaktifkan lewat command "/creativedurability".
 *
 * Cakupan pengurangan durability pada versi ini:
 *  - Memecahkan block dengan tool di tangan utama.
 *  - Menyerang entity dengan weapon di tangan (kiri/kanan).
 *
 * Survival dan Adventure Mode TIDAK disentuh sama sekali oleh mod ini —
 * seluruh logika di bawah hanya berjalan kalau pemain sedang Creative DAN
 * fitur ini sedang aktif. Di luar itu, perilaku vanilla berjalan normal
 * seperti biasa.
 */
public class CreativeDurabilityMod implements ModInitializer {
    public static final String MOD_ID = "creativedurability";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CreativeDurabilitySettings.load();

        registerCommand();
        registerBlockBreakHandling();
        registerAttackHandling();

        LOGGER.info("Creative Durability aktif. Status durability-di-Creative saat ini: {}",
                CreativeDurabilitySettings.isEnabled() ? "ON" : "OFF");
    }

    private void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("creativedurability")
                        // Permission level 2, sama seperti /gamerule, supaya bisa dijalankan
                        // oleh command block dan operator, bukan pemain biasa.
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("aktif", BoolArgumentType.bool())
                                .executes(this::runCommand))));
    }

    private int runCommand(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
        boolean value = BoolArgumentType.getBool(context, "aktif");
        CreativeDurabilitySettings.setEnabled(value);

        CommandSourceStack source = context.getSource();
        String pesan = value
                ? "Durability di Creative Mode sekarang AKTIF."
                : "Durability di Creative Mode sekarang NONAKTIF.";

        // sendSuccess = pesan normal (putih/hijau), BUKAN sendFailure yang tampil merah.
        source.sendSuccess(() -> Component.literal(pesan), true);
        return 1;
    }

    private void registerBlockBreakHandling() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide()) {
                return;
            }
            applyCreativeDamage(player, InteractionHand.MAIN_HAND);
        });
    }

    private void registerAttackHandling() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide()) {
                applyCreativeDamage(player, hand);
            }
            return InteractionResult.PASS;
        });
    }

    /**
     * Mengurangi durability item di tangan pemain sebesar 1, dan
     * menghancurkan item itu kalau durability mencapai batas maksimum.
     * Hanya berjalan kalau pemain Creative DAN fitur sedang aktif.
     */
    private void applyCreativeDamage(Player player, InteractionHand hand) {
        if (!CreativeDurabilitySettings.isEnabled()) {
            return;
        }
        if (!player.isCreative()) {
            return;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !stack.isDamageableItem()) {
            return;
        }

        int newDamage = stack.getDamageValue() + 1;
        if (newDamage >= stack.getMaxDamage()) {
            player.setItemInHand(hand, ItemStack.EMPTY);
            player.level().playSound(null, player.blockPosition(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            stack.setDamageValue(newDamage);
        }
    }
}
