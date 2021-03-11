package com.wonshinhyo.dragrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Shinhyo
 */

public class DragRecyclerView extends RecyclerView implements ImpRecycleView {
    private ItemTouchHelper mItemTouchHelper;

    private int mHandleId = -1;
    private DragTouchCallback mTouchHelperCallback;

    public DragRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttrs(context, attrs);
    }

    public DragRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttrs(context, attrs);
    }

    private void setAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Drag);
        mHandleId = a.getResourceId(R.styleable.Drag_handle_id, -1);
        a.recycle();
    }

    ImpAdapter getDragAdapter() {
        return (ImpAdapter) getAdapter();
    }

    @Override
    public void setAdapter(DragRecyclerView.Adapter adapter) {
        super.setAdapter(adapter);

        getDragAdapter().setHandleId(mHandleId);

        mTouchHelperCallback = new DragTouchCallback((com.wonshinhyo.dragrecyclerview.OnDragListener) super.getAdapter());
        mItemTouchHelper = new ItemTouchHelper(mTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(this);
        getDragAdapter().setRecycleView(this);
    }

    public DragTouchCallback getTouchHelperCallback() {
        return mTouchHelperCallback;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return mItemTouchHelper;
    }

}
