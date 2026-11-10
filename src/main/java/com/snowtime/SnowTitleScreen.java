package com.snowtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import java.util.Random;

public class SnowTitleScreen extends Screen {

    private static final int FLAKES = 260;
    private final float[] bx = new float[FLAKES];
    private final float[] by = new float[FLAKES];
    private final float[] speed = new float[FLAKES];
    private final float[] amp = new float[FLAKES];
    private final float[] freq = new float[FLAKES];
    private final float[] phase = new float[FLAKES];
    private final int[] size = new int[FLAKES];
    private final long start = System.currentTimeMillis();

    public SnowTitleScreen() {
        super(Text.literal("SNOW TIME"));
        Random r = new Random(1337);
        for (int i = 0; i < FLAKES; i++) {
            bx[i] = r.nextFloat();
            by[i] = r.nextFloat();
            int layer = r.nextInt(3); // 0 - далеко, 2 - близко
            size[i] = 1 + layer;
            speed[i] = 0.02f + layer * 0.03f + r.nextFloat() * 0.015f; // доля экрана в секунду
            amp[i] = 6 + layer * 8 + r.nextFloat() * 6;
            freq[i] = 0.4f + r.nextFloat() * 0.8f;
            phase[i] = r.nextFloat() * 6.28f;
        }
    }

    @Override
    protected void init() {
        int w = 200, h = 20, gap = 28;
        int x = this.width / 2 - w / 2;
        int y = this.height / 2 - 6;

        addDrawableChild(new FrozenButton(x, y, w, h, Text.translatable("menu.singleplayer"),
                b -> client.setScreen(new SelectWorldScreen(this))));

        addDrawableChild(new FrozenButton(x, y + gap, w, h, Text.translatable("menu.multiplayer"),
                b -> client.setScreen(client.options.skipMultiplayerWarning
                        ? new MultiplayerScreen(this)
                        : new MultiplayerWarningScreen(this))));

        addDrawableChild(new FrozenButton(x, y + gap * 2, w, h, Text.translatable("menu.options"),
                b -> client.setScreen(new OptionsScreen(this, client.options))));

        addDrawableChild(new FrozenButton(x, y + gap * 3, w, h, Text.translatable("menu.quit"),
                b -> client.scheduleStop()));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        float t = (System.currentTimeMillis() - start) / 1000f;

        // ночное небо
        c.fillGradient(0, 0, width, height, 0xFF050B1E, 0xFF1B3A63);
        // холодное свечение у горизонта
        c.fillGradient(0, height - height / 3, width, height, 0x00000000, 0x404FA8E0);

        drawSnow(c, t, 0);   // далёкие
        drawSnow(c, t, 1);   // средние
        drawDrifts(c);
        drawSnow(c, t, 2);   // ближние поверх сугробов

        drawTitle(c, t);

        super.render(c, mouseX, mouseY, delta);

        c.drawTextWithShadow(textRenderer, Text.literal("SNOW TIME 1.0"), 4, height - 12, 0x90FFFFFF);
    }

    private void drawSnow(DrawContext c, float t, int layer) {
        for (int i = 0; i < FLAKES; i++) {
            if (size[i] - 1 != layer) continue;
            float y = ((by[i] + t * speed[i]) % 1f) * (height + 10) - 5;
            float x = bx[i] * width + (float) Math.sin(t * freq[i] + phase[i]) * amp[i];
            x = ((x % width) + width) % width;
            int s = size[i];
            int a = 90 + layer * 60;
            int col = (a << 24) | 0xFFFFFF;
            int ix = (int) x, iy = (int) y;
            c.fill(ix, iy, ix + s, iy + s, col);
            if (s == 3) { // крупные - крестиком
                c.fill(ix - 1, iy + 1, ix + 4, iy + 2, 0x60FFFFFF);
                c.fill(ix + 1, iy - 1, ix + 2, iy + 4, 0x60FFFFFF);
            }
        }
    }

    private void drawDrifts(DrawContext c) {
        for (int x = 0; x < width; x += 2) {
            double hh = 16 + Math.sin(x * 0.015) * 7 + Math.sin(x * 0.06 + 1.3) * 3;
            int top = height - (int) hh;
            c.fillGradient(x, top, x + 2, height, 0xFFF2FAFF, 0xFFB5D8F0);
        }
    }

    private void drawTitle(DrawContext c, float t) {
        String title = "SNOW TIME";
        float scale = 5f;
        int cx = width / 2;
        int cy = Math.max(20, height / 2 - 6 - 70);

        // мерцающий иней-градиент по буквам
        var m = c.getMatrices();
        m.push();
        m.translate(cx, cy, 0);
        m.scale(scale, scale, 1f);

        int tw = textRenderer.getWidth(title);
        float glow = 0.5f + 0.5f * (float) Math.sin(t * 1.5);

        // свечение
        int ga = (int) (40 + 40 * glow);
        int gcol = (ga << 24) | 0x7FD6FF;
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                if (dx != 0 || dy != 0)
                    c.drawText(textRenderer, title, -tw / 2 + dx, dy, gcol, false);

        // буквы с переливом
        int xx = -tw / 2;
        for (int i = 0; i < title.length(); i++) {
            String ch = String.valueOf(title.charAt(i));
            float k = 0.5f + 0.5f * (float) Math.sin(t * 2f + i * 0.7f);
            int r = (int) (190 + 65 * k), g = (int) (230 + 25 * k);
            int col = 0xFF000000 | (r << 16) | (g << 8) | 0xFF;
            c.drawText(textRenderer, ch, xx, 0, col, true);
            xx += textRenderer.getWidth(ch);
        }
        m.pop();
    }
}
