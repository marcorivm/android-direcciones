package mx.doscerod.visualizadordirecciones;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	public MyOverlayItem(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}
	
	@Override
	public String toString() {
		return this.getSnippet();
	}

}
