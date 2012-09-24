package mx.doscerod.visualizadordirecciones;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<MyOverlayItem> {

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	private ArrayList<String> overlaysNames = new ArrayList<String>();
	private Context mContext;
	private int size;
	
	public String getOverlayString (int i) {
		OverlayItem ovr_tmp = mOverlays.get(i);
		return ovr_tmp.getPoint().getLatitudeE6() + "##" + ovr_tmp.getPoint().getLongitudeE6() + "##" + ovr_tmp.getSnippet();
	}
	
	public ArrayList<MyOverlayItem> getOverlays() {
		return mOverlays;
	}
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		size = 0;
		mContext = context;
	}
	
	
	@Override
	public boolean onTap(int index) {
		MyOverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	protected void addOverlay(MyOverlayItem overlay) {
		mOverlays.add(overlay);
		overlaysNames.add(overlay.getSnippet());
		populate();
		size++;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	protected MyOverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
