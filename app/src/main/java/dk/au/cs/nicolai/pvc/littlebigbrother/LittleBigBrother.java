package dk.au.cs.nicolai.pvc.littlebigbrother;

/**
 * <p>Main class for methods and constants specific to the Little Big Brother application.</p>
 */
public class LittleBigBrother {
    public static final String ID_PREFIX = "dk.au.cs.nicolai.pvc.littlebigbrother";
    public static final float DEFAULT_ZOOM_LEVEL = 14;

    private LittleBigBrother() {

    }

    /**
     * <p>Constants used for identifying message types used for interfacing between activities in
     * the Little Big Brother application.</p>
     *
     * <p>Most commonly used for communicating via {@link android.content.Intent} objects when switching
     * activity.</p>
     */
    public static class MessageType {
        public static final String MAP_CAMERA_POSITION = LittleBigBrother.ID_PREFIX + "MAP_CAMERA_POSITION";

        private MessageType() {}
    }
}
