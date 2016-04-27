package vdvman1.betterAnvil.common;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import vdvman1.betterAnvil.BetterAnvil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class LocateBlockUtil {

	public LocateBlockUtil() {
	}
	
	
	public static boolean isHeatSourceAtCoordinates(World theWorld, ChunkCoordinates coords) {

		return CoordTriple.isBlockFireOrLava().test(new CoordTriple(coords, theWorld));
		
	}
	
	public static Optional<ChunkCoordinates> LocateHeatSource(World theWorld, ChunkCoordinates coor, int horDistance) {
		int x = coor.posX;
		int y = coor.posY;
		int z = coor.posZ;
		
		return LocateBlockUtil.LocateHeatSource(theWorld, x, y, z, horDistance);
	}
	
	public static Optional<ChunkCoordinates> LocateHeatSource(World theWorld, int startX, int startY, int startZ, int horDistance) {
	
		Supplier<IntStream> streamSupplier =
				() -> IntStream.rangeClosed(horDistance * -1, horDistance);

		Function<CoordTriple, Stream<CoordTriple>> flatMapDist = new Function<CoordTriple,Stream<CoordTriple>>() {
			public Stream<CoordTriple> apply(CoordTriple tripl) {
				Stream<CoordTriple> hz = IntStream.rangeClosed(horDistance * -1, horDistance)
												  .mapToObj(z -> tripl.setZ(startZ + z))
												  .map(t -> t.setY(startY));
				    	
				return hz;
			}
		};
		
		Function<CoordTriple, CoordTriple> loggingMapper = new Function<CoordTriple, CoordTriple>() {
			public CoordTriple apply(CoordTriple tripl) {
				    	
				BetterAnvil.BETTER_ANVIL_LOGGER.info(
	        			String.format("LocateHeatSource Triple X: %s Y: %s, Z: %s BlockName: %s", 
	        					tripl.getX(), tripl.getY(), tripl.getZ(), tripl.getBlockAt().getUnlocalizedName()));
				return tripl;
			}
		};
		
		Optional<ChunkCoordinates> heatSource = streamSupplier.get()
				.mapToObj(x -> new CoordTriple(startX + x, theWorld))
				.flatMap(flatMapDist)
				.map(loggingMapper)
				.filter(CoordTriple.isBlockFireOrLava())
				.filter(CoordTriple.isNeighborASolidBlock())
				.map(CoordTriple::getChunkCoordinates)
				.findFirst();
		
		return heatSource;
	}
	
	
	
	
}
