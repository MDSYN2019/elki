/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 * 
 * Copyright (C) 2020
 * ELKI Development Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package elki.clustering.hierarchical.betula.distance;

import elki.clustering.hierarchical.betula.features.ClusterFeature;
import elki.data.NumberVector;
import elki.utilities.Alias;
import elki.utilities.documentation.Reference;
import elki.utilities.optionhandling.Parameterizer;

/**
 * Average intercluster distance.
 * <p>
 * Reference:
 * Andreas Lang and Erich Schubert<br>
 * BETULA: Fast Clustering of Large Data with Improved BIRCH CF-Trees<br>
 * Information Systems (under review)
 *
 * @author Andreas Lang
 * @author Erich Schubert
 */
@Alias({ "D2" })
@Reference(authors = "Andreas Lang and Erich Schubert", //
    title = "BETULA: Fast Clustering of Large Data with Improved BIRCH CF-Trees", //
    booktitle = "Information Systems (under review)")
public class AverageInterclusterDistance implements CFDistance {
  /**
   * Static instance.
   */
  public static final AverageInterclusterDistance STATIC = new AverageInterclusterDistance();

  @Override
  public double squaredDistance(NumberVector nv, ClusterFeature cf) {
    return cf.sumdev() / cf.getWeight() + cf.squaredCenterDistance(nv);
  }

  @Override
  public double squaredDistance(ClusterFeature cf1, ClusterFeature cf2) {
    return cf1.sumdev() / cf1.getWeight() + cf2.sumdev() / cf2.getWeight() + cf1.squaredCenterDistance(cf2);
  }

  /**
   * Parameterization class.
   *
   * @author Andreas Lang
   */
  public static class Par implements Parameterizer {
    @Override
    public AverageInterclusterDistance make() {
      return STATIC;
    }
  }
}
