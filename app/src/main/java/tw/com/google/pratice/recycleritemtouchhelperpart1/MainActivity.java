package tw.com.google.pratice.recycleritemtouchhelperpart1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private MyListAdapter mAdapter;

    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setupRecyclerView((RecyclerView) findViewById(R.id.recyclerView));
    }



    private void setupRecyclerView (RecyclerView recyclerView) {
        if (null == recyclerView) {
            return;
        }

        List<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            dataList.add(i + 1);
        }

        mAdapter = new MyListAdapter();
        ((MyListAdapter)mAdapter).appendNewDataSet(dataList, true);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter)mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private class MyListAdapter
            extends AbstractRecycleListAdapter<Integer, RecyclerView.ViewHolder>
            implements ItemTouchHelperAdapter {

        private boolean isDragging = false;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            return new MyListItem( inflater.inflate(
                    R.layout.recyclerview_item, parent, false) );
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MyListItem) {
                MyListItem cell = (MyListItem) viewHolder;
                cell.onBind(getObjectAtPosition(position), position);
            }
        }


        @Override
        public void onItemDismiss(int position) {
            if (isPositionValid(position)) {
                removeData(position, true);
            }
        }

        @Override
        public void onSelectedChanged(int actionState) {
            switch (actionState) {
                case ItemTouchHelper.ACTION_STATE_IDLE :{ // 0
                    Log.i("MyListItem", "onItemSelected: ACTION_STATE_IDLE");
                    if (isDragging) {
                        isDragging = false;
                        // time to save the order of passbook to the Data base or Shared Preference
                        doSaveOrderOfDataSet();
                    }
                    break;
                }
                case ItemTouchHelper.ACTION_STATE_SWIPE :{// 1
                    Log.i("MyListItem", "onItemSelected: ACTION_STATE_SWIPE");
                    break;
                }
                case ItemTouchHelper.ACTION_STATE_DRAG :{// 2
                    Log.i("MyListItem", "onItemSelected: ACTION_STATE_DRAG");
                    if (!isDragging) {
                        isDragging = true;
                    }
                    // time to save the order of passbook to the Data base or Shared Preference
                    break;
                }
                default: {
                    Log.i("MyListItem", "onItemSelected: " + actionState);
                    break;
                }
            }
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Log.i("MyListAdapter", "onItemMove: (" + fromPosition + ", " + toPosition + ")");
            swapDataFromAtoB(fromPosition, toPosition);
            return true;
        }
    }

    private class MyListItem extends RecyclerView.ViewHolder {

        private View mContainer;
        private TextView mLabelView;

        private MyListItem (View itemView) {
            super(itemView);
            //itemView.setBackgroundColor(mBackgroundColor);
            mContainer = itemView;
            mLabelView = (TextView) itemView.findViewById(R.id.label);
        }

        private void onBind (Integer item, int position) {
            resetItemViewLooks();
            if (null == item) {
                return;
            }

            String text = "";
            text = text + item;
            mLabelView.setText(text);
        }

        private void resetItemViewLooks () {
            mLabelView.setText("");
        }
    }

    private void doSaveOrderOfDataSet () {
        String text = "";
        for (Integer integer : mAdapter.CloneCurrentDataSet()) {
            text = text + integer + " ";
        }
        Log.i("MainActivity", "doSaveOrderOfDataSet: " + text);
    }
}
