package bfst22.vector.Util;

/**
 * This class is responsible for making KDTree split line from the Range search
 */
public class KdSplitLine {
    public float xSplit, ySplit;
    public int axis;
    public boolean greater;

    /**
     * 
     * @param xSplit the x coordinate where the split is made
     * @param ySplit the x coordinate where the split is made
     * @param axis   the axis in which the split was made
     */
    KdSplitLine(float xSplit, float ySplit, int axis) {
        this.xSplit = xSplit;
        this.ySplit = ySplit;
        this.axis = axis;
    }
}
