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
package elki.similarity.kernel;

import static elki.math.linearalgebra.VMath.*;

import java.util.Arrays;
import java.util.logging.Level;

import elki.database.ids.*;
import elki.database.query.similarity.SimilarityQuery;
import elki.database.relation.Relation;
import elki.logging.LoggingUtil;
import elki.similarity.PrimitiveSimilarity;

/**
 * Kernel matrix representation.
 * 
 * @author Simon Paradies
 * @since 0.1
 * 
 * @assoc - - - PrimitiveSimilarity
 */
public class KernelMatrix {
  /**
   * The kernel matrix
   */
  double[][] kernel;

  /**
   * Static mapping from DBIDs to indexes.
   */
  DBIDEnum idmap;

  /**
   * Provides a new kernel matrix.
   * 
   * @param <O> object type
   * @param kernelFunction the kernel function used to compute the kernel matrix
   * @param relation the database that holds the objects
   * @param ids the IDs of those objects for which the kernel matrix is computed
   */
  public <O> KernelMatrix(PrimitiveSimilarity<? super O> kernelFunction, final Relation<? extends O> relation, final DBIDs ids) {
    this.kernel = new double[ids.size()][ids.size()];
    this.idmap = DBIDUtil.ensureEnum(ids);

    DBIDArrayIter i1 = this.idmap.iter(), i2 = this.idmap.iter();
    for(i1.seek(0); i1.valid(); i1.advance()) {
      O o1 = relation.get(i1);
      for(i2.seek(i1.getOffset()); i2.valid(); i2.advance()) {
        double value = kernelFunction.similarity(o1, relation.get(i2));
        kernel[i1.getOffset()][i2.getOffset()] = value;
        kernel[i2.getOffset()][i1.getOffset()] = value;
      }
    }
  }

  /**
   * Provides a new kernel matrix.
   * 
   * @param <O> object type
   * @param kernelFunction the kernel function used to compute the kernel matrix
   * @param relation the database that holds the objects
   * @param ids the IDs of those objects for which the kernel matrix is computed
   */
  public <O> KernelMatrix(SimilarityQuery<? super O> kernelFunction, final Relation<? extends O> relation, final DBIDs ids) {
    LoggingUtil.logExpensive(Level.FINER, "Computing kernel matrix");
    kernel = new double[ids.size()][ids.size()];
    idmap = DBIDUtil.ensureEnum(ids);
    DBIDArrayIter i1 = idmap.iter(), i2 = idmap.iter();
    for(i1.seek(0); i1.valid(); i1.advance()) {
      O o1 = relation.get(i1);
      for(i2.seek(i1.getOffset()); i2.valid(); i2.advance()) {
        double value = kernelFunction.similarity(o1, i2);
        kernel[i1.getOffset()][i2.getOffset()] = value;
        kernel[i2.getOffset()][i1.getOffset()] = value;
      }
    }
  }

  /**
   * Makes a new kernel matrix from matrix (with data copying).
   * 
   * @param matrix a matrix
   */
  public KernelMatrix(final double[][] matrix) {
    kernel = copy(matrix);
  }

  /**
   * Returns the kernel distance between the two specified objects.
   * 
   * @param o1 first ObjectID
   * @param o2 second ObjectID
   * @return the distance between the two objects
   */
  public double getDistance(final DBIDRef o1, final DBIDRef o2) {
    return Math.sqrt(getSquaredDistance(o1, o2));
  }

  /**
   * Get the kernel matrix.
   * 
   * @return kernel
   */
  public double[][] getKernel() {
    return kernel;
  }

  /**
   * Returns the squared kernel distance between the two specified objects.
   * 
   * @param id1 first ObjectID
   * @param id2 second ObjectID
   * @return the distance between the two objects
   */
  public double getSquaredDistance(final DBIDRef id1, final DBIDRef id2) {
    final int o1 = idmap.index(id1), o2 = idmap.index(id2);
    return kernel[o1][o1] + kernel[o2][o2] - 2 * kernel[o1][o2];
  }

  /**
   * Centers the matrix in feature space according to Smola et Schoelkopf,
   * Learning with Kernels p. 431 Alters the input matrix. If you still need the
   * original matrix, use
   * <code>centeredMatrix = centerKernelMatrix(uncenteredMatrix.copy()) {</code>
   * 
   * @param matrix the matrix to be centered
   * @return centered matrix (for convenience)
   */
  public static double[][] centerMatrix(final double[][] matrix) {
    // FIXME: implement more efficiently. Maybe in matrix class itself?
    final double[][] normalizingMatrix = new double[matrix.length][matrix[0].length];
    for(double[] row : normalizingMatrix) {
      Arrays.fill(row, 1.0 / matrix[0].length);
    }
    return times(plusEquals(minusEquals(minus(matrix, times(normalizingMatrix, matrix)), times(matrix, normalizingMatrix)), times(normalizingMatrix, matrix)), normalizingMatrix);
  }

  /**
   * Centers the Kernel Matrix in Feature Space according to Smola et.
   * Schoelkopf, Learning with Kernels p. 431 Alters the input matrix. If you
   * still need the original matrix, use
   * <code>centeredMatrix = centerKernelMatrix(uncenteredMatrix.copy()) {</code>
   * 
   * @param kernelMatrix the kernel matrix to be centered
   * @return centered kernelMatrix (for convenience)
   */
  public static double[][] centerKernelMatrix(final KernelMatrix kernelMatrix) {
    return centerMatrix(kernelMatrix.getKernel());
  }

  /**
   * Get the kernel similarity for the given objects.
   * 
   * @param id1 First object
   * @param id2 Second object
   * @return Similarity.
   */
  public double getSimilarity(DBIDRef id1, DBIDRef id2) {
    return kernel[idmap.index(id1)][idmap.index(id2)];
  }
}
