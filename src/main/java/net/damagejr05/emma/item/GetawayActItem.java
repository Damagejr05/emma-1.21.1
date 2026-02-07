package net.damagejr05.emma.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GetawayActItem extends Item {
    public GetawayActItem(Settings settings) {
        super(settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 4.0F,
                        EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND).add(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -2.4F,
                        EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).build();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.emma.getaway_plan.tooltip").formatted(Formatting.YELLOW).formatted(Formatting.ITALIC));
            tooltip.add(Text.translatable("tooltip.emma.blank_spot.tooltip"));

        if (stack.isOf(ModItems.EXPLOSIVE_EXIT)) {
            tooltip.add(Text.translatable("tooltip.emma.explosive_exit.tooltip"));
            tooltip.add(Text.translatable("tooltip.emma.explosive_exit.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.emma.short_cooldown.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.isOf(ModItems.OBSCURING_HAZE)) {
            tooltip.add(Text.translatable("tooltip.emma.obscuring_haze.tooltip"));
            tooltip.add(Text.translatable("tooltip.emma.obscuring_haze.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.emma.obscuring_haze.tooltip_2"));
            tooltip.add(Text.translatable("tooltip.emma.variable_cooldown.tooltip").formatted(Formatting.DARK_BLUE));
            tooltip.add(Text.translatable("tooltip.emma.variable_cooldown.tooltip_1").formatted(Formatting.DARK_BLUE));
        }

        if (stack.isOf(ModItems.WARDENS_INSPIRATION)) {
            tooltip.add(Text.translatable("tooltip.emma.wardens_inspiration.tooltip"));
            tooltip.add(Text.translatable("tooltip.emma.wardens_inspiration.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.emma.wardens_inspiration.tooltip_2"));
            tooltip.add(Text.translatable("tooltip.emma.moderate_cooldown.tooltip").formatted(Formatting.GOLD));

        }

        if (stack.isOf(ModItems.SHADOWSTEP)) {
            tooltip.add(Text.translatable("tooltip.emma.shadowstep.tooltip"));
            tooltip.add(Text.translatable("tooltip.emma.shadowstep.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.emma.moderate_cooldown.tooltip").formatted(Formatting.GOLD));
        }

        if (stack.isOf(ModItems.GRAPPLING_BLADE)) {
            tooltip.add(Text.translatable("tooltip.emma.grappling_blade.tooltip"));
            tooltip.add(Text.translatable("tooltip.emma.grappling_blade.tooltip_1"));
            tooltip.add(Text.translatable("tooltip.emma.grappling_blade.tooltip_2"));
            tooltip.add(Text.translatable("tooltip.emma.variable_cooldown.tooltip").formatted(Formatting.DARK_BLUE));
            tooltip.add(Text.translatable("tooltip.emma.variable_cooldown.tooltip_1").formatted(Formatting.DARK_BLUE));
        }

        tooltip.add(Text.translatable("tooltip.emma.blank_spot.tooltip"));
        tooltip.add(Text.translatable("tooltip.emma.getaway_plan.tooltip_1").formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
