package io.github.sakurawald.module.mixin.color.sign;

import io.github.sakurawald.core.auxiliary.minecraft.TextHelper;
import io.github.sakurawald.core.structure.SpatialBlock;
import io.github.sakurawald.module.initializer.color.sign.SignInitializer;
import io.github.sakurawald.module.initializer.color.sign.structure.SignCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;
import java.util.List;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin extends BlockEntity {

    public SignBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @ModifyVariable(method = "setText", at = @At("HEAD"), argsOnly = true)
    @NotNull
    SignText method(@NotNull SignText signText) {
        Text[] messages = signText.getMessages(false);
        Text[] newMessages = new Text[messages.length];
        for (int i = 0; i < messages.length; i++) {
            String string = messages[i].getString();
            newMessages[i] = TextHelper.getTextByValue(null, string);
        }

        /* write cache */
        List<String> lines = Arrays.stream(messages)
            .map(Text::getString)
            .toList();
        SignInitializer.writeSignCache(new SpatialBlock(getWorld(), getPos()), new SignCache(lines));

        /* return the modified text */
        return new SignText(newMessages, newMessages, signText.getColor(), signText.isGlowing());
    }
}
