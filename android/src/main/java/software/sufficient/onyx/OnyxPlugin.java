package software.sufficient.onyx;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import com.getcapacitor.JSObject;
import com.getcapacitor.JSValue;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.onyx.android.sdk.data.note.TouchPoint;

@CapacitorPlugin(name = "Onyx")
public class OnyxPlugin extends Plugin {

    private Onyx implementation;

    @Override
    public void load() {
        DisplayMetrics metrics = this.getContext().getApplicationContext().getResources().getDisplayMetrics();
        implementation = new Onyx(this.getBridge().getWebView(), metrics.density);
        implementation.setDrawingStrokeListener(this::notifyOnStroke);
        implementation.setDrawingEndListener(this::notifyOnDrawingEnd);
        implementation.setDrawingStartListener(this::notifyOnDrawingStart);
        implementation.setErasingStrokeListener(this::notifyOnErase);
        implementation.setErasingStartListener(this::notifyOnErasingStart);
        implementation.setErasingEndListener(this::notifyOnErasingEnd);
    }

    @PluginMethod
    public void start(PluginCall call) {
        Float x = call.getFloat("x");
        Float y = call.getFloat("y");
        Float width = call.getFloat("width");
        Float height = call.getFloat("height");
        Rect rect = new Rect();
        this.bridge.getWebView().getLocalVisibleRect(rect);
        implementation.draw(x, y, width, height, rect);
        call.resolve();
    }

    @PluginMethod
    public void stop(PluginCall call) {
        implementation.stop();
        call.resolve();
    }

    @PluginMethod
    public void configureStroke(PluginCall call) {
        if (call.hasOption("width")) {
            Float width = call.getFloat("width");
            implementation.setStrokeWidth(width);
        }
        if (call.hasOption("style")) {
            String style = call.getString("style");
            implementation.setStrokeStyle(style);
        }
        call.resolve();
    }

    private void notifyOnStroke(JSObject stroke) {
        if (hasListeners("onStroke")) {
            notifyListeners("onStroke", stroke);
        }
    }

    private void notifyOnDrawingStart(JSObject point) {
        if (hasListeners("onDrawingStart")) {
            notifyListeners("onDrawingStart", point);
        }
    }

    private void notifyOnDrawingEnd() {
        if (hasListeners("onDrawingEnd")) {
            notifyListeners("onDrawingEnd", null);
        }
    }

    private void notifyOnErase(JSObject stroke) {
        if (hasListeners("onErase")) {
            notifyListeners("onErase", stroke);
        }
    }

    private void notifyOnErasingStart(JSObject point) {
        if (hasListeners("onErasingStart")) {
            notifyListeners("onErasingStart", point);
        }
    }

    private void notifyOnErasingEnd() {
        if (hasListeners("onErasingEnd")) {
            notifyListeners("onErasingEnd", null);
        }
    }
}
