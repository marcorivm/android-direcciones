package mx.doscerod.visualizadordirecciones;

import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface; 
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/*
 * Author: Marco Rivadeneyra Morales - 1099497
 */

@TargetApi(9)
public class MapViewer extends MapActivity {
	private MyMapView mapView;
	private MyItemizedOverlay itemized_overlay;
	private List<Overlay> mapOverlays;
	private LinearLayout viewGroup;
	private View map_view;
	private View list_view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_viewer);
        viewGroup = (LinearLayout) findViewById(R.id.lienar_layout1);
        map_view = View.inflate(this, R.layout.map_viewer, null);
        list_view = View.inflate(this, R.layout.list_viewer, null);
        loadMapa();     
    }
    
    void saveData() { 
    	SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit(); 
    	StringBuilder sb = new StringBuilder();
    	int i;
    	for (i = 0; i < itemized_overlay.getSize(); i++) {
    		sb.append( ((i == 0) ? "" : ";") + itemized_overlay.getOverlayString(i));
    	}
    	spe.putString("direcciones", sb.toString());
    	spe.commit(); 
    }
    
	void loadData() {
	  SharedPreferences sp = getPreferences(MODE_PRIVATE);
	  String direccionList = sp.getString("direcciones", ""); 
	  String[] direcciones = direccionList.split(";");
	  for (String direccion : direcciones) {
      	String[] tmp = direccion.split("##");
      	GeoPoint geo_tmp = new GeoPoint(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
		MyOverlayItem ovr_tmp = new MyOverlayItem(geo_tmp, "Información", tmp[2]);
		itemized_overlay.addOverlay(ovr_tmp);
	  }
	  mapOverlays.add(itemized_overlay);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map_viewer, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mapa: {
            	loadMapa();
            	break;
            }
            case R.id.menu_lista: {
            	loadLista();
            	break;
            }
        }
        return true;
	}

	private void loadLista() {
		viewGroup.removeAllViews();
		viewGroup.addView(list_view);
		final ListView listView = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<MyOverlayItem> mAdaptaer = new ArrayAdapter<MyOverlayItem>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemized_overlay.getOverlays());
		listView.setAdapter(mAdaptaer);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View currView, int position, long id) {
			    MyOverlayItem selected = (MyOverlayItem) listView.getItemAtPosition(position);
			    loadMapa();
			    setMap(selected.getPoint());
			}
		});
	}
	
	private void setMap(GeoPoint location) {
		mapView.getController().setCenter(location);
	}
	
	private void loadMapa() {
		viewGroup.removeAllViews();
		viewGroup.addView(map_view);

        
        mapView = (MyMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        Drawable drawable = this.getResources().getDrawable(R.drawable.map_pin);
        mapOverlays = mapView.getOverlays();
        itemized_overlay = new MyItemizedOverlay(drawable, this);
        
        mapView.setOnLongpressListener(new MyMapView.OnLongpressListener() {
            public void onLongpress(final MapView view, final GeoPoint longpressLocation) {
                runOnUiThread(new Runnable() {
                public void run() {
                	AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                	final EditText input = new EditText(view.getContext());
                	dialog.setView(input);
            		dialog.setTitle("Quieres guardar esta posición?");
            		dialog.setMessage("Guardar: "+longpressLocation.toString());
            		
            		dialog.setPositiveButton("Guardar",  new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				  String value = input.getText().toString();
            				  if(value.isEmpty()) {
            					  AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            					  alert.setTitle("Error!").setMessage("Debes ingresar un nombre!").show(); 
            				  } else {
            					  MyOverlayItem overlayitem = new MyOverlayItem(longpressLocation, "Información", value);
            					  itemized_overlay.addOverlay(overlayitem);
            					  mapOverlays.add(itemized_overlay);
            					  saveData();
            					  mapView.postInvalidate();
            				  }
            			}
            		});
            		dialog.setNegativeButton("Cancelar",  new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				// Cancel
            			}
            		});
            		
            		dialog.show();
                }
            });
            }
        });
        loadData();
	}
}
