package com.lt3d.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.lt3d.data.Book;
import com.lt3d.data.Books;
import com.lt3d.data.Models;
import com.lt3d.data.User;
import com.lt3d.tools.touchHelper.ItemTouchHelperAdapter;
import com.lt3d.tools.touchHelper.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

//3343
public class LibraryFragment extends Fragment {
    private EditText edt_search;
    private RecyclerView libraryRecyclerView;
    private LibraryRecyclerViewAdapter libraryRecyclerViewAdapter;
    private LibraryRecyclerViewModelAdapter libraryRecyclerViewModelAdapter;
    private LibraryRecyclerViewAddBookAdapter libraryRecyclerViewAddBookAdapter;

    private View view;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private Menu myMenu;
    private MainActivity mainActivity;

    private Books books;
    private Models models;

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

        libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter(new ArrayList<>());

        recyclerViewConfigBook();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                libraryRecyclerViewAdapter.search(sequence);
                if(libraryRecyclerViewModelAdapter != null)
                libraryRecyclerViewModelAdapter.search(sequence);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                recyclerViewConfigBook();
                edt_search.setText("");
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            case R.id.menu_add:
                recyclerViewConfigAddBook();
                edt_search.setText("");
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                return true;
            case R.id.menu_sortAZ:
                if(libraryRecyclerViewModelAdapter!=null)
                    libraryRecyclerViewModelAdapter.sortModelAZ();
                if (libraryRecyclerViewAddBookAdapter!=null)
                    libraryRecyclerViewAddBookAdapter.sortBookAZ();
                libraryRecyclerViewAdapter.sortBookAZ();
                return true;
            case R.id.menu_sortZA:
                if(libraryRecyclerViewModelAdapter!=null)
                    libraryRecyclerViewModelAdapter.sortModelZA();
                if(libraryRecyclerViewAddBookAdapter!=null)
                    libraryRecyclerViewAddBookAdapter.sortBookZA();
                libraryRecyclerViewAdapter.sortBookZA();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMenu(){
        myMenu.getItem(0).setVisible(true);
    }

    private void recyclerViewConfigBook() {
        libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        libraryRecyclerView.setAdapter(libraryRecyclerViewAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(libraryRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(libraryRecyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("books");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                books = dataSnapshot.getValue(Books.class);
                int id = 0;
                User user = mainActivity.getUser();
                if (user.library.isEmpty()) return;
                
                for (Book b : books.books) {
                    if (user.library.contains(String.valueOf(id))) {
                        libraryRecyclerViewAdapter.addData(new DataEntity(b.title, String.valueOf(id)));
                    }
                    id++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    private void recyclerViewConfigModel(String bid) {
        libraryRecyclerViewModelAdapter = new LibraryRecyclerViewModelAdapter(new ArrayList<>());
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
                List<HashMap> modelData = (ArrayList) dataSnapshot.getValue();
                if (modelData == null) { return; }

                for (int i = 0; i < modelData.size(); i++) {
                    tmp = new DataEntity(modelData.get(i).get("title").toString(), String.valueOf(i));
                    libraryRecyclerViewModelAdapter.addData(tmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }
    
    private void recyclerViewConfigAddBook() {
        libraryRecyclerViewAddBookAdapter = new LibraryRecyclerViewAddBookAdapter(new ArrayList<>());
        libraryRecyclerView.setAdapter(libraryRecyclerViewAddBookAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(libraryRecyclerViewAddBookAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(libraryRecyclerView);

        int id = 0;
        for (Book b : books.books) {
            if (!mainActivity.getUser().library.contains(String.valueOf(id))) {
                libraryRecyclerViewAddBookAdapter.addData(new DataEntity(b.title, String.valueOf(id)));
            }
            id++;
        }
    }

    class LibraryRecyclerViewAdapter
            extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.LibraryViewHolder>
            implements ItemTouchHelperAdapter {

        private List<DataEntity> books;
        private List<DataEntity> books_tmp;

        LibraryRecyclerViewAdapter(List<DataEntity> books) {
            this.books = books;
            this.books_tmp = books;
        }

        List<DataEntity> getBooks(){
            return this.books;
        }

        void addData(DataEntity book) {
            Boolean flag = true;
            for (DataEntity d : books) {
                if (book.getId().equals(d.getId())) {
                    flag = false;
                }
            }
            if (flag) {
                books.add(book);
                notifyItemInserted(books.size());
            }
        }

        public void sortBookAZ(){
            Collections.sort(books, new Comparator<DataEntity>() {
                @Override
                public int compare(DataEntity dataEntity, DataEntity t1) {
                    return dataEntity.getLabel().compareTo(t1.getLabel());
                }
            });notifyDataSetChanged();
        }

        public void sortBookZA(){
            Collections.sort(books, new Comparator<DataEntity>() {
                @Override
                public int compare(DataEntity dataEntity, DataEntity t1) {
                    return t1.getLabel().compareTo(dataEntity.getLabel());
                }
            });notifyDataSetChanged();
        }

        public void search(CharSequence sequence){
            books = books_tmp;
            List<DataEntity> mbooks =new ArrayList<>();
            String charString = sequence.toString();
            if (charString.isEmpty()) {
                mbooks = libraryRecyclerViewAdapter.getBooks();
            } else {
                mbooks.clear();
                List<DataEntity> books_search = new ArrayList<>();
                for(DataEntity book  : libraryRecyclerViewAdapter.getBooks()){
                    if(book.getLabel().toUpperCase().contains(charString.toUpperCase())){
                        books_search.add(book);
                    }
                }
                mbooks= books_search;
            }
            books=mbooks;
            notifyDataSetChanged();
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
            mainActivity.getUser().library.remove(books.get(position).id);

            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(mainActivity.getCurrentUser().getUid());
            databaseReference.setValue(mainActivity.getUser());

            books.remove(position);
            notifyItemRemoved(position);
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
                    edt_search.setText("");
                    mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    class LibraryRecyclerViewModelAdapter
            extends RecyclerView.Adapter<LibraryRecyclerViewModelAdapter.LibraryViewModelHolder>
            implements ItemTouchHelperAdapter{
        private List<DataEntity>models;
        private List<DataEntity>models_tmp;

        LibraryRecyclerViewModelAdapter(List<DataEntity> models) {
            this.models = models;
            this.models_tmp = models;
        }

        void addData(DataEntity model){
            models.add(model);
            notifyItemInserted(models.size());
        }
        public void sortModelAZ(){
            Collections.sort(models, new Comparator<DataEntity>() {
                @Override
                public int compare(DataEntity dataEntity, DataEntity t1) {
                    return dataEntity.getLabel().compareTo(t1.getLabel());
                }
            });notifyDataSetChanged();
        }
        public void sortModelZA(){
            Collections.sort(models, new Comparator<DataEntity>() {
                @Override
                public int compare(DataEntity dataEntity, DataEntity t1) {
                    return t1.getLabel().compareTo(dataEntity.getLabel());
                }
            });notifyDataSetChanged();
        }

        public void search(CharSequence sequence){
            models = models_tmp;
            List<DataEntity> mModels =new ArrayList<>();
            String charString = sequence.toString();
            if (charString.isEmpty()) {
                mModels = models;
            } else {
                mModels.clear();
                List<DataEntity> models_search = new ArrayList<>();
                for(DataEntity model  : models){
                    if(model.getLabel().toUpperCase().contains(charString.toUpperCase())){
                        models_search.add(model);
                    }
                }
                mModels= models_search;
            }
            models=mModels;
            notifyDataSetChanged();
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
            return models == null ? 0 : models.size();
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
                Intent i=new Intent();
                i.setClass(getActivity(), com.lt3d.ModelActivity.class);
                i.putExtra("modelName",models.get(getAdapterPosition()).getLabel());
                startActivity(i);


            }
        }
    }

    class LibraryRecyclerViewAddBookAdapter
            extends RecyclerView.Adapter<LibraryRecyclerViewAddBookAdapter.LibraryViewHolder>
            implements ItemTouchHelperAdapter {

        private List<DataEntity> books;
        private List<DataEntity> books_tmp;

        LibraryRecyclerViewAddBookAdapter(List<DataEntity> books) {
            this.books = books;
            this.books_tmp = books;
        }

        List<DataEntity> getBooks(){
            return this.books;
        }

        void addData(DataEntity book) {
            books.add(book);
            notifyItemInserted(books.size());
        }

        public void sortBookAZ(){
            Collections.sort(books, new Comparator<DataEntity>() {
                @Override
                public int compare(DataEntity dataEntity, DataEntity t1) {
                    return dataEntity.getLabel().compareTo(t1.getLabel());
                }
            });notifyDataSetChanged();
        }

        public void sortBookZA(){
            Collections.sort(books, new Comparator<DataEntity>() {
                @Override
                public int compare(DataEntity dataEntity, DataEntity t1) {
                    return t1.getLabel().compareTo(dataEntity.getLabel());
                }
            });notifyDataSetChanged();
        }

        public void search(CharSequence sequence){
            books = books_tmp;
            List<DataEntity> mbooks =new ArrayList<>();
            String charString = sequence.toString();
            if (charString.isEmpty()) {
                mbooks = libraryRecyclerViewAdapter.getBooks();
            } else {
                mbooks.clear();
                List<DataEntity> books_search = new ArrayList<>();
                for(DataEntity book  : libraryRecyclerViewAdapter.getBooks()){
                    if(book.getLabel().toUpperCase().contains(charString.toUpperCase())){
                        books_search.add(book);
                    }
                }
                mbooks= books_search;
            }
            books=mbooks;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.book,parent,false);
            return new LibraryRecyclerViewAddBookAdapter.LibraryViewHolder(view);
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
                    mainActivity.getUser().library.add(books.get(getAdapterPosition()).getId());
                    databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(mainActivity.getCurrentUser().getUid());
                    databaseReference.setValue(mainActivity.getUser());

                    showMenu();
                    mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    edt_search.setText("");
                    recyclerViewConfigBook();
                }
            }
        }
    }
}
