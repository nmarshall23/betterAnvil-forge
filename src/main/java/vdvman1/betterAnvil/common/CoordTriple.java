package vdvman1.betterAnvil.common;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class CoordTriple {
	
	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getZ() {
		return Z;
	}

	public CoordTriple setX(int x) {
		X = x;
		return this;
	}

	public CoordTriple setY(int y) {
		Y = y;
		return this;
	}

	public CoordTriple setZ(int z) {
		Z = z;
		return this;
	}

	private int X;
	private int Y;
	private int Z;
	private World theWorld;
	
	
	public CoordTriple(int x, World theWorld) {
		this.X = x;
		this.Y = 0;
		this.Z = 0;
		this.theWorld = theWorld;
	}
	
	public CoordTriple(int x, int y, int z, World theWorld) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.theWorld = theWorld;
	}
	
	public CoordTriple(ChunkCoordinates coords, World theWorld) {
		this.X = coords.posX;
		this.Y = coords.posY;
		this.Z = coords.posZ;
		this.theWorld = theWorld;
	}
	
	public Block getBlockAt() {
		return theWorld.getBlock(X, Y, Z);
		
	}
	
	public ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(X, Y, Z);
	}
	
	public CoordTriple getNeighbor(int xOffset, int yOffset, int zOffset) {
		
		return new CoordTriple(X + xOffset, Y + yOffset, Z + zOffset, theWorld);
	}
	
    public static Predicate<CoordTriple> isBlockFireOrLava() {
        return p -> p.getBlockAt() == Blocks.fire || p.getBlockAt() == Blocks.lava;
    } 
    
    public static Predicate<CoordTriple> isSolidBlock() {
        return p -> p.getBlockAt().isBlockNormalCube() || p.getBlockAt().isOpaqueCube();
    } 
    
    public static Predicate<CoordTriple> isNeighborASolidBlock() {
        return p -> 
        Stream.of(p.getNeighbor(1, 0, 0), p.getNeighbor(-1, 0, 0), 
        		  p.getNeighbor(0, 0, 1), p.getNeighbor(0,  0, -1))
        	  .filter(CoordTriple.isSolidBlock())
        	  .findFirst()
        	  .isPresent();
    } 

}
