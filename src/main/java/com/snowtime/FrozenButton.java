package com.snowtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class FrozenButton extends ButtonWidget {

    public FrozenButton(int x, int y, int w, int h, Text text, PressAction onPress) {
        super(x, y, w, h, text, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void renderButton(DrawContext c, int mouseX, int mouseY, float delta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        boolean hov = isHovered();

        int top = hov ? 0xE0BFEFFF : 0xD08FCBEA;
        int bot = hov ? 0xE06FB8E8 : 0xD04F8FC0;

        // ледяное тело
        c.fillGradient(x, y, x + w, y + h, top, bot);
        // блик сверху и иней по краям
        c.fill(x + 1, y + 1, x + w - 1, y + 2, 0xB0FFFFFF);
        c.fill(x + 1, y + 1, x + 2, y + h - 1, 0x60FFFFFF);
        c.drawBorder(x, y, w, h, hov ? 0xFFFFFFFF : 0xFFD8F4FF);

        // трещинки
        int seed = x * 31 + y * 17;
        c.fill(x + 12 + (seed % 20), y + 3, x + 13 + (seed % 20), y + 9, 0x40FFFFFF);
        c.fill(x + w - 30 - (seed % 15), y + h - 9, x + w - 29 - (seed % 15), y + h - 3, 0x40FFFFFF);

        // сосульки снизу
        for (int px = x + 4; px < x + w - 4; px += 7) {
            int hash = (px * 7919 + seed) & 0xFF;
            int len = 3 + (hash % 8) + (hov ? 2 : 0);
            for (int k = 0; k < len; k++) {
                int half = Math.max(0, (len - k) * 2 / len);
                c.fill(px - half, y + h + k, px + half + 1, y + h + k + 1, 0xC8DFF6FF);
            }
        }

        // текст
        int color = active ? (hov ? 0xFFFFFF : 0xE8F8FF) : 0xA0A0A0;
        c.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,
                getMessage(), x + w / 2, y + (h - 8) / 2, color);
    }
}
