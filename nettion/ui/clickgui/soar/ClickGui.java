package nettion.ui.clickgui.soar;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import nettion.Nettion;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.features.module.ModuleType;
import nettion.features.module.modules.render.ArrayListMod;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.features.value.Value;
import nettion.ui.fonts.CFontRenderer;
import nettion.ui.fonts.FontLoaders;
import nettion.ui.fonts.old.Fonts;
import nettion.utils.AnimationUtil;
import nettion.utils.render.ColorUtils;
import nettion.utils.render.RenderUtils;
import nettion.utils.render.RoundedUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ClickGui extends GuiScreen implements GuiYesNoCallback {
	public ModuleType currentModuleType = ModuleType.Combat;
	public Module currentModule = null;
	public float startX = 200, startY = 85;
	public int moduleStart = 0;
	public int valueStart = 0;
	boolean previousmouse = true;
	boolean mouse;
	public float moveX = 0, moveY = 0;
	private float SettingButtonAnim = 30,SettingGuiAnim = 0,AnimType = 100;
	int animheight = 0;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (isHovered(startX - 40, startY, startX + 280, startY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
			if (moveX == 0 && moveY == 0) {
				moveX = mouseX - startX;
				moveY = mouseY - startY;
			} else {
				startX = mouseX - moveX;
				startY = mouseY - moveY;
			}
			this.previousmouse = true;
		} else if (moveX != 0 || moveY != 0) {
			moveX = 0;
			moveY = 0;
		}
		float rectScale = 7;
		RoundedUtils.drawRound(startX - 40, startY, 385, 260, 4, new Color(220, 220, 220, 255));
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtils.prepareScissorBox(startX - 40, 0, startX-40+385, height);
		RoundedUtils.drawRound(startX - 40, startY, 100 - SettingGuiAnim, 260, 4, new Color(255, 255, 255, 255));
		AnimType = AnimationUtil.moveUD(AnimType,95,0.01f,0.05f);
		for (int i = 0; i < ModuleType.values().length; i++) {
			ModuleType[] iterator = ModuleType.values();
			if (iterator[i] == currentModuleType) {
				animheight = i * 30;
				RoundedUtils.drawRound(startX - 40 - AnimType / 2 + 50, startY + 50 + animheight, AnimType - SettingGuiAnim, 25, 4, ColorUtils.applyOpacity(new Color(95, 195, 230, 255), rectScale));
				FontLoaders.F20.drawString(iterator[i].name(), startX - 20 - SettingGuiAnim, startY + 60 + i * 30, new Color(255, 255, 255, 255).getRGB());
			} else {
				FontLoaders.F20.drawString(iterator[i].name(), startX - 20 - SettingGuiAnim, startY + 60 + i * 30, new Color(180, 180, 180, 255).getRGB());
			}
			try {
				if (this.isCategoryHovered(startX - 40, startY + 50 + i * 30, startX + 60, startY + 75 + i * 30, mouseX,
						mouseY) && Mouse.isButtonDown((int) 0) && currentModule == null) {
					currentModuleType = iterator[i];
					currentModule = null;
					moduleStart = 0;
					AnimType = 0;
					Mouse.destroy();
					Mouse.create();
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
		int m = Mouse.getDWheel();
		if (this.isCategoryHovered(startX+60-SettingGuiAnim, startY, startX+60+260-SettingGuiAnim, startY + 235, mouseX, mouseY)) {
			if (m < 0 && moduleStart < (ModuleManager.getModulesInType(currentModuleType).size() - 1)*35) {
				moduleStart+=20;
			}
			if (m > 0 && moduleStart > 0) {
				moduleStart-=20;
			}
		}

		if (this.isCategoryHovered(startX +60+260-SettingGuiAnim, startY, startX+60+260, startY + 235, mouseX, mouseY)&&currentModule!=null) {
			if (m < 0 && valueStart < currentModule.getValues().size()- 1) {
				valueStart++;
			}
			if (m > 0 && valueStart > 0) {
				valueStart--;
			}
		}
		if(currentModule==null){
			SettingGuiAnim = AnimationUtil.moveUD(SettingGuiAnim,0,0.02f,0.02f);
		}
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtils.prepareScissorBox(startX-40, startY+25, startX-40+385, startY+250);
		float mY = startY + 30-moduleStart;
		for (int i = 0; i < ModuleManager.getModulesInType(currentModuleType).size(); i++) {
			Module module = ModuleManager.getModulesInType(currentModuleType).get(i);
			RenderUtils.drawRect2(startX + 75 - SettingGuiAnim, mY, startX + 185 - SettingGuiAnim, mY + 2,
					new Color(255, 255, 255, 0).getRGB());
			RoundedUtils.drawRound(startX + 75 - SettingGuiAnim, mY,260,30,6,new Color(255,255,255));
			FontLoaders.F22.drawString(module.getName(), startX + 90+30 - SettingGuiAnim, mY+10,
					new Color(0, 0, 0, 255).getRGB(), false);
			if (module.isEnabled()) {
				module.AnimationX = AnimationUtil.moveUD(module.AnimationX,28f,0.05f,0.05f);
				module.AnimationY = AnimationUtil.moveUD(module.AnimationY,30f,0.05f,0.05f);
				RoundedUtils.drawGradientCornerLR(startX + 75f+14-(float) module.AnimationX/2 - SettingGuiAnim,   mY+15-(float) module.AnimationY/2, (float) module.AnimationX, (float) module.AnimationY,6, ColorUtils.applyOpacity(new Color(150, 225, 220, 255), rectScale),
						ColorUtils.applyOpacity(new Color(95, 195, 230, 255), rectScale));
			} else {
				module.AnimationX = AnimationUtil.moveUD(module.AnimationX,28f,0.05f,0.05f);
				module.AnimationY = AnimationUtil.moveUD(module.AnimationY,30f,0.05f,0.05f);
				RoundedUtils.drawGradientCornerLR(startX + 75f+14-(float) module.AnimationX/2 - SettingGuiAnim,  mY+15-(float) module.AnimationY/2, (float) module.AnimationX, (float) module.AnimationY,6, ColorUtils.applyOpacity(new Color(205, 210, 215, 255), rectScale),
						ColorUtils.applyOpacity(new Color(205, 210, 215, 255), rectScale));
			}
			RenderUtils.drawGoodCircle(startX + 75f+13.5 - SettingGuiAnim, mY+15,4,module.isEnabled()?new Color(255, 255, 255, 255).getRGB():new Color(162, 162, 162, 255).getRGB());
			if (isSettingsButtonHovered(startX + 75+30 - SettingGuiAnim, mY,
					startX + 140 + (FontLoaders.F20.getStringWidth(module.getName())) - SettingGuiAnim,
					mY+30, mouseX, mouseY)&&mouseY<startY+250&&mouseY>startY+25) {
				if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
					module.setEnabled(!module.isEnabled());
					module.AnimationX = 0;
					module.AnimationY = 0;
					previousmouse = true;
				}
				if (!this.previousmouse && Mouse.isButtonDown((int) 1)) {
					previousmouse = true;
				}
			}
			if (module == currentModule) {
				SettingButtonAnim = AnimationUtil.moveUD(SettingButtonAnim, 30, 0.1f, 0.1f);
				RoundedUtils.drawGradientCornerLR(startX + 75 + 245 - SettingGuiAnim, mY + 15 - (SettingButtonAnim / 2), 15, SettingButtonAnim, 6, ColorUtils.applyOpacity(new Color(150, 225, 220, 255), rectScale),
						ColorUtils.applyOpacity(new Color(95, 195, 230, 255), rectScale));
			}
			RenderUtils.drawGoodCircle(startX + 75 + 245 + 7.5 - SettingGuiAnim, mY + 7, 2.5f, module == currentModule ? new Color(255, 255, 255, 255).getRGB() : new Color(162, 162, 162, 255).getRGB());
			RenderUtils.drawGoodCircle(startX + 75 + 245 + 7.5 - SettingGuiAnim, mY + 15, 2.5f, module == currentModule ? new Color(255, 255, 255, 255).getRGB() : new Color(162, 162, 162, 255).getRGB());
			RenderUtils.drawGoodCircle(startX + 75 + 245 + 7.5 - SettingGuiAnim, mY + 23, 2.5f, module == currentModule ? new Color(255, 255, 255, 255).getRGB() : new Color(162, 162, 162, 255).getRGB());
			if (!Mouse.isButtonDown((int) 0)) {
				this.previousmouse = false;
			}
			if (isSettingsButtonHovered(startX + 75+245 - SettingGuiAnim, mY, startX + 75+260 - SettingGuiAnim,
					mY + 30, mouseX, mouseY) && Mouse.isButtonDown(0)&&mouseY<startY+250&&mouseY>startY+25) {
				if(currentModule!=module) {
					currentModule = module;
				}else {
					currentModule = null;
				}
				SettingButtonAnim = 2;
				valueStart = 0;
				Mouse.destroy();
				try {
					Mouse.create();
				} catch (LWJGLException e) {
					throw new RuntimeException(e);
				}
			}
			mY += 38;
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
////////设置
		RoundedUtils.drawRound(startX - 40+290+95-SettingGuiAnim, startY, SettingGuiAnim, 260, 4, new Color(255, 255, 255, 255));
		if (currentModule != null) {
			SettingGuiAnim = AnimationUtil.moveUD(SettingGuiAnim,100,0.02f,0.02f);
			mY = startY + 30-(valueStart*30.0f);
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			RenderUtils.prepareScissorBox(0, startY+25, startX-40+290+95, startY+250);
			if(currentModule.getValues().size()<1){
				RenderUtils.drawRect2(0,0,0,0,-1);
				FontLoaders.F20.drawString("NoSettingsHere",startX+185,startY+10,new Color(178,178,178).getRGB());
			}
			for (int i = 0; i < currentModule.getValues().size()+1; i++) {
				CFontRenderer font = FontLoaders.F16;
				float x;
				if(i >0) {
					Value value = currentModule.getValues().get(i-1);
					//if (value.hidden) {
						//continue;
					//}
					if (value instanceof Option) {
						((Option<?>) value).AnimOption = AnimationUtil.moveUD(((Option<?>) value).AnimOption, (boolean) value.getValue() ? 11 : 0, 0.1f, 0.1f);
						x = startX + 75 - SettingGuiAnim + 285;
						FontLoaders.F16.drawString(value.getName(), x - 5, mY + 3, new Color(136, 136, 136).getRGB());
						RoundedUtils.drawGradientCornerLR(x + 65.0f, mY, 18, 8, 3.5f, (boolean) value.getValue() ? ColorUtils.applyOpacity(new Color(150, 225, 220, 255), rectScale) : new Color(115, 115, 125),
								(boolean) value.getValue() ? ColorUtils.applyOpacity(new Color(61,154,255), rectScale) : new Color(115, 115, 125));
						RenderUtils.drawGoodCircle(x + 68.5f + ((Option<?>) value).AnimOption, mY + 4, 5, (boolean) value.getValue() ? ColorUtils.applyOpacity(new Color(250,250,250), rectScale).getRGB() : new Color(175, 175, 175, 255).getRGB());
					/*
						RenderUtils.drawGoodCircle(x + 68.5f , mY + 4);
					*/
						if (this.isCheckBoxHovered(x + 55.0f, mY, x + 76.0f, mY + 9.0f, mouseX, mouseY)) {
							if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
								mc.thePlayer.playSound("random.click", 1.0f, 1.0f);
								this.previousmouse = true;
								this.mouse = true;
							}
							if (this.mouse) {
								value.setValue(!((Boolean) value.getValue()));
								this.mouse = false;
							}
						}
						if (!Mouse.isButtonDown((int) 0)) {
							this.previousmouse = false;
						}
						mY += 25.0f;
					}
					if (value instanceof Mode) {
						x = startX + 75 - SettingGuiAnim + 285;
						FontLoaders.F16.drawString(value.getName(), x - 5, mY - 1.0f + 2, new Color(136, 136, 136).getRGB());
						RoundedUtils.drawGradientCornerLR(x - 10.0f, mY + 8.5f, 85.0f, 22.0f - 8.5f+((Mode<?>) value).Anim, 3, ColorUtils.applyOpacity(new Color(150, 225, 220, 255), rectScale),
								ColorUtils.applyOpacity(new Color(95, 195, 230, 255), rectScale));
						FontLoaders.F18.drawString(((Mode) value).getModeAsString(), x + 30.0f - (float) (font.getStringWidth(((Mode) value).getModeAsString()) / 2), mY + 10.0f + 2, -1);
						if (this.isStringHovered(x - 10.0f, mY + 6.0f, x + 75.0f, mY + 22.0f, mouseX, mouseY)) {
							if (Mouse.isButtonDown((int) 0)) {
								((Mode<?>) value).state = !((Mode<?>) value).state;
								Mouse.destroy();
								try {
									Mouse.create();
								} catch (LWJGLException e) {
									throw new RuntimeException(e);
								}
							}
						}
						if(((Mode<?>) value).state){
							((Mode<?>) value).Anim = AnimationUtil.moveUD(((Mode<?>) value).Anim,((Mode<?>) value).getModes().length*20-20,0.1f,0.1f);
							int n =0;
							for(Enum mode : ((Mode<?>) value).getModes()){
								if(mode==((Mode<?>) value).getValue()){
									continue;
								}
								n++;
								FontLoaders.F18.drawString(mode.name(),x + 30.0f - (float) (font.getStringWidth(mode.name()) / 2), mY + 8.5f+n*20,this.isStringHovered(x - 10.0f, mY + 8.5f+n*18, x + 75.0f, mY + 8.5f+n*18+20, mouseX, mouseY)?new Color(225,225,225).getRGB():new Color(255,255,255).getRGB());
								if(this.isStringHovered(x - 10.0f, mY + 8.5f+n*18, x + 75.0f, mY + 8.5f+n*18+20, mouseX, mouseY)){
									if (Mouse.isButtonDown((int) 0)) {
										((Mode<Enum>) value).setValue(mode);
										Mouse.destroy();
										try {
											Mouse.create();
										} catch (LWJGLException e) {
											throw new RuntimeException(e);
										}
									}
								}
							}
						} else{
							((Mode<?>) value).Anim = AnimationUtil.moveUD(((Mode<?>) value).Anim,0,0.1f,0.1f);
						}
						mY += 35.0f+((Mode<?>) value).Anim;
					}
					if (value instanceof Numbers) {
						x = startX + 75 - SettingGuiAnim + 285;
						double render = (double) (68.0F
								* (((Number) value.getValue()).floatValue() - ((Numbers) value).getMin().floatValue())
								/ (((Numbers) value).getMax().floatValue()
								- ((Numbers) value).getMin().floatValue()));
						RoundedUtils.drawGradientCornerLR((float) x - 2, mY + 7, 70, 2, 1f,
								(new Color(213, 213, 213, 255)), (new Color(213, 213, 213, 255)));
						RoundedUtils.drawGradientCornerLR((float) x - 2, mY + 7, (float) (render + 0.5D) + 2, 2, 1f,
								ColorUtils.applyOpacity(new Color(150, 225, 220, 255), rectScale),
								ColorUtils.applyOpacity(new Color(95, 195, 230, 255), rectScale));
						font.drawString(value.getName() + ": " + value.getValue(), x - 5, mY - 3, new Color(136, 136, 136).getRGB());
						if (!Mouse.isButtonDown((int) 0)) {
							this.previousmouse = false;
						}
						if (this.isButtonHovered(x, mY - 4, x + 100, mY + 9, mouseX, mouseY)
								&& Mouse.isButtonDown((int) 0)) {
							if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
								render = ((Numbers) value).getMin().doubleValue();
								double max = ((Numbers) value).getMax().doubleValue();
								double inc = ((Numbers) value).getIncrement().doubleValue();
								double valAbs = (double) mouseX - ((double) x + 1.0D);
								double perc = valAbs / 68.0D;
								perc = Math.min(Math.max(0.0D, perc), 1.0D);
								double valRel = (max - render) * perc;
								double val = render + valRel;
								val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
								((Numbers) value).setValue(val);
							}
							if (!Mouse.isButtonDown((int) 0)) {
								this.previousmouse = false;
							}
						}
						mY += 25;
					}
				}
			}
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glPopMatrix();
			RenderUtils.drawGoodCircle(startX - 40+290+95-SettingGuiAnim+6, startY+8,4.5f,new Color(250, 136, 137,255).getRGB());
			if(this.isButtonHovered(startX - 40+290+95-SettingGuiAnim+2, startY+4, startX - 40+290+95-SettingGuiAnim+2+6, startY+4+6, mouseX, mouseY) && Mouse.isButtonDown((int) 0)){
				currentModule = null;
				Mouse.destroy();
				try {
					Mouse.create();
				} catch (LWJGLException e) {
					throw new RuntimeException(e);
				}
			}
			RenderUtils.drawGoodCircle(startX - 40+290+95-SettingGuiAnim+6+12, startY+8,4.5f,new Color(108, 229, 173,255).getRGB());
			if(this.isButtonHovered(startX - 40+290+95-SettingGuiAnim+2+12, startY+4, startX - 40+290+95-SettingGuiAnim+2+6+12, startY+4+6, mouseX, mouseY) && Mouse.isButtonDown((int) 0)){
				Mouse.destroy();
				try {
					Mouse.create();
				} catch (LWJGLException e) {
					throw new RuntimeException(e);
				}
			}
		}
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtils.prepareScissorBox(startX-40, 0, startX-40+290+95, height);
		FontLoaders.F16.drawString(
				currentModule == null ? currentModuleType.toString()
						: currentModuleType.toString() + "/" + currentModule.getName(),
				startX + 70-SettingGuiAnim, startY + 15, new Color(0, 0, 0).getRGB());
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
	}

	@Override
	public void initGui() {
	}

	public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*1f && mouseX <= g*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*1f && mouseX <= x2*1f && mouseY >= y*1f && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*1f && mouseX <= g*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*1f && mouseX <= g*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*1f && mouseX <= x2*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*1f && mouseX <= x2*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	@Override
	public void onGuiClosed() {
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
