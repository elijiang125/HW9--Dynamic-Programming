public class SeamCarver {
    private int width;
    private int height;
    private Picture pictureCopy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("argument to SeamCarver() is null\n");
        }

        pictureCopy = new Picture(picture);
        width = picture.width();
        height = picture.height();
    }

    // current picture
    public Picture picture() {
        return new Picture(pictureCopy);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);

        // border pixels
        if (x == 0 || x == width -1 || y == 0 || y == height -1) {
            return 1000;
        }

        int up, down, left, right;
        up = pictureCopy.getRGB(x, y - 1);
        down = pictureCopy.getRGB(x, y + 1);
        left = pictureCopy.getRGB(x - 1, y);
        right = pictureCopy.getRGB(x + 1, y);
        double gradientY = gradient(up, down);
        double gradientX = gradient(left, right);

        return Math.sqrt(gradientX + gradientY);
    }

    private double gradient(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >>  8) & 0xFF;
        int b1 = (rgb1 >>  0) & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >>  8) & 0xFF;
        int b2 = (rgb2 >>  0) & 0xFF;

        return Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2)
                + Math.pow(b1 - b2, 2);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energy = new double[width][height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[col][row] = energy(col, row);
            }
        }

        //TODO: Use Dynamic Programming to find the vertical seam
        int[] vSeam = new int[height];

        //Okay first, initialize array with energy of top row
        double[][] arr = new double[width][height];
        for (int i = 0; i < width; i++) {
            arr[i][0] = energy[i][0];
        }

        //Next, fill arr with dynamic programming
        for (int j = 1; j < height; j++) {
            for (int k = 0; k < width; k++) {
                double left = (k == 0) ? Double.POSITIVE_INFINITY : arr[k-1][j-1];
                double middle = arr[k][j-1];
                double right = (k == width - 1) ? Double.POSITIVE_INFINITY : arr[k+1][j-1];
                arr[k][j] = energy[k][j] + Math.min(left, Math.min(middle, right));
            }
        }

        //Then, find pixel in the bottom row with smallest value
        int minCol = 0;
        for (int col = 1; col < width; col++) {
            if (arr[col][height-1] < arr[minCol][height-1]) {
                minCol = col;
            }
        }

        //Finally, background to find vertical seam
        vSeam[height-1] = minCol;
        for (int row = height-2; row >= 0; row--) {
            double left = (minCol == 0) ? Double.POSITIVE_INFINITY : arr[minCol-1][row];
            double middle = arr[minCol][row];
            double right = (minCol == width-1) ? Double.POSITIVE_INFINITY : arr[minCol+1][row];
            if (left < middle && left < right) {
                minCol--;
            }
            else if (right < middle && right < left) {
                minCol++;
            }

            vSeam[row] = minCol;
        }

        return vSeam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        if (height == 1) {
            return new int[width];
        }
        else if (width == 1) {
            return new int[]{0};
        }
        int[] hSeam = new int[height];
        double[][] energy = new double[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[row][col] = energy(col, row);
            }
        }

        //TODO: Use Dynamic Programming to find the horizontal seam

        // Okay first, find the minimum energy path; this is same as last time, but different with dynamic programming

        double[][] arr = new double[height][width];
        int[][] edges = new int[height][width];
        //initialize edges array first
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                edges[row][col] = -1;
            }
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row == 0) {
                    arr[row][col] = energy[row][col];
                } else {
                    double left = (col == 0) ? Double.POSITIVE_INFINITY : arr[row-1][col-1];
                    double middle = arr[row-1][col];
                    double right = (col == width - 1) ? Double.POSITIVE_INFINITY : arr[row-1][col+1];
                    double minEnergy = energy[row][col] + Math.min(left, Math.min(middle, right));
                    arr[row][col] = minEnergy;
                    if (col == 0) {
                        edges[row][col] = (minEnergy == left) ? col : col + 1;
                    } else if (col == width-1) {
                        edges[row][col] = (minEnergy == right) ? col : col - 1;
                    } else {
                        if (minEnergy == left) {
                            edges[row][col] = col-1;
                        } else if (minEnergy == middle) {
                            edges[row][col] = col;
                        } else {
                            edges[row][col] = col+1;
                        }
                    }
                }
            }
        }

// Now we can just find the col index of the minimum energy path in the last row
        int minCol = 0;
        double minEnergy = arr[height-1][0];
        for (int col = 1; col < width; col++) {
            if (arr[height - 1][col] < minEnergy) {
                minEnergy = arr[height - 1][col];
                minCol = col;
            }
        }

        //Now we backtrack to find horizontal seam

        for (int row = height-1; row >= 0; row--) {
            hSeam[row] = minCol;
            if (minCol == 0) {
                minCol = (arr[row-1][minCol] < arr[row-1][minCol+1]) ? minCol : minCol + 1;
            } else if (minCol == width-1) {
                minCol = (arr[row-1][minCol] < arr[row-1][minCol-1]) ? minCol : minCol - 1;
            } else {
                double left = arr[row-1][minCol-1];
                double middle = arr[row-1][minCol];
                double right = arr[row-1][minCol+1];
                if (left < middle && left < right) {
                    minCol--;
                } else if (right < middle && right < left) {
                    minCol++;
                }
                else {
                    //forgot this; if all else fails, then set to mincol - 1 if left equals to middle
                    minCol = (left == middle) ? minCol - 1: minCol;
                }
            }
        }

        return hSeam;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("the argument to removeVerticalSeam() is null\n");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("the length of seam not equal height\n");
        }
        validateSeam(seam);
        if (width <= 1) {
            throw new IllegalArgumentException("the width of the picture is less than or equal to 1\n");
        }

        Picture tmpPicture = new Picture(width - 1, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                validateColumnIndex(seam[row]);
                if (col < seam[row]) {
                    tmpPicture.setRGB(col, row, pictureCopy.getRGB(col, row));
                } else {
                    tmpPicture.setRGB(col, row, pictureCopy.getRGB(col + 1, row));
                }
            }
        }
        pictureCopy = tmpPicture;
        width--;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        //TODO: Remove a horizontal seam

        if (seam == null) {
            throw new IllegalArgumentException("the argument to removeVerticalSeam() is null\n");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("the length of seam not equal height\n");
        }
        validateSeam(seam);
        if (width <= 1) {
            throw new IllegalArgumentException("the width of the picture is less than or equal to 1\n");
        }

        // Shift pixels down by one row
        Picture newPicture = new Picture(width, height-1);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height-1; row++) {
                if (row < seam[col]) {
                    newPicture.set(col, row, pictureCopy.get(col, row));
                } else {
                    newPicture.set(col, row, pictureCopy.get(col, row+1));
                }
            }
        }

        // Update the picture and height
        pictureCopy = newPicture;
        height--;
    }

    // transpose the current pictureCopy
    private void transpose() {
        Picture tmpPicture = new Picture(height, width);
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                tmpPicture.setRGB(col, row, pictureCopy.getRGB(row, col));
            }
        }
        pictureCopy = tmpPicture;
        int tmp = height;
        height = width;
        width = tmp;
    }

    // make sure column index is bewteen 0 and width - 1
    private void validateColumnIndex(int col) {
        if (col < 0 || col > width -1) {
            throw new IllegalArgumentException("colmun index is outside its prescribed range\n");
        }
    }

    // make sure row index is bewteen 0 and height - 1
    private void validateRowIndex(int row) {
        if (row < 0 || row > height -1) {
            throw new IllegalArgumentException("row index is outside its prescribed range\n");
        }
    }

    // make sure two adjacent entries differ within 1
    private void validateSeam(int[] seam) {
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("two adjacent entries differ by more than 1 in seam\n");
            }
        }
    }
}