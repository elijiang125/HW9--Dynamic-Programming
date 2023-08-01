import java.util.*;
import java.io.*;

public class Runner {

    public static void main(String[] args) throws IOException{
        // TODO: write the runner so that it follows the same format as the directions

        Scanner scanner = new Scanner(System.in);

        // Ask for input image
        System.out.print("Enter image path: ");
        String inputImagePath = scanner.nextLine();
        Picture inputPicture = new Picture(inputImagePath);

        // Ask for number of vertical seams to remove
        System.out.print("Enter the number of vertical seams you would like to remove: ");
        int numVerticalSeamsToRemove = scanner.nextInt();

        // Ask for number of horizontal seams to remove
        System.out.print("Enter the number of horizontal seams you would to remove: ");
        int numHorizontalSeamsToRemove = scanner.nextInt();

        // Ask for output file
        System.out.print("Where would you like to save your output file? ");
        String outputImagePath = scanner.next();

        // Create seam carver object based on input picture
        SeamCarver seamCarver = new SeamCarver(inputPicture);

        // Remove vertical seams
        for (int i = 0; i < numVerticalSeamsToRemove; i++) {
            int[] verticalSeam = seamCarver.findVerticalSeam();
            seamCarver.removeVerticalSeam(verticalSeam);
        }

        // Remove horizontal seams
        for (int i = 0; i < numHorizontalSeamsToRemove; i++) {
            int[] horizontalSeam = seamCarver.findHorizontalSeam();
            seamCarver.removeHorizontalSeam(horizontalSeam);
        }

        // Save output picture
        Picture outputPicture = seamCarver.picture();
        outputPicture.save(new File(outputImagePath));

    }
}
