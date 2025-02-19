/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2024
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
package elki.utilities.datastructures.unionfind;

import java.util.Arrays;

import elki.database.ids.*;
import elki.utilities.documentation.Reference;

/**
 * Union-find using the weighted quick union approach, weighted by count and
 * using path-halving for optimization.
 * <p>
 * Reference:
 * <p>
 * R. Sedgewick<br>
 * 1.3 Union-Find Algorithms<br>
 * Algorithms in C, Parts 1-4
 *
 * @author Evgeniy Faerman
 * @author Erich Schubert
 * @since 0.7.0
 */
@Reference(authors = "R. Sedgewick", //
    title = "Algorithms in C, Parts 1-4", //
    booktitle = "", //
    bibkey = "DBLP:books/daglib/0004943")
public class WeightedQuickUnionDBIDs implements UnionFind {
  /**
   * Object ID range.
   */
  private DBIDEnum ids;

  /**
   * Parent element
   */
  private int[] parent;

  /**
   * Weight, for optimization.
   */
  private int[] weight;

  /**
   * Constructor.
   *
   * @param ids Range to use
   */
  public WeightedQuickUnionDBIDs(StaticDBIDs ids) {
    this.ids = DBIDUtil.ensureEnum(ids);
    weight = new int[this.ids.size()];
    Arrays.fill(weight, 1);
    parent = new int[this.ids.size()];
    for(int i = 0; i < parent.length; i++) {
      parent[i] = i;
    }
  }

  @Override
  public int find(DBIDRef element) {
    int cur = ids.index(element);
    assert (cur >= 0 && cur < ids.size());
    int p = parent[cur], tmp;
    while(cur != p) {
      tmp = p;
      p = parent[cur] = parent[p]; // Perform simple path compression.
      cur = tmp;
    }
    return cur;
  }

  @Override
  public int union(DBIDRef first, DBIDRef second) {
    int firstComponent = find(first), secondComponent = find(second);
    if(firstComponent == secondComponent) {
      return firstComponent;
    }
    final int w1 = weight[firstComponent], w2 = weight[secondComponent];
    if(w1 > w2) {
      parent[secondComponent] = firstComponent;
      weight[firstComponent] += w2;
      return firstComponent;
    }
    else {
      parent[firstComponent] = secondComponent;
      weight[secondComponent] += w1;
      return secondComponent;
    }
  }

  @Override
  public boolean isConnected(DBIDRef first, DBIDRef second) {
    return find(first) == find(second);
  }

  @Override
  public DBIDs getRoots() {
    ArrayModifiableDBIDs roots = DBIDUtil.newArray();
    for(DBIDArrayIter iter = ids.iter(); iter.valid(); iter.advance()) {
      // roots or one element in component
      if(parent[iter.getOffset()] == iter.getOffset()) {
        roots.add(iter);
      }
    }
    return roots;
  }

  @Override
  public int size(int component) {
    return weight[component];
  }
}
