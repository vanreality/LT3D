package com.lt3d.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lt3d.MainActivity;
import com.lt3d.R;
import com.lt3d.data.User;
import com.lt3d.tools.LocalDataProcessor;
import com.lt3d.tools.retrofit.Books;
import com.lt3d.tools.retrofit.Models;
import com.lt3d.tools.retrofit.Service;
import com.lt3d.tools.retrofit.ServiceFactory;
import com.lt3d.tools.retrofit.Users;
import com.lt3d.tools.touchHelper.ItemTouchHelperAdapter;
import com.lt3d.tools.touchHelper.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {
    EditText edt_search;
    User user;
    RecyclerView libraryRecyclerView;
    LibraryRecyclerViewAdapter libraryRecyclerViewAdapter;
    LibraryRecyclerViewModelAdapter libraryRecyclerViewModelAdapter;
    Service service;
    String userId;
    View view;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        user = ((MainActivity) context).getUser();
//        init();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, null);

        setHasOptionsMenu(true);
        init();
        return view;
    }

    private void init() {
        edt_search = view.findViewById(R.id.edt_library_search);
        libraryRecyclerView = view.findViewById(R.id.library_recyclerView);

        service = ServiceFactory.createService(
                LocalDataProcessor.readPreference(getActivity()).getUrl(),
                Service.class);
//        getCurrentUserId(user);
        recyclerViewConfig();
    }

    class DataEntity {
        private String bookName;
        private String id;

        DataEntity(String bookName, String id) {
            this.bookName = bookName;
            this.id = id;
        }

        String getBookName() {
            return bookName;
        }

        String getBookId() {
            return id;
        }
    }

    private void recyclerViewConfig() {

        libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter(new ArrayList<DataEntity>());
        libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        libraryRecyclerView.setAdapter(libraryRecyclerViewAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(libraryRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(libraryRecyclerView);

//        Call<Books> call = service.getBooks(user.getHash());
//
//        call.enqueue(new Callback<Books>() {
//            @Override
//            public void onResponse(Call<Books> call, Response<Books> response) {
//                if (response.isSuccessful()) {
//                    for (Books.ListsBean b : response.body().getLists()) {
//                        libraryRecyclerViewAdapter.addData(new DataEntity(b.getLabel(), b.getId()));
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Books> call, Throwable t) {
//
//            }
//        });
    }

    private void recyclerViewConfigModel(String hash,String id) {

        libraryRecyclerViewModelAdapter = new LibraryRecyclerViewModelAdapter(new ArrayList<DataEntity>());
        libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        libraryRecyclerView.setAdapter(libraryRecyclerViewModelAdapter);

//        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(libraryRecyclerViewAdapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
//        itemTouchHelper.attachToRecyclerView(libraryRecyclerView);

        Call<Models> call = service.getModels(hash, id);

        call.enqueue(new Callback<Models>() {
            @Override
            public void onResponse(Call<Models> call, Response<Models> response) {
                if (response.isSuccessful()) {
                    for (Models.ItemsBean b : response.body().getItems()) {
                        libraryRecyclerViewModelAdapter.addData(new DataEntity(b.getLabel(), b.getId()));
                    }
                }
            }

            @Override
            public void onFailure(Call<Models> call, Throwable t) {


            }
        });
    }

    private void getCurrentUserId(final User user) {
        Call<Users> call = service.getUsers(user.getHash());

        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    userId = response.body().getUserId(user.getPseudo());
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.library_actionbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.recyclerViewConfig();
        return super.onOptionsItemSelected(item);
    }

    class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.LibraryViewHolder> implements ItemTouchHelperAdapter {
        private final List<DataEntity> books;
        LibraryRecyclerViewAdapter(List<DataEntity> books) {
            this.books = books;
        }

        public void addData(DataEntity book) {
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
            holder.bind(books.get(position).getBookName());
        }

        @Override
        public int getItemCount() {
            return books == null ? 0 : books.size();
        }

        @Override
        public void onItemDissmiss(int postion) {
            //TODO delete function
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            //TODO move book function
        }

        class LibraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView textView;

            public LibraryViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.library_book);

                itemView.setOnClickListener(this);
            }

            void bind(String data) {
                textView.setText(data);
            }

            @Override
            public void onClick(View v) {
                //TODO onclick event to change the fragment content
                if(getAdapterPosition()!=RecyclerView.NO_POSITION){
                    recyclerViewConfigModel(user.getHash(),books.get(getAdapterPosition()).getBookId());
                }

            }
        }
    }
    class LibraryRecyclerViewModelAdapter extends RecyclerView.Adapter<LibraryRecyclerViewModelAdapter.LibraryViewModelHolder>implements ItemTouchHelperAdapter{
        private final List<DataEntity>models;

        LibraryRecyclerViewModelAdapter(List<DataEntity> models) {
            this.models = models;
        }
        public void addData(DataEntity model){
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
            holder.bind(models.get(position).getBookName());
        }

        @Override
        public int getItemCount() {
            return models==null?0:models.size();
        }

        @Override
        public void onItemDissmiss(int postion) {
            //TODO delete function
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            //TODO move models function
        }

        public class LibraryViewModelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView textView;
            public LibraryViewModelHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.library_model);
                itemView.setOnClickListener(this);
            }

            public void bind(String modelName) {
                textView.setText(modelName);
            }

            @Override
            public void onClick(View view) {

            }
        }
    }


}
