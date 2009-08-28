package experimentalcode.remigius.Visualizers;

import de.lmu.ifi.dbs.elki.data.NumberVector;

/**
 * Produces 2-dimensional visualizations.
 * 
 * @author Remigius Wojdanowski
 *
 * @param <O> The type of object to process.
 */
public abstract class PlanarVisualizer<NV extends NumberVector<NV, N>, N extends Number> extends NumberVectorVisualizer<NV, N> {
  
  /**
   * the dimension to appear as horizontal dimension.
   */
  protected int dimx;
  
  /**
   * the dimension to appear as vertical dimension.
   */
  protected int dimy;
  
  /**
   * Setting up parameters individual to each run of the visualization.
   * 
   * @param dimx
   * @param dimy
   */
  public void setup(int dimx, int dimy){
    this.dimx = dimx;
    this.dimy = dimy;
  }
}
