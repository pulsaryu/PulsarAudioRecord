package me.yuxing.demo.audio;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuxing on 2/7/14.
 */
public class RecorderListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<File>> {

    private static final String TAG = "RecorderListFragment";
    private RecorderFileAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new RecorderFileAdapter(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);
        setListShown(false);

//        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                menu.add(0, 1, 0, "Delete");
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                return true;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//            }
//        });

        registerForContextMenu(getListView());

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new RecorderFileListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        mAdapter.clear();
        if (data != null) {
            mAdapter.addAll(data);
        }
        mAdapter.notifyDataSetChanged();
        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        File file = mAdapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "audio/amr");
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_audio_recorder_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        File file = mAdapter.getItem(info.position);
        deleteAudioFile(file);

        return true;
    }

    public void refresh() {
        Log.i(TAG, "refresh");
        getLoaderManager().restartLoader(0, null, this);
    }

    private void deleteAudioFile(final File file) {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_warning)
                .setMessage(R.string.message_confirm_delete)
                .setNegativeButton(R.string.button_no, null)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (file.isFile()) {
                            file.delete();
                            mAdapter.removeItem(file);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }

    private static class RecorderFileListLoader extends AsyncTaskLoader<List<File>> {

        public RecorderFileListLoader(Context context) {
            super(context);
        }

        @Override
        public List<File> loadInBackground() {

            List<File> entries = new ArrayList<File>();
            File audioDir = getContext().getExternalFilesDir("audio");
            File[] children = audioDir.listFiles();
            if (children != null) {
                entries.addAll(Arrays.asList(audioDir.listFiles()));
            }

            return entries;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
    }

    private static class RecorderFileAdapter extends BaseAdapter {

        private final Context mContext;
        private List<File> mFiles = new ArrayList<File>();

        private RecorderFileAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mFiles.size();
        }

        public void addAll(List<File> files) {
            mFiles.addAll(files);
        }

        public void removeItem(File item) {
            mFiles.remove(item);
        }

        public void clear() {
            mFiles.clear();
        }

        @Override
        public File getItem(int position) {
            return mFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            File file = mFiles.get(position);

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(file.getName());

            return convertView;
        }
    }
}
