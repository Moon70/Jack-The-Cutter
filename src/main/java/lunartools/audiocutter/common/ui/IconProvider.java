package lunartools.audiocutter.common.ui;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class IconProvider {

	public static FlatSVGIcon getFlatSvgIcon(Icons icon, JComponent component) {
		int size=getDefaultSize(component);
		FlatSVGIcon flatSVGIcon = new FlatSVGIcon(icon.getPath(), size, size,FlatSVGIcon.class.getClassLoader());
		flatSVGIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> UIManager.getColor("Label.foreground")));
		return flatSVGIcon;
	}

	private static int getDefaultSize(JComponent component) {
		if(component instanceof JMenuItem) {
			return 16;
		}else if(component instanceof JToolBar || component instanceof JButton) {
			return 24;
		}else {
			return 16;
		}
	}

	public static FlatSVGIcon getFlatSvgIcon(Icons icon, Class<?> clazz) {
		int size=getDefaultSize(clazz);
		FlatSVGIcon flatSVGIcon = new FlatSVGIcon(icon.getPath(), size, size,FlatSVGIcon.class.getClassLoader());
		flatSVGIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> UIManager.getColor("Label.foreground")));
		return flatSVGIcon;
	}

	private static int getDefaultSize(Class<?> clazz) {
		if(clazz == JMenuItem.class) {
			return 16;
		}else if(clazz == JToolBar.class || clazz == JButton.class || clazz == AbstractAction.class) {
			return 24;
		}else {
			return 16;
		}
	}
}
