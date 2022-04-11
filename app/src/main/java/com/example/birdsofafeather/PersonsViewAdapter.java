package com.example.birdsofafeather;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Session;
import com.example.birdsofafeather.sorting.SortingStrategy;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.sorting.WavingSortStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PersonsViewAdapter extends RecyclerView.Adapter<PersonsViewAdapter.ViewHolder> {
    private List<BoF> persons;
    private List<BoF> favorite_persons;
    private List<BoF> all_persons;
    private SortingStrategy sortingStrategy;
    private WavingSortStrategy wavingSortStrategy;

    public PersonsViewAdapter(List<BoF> persons, SortingStrategy startingStrategy,
                              WavingSortStrategy wavingSortStrategy) {
        super();
        this.persons = persons;
        this.favorite_persons = new ArrayList<>();
        this.all_persons = persons;
        this.sortingStrategy = startingStrategy;
        this.wavingSortStrategy = wavingSortStrategy;
    }

    public List<BoF> getPersons() {
        return persons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.person_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPerson(persons.get(position));
        holder.setNumCourses(persons.get(position).getNumCoursesShared());
        holder.setHandVisibility(persons.get(position).getHasWaved() == 1);
        holder.setStarFilled(persons.get(position).getIsFavorite() == 1);
    }

    @Override
    public int getItemCount() {
        return this.persons.size();
    }

    public void addPerson(BoF person) {
        // Checks list to see if BoF is a duplicate
        for (int i = 0; i < persons.size(); i++) {
            BoF boF = persons.get(i);
            if (boF.getUserId() == person.getUserId()) {
                persons.remove(i);
                i--;
            }
        }

        // Already in the database
        persons.add(person);
        // For Dutta ______________________________________________ --> These are underscores
        this.reSort();
    }

    public void setSortingStrategy(SortingStrategy newStrategy) {
        this.sortingStrategy = newStrategy;
        this.reSort();
    }

    private void reSort() {
        sortingStrategy.sort(persons);
        wavingSortStrategy.sort(persons);
        this.notifyDataSetChanged();
    }

    public void clearBoFList() {
        persons.clear();
        favorite_persons.clear();
        all_persons.clear();

        this.notifyDataSetChanged();
    }

    public void switchFavoriteList(AppDatabase db) {
        if (this.persons == this.all_persons) {
            this.favorite_persons = new ArrayList<>();
            List<BoF> x = db.boFDao().getFavorites();
            for (BoF b: x) {
                this.favorite_persons.add(b);
            }
            this.persons = this.favorite_persons;
        } else if (this.persons == this.favorite_persons) {
            for (BoF b: this.all_persons) {
                b.setIsFavorite(db.boFDao().get(b.getUserId()).getIsFavorite());
            }
            this.persons = this.all_persons;
        }
        this.notifyDataSetChanged();
    }

    public void updateFavoriteList(AppDatabase db, String session_name) {
        this.favorite_persons = new ArrayList<>();
        List<BoF> x = db.boFDao().getFavorites();
        for (BoF b: x) {
            this.favorite_persons.add(b);
        }
        /***
        Session sn = db.sessionDao().getSessionByName(session_name);
        if (sn != null) {
            String[] ids = sn.getConcatIds().split(",");
            for(String id: ids) {
                BoF b = db.boFDao().get(Long.valueOf(id));
                if (b.getIsFavorite() == 1) {
                    this.favorite_persons.add(b);
                }
            }
        }***/

    }

//    private int getPositionToAdd(BoF person) {
//        int position = 0;
//        int numMatched = person.getNumCoursesShared();
//
//        for (int i = 0; i <= persons.size(); i++) {
//            if (i == persons.size()) {
//                position = i;
//            } else {
//                int currNumMatched = persons.get(i).getNumCoursesShared();
//                if (currNumMatched <= numMatched) return i;
//            }
//        }
//
//        return position;
//    }

    public void waveSent(BoF wavingBoF) {
        // Checks list to see if BoF exists
        int foundIndex = -1;
        for (int i = 0; i < persons.size(); i++) {
            BoF boF = persons.get(i);
            if (boF.getUserId() == wavingBoF.getUserId()) {
                foundIndex = i;
            }
        }

        if (foundIndex == -1) {
            return;
        }

        this.persons.get(foundIndex).setHasWaved(1);
        this.notifyItemChanged(foundIndex);
        this.reSort();
    }

    public void updateAllPersons(BoF bof, int fav, AppDatabase db, Session sn) {
        if (sn != null) {
            String[] ids = sn.getConcatIds().split(",");
            for(String id: ids) {
                BoF b = db.boFDao().get(Long.valueOf(id));
            }
        }
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final TextView personNameView;
        private final TextView courseNumberView;
        private final ImageView imageView;
        private final ImageView hand;
        private final ImageButton favoriteButton;
        private BoF person;



        ViewHolder(View itemView) {
            super(itemView);
            AppDatabase db = AppDatabase.singleton(itemView.getContext());
            this.personNameView = itemView.findViewById(R.id.person_row_name);
            this.courseNumberView = itemView.findViewById(R.id.num_course_view);
            this.imageView = itemView.findViewById(R.id.imageView2);
            this.hand = itemView.findViewById(R.id.waving_hand);
            this.favoriteButton = (ImageButton) itemView.findViewById(R.id.favorite_button);



            this.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (favoriteButton.getTag() == null) {
                        favoriteButton.setTag("off");
                        person.setIsFavorite(0);
                    }
                    if (favoriteButton.getTag() == "on"){
                        favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                        favoriteButton.setTag("off");
                        person.setIsFavorite(0);
                    } else if (favoriteButton.getTag() == "off"){
                        favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                        favoriteButton.setTag("on");
                        person.setIsFavorite(1);
                    }
                    db.boFDao().updateBoF(person);

                }
            });
            itemView.setOnClickListener(this);
        }



        public void setPerson(BoF person) {
            this.person = person;
            this.personNameView.setText(person.getName());
            String url = this.person.getProfileImgURL();
            if (url != null) {
                new PersonsViewAdapter.DownloadImageTask((ImageView) this.imageView)
                        .execute(url);
            } else {
                this.imageView.setImageDrawable(Drawable.createFromPath("@drawable/bof"));
            }
            if (this.person.getIsFavorite() == 1) {
                this.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                this.favoriteButton.setTag("on");
            }
            if (this.person.getIsFavorite() == 0) {
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                favoriteButton.setTag("off");
            }
        }


        public void setNumCourses(int numCourses) {
            this.courseNumberView.setText(numCourses + " Courses");
        }

        public void setHandVisibility(boolean visible) {
            if (visible) {
                this.hand.setVisibility(View.VISIBLE);
            } else {
                this.hand.setVisibility(View.INVISIBLE);
            }
        }

        public void setStarFilled(boolean isFavorite) {
            if (isFavorite) {
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                favoriteButton.setTag("on");
            } else {
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                favoriteButton.setTag("off");
            }
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PersonDetailActivity.class);
            intent.putExtra("person_id", this.person.getUserId());
            context.startActivity(intent);
        }
    }



    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            //Toast.makeText(EnterPhotoURL.this, urldisplay, Toast.LENGTH_SHORT).show();
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }

}
