package net.skidcode.gh.maybeaclient.hacks;

import net.minecraft.src.*;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.ImageViewerTab;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingButton;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

public class ImageViewerHack extends Hack {
    public static ImageViewerHack instance;
    public SettingButton select = new SettingButton(this, "Select");
    public SettingInteger width = new SettingInteger(this, "Width", 50, 5, 250, 5);
    public SettingInteger height = new SettingInteger(this, "Height", 50, 5, 250, 5);
    static String errorMsg = "";

    public ImageViewerHack() {
        super("ImageViewer", "Display an image of your choice", Keyboard.KEY_NONE, Category.UI);
        this.addSetting(select);
        this.addSetting(width);
        this.addSetting(height);
        instance = this;
    }

    public void onPressed(SettingButton b) {
        if (b == select) {
            mc.displayGuiScreen(new SelectImageGUI());
            return;
        }
        super.onPressed(b);
    }

    static class SelectImageGUI extends GuiScreen {
        static class CustomTextField extends GuiTextField {
            public CustomTextField(GuiScreen var1, FontRenderer var2, int var3, int var4, int var5, int var6) {
                super(var1, var2, var3, var4, var5, var6, "");
            }

            @Override
            public void textboxKeyTyped(char var1, int var2) {
                    if (var1 == 22) {
                        this.setText(this.getText() + GuiScreen.getClipboardString());
                    }
                    if (var2 == 14 && !this.getText().isEmpty()) {
                        this.setText(this.getText().substring(0, this.getText().length() - 1));
                    }
                    if (ChatAllowedCharacters.allowedCharacters.indexOf(var1) >= 0 &&
                            (this.getText().length() < this.maxStringLength || this.maxStringLength == 0)) {
                        this.setText(this.getText() + var1);
                    }
            }
        }

        GuiTextField textInput;

        @Override
        public void initGui() {
            this.textInput = new CustomTextField(this, this.fontRenderer, this.width / 2 - 200,
                    this.height / 4 - 10 + 50 + 18, 400, 20);
            this.controlList.add(new GuiButton(0, this.width / 2 - 200, this.height / 4 + 96 + 12, 195, 20, "Cancel"));
            this.controlList.add(new GuiButton(1, this.width / 2 + 5, this.height / 4 + 96 + 12, 195, 20, "Apply"));
        }

        @Override
        public void actionPerformed(GuiButton button) {
            switch (button.id) {
                case 0:
                    mc.displayGuiScreen(new ClickGUI(null));
                    break;
                case 1:
                    File file = new File(this.textInput.getText().replaceAll("[\"*?<>|]", ""));
                    try {
                        file.toPath();
                    } catch (InvalidPathException e) {
                        errorMsg = "§cInvalid path.";
                        break;
                    }
                    if (!Files.exists(file.toPath())) {
                        errorMsg = "§cFile not found! Check the path again.";
                        break;
                    }
                    try {
                        ImageViewerTab.loadTexture(file);
                    } catch (IOException ex) {
                        errorMsg = "§cUnable to load file. (wrong format?)";
                        break;
                    }
                    try {
                        Files.copy(file.toPath(), ImageViewerTab.PATH);
                    } catch (IOException ignored) {
                    }
                    mc.displayGuiScreen(new ClickGUI(null));
                    break;
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, errorMsg, this.width / 2, this.height / 4 - 60 + 60 + 36, Integer.MAX_VALUE);
            this.textInput.drawTextBox();
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.textInput.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public void keyTyped(char symbol, int key) {
            super.keyTyped(symbol, key);
            this.textInput.textboxKeyTyped(symbol, key);
            if (key == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(new ClickGUI(null));
            }
        }

        @Override
        public void updateScreen() {
            this.textInput.updateCursorCounter();
        }
    }
}
