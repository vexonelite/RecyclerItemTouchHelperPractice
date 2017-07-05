package tw.com.google.pratice.recycleritemtouchhelperpart1;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base adapter that has some common features and is set to the Recyclerview
 *
 * Created by per-erik on 14/11/14.
 */
public abstract class AbstractRecycleListAdapter<V, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {

    private final byte[] mLock = new byte[0];
    private List<V> mData = new ArrayList<V>();
    private int mNumberOfTypes = 1;


    @Override
    public abstract K onCreateViewHolder(ViewGroup viewGroup, int i);

    @Override
    public abstract void onBindViewHolder(K k, int i);

    @Override
    public int getItemCount() {
        return getRealDataCount();
    }


    public int getNumberOfTypes () {
        return mNumberOfTypes;
    }

    public void setNumberOfTypes (int number) {
        if (number > 1) {
            mNumberOfTypes = number;
        }
    }


    public boolean isEmpty () {
        if (mNumberOfTypes == 1) {
            return (getRealDataCount() <= 0);
        }
        else if (mNumberOfTypes > 1) {
            return (getItemCount() <= 0);
        }
        else {
            return true;
        }
    }

    /** get number of data elements in the adapter, excluding headers and footers. */
    public int getRealDataCount () {
        if ( (null == mData) || (mData.isEmpty()) ) {
            return 0;
        }
        return mData.size();
    }

    /**
     * get the corresponding element in the data holder by the given position.
     * If it has not existed, 'null' will be return.
     */
    public V getObjectAtPosition (int position) {

        if ((null == mData) || (mData.isEmpty())
                || (position < 0) ||
                (position >= mData.size()) ) {
            return null;
        }
        return mData.get(position);
    }

    /** append the given list of data to the tail of data holder in the adapter. */
    public void appendNewDataSet(final List<V> data, boolean defaultNotify) {
        if ( (null == mData) || (null == data) ) {
            return;
        }
        final int size = data.size();
        //final int start = (mData.isEmpty()) ? 0 : (mData.size() - 1);
        final int start = mData.size();
        synchronized (mLock) {
            mData.addAll(data);
        }
        if (defaultNotify) {
            notifyItemRangeInserted(start, size);
        }
    }

    /** append the given list of data to the head of data holder in the adapter. */
    public void appendNewDataSetToTheTop (final List<V> data, boolean defaultNotify) {
        if ( (null == mData) || (null == data) ) {
            return;
        }
        final int size = data.size();
        //final int start = (mData.isEmpty()) ? 0 : (mData.size() - 1);
        final int start = 0;
        synchronized (mLock) {
            mData.addAll(start, data);
        }
        if (defaultNotify) {
            notifyItemRangeInserted(start, (size - 1));
        }
    }

    /** for each element of the given list, and append it to the tail of data holder in the adapter. */
    private void animatedAddNewDataSet(final List<V> data, boolean defaultNotify) {
        if ( (null == mData) || (null == data) ) {
            return;
        }
        int start = mData.size();
        for (int i = 0; i < data.size(); i++) {
            final V entity = data.get(i);
            if (!mData.contains(entity)) {
                appendNewDataAtPosition(start + i, entity, defaultNotify);
            }
        }
    }

    /** remove all data elements from the data holder in the adapter. */
    public void removeAllExistingData(boolean defaultNotify) {
        if ( (null == mData) || (mData.isEmpty()) ) {
            return;
        }
        final int size = mData.size();
        synchronized (mLock) {
            mData.clear();
        }
        if (defaultNotify) {
            notifyItemRangeRemoved(0, size);
        }
    }

    /** for each element of the given list, remove all data elements from the data holder in the adapter. */
    public void animatedRemoveAllExistingData(boolean defaultNotify) {
        if ( (null != mData) && (!mData.isEmpty()) ) {
            for (int i = mData.size() - 1; i >= 0; i--) {
                final V entity = mData.get(i);
                if (!mData.contains(entity)) {
                    removeData(i, defaultNotify);
                }
            }
        }
    }

    /** for each element of the data holder, remove it from the data holder in the adapter. */
    public void appendNewDataToTheEnd(V entity, boolean defaultNotify) {
        if ( (null == mData) || (null == entity) ) {
            return;
        }
        final int position = mData.size();
        synchronized (mLock) {
            mData.add(entity);
        }
        if (defaultNotify) {
            notifyItemInserted(position);
        }
    }

    /** add the given data to the specified position of data holder in the adapter. */
    public void appendNewDataAtPosition(int position, V entity, boolean defaultNotify) {
        if ( (null == mData) || (null == entity) ) {
            return;
        }
        synchronized (mLock) {
            mData.add(position, entity);
        }
        if (defaultNotify) {
            notifyItemInserted(position);
        }
    }


    public void appendNewDataAtPosition(int position, int positionOffset, V entity, boolean defaultNotify) {

        if (positionOffset <= 0) {
            appendNewDataAtPosition(position, entity, defaultNotify);
        } else {
            if ( (null == mData) || (null == entity) ) {
                return;
            }
            synchronized (mLock) {
                mData.add(position, entity);
            }
            if (defaultNotify) {
                notifyItemInserted(position + positionOffset);
            }
        }
    }

    /** remove the given data from the data holder in the adapter if it has existed. */
    public void removeSpecifiedData (V entity, boolean defaultNotify) {
        if ( (null == mData) || (mData.isEmpty())
                || (null == entity) ) {
            return;
        }
        final int position = mData.indexOf(entity);
        if (position < 0) {
            return;
        }
        removeData(position, defaultNotify);
    }

    /** remove the corresponding data from the data holder in the adapter by the given position. */
    protected void removeData(final int position, boolean defaultNotify) {
        synchronized (mLock) {
            mData.remove(position);
        }
        if (defaultNotify) {
            notifyItemRemoved(position);
        }
    }

    protected boolean isPositionValid (final int position) {
        if ( (null == mData) || (mData.isEmpty()) ) {
            return false;
        }
        return ( (position >= 0) && (position < mData.size()) );
    }

    /**
     * for each element of data list ,
     * move the object from the position 'fromPosition' to the position 'toPosition' in the data holder.
     */
    public void animateMoveNewDataSet(final List<V> data) {
        if (null == data) {
            return;
        }
        for (int toPosition = (data.size() - 1) ; toPosition >= 0; toPosition--) {
            final V entity = data.get(toPosition);
            final int fromPosition = mData.indexOf(entity);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveDataFromAtoB(fromPosition, toPosition);
            }
        }
    }

    /**
     * Move the object from the position 'fromPosition' to the position 'toPosition' in the data holder.
     */
    public void moveDataFromAtoB (final int fromPosition,
                                  final int toPosition) {

        if ( (null == mData) || (mData.isEmpty()) ) {
            return;
        }
        synchronized (mLock) {
            moveData(mData, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    /**
     * Move the object from the position 'a' to the position 'b' in the given data list.
     */
    private void moveData (List<V> data,
                           final int fromPosition,
                           final int toPosition) {

        V temp = data.remove(fromPosition);
        data.add(toPosition, temp);
    }

    protected void swapDataFromAtoB (int fromPosition, int toPosition) {
        if ( (null == mData) || (mData.isEmpty()) ) {
            return;
        }
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }



    /**
     * return a copy of data holder.
     */
    public List<V> CloneCurrentDataSet () {
        List<V> newData = new ArrayList<V>(mData);
        return newData;
    }

    public void setData(final List<V> data, boolean defaultNotify) {

        synchronized (mLock) {

            // Remove all deleted items.
            if ( (null != mData) && (!mData.isEmpty()) ) {
                for (int i = mData.size() - 1; i >= 0; --i) {
                    if (getLocation(data, mData.get(i)) < 0) {
                        removeData(i, defaultNotify);
                    }
                }
            }

            // Add and move items.
            for (int i = 0; i < data.size(); ++i) {
                V entity = data.get(i);
                int loc = getLocation(mData, entity);
                if (loc < 0) {
                    appendNewDataAtPosition(i, entity, false);
                } else if (loc != i) {
                    moveDataFromAtoB(i, loc);
                }
            }
        }
    }

    /**
     * get the corresponding position in the data list by the given data.
     * If it has not existed, value '-1' will be return.
     */
    private int getLocation(List<V> data, V entity) {

        if ( (null == data) || (data.isEmpty()) ||
                (null == entity) ) {
            return -1;
        }
        return data.indexOf(entity);
        //for (int j = 0; j < data.size(); ++j) {
        //    V newEntity = data.get(j);
        //    if (entity.equals(newEntity)) {
        //        return j;
        //    }
        //}
        //return -1;
    }

    /**
     * get the corresponding position in the data holder by the given data.
     * If it has not existed, value '-1' will be return.
     */
    public int getIndexOfObject(V entity) {
        if ( (null == mData) || (mData.isEmpty()) ) {
            return -1;
        }
        return mData.indexOf(entity);
    }
}
