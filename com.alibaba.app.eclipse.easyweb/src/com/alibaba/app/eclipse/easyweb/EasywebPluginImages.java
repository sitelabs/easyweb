package com.alibaba.app.eclipse.easyweb;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Bundle of images used by Velocity plugin.
 */
public class EasywebPluginImages {

    private static URL                  fgIconBaseURL  = null;
    // Determine display depth. If depth > 4 then we use high color images.
    // Otherwise low color images are used
    static {
        // Don't consider the default display since accessing it throws an
        // SWTException anyway.
        Display display = Display.getCurrent();
        String pathSuffix = "icons/";
        try {
            URL url = EasywebPlugin.getDefault().getBundle().getEntry("/");
            fgIconBaseURL = new URL(EasywebPlugin.getDefault().getBundle().getEntry("/"), pathSuffix);
        } catch (MalformedURLException e) {
            EasywebPlugin.log(e);
        }
    }
    // The plugin's image registry
    private static final ImageRegistry  IMAGE_REGISTRY = EasywebPlugin.getDefault().getImageRegistry();
    // Set of predefined Image Descriptors
    private static final String         T_OBJ          = "";
    // private static final String T_CLCL = "clcl16";
    // private static final String T_CTOOL = "ctool16";
    // Define image names
    public static final String          IMG_EW         = "ew.jpg";
    // Define image descriptors
    public static final ImageDescriptor EW             = createManaged(T_OBJ, IMG_EW);

    public static final ImageDescriptor CHECKED        = createManaged(T_OBJ, "checked.gif");
    public static final ImageDescriptor UNCHECKED      = createManaged(T_OBJ, "unchecked.gif");

    /**
     * Returns the image managed under the given key in this registry.
     * 
     * @param aKey the image's key
     * @return the image managed under the given key
     */
    public static Image get(String aKey) {
        return IMAGE_REGISTRY.get(aKey);
    }

    private static ImageDescriptor createManaged(String aPrefix, String aName) {
        ImageDescriptor result = create(aPrefix, aName);
        IMAGE_REGISTRY.put(aName, result);
        return result;
    }

    private static ImageDescriptor create(String aPrefix, String aName) {
        ImageDescriptor result;
        try {
            result = ImageDescriptor.createFromURL(makeIconFileURL(aPrefix, aName));
        } catch (MalformedURLException e) {
            result = ImageDescriptor.getMissingImageDescriptor();
        }
        return result;
    }

    private static URL makeIconFileURL(String aPrefix, String aName) throws MalformedURLException {
        if (fgIconBaseURL == null) {
            throw new MalformedURLException();
        }
        StringBuffer buffer = new StringBuffer(aPrefix);
        buffer.append('/');
        buffer.append(aName);
        return new URL(fgIconBaseURL, buffer.toString());
    }
}
