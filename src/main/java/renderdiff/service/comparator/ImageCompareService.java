package renderdiff.service.comparator;

/**
 * Original nabbed from: http://mindmeat.blogspot.com/2008/07/java-image-comparison.html
 * Modified to conform to a service and it's domain.
 */

import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.ImageCommand;

import java.io.IOException;

/**
 *
 */
public class ImageCompareService {
    /**
     *
     * @param source1 Source1 image path
     * @param source2 Source2 image path
     * @param output Diff image path
     * @return Difference?
     */
    public static boolean pdiff(String source1, String source2, String output) {
        ImageCommand compare=new ImageCommand();
        compare.setCommand("compare");
        IMOperation op = new IMOperation();
        op.fuzz(15.0);
        op.metric("RMSE");
        op.highlightColor("Red");
        op.compose("src");
        op.addImage(source1);
        op.addImage(source2);
        op.addImage(output);

        try {
            compare.run(op);
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException" + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("IOException" + ex.getMessage());
            ex.printStackTrace();
        } catch (IM4JavaException ex) {
            // Exception if the images are different.
            return true;
        }
        return false;
    }
}
