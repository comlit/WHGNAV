package com.lit.whgnav;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.util.ArrayList;

public class CustomView extends View
{
    public ArrayList<int[]> ug1 = new ArrayList<>();
    public ArrayList<int[]> eg = new ArrayList<>();
    public ArrayList<int[]> og1 = new ArrayList<>();
    public ArrayList<int[]> og2 = new ArrayList<>();
    public ArrayList<int[]> current = new ArrayList<>();
    private int[] startEndFloor = new int[2];
    public int floor;
    public double factor;


    public CustomView(Context context)
    {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = (int)(getHeight()*1.5);
        factor = getHeight()*1.5/1350;
        setLayoutParams(layoutParams);
        Paint cPaint = new Paint();
        Paint zPaint = new Paint(Color.BLUE);
        cPaint.setColor(Color.GREEN);
        Paint lPaint = new Paint();
        lPaint.setColor(Color.RED);
        lPaint.setStrokeWidth(5);
        lPaint.setFlags(Paint.ANTI_ALIAS_FLAG);


        if(current.size()>=2)
        {
            for(int i=0;current.size()-1>i;i++)
            {
                if(current.get(i)!=null && current.get(i+1)!=null)
                {
                    canvas.drawLine((int)(current.get(i)[0]*factor), (int)(current.get(i)[1]*factor), (int)(current.get(i + 1)[0]*factor), (int)(current.get(i + 1)[1]*factor), lPaint);
                }
            }

        }
            if (current == og2) {
                setBackgroundResource(R.drawable.og2);
            } else if (current == og1) {
                setBackgroundResource(R.drawable.og1);
            } else if (current == eg) {
                setBackgroundResource(R.drawable.eg);
            } else if (current == ug1) {
                setBackgroundResource(R.drawable.ug1);
            }
        if(current.size()>=1)
        {
            if (current == og2) {
                if (startEndFloor[0] == 3) {
                    canvas.drawCircle((int)(current.get(0)[0]*factor), (int)(current.get(0)[1]*factor), 10, cPaint);
                }
                if (startEndFloor[1] == 3) {
                    canvas.drawCircle((int)(current.get(current.size() - 1)[0]*factor), (int)(current.get(current.size() - 1)[1]*factor), 10, zPaint);
                }
            } else if (current == og1) {
                if (startEndFloor[0] == 2) {
                    canvas.drawCircle((int)(current.get(0)[0]*factor), (int)(current.get(0)[1]*factor), 10, cPaint);
                }
                if (startEndFloor[1] == 2) {
                    canvas.drawCircle((int)(current.get(current.size() - 1)[0]*factor), (int)(current.get(current.size() - 1)[1]*factor), 10, zPaint);
                }
            } else if (current == eg) {
                if (startEndFloor[0] == 1) {
                    canvas.drawCircle((int)(current.get(0)[0]*factor), (int)(current.get(0)[1]*factor), 10, cPaint);
                }
                if (startEndFloor[1] == 1) {
                    canvas.drawCircle((int)(current.get(current.size() - 1)[0]*factor), (int)(current.get(current.size() - 1)[1]*factor), 10, zPaint);
                }
            } else if (current == ug1) {
                if (startEndFloor[0] == 0) {
                    canvas.drawCircle((int)(current.get(0)[0]*factor), (int)(current.get(0)[1]*factor), 10, cPaint);
                }
                if (startEndFloor[1] == 0) {
                    canvas.drawCircle((int)(current.get(current.size() - 1)[0]*factor), (int)(current.get(current.size() - 1)[1]*factor), 10, zPaint);
                }
            }

            for(int i=0;current.size()>i;i++)
            {
                if(current.get(i)!=null)
                {
                    if(current.get(i)[2] == 0)
                    {
                        canvas.drawCircle((int) (current.get(i)[0] * factor), (int) (current.get(i)[1] * factor), 3, lPaint);
                    }
                    else if(current.get(i)[2] == 1)
                    {
                        drawTriangleDown(canvas, cPaint, (int) (current.get(i)[0] * factor), (int) (current.get(i)[1] * factor), 25);
                    }
                    else
                    {
                        drawTriangle(canvas, cPaint, (int) (current.get(i)[0] * factor), (int) (current.get(i)[1] * factor), 25);
                    }
                }
            }
        }
    }

    public void setxyPos(ArrayList<int[]> pUg, ArrayList<int[]> pEg, ArrayList<int[]> pOg1, ArrayList<int[]> pOg2, int[] pStartEndFloor)
    {
        og2 = pOg2;
        og1 = pOg1;
        eg = pEg;
        ug1 = pUg;
        startEndFloor = pStartEndFloor;
        invalidate();
    }

    public void floorChoice(int pFloor)
    {
        floor = pFloor;

        switch (floor)
        {
            case(3):
                current = og2;
                break;

            case(2):
                current = og1;
                break;

            case(1):
                current = eg;
                break;

            case(0):
                current = ug1;
                break;
        }
        invalidate();
    }

    public void drawTriangle(Canvas canvas, Paint paint, int x, int y, int width)
    {
        int halfWidth = width / 2;

        Path path = new Path();
        path.moveTo(x, y - halfWidth); // Top
        path.lineTo(x - halfWidth, y + halfWidth); // Bottom left
        path.lineTo(x - halfWidth/2, y + halfWidth);
        path.lineTo(x - halfWidth/2, y + halfWidth*2);
        path.lineTo(x + halfWidth/2, y + halfWidth*2);
        path.lineTo(x + halfWidth/2, y + halfWidth);
        path.lineTo(x + halfWidth, y + halfWidth); // Bottom right
        path.lineTo(x, y - halfWidth); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }
    public void drawTriangleDown(Canvas canvas, Paint paint, int x, int y, int width)
    {
        int halfWidth = width / 2;

        Path path = new Path();
        path.moveTo(x, y + halfWidth); // Top
        path.lineTo(x - halfWidth, y - halfWidth); // Bottom left
        path.lineTo(x - halfWidth/2, y - halfWidth);
        path.lineTo(x - halfWidth/2, y - halfWidth*2);
        path.lineTo(x + halfWidth/2, y - halfWidth*2);
        path.lineTo(x + halfWidth/2, y - halfWidth);
        path.lineTo(x + halfWidth, y - halfWidth); // Bottom right
        path.lineTo(x, y + halfWidth); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }
    //TODO: NN dreicke in alle richtungen für weglinie

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        return false;
    }
}
