package software.sufficient.onyx;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.onyx.android.sdk.data.note.TouchPoint;
import com.onyx.android.sdk.pen.RawInputCallback;
import com.onyx.android.sdk.pen.TouchHelper;
import com.onyx.android.sdk.pen.data.TouchPointList;
import java.util.ArrayList;

public class Onyx {

    private TouchHelper touchHelper;
    private View view;

    private float density;

    public float x;
    public float y;

    interface DrawingStartListener {
        void onDrawingStart(JSObject point);
    }

    private DrawingStartListener drawingStartListener;

    public void setDrawingStartListener(DrawingStartListener listener) {
        this.drawingStartListener = listener;
    }

    interface DrawingEndListener {
        void onDrawingEnd();
    }

    private DrawingEndListener drawingEndListener;

    public void setDrawingEndListener(DrawingEndListener listener) {
        this.drawingEndListener = listener;
    }

    interface DrawingStrokeListener {
        void onStroke(JSObject list);
    }

    private DrawingStrokeListener drawingStrokeListener;

    public void setDrawingStrokeListener(DrawingStrokeListener drawingStrokeListener) {
        this.drawingStrokeListener = drawingStrokeListener;
    }

    interface ErasingStartListener {
        void onErasingStart(JSObject point);
    }

    private ErasingStartListener erasingStartListener;

    public void setErasingStartListener(ErasingStartListener listener) {
        this.erasingStartListener = listener;
    }

    interface ErasingEndListener {
        void onErasingEnd();
    }

    private ErasingEndListener erasingEndListener;

    public void setErasingEndListener(ErasingEndListener listener) {
        this.erasingEndListener = listener;
    }

    interface ErasingStrokeListener {
        void onErase(JSObject list);
    }

    private ErasingStrokeListener erasingStrokeListener;

    public void setErasingStrokeListener(ErasingStrokeListener erasingStrokeListener) {
        this.erasingStrokeListener = erasingStrokeListener;
    }

    private final RawInputCallback callback = new RawInputCallback() {
        @Override
        public void onPenUpRefresh(RectF refreshRect) {
            super.onPenUpRefresh(refreshRect);
            touchHelper.setRawDrawingEnabled(false);
            touchHelper.setRawDrawingEnabled(true);
        }

        @Override
        public void onBeginRawDrawing(boolean b, TouchPoint touchPoint) {
            touchHelper.setRawDrawingRenderEnabled(true);
            drawingStartListener.onDrawingStart(touchPointToJSObject(touchPoint));
        }

        @Override
        public void onEndRawDrawing(boolean b, TouchPoint touchPoint) {
            drawingEndListener.onDrawingEnd();
        }

        @Override
        public void onRawDrawingTouchPointMoveReceived(TouchPoint touchPoint) {
            // Skip processing for performance
        }

        @Override
        public void onRawDrawingTouchPointListReceived(TouchPointList touchPointList) {
            JSObject stroke = new JSObject();
            stroke.put("points", touchpointListToJSArray(touchPointList));
            drawingStrokeListener.onStroke(stroke);
        }

        @Override
        public void onBeginRawErasing(boolean b, TouchPoint touchPoint) {
            erasingStartListener.onErasingStart(touchPointToJSObject(touchPoint));
        }

        @Override
        public void onEndRawErasing(boolean b, TouchPoint touchPoint) {
            erasingEndListener.onErasingEnd();
        }

        @Override
        public void onRawErasingTouchPointMoveReceived(TouchPoint touchPoint) {
            // Skip processing for performance
        }

        @Override
        public void onRawErasingTouchPointListReceived(TouchPointList touchPointList) {
            JSObject stroke = new JSObject();
            stroke.put("points", touchpointListToJSArray(touchPointList));
            erasingStrokeListener.onErase(stroke);
        }
    };

    public Onyx(View view, float density) {
        this.view = view;
        this.density = density;
        this.touchHelper = TouchHelper.create(this.view, this.callback);
        this.touchHelper.setStrokeWidth(4.0f);
        this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_PENCIL);
    }

    public void draw(float x, float y, float width, float height, Rect visibleRect) {
        this.x = x;
        this.y = y;

        Logger.info("✏️ Drawing start: (" + Float.toString(this.x) + ", " + Float.toString(this.y) + ")");

        Rect rect = new Rect(
            Math.round(x * this.density),
            Math.round(y * this.density),
            Math.round(width * this.density + x),
            Math.round(height * this.density + y)
        );
        rect.setIntersect(rect, visibleRect);

        Logger.info("✏️ RECT: " + rect.toString());

        ArrayList<Rect> excludes = new ArrayList<>();

        // For now, no interface for setting excluded rects
        this.touchHelper.setLimitRect(rect, excludes);
        this.touchHelper.openRawDrawing();
        this.touchHelper.setRawDrawingEnabled(true);
        this.touchHelper.setRawDrawingRenderEnabled(false);
    }

    public void stop() {
        this.touchHelper.setRawDrawingEnabled(false);
        this.touchHelper.closeRawDrawing();
    }

    public void setStrokeWidth(float width) {
        this.touchHelper.setStrokeWidth(width);
    }

    public void setStrokeStyle(String style) {
        switch (style) {
            case "pencil":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_PENCIL);
                break;
            case "fountain":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_FOUNTAIN);
                break;
            case "marker":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_MARKER);
                break;
            case "neo-brush":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_NEO_BRUSH);
                break;
            case "charcoal":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_CHARCOAL);
                break;
            case "dash":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_DASH);
                break;
            case "charcoal-v2":
                this.touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_CHARCOAL_V2);
                break;
            default:
                break;
        }
    }

    public JSObject touchPointToJSObject(TouchPoint touchPoint) {
        JSObject ret = new JSObject();
        ret.put("x", Math.round(((touchPoint.x / this.density) - this.x) * 10) / 10D);
        ret.put("y", Math.round(((touchPoint.y / this.density) - this.y) * 10) / 10D);
        ret.put("size", touchPoint.size);
        ret.put("pressure", touchPoint.pressure);
        ret.put("timestamp", touchPoint.timestamp);
        ret.put("tiltX", touchPoint.tiltX);
        ret.put("tiltY", touchPoint.tiltY);
        return ret;
    }

    private JSArray touchpointListToJSArray(TouchPointList list) {
        JSArray ret = new JSArray();
        for (int i = 0; i < list.size(); ++i) {
            ret.put(touchPointToJSObject(list.get(i)));
        }
        return ret;
    }
}
