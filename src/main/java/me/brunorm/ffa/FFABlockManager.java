package me.brunorm.ffa;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

public class FFABlockManager {

    ArrayList<BlockState> savedBlockStates = new ArrayList<BlockState>();

    public void removeBlocksSmooth(FFAPlayer player) {
        int i = 0;
        for (final Block block : player.getPlacedBlocks()) {
            final BlockState saved_state = this.getBlockState(block);
            if (!this.savedBlockStates.remove(saved_state))
                continue;
            final Location loc = block.getLocation();
            Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
                @Override
                public void run() {
                    FFA.get().NMS().sendParticles(loc, "SMOKE", 10);
                    block.setType(Material.BEDROCK);
                }
            }, i);
            Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
                @Override
                public void run() {
                    FFA.get().NMS().sendParticles(loc, "SMOKE", 10);
                    block.setType(saved_state.getType());
                }
            }, i + 20);
            Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
                @Override
                public void run() {
                    Block below = block.getLocation().add(new Vector(0, -1, 0)).getBlock();
                    if (below.getType().equals(Material.DIRT)) {
                        below.setType(Material.GRASS);
                        FFA.get().NMS().sendParticles(loc.add(new Vector(0, 0.5, 1)), "HAPPY_VILLAGER", 1);
                    }
                }
            }, i + 40);
            i++;
        }
    }

    public void removeBlocks() {
    }

    public void restorePlacedBlock(Block block) {
        BlockState state = this.getBlockState(block);
        if (state == null)
            return;
        state.getBlock().setType(state.getType());
    }

    public void addPlacedBlock(BlockState state) {
        this.savedBlockStates.add(state);
    }

    public void breakAndRegenerateBlock(Block b) {
        if (this.getBlockState(b) != null)
            return;
        BlockState state = b.getState();
        this.savedBlockStates.add(state);
        b.breakNaturally();
        Bukkit.getScheduler().runTaskLater(FFA.get(), new Runnable() {
            @Override
            public void run() {
                b.setType(state.getType());
            }
        }, 100L);
    }

    BlockState getBlockState(Block b) {
        for (BlockState state : this.savedBlockStates)
            if (state.getBlock().getLocation().equals(b.getLocation())) {
                System.out.println("equal");
                return state;
            }
        return null;
    }

    public void restoreBlocks() {
        for (BlockState state : this.savedBlockStates)
            state.getBlock().setType(state.getType());
        this.savedBlockStates.clear();
    }

}
