package com.example.mindmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class DrawView extends View {
    private Paint paint = new Paint();

    private MindMapEditorActivity activity;

    public DrawView(MindMapEditorActivity context) {
        super(context);

        paint.setColor(Color.BLACK);

        activity = context;
    }

    private void drawConnections(Canvas canvas, NodeFragment fragment)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return;
        }

        int barsHeight = activity.getBarHseight();

        int[] parentLocation = new int[2];
        fragment.getView().getLocationInSurface(parentLocation);

        for (Node child : fragment.node.children)
        {
            int[] childLocation = new int[2];
            View childView = child.fragment.getView();
            if (childView == null)
            {
                continue;
            }

            childView.getLocationInSurface(childLocation);
            canvas.drawLine(parentLocation[0], parentLocation[1] - barsHeight, childLocation[0], childLocation[1] - barsHeight, paint);

            drawConnections(canvas, child.fragment);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawConnections(canvas, activity.getNodeFragments().get(0));
        invalidate();
    }

}