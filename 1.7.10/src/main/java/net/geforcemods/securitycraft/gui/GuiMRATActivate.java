package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiMRATActivate extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack item;
	private GuiButton[] buttons = new GuiButton[6];

	public GuiMRATActivate(InventoryPlayer inventory, ItemStack item) {
		super(new ContainerGeneric(inventory, null));
		this.item = item;
	}

	@Override
	public void initGui(){
		super.initGui();
		for(int i = 1; i < 7; i++){
			buttons[i - 1] = new GuiButton(i - 1, width / 2 - 49 - 25, height / 2 - 7 - 60  + ((i - 1) * 25), 149, 20, StatCollector.translateToLocal("gui.mrat.notBound"));
			buttons[i - 1].enabled = false;

			if(item.getItem() != null && item.getItem() == SCContent.remoteAccessMine && item.stackTagCompound != null &&  item.stackTagCompound.getIntArray("mine" + i) != null && item.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = item.stackTagCompound.getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					buttonList.add(buttons[i - 1]);
					continue;
				}

				buttons[i - 1].displayString = StatCollector.translateToLocal("gui.mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(coords[0], coords[1], coords[2]));
				buttons[i - 1].enabled = (mc.theWorld.getBlock(coords[0], coords[1], coords[2]) instanceof IExplosive && ((IExplosive) mc.theWorld.getBlock(coords[0], coords[1], coords[2])).isDefusable() && !((IExplosive) mc.theWorld.getBlock(coords[0], coords[1], coords[2])).isActive(mc.theWorld, coords[0], coords[1], coords[2])) ? true : false;
				buttons[i - 1].id = i - 1;
			}

			buttonList.add(buttons[i - 1]);
		}
	}


	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("gui.mrat.activate"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.mrat.deactivate")) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		int[] coords = item.stackTagCompound.getIntArray("mine" + (guibutton.id + 1));

		if(Minecraft.getMinecraft().theWorld.getBlock(coords[0], coords[1], coords[2]) instanceof IExplosive)
			SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "activate"));

		updateButton(guibutton);
	}

	private void updateButton(GuiButton guibutton) {
		guibutton.enabled = false;
		guibutton.displayString = guibutton.enabled ? "" : StatCollector.translateToLocal("gui.mrat.activated");
	}

}
