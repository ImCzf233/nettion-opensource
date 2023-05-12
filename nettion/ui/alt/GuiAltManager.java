package nettion.ui.alt;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import nettion.ui.alt.altimpl.MicrosoftAlt;
import nettion.ui.alt.altimpl.MojangAlt;
import nettion.ui.alt.altimpl.OriginalAlt;
import nettion.ui.alt.microsoft.GuiMicrosoftLogin;
import nettion.ui.alt.microsoft.MicrosoftLogin;
import nettion.utils.ClientUtils;

import java.io.IOException;

public final class GuiAltManager extends GuiScreen {
    private final GuiScreen parentScreen;

    private GuiButton buttonLogin;
    private GuiButton buttonRemove;
    private volatile String status = EnumChatFormatting.YELLOW + "Pending...";
    private volatile MicrosoftLogin microsoftLogin;
    private volatile Thread runningThread;

    private static Alt selectAlt;

    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            if (microsoftLogin != null) {
                status = microsoftLogin.getStatus();
            }
        } catch (NullPointerException ignored) {}

        drawDefaultBackground();

        ClientUtils.drawBackground();
        mc.fontRenderer.drawCenteredString(EnumChatFormatting.YELLOW + "Current user name: " + mc.session.getUsername(),width / 2.0f,height / 2.0f - 10,-1);
        mc.fontRenderer.drawCenteredString(status,width / 2.0f,height / 2.0f,-1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (runningThread != null) {
                runningThread.interrupt();
            }

            mc.displayGuiScreen(parentScreen);
        } else if (button.id == 2) {
            if (selectAlt != null) {
                final Thread thread = new Thread(() -> {
                    status = EnumChatFormatting.YELLOW + "Logging in...";

                    switch (selectAlt.getAccountType()) {
                        case OFFLINE:
                            Minecraft.getMinecraft().session = new Session(selectAlt.getUserName(), "", "", "mojang");
                            status = EnumChatFormatting.GREEN + "Login successful! " + mc.session.getUsername();
                            break;
                        case MOJANG: {
                            try {
                                final MojangAlt mojangAlt = (MojangAlt) selectAlt;
                                final AltManager.LoginStatus loginStatus = AltManager.loginAlt(mojangAlt.getAccount(), mojangAlt.getPassword());

                                switch (loginStatus) {
                                    case FAILED:
                                        status = EnumChatFormatting.RED + "Login failed!";
                                        break;
                                    case SUCCESS:
                                        status = EnumChatFormatting.GREEN + "Login successful! " + mc.session.getUsername();
                                        break;
                                }
                            } catch (AuthenticationException e) {
                                if (e.getMessage().equals("Migrated")) {
                                    status = EnumChatFormatting.RED + "This user migrated to Microsoft!";
                                } else {
                                    e.printStackTrace();
                                    status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                                }
                            }
                            break;
                        }
                        case MICROSOFT: {
                            try {
                                microsoftLogin = new MicrosoftLogin(((MicrosoftAlt) selectAlt).getRefreshToken());

                                while (Minecraft.getMinecraft().running) {
                                    if (microsoftLogin.getLogged()) {
                                        mc.session = new Session(microsoftLogin.getUserName(), microsoftLogin.getUuid(), microsoftLogin.getAccessToken(), "mojang");
                                        status = EnumChatFormatting.GREEN + "Login successful! " + mc.session.getUsername();
                                        break;
                                    }
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                            }

                            microsoftLogin = null;

                            break;
                        }
                        case ORIGINAL: {
                            final OriginalAlt originalAlt = (OriginalAlt) selectAlt;
                            mc.session = new Session(originalAlt.getUserName(), originalAlt.getUUID(), originalAlt.getAccessToken(), originalAlt.getType());
                            status = EnumChatFormatting.GREEN + "Login successful! " + mc.session.getUsername();
                            break;
                        }
                    }
                },"AltManager Login Thread");

                thread.setDaemon(true);
                thread.start();

                setRunningThread(thread);
            }
        } else if (button.id == 3) {
            if (selectAlt != null) {
                AltManager.Instance.getAltList().remove(selectAlt);
                selectAlt = null;
            }
        } else if (button.id == 4) {
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account,String password) {
                    final Thread thread = new Thread() {
                        @Override
                        public void run() {
                            final AltManager.LoginStatus loginStatus;
                            try {
                                status = EnumChatFormatting.YELLOW + "Logging in...";
                                loginStatus = AltManager.loginAlt(account, password);

                                switch (loginStatus) {
                                    case FAILED:
                                        status = EnumChatFormatting.RED + "Login failed!";
                                        break;
                                    case SUCCESS:
                                        status = EnumChatFormatting.GREEN + "Login successful! " + mc.session.getUsername();
                                        break;
                                }
                            } catch (AuthenticationException e) {
                                e.printStackTrace();
                                status = EnumChatFormatting.RED + "Login failed! " + e.getClass().getName() + ": " + e.getMessage();
                            }

                            interrupt();
                        }
                    };

                    thread.setDaemon(true);
                    thread.start();

                    setRunningThread(thread);
                }
            });
        } else if (button.id == 5) {
            mc.displayGuiScreen(new GuiMicrosoftLogin(this));
        }
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(4,this.width / 2 - 120, this.height - 48, 70, 20,"Directly Login"));
        buttonList.add(new GuiButton(5,this.width / 2 - 40, this.height - 48, 70, 20,"Microsoft"));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 40, this.height - 48, 70, 20, "Back"));

        buttonList.add(buttonLogin = new GuiButton(2,-1145141919, -1145141919, 70, 20,"Login"));
        buttonList.add(buttonRemove = new GuiButton(3,-1145141919, -1145141919, 70, 20,"Delete"));
        super.initGui();
    }

    public void setRunningThread(Thread runningThread) {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
        }

        this.runningThread = runningThread;
    }
}
