package com.lt3d.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lt3d.MainActivity;
import com.lt3d.R;
import com.lt3d.tools.touchHelper.ItemTouchHelperAdapter;
import com.lt3d.tools.touchHelper.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LibraryFragment extends Fragment {
    private EditText edt_search;
    private RecyclerView libraryRecyclerView;
    private LibraryRecyclerViewAdapter libraryRecyclerViewAdapter;
    private LibraryRecyclerViewModelAdapter libraryRecyclerViewModelAdapter;
    private View view;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private Menu myMenu;
    private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, container, false);
        init();
        return view;
    }

    private void init() {
        edt_search = view.findViewById(R.id.edt_library_search);
        libraryRecyclerView = view.findViewById(R.id.library_recyclerView);
        mainActivity = (MainActivity) getActivity();

        setHasOptionsMenu(true);
        recyclerViewConfig();
    }

    class DataEntity {
        private String label;
        private String id;

        DataEntity(String label, String id) {
            this.label = label;
            this.id = id;
        }

        String getLabel() {
            return label;
        }

        String getId() {
            return id;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        myMenu = menu;
        inflater.inflate(R.menu.library_actionbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                recyclerViewConfig();
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMenu(){
        myMenu.getItem(0).setVisible(true);
    }

    private void recyclerViewConfig() {
        libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter(new ArrayList<DataEntity>());
        libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        libraryRecyclerView.setAdapter(libraryRecyclerViewAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(libraryRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(libraryRecyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("books");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataEntity tmp;
                String bid;
                HashMap bookData = (HashMap) dataSnapshot.getValue();
                if (bookData == null) { return; }

                for (int i = 1; i < bookData.size() + 1; i++) {
                    bid = "bk" + i;
                    if (bookData.get(bid) != null) {
                        tmp = new DataEntity(Objects.requireNonNull(bookData.get(bid)).toString(), bid);
                        libraryRecyclerViewAdapter.addData(tmp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    private void recyclerViewConfigModel(String bid) {
        libraryRecyclerViewModelAdapter = new LibraryRecyclerViewModelAdapter(new ArrayList<DataEntity>());
        libraryRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        libraryRecyclerView.setAdapter(libraryRecyclerViewModelAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(libraryRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(libraryRecyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("models/" + bid);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataEntity tmp;
                String mid;
                HashMap modelData = (HashMap) dataSnapshot.getValue();
                if (modelData == null) { return; }

                for (int i = 1; i < modelData.size() + 1; i++) {
                    mid = "md" + i;
                    if (modelData.get(mid) != null) {
                        tmp = new DataEntity(((HashMap) modelData.get(mid)).get("title").toString(), mid);
                        libraryRecyclerViewModelAdapter.addData(tmp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    class LibraryRecyclerViewAdapter
            extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.LibraryViewHolder>
            implements ItemTouchHelperAdapter {

        private final List<DataEntity> books;
        LibraryRecyclerViewAdapter(List<DataEntity> books) {
            this.books = books;
        }

        void addData(DataEntity book) {
            books.add(book);
            notifyItemInserted(books.size());
        }

        @NonNull
        @Override
        public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.book,parent,false);
            return new LibraryRecyclerViewAdapter.LibraryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
            holder.bind(books.get(position).getLabel());
        }

        @Override
        public int getItemCount() {
            return books == null ? 0 : books.size();
        }

        @Override
        public void onItemDissmiss(int position) {
            books.remove(position);
            notifyItemRemoved(position);

            //TODO delete bid of the current user from firebase
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            DataEntity tmp = books.get(fromPosition);
            books.remove(fromPosition);
            books.add(toPosition > fromPosition ? toPosition - 1 : toPosition, tmp);
            notifyItemMoved(fromPosition,toPosition);
        }

        class LibraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView textView;

            LibraryViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.library_book);

                itemView.setOnClickListener(this);
            }

            void bind(String data) {
                textView.setText(data);
            }

            @Override
            public void onClick(View v) {
                if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                    recyclerViewConfigModel(books.get(getAdapterPosition()).getId());
                    showMenu();
                    mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }


    class LibraryRecyclerViewModelAdapter
            extends RecyclerView.Adapter<LibraryRecyclerViewModelAdapter.LibraryViewModelHolder>
            implements ItemTouchHelperAdapter{
        private final List<DataEntity>models;

        LibraryRecyclerViewModelAdapter(List<DataEntity> models) {
            this.models = models;
        }
        void addData(DataEntity model){
            models.add(model);
            notifyItemInserted(models.size());
        }

        @NonNull
        @Override
        public LibraryRecyclerViewModelAdapter.LibraryViewModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.model,parent,false);
            return new LibraryRecyclerViewModelAdapter.LibraryViewModelHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LibraryViewModelHolder holder, int position) {
            holder.bind(models.get(position).getLabel());
        }

        @Override
        public int getItemCount() {
            return models==null?0:models.size();
        }

        @Override
        public void onItemDissmiss(int position) {
            models.remove(position);
            notifyItemRemoved(position);

            //TODO delete mid of the current user in firebase
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            DataEntity tmp = models.get(fromPosition);
            models.remove(fromPosition);
            models.add(toPosition > fromPosition ? toPosition - 1 : toPosition, tmp);
            notifyItemMoved(fromPosition,toPosition);
        }

        public class LibraryViewModelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView textView;
            LibraryViewModelHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.library_model);
                itemView.setOnClickListener(this);
            }

            void bind(String modelName) {
                textView.setText(modelName);
            }

            @Override
            public void onClick(View view) {
                //TODO Open sceneForm fragment
            }
        }
    }
}
