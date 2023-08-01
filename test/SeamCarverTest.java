import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SeamCarverTest {
    // TODO: Provide accuracy tests on the methods marked with TODO && provide your own images to SeamCarve! (original + seamcarved)

    String path = "C:\\Users\\Elizabeth\\Documents\\C343Labs\\HW9\\image.png";
    Picture pictureTest = new Picture(path);
    SeamCarver test = new SeamCarver(pictureTest);

    @Test
    public void findVerticalSeamTest() {

        int[] arr = new int[test.height() - 1];

        arr = test.findVerticalSeam();

        assertEquals(254, arr[0]);
        assertEquals(254, arr[1]);
        assertEquals(254, arr[2]);
        assertEquals(254, arr[3]);
        assertEquals(254, arr[4]);
        assertEquals(268, arr[184]);

        test.removeVerticalSeam(arr);


    }



}
