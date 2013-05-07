package vdvman1.betterAnvil.inventory;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vdvman1.betterAnvil.BetterAnvil;

public class ContainerRepairBA extends ContainerRepair
{
    /** Here comes out item you merged and/or renamed. */
    private IInventory outputSlot = new InventoryCraftResult();

    /**
     * The 2slots where you put your items in that you want to merge and/or rename.
     */
    private IInventory inputSlots = new InventoryRepairBA(this, "Repair", true, 2);
    private World theWorld;
    private int x;
    private int y;
    private int z;

    /** determined by damage of input item and stackSize of repair materials */
    private int stackSizeToBeUsedInRepair = 0;
    private String repairedItemName;

    /** The player that has this container open. */
    private final EntityPlayer thePlayer;

    public ContainerRepairBA(InventoryPlayer inventoryPlayer, World world, int x, int y, int z, EntityPlayer entityPlayer)
    {
        super(inventoryPlayer, world, x, y, z, entityPlayer);
    	this.theWorld = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.thePlayer = entityPlayer;
        
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(new SlotRepairBA(this, this.outputSlot, 2, 134, 47, world, x, y, z));
        int l;

        for (l = 0; l < 3; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, l, 8 + l * 18, 142));
        }
    }
    
    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory iInventory)
    {
        super.onCraftMatrixChanged(iInventory);

        if (iInventory == this.inputSlots)
        {
            this.updateRepairOutput();
        }
    }

    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
	public void updateRepairOutput()
    {
        ItemStack itemstack = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 0;
        int itemDamage = 0;
        byte b0 = 0;
        int repairAmount = 0;

        if (itemstack == null)
        {
            this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
            this.maximumCost = 0;
        }
        else
        {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
            Map enchantmentMap = EnchantmentHelper.getEnchantments(itemstack1);
            boolean flag = false;
            int repairCost = b0 + itemstack.getRepairCost() + (itemstack2 == null ? 0 : itemstack2.getRepairCost());
            this.stackSizeToBeUsedInRepair = 0;
            int itemDamage1;
            int tempInt;
            int tempInt1;
            int tempInt2;
            int tempInt3;
            Iterator iterator;
            Enchantment enchantment;

            if (itemstack2 != null)
            {
                flag = itemstack2.itemID == Item.enchantedBook.itemID && Item.enchantedBook.func_92110_g(itemstack2).tagCount() > 0;//has enchantments on book

                if (itemstack1.isItemStackDamageable() && Item.itemsList[itemstack1.itemID].getIsRepairable(itemstack, itemstack2))
                {
                    itemDamage1 = Math.min(itemstack1.getItemDamageForDisplay(), itemstack1.getMaxDamage() / 4);

                    if (itemDamage1 <= 0)
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    for (tempInt = 0; itemDamage1 > 0 && tempInt < itemstack2.stackSize; ++tempInt)
                    {
                        tempInt1 = itemstack1.getItemDamageForDisplay() - itemDamage1;
                        itemstack1.setItemDamage(tempInt1);
                        itemDamage += Math.max(1, itemDamage1 / 100) + enchantmentMap.size();
                        itemDamage1 = Math.min(itemstack1.getItemDamageForDisplay(), itemstack1.getMaxDamage() / 4);
                    }

                    this.stackSizeToBeUsedInRepair = tempInt;
                }
                else
                {
                    if (!flag && (itemstack1.itemID != itemstack2.itemID || !itemstack1.isItemStackDamageable()))
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (itemstack1.isItemStackDamageable() && !flag)
                    {
                        itemDamage1 = itemstack.getMaxDamage() - itemstack.getItemDamageForDisplay();
                        tempInt = itemstack2.getMaxDamage() - itemstack2.getItemDamageForDisplay();
                        tempInt1 = tempInt + itemstack1.getMaxDamage() * 12 / 100;
                        int itemDamage2 = itemDamage1 + tempInt1;
                        tempInt2 = itemstack1.getMaxDamage() - itemDamage2;

                        if (tempInt2 < 0)
                        {
                            tempInt2 = 0;
                        }

                        if (tempInt2 < itemstack1.getItemDamage())
                        {
                            itemstack1.setItemDamage(tempInt2);
                            itemDamage += Math.max(1, tempInt1 / 100);
                        }
                    }

                    Map enchantmentMap1 = EnchantmentHelper.getEnchantments(itemstack2);
                    iterator = enchantmentMap1.keySet().iterator();

                    while (iterator.hasNext())
                    {
                        tempInt1 = ((Integer)iterator.next()).intValue();
                        enchantment = Enchantment.enchantmentsList[tempInt1];
                        tempInt2 = enchantmentMap.containsKey(Integer.valueOf(tempInt1)) ? ((Integer)enchantmentMap.get(Integer.valueOf(tempInt1))).intValue() : 0;
                        tempInt3 = ((Integer)enchantmentMap1.get(Integer.valueOf(tempInt1))).intValue();
                        int enchantmentValue;

                        if (tempInt2 == tempInt3)
                        {
                            ++tempInt3;
                            enchantmentValue = tempInt3;
                        }
                        else
                        {
                            enchantmentValue = Math.max(tempInt3, tempInt2);
                        }

                        tempInt3 = enchantmentValue;
                        int finalEnchantmentValue = tempInt3 - tempInt2;
                        boolean flag1 = enchantment.func_92089_a(itemstack);//can enchant item

                        if (this.thePlayer.capabilities.isCreativeMode || itemstack.itemID == ItemEnchantedBook.enchantedBook.itemID)
                        {
                            flag1 = true;
                        }

                        Iterator iterator1 = enchantmentMap.keySet().iterator();

                        while (iterator1.hasNext())
                        {
                            int enchantmentValue1 = ((Integer)iterator1.next()).intValue();

                            if (enchantmentValue1 != tempInt1 && !enchantment.canApplyTogether(Enchantment.enchantmentsList[enchantmentValue1]))
                            {
                                flag1 = false;
                                itemDamage += finalEnchantmentValue;
                            }
                        }

                        if (flag1)
                        {
                            if (tempInt3 > enchantment.getMaxLevel())
                            {
                                tempInt3 = enchantment.getMaxLevel();
                            }

                            enchantmentMap.put(Integer.valueOf(tempInt1), Integer.valueOf(tempInt3));
                            int enchantmentWeight = 0;

                            switch (enchantment.getWeight())
                            {
                                case 1:
                                    enchantmentWeight = 8;
                                    break;
                                case 2:
                                    enchantmentWeight = 4;
                                case 3:
                                case 4:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                default:
                                    break;
                                case 5:
                                    enchantmentWeight = 2;
                                    break;
                                case 10:
                                    enchantmentWeight = 1;
                            }

                            if (flag)
                            {
                                enchantmentWeight = Math.max(1, enchantmentWeight / 2);
                            }

                            itemDamage += enchantmentWeight * finalEnchantmentValue;
                        }
                    }
                }
            }

            if (this.repairedItemName != null && !this.repairedItemName.equalsIgnoreCase(itemstack.getDisplayName()) && this.repairedItemName.length() > 0)
            {
                repairAmount = itemstack.isItemStackDamageable() ? 7 : itemstack.stackSize * 5;
                itemDamage += repairAmount;

                if (itemstack.hasDisplayName())
                {
                    repairCost += repairAmount / 2;
                }

                itemstack1.setItemName(this.repairedItemName);
            }

            itemDamage1 = 0;

            for (iterator = enchantmentMap.keySet().iterator(); iterator.hasNext(); repairCost += itemDamage1 + tempInt2 * tempInt3)
            {
                tempInt1 = ((Integer)iterator.next()).intValue();
                enchantment = Enchantment.enchantmentsList[tempInt1];
                tempInt2 = ((Integer)enchantmentMap.get(Integer.valueOf(tempInt1))).intValue();
                tempInt3 = 0;
                ++itemDamage1;

                switch (enchantment.getWeight())
                {
                    case 1:
                        tempInt3 = 8;
                        break;
                    case 2:
                        tempInt3 = 4;
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        break;
                    case 5:
                        tempInt3 = 2;
                        break;
                    case 10:
                        tempInt3 = 1;
                }

                if (flag)
                {
                    tempInt3 = Math.max(1, tempInt3 / 2);
                }
            }

            if (flag)
            {
                repairCost = Math.max(1, repairCost / 2);
            }

            this.maximumCost = (int)Math.round((repairCost + itemDamage) * BetterAnvil.costMultiplier);

            if (itemDamage <= 0)
            {
                itemstack1 = null;
            }

            if (repairAmount == itemDamage && repairAmount > 0 && this.maximumCost >= 40)
            {
                this.theWorld.getWorldLogAgent().logInfo("Naming an item only, cost too high; giving discount to cap cost to 39 levels");
                this.maximumCost = 39;
            }

            if (itemstack1 != null)
            {
                tempInt = itemstack1.getRepairCost();

                if (itemstack2 != null && tempInt < itemstack2.getRepairCost())
                {
                    tempInt = itemstack2.getRepairCost();
                }

                if (itemstack1.hasDisplayName())
                {
                    tempInt -= 9;
                }

                if (tempInt < 0)
                {
                    tempInt = 0;
                }

                tempInt += 2;
                itemstack1.setRepairCost(tempInt);
                EnchantmentHelper.setEnchantments(enchantmentMap, itemstack1);
            }

            this.outputSlot.setInventorySlotContents(0, itemstack1);
            this.detectAndSendChanges();
        }
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onCraftGuiClosed(EntityPlayer entityPlayer)
    {
        super.onCraftGuiClosed(entityPlayer);

        if (!this.theWorld.isRemote)
        {
            for (int i = 0; i < this.inputSlots.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.inputSlots.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    entityPlayer.dropPlayerItem(itemstack);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return this.theWorld.getBlockId(this.x, this.y, this.z) != Block.anvil.blockID ? false : entityPlayer.getDistanceSq((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 64.0D;
    }

    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    @Override
    public void updateItemName(String name)
    {
        this.repairedItemName = name;

        if (this.getSlot(2).getHasStack())
        {
            this.getSlot(2).getStack().setItemName(this.repairedItemName);
        }

        this.updateRepairOutput();
    }

    public static IInventory getRepairInputInventory(ContainerRepairBA containerRepairBA)
    {
        return containerRepairBA.inputSlots;
    }

    public static int getStackSizeUsedInRepair(ContainerRepairBA containerRepairBA)
    {
        return containerRepairBA.stackSizeToBeUsedInRepair;
    }
}