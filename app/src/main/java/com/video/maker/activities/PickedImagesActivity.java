package com.video.maker.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.video.imagepicker.activity.ImagePickerActivity;
import com.video.maker.util.AdManager;
import com.video.photoeditor.activity.EditPhotoActivity;
import com.video.maker.R;
import com.video.maker.adapter.PhotoAdapter;
import com.video.maker.listeners.ItemClickListener;
import com.video.maker.model.Photo;
import com.video.maker.view.RoundRectView;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.io.File;
import java.util.ArrayList;

public class PickedImagesActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout btnAddPhoto;
    private ImageView btnEditPhoto, btnAddMore;
    private CardView btnMovie;
    private RecyclerViewDragDropManager dragMgr;

    public ImageView imgPhoto;

    public ArrayList<Photo> listPhoto = new ArrayList<>();
    private RoundRectView llHolderRecyclerView;

    public String pathSelected;
    private Photo photo1;

    public PhotoAdapter photoAdapter;
    private ArrayList<String> photos = null;

    public Photo photoselected;

    public int positionSelected = 0;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerPhoto;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private ArrayList<String> sendPhotos = new ArrayList<>();
    private Uri uriSelected = null;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_picked_images);

        addControls();
        addRecyclerView();
        addPhoto();


        findViewById(R.id.back).setOnClickListener(view -> {
            onBackPressed();
        });


        LinearLayout adContainer = findViewById(R.id.banner_container);
        //MAX + Fb bidding Ads
        AdManager.initAD(PickedImagesActivity.this);
        AdManager.BannerAd(PickedImagesActivity.this, adContainer, getResources().getColor(R.color.bg_color));
        AdManager.LoadInterstitalAd(PickedImagesActivity.this);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PickedImagesActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void addRecyclerView() {
        this.recyclerPhoto = findViewById(R.id.recyclerPhoto);
        RecyclerViewDragDropManager recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        this.dragMgr = recyclerViewDragDropManager;
        recyclerViewDragDropManager.setInitiateOnMove(false);
        this.dragMgr.setInitiateOnLongPress(true);
        this.recyclerPhoto.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        PhotoAdapter photoAdapter2 = new PhotoAdapter(this.listPhoto, this, new ItemClickListener() {
            public void onItemClick(View view, int i) {
                PickedImagesActivity.this.positionSelected = i;
                PickedImagesActivity selectedPhotoActivity = PickedImagesActivity.this;
                selectedPhotoActivity.photoselected = (Photo) selectedPhotoActivity.listPhoto.get(i);
                PickedImagesActivity selectedPhotoActivity2 = PickedImagesActivity.this;
                selectedPhotoActivity2.pathSelected = selectedPhotoActivity2.photoselected.paths;
                Glide.with(PickedImagesActivity.this).load(PickedImagesActivity.this.photoselected.paths).into(PickedImagesActivity.this.imgPhoto);
            }

            public void onItemDeleteClick(View view, int i) {
                PickedImagesActivity.this.listPhoto.remove(i);
                PickedImagesActivity.this.photoAdapter.notifyDataSetChanged();
            }
        });
        this.photoAdapter = photoAdapter2;
        photoAdapter2.setHasStableIds(true);
        this.recyclerPhoto.setAdapter(this.dragMgr.createWrappedAdapter(this.photoAdapter));
        this.dragMgr.attachRecyclerView(this.recyclerPhoto);
    }

    private void addControls() {
        this.llHolderRecyclerView = findViewById(R.id.llRecyclerView);
        this.btnEditPhoto = findViewById(R.id.btnEditPhoto);
        this.btnMovie = findViewById(R.id.movie_add_float);
        this.btnAddMore = findViewById(R.id.photo_add_float);
        this.btnAddPhoto = findViewById(R.id.movie_add);
        this.imgPhoto = findViewById(R.id.imageViewPhoto);
        this.btnAddMore.setOnClickListener(this);
        this.btnAddPhoto.setOnClickListener(this);
        this.btnEditPhoto.setOnClickListener(this);
        this.btnMovie.setOnClickListener(this);
    }


    @SuppressLint("RestrictedApi")
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && intent != null && i == ImagePickerActivity.PICKER_REQUEST_CODE) {
            this.selectedPhotos.clear();
            ArrayList<String> arrayList = intent.getExtras().getStringArrayList(ImagePickerActivity.KEY_DATA_RESULT);//(ArrayList) Matisse.obtainPathResult(intent);

            this.selectedPhotos = arrayList;
            if (arrayList != null && !arrayList.isEmpty()) {
                if (intent != null) {
                    this.btnAddPhoto.setVisibility(View.GONE);
                    this.llHolderRecyclerView.setVisibility(View.VISIBLE);
                    this.btnAddMore.setVisibility(View.VISIBLE);
                    this.btnEditPhoto.setVisibility(View.VISIBLE);
                }
                for (int i3 = 0; i3 < this.selectedPhotos.size(); i3++) {
                    this.listPhoto.add(new Photo((long) i3, this.selectedPhotos.get(i3)));
                }
                this.photo1 = this.listPhoto.get(0);
                this.uriSelected = Uri.fromFile(new File(this.photo1.paths));
                Glide.with(this).load(this.uriSelected).into(this.imgPhoto);
                this.photoAdapter.notifyDataSetChanged();
            }
        }
        if (i == 113 && i2 == 115) {
            this.photos = intent.getStringArrayListExtra("AFTER");
            this.selectedPhotos.clear();
            this.sendPhotos.clear();
            this.listPhoto.clear();
            ArrayList<String> arrayList2 = this.photos;
            if (arrayList2 != null) {
                this.selectedPhotos.addAll(arrayList2);
                for (int i4 = 0; i4 < this.selectedPhotos.size(); i4++) {
                    this.listPhoto.add(new Photo((long) i4, this.selectedPhotos.get(i4)));
                }
            }
            this.photo1 = this.listPhoto.get(0);
            this.uriSelected = Uri.fromFile(new File(this.photo1.paths));
            Glide.with((FragmentActivity) this).load(this.uriSelected).into(this.imgPhoto);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditPhoto:
                gotoPhotoEditor();
                return;

            case R.id.movie_add:
            case R.id.photo_add_float:
                addPhoto();
                return;

            case R.id.movie_add_float:
                this.photoAdapter.notifyDataSetChanged();
                new CreateMovieAsync().execute();
                return;

            default:
                return;
        }
    }

    private class CreateMovieAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog2 = new ProgressDialog(PickedImagesActivity.this);
            progressDialog2.setMessage(getString(R.string.com_facebook_loading));
            progressDialog2.setCanceledOnTouchOutside(false);
            progressDialog2.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sendPhotos.clear();
            for (int i = 0; i < listPhoto.size(); i++) {
                sendPhotos.add(listPhoto.get(i).paths);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog2.dismiss();
            if (sendPhotos.size() < 3) {
                Toast.makeText(PickedImagesActivity.this, getString(R.string.more_than_3_photos), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(PickedImagesActivity.this, VideoPreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("PHOTO", sendPhotos);
                intent.putExtras(bundle);
//                startActivity(intent);

                AdManager.adCounter = AdManager.adDisplayCounter;
                AdManager.showMaxInterstitial(PickedImagesActivity.this, intent, 0);

            }
        }
    }


    private void addPhoto() {
        Intent mIntent = new Intent(PickedImagesActivity.this, ImagePickerActivity.class);
        mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
        mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);
        startActivityForResult(mIntent, ImagePickerActivity.PICKER_REQUEST_CODE);
    }


    public void onPostResume() {
        super.onPostResume();
    }


    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


    public void onStop() {
        ProgressDialog progressDialog2 = this.progressDialog;
        if (progressDialog2 != null && progressDialog2.isShowing()) {
            this.progressDialog.dismiss();
        }
        super.onStop();
    }


    public void gotoPhotoEditor() {
        Intent intent = new Intent(PickedImagesActivity.this, EditPhotoActivity.class);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.listPhoto.size(); i++) {
            arrayList.add(this.listPhoto.get(i).paths);
        }
        intent.putStringArrayListExtra("PHOTO", arrayList);
        intent.putExtra("POSITION", this.positionSelected);
        if (this.positionSelected < arrayList.size()) {
            startActivityForResult(intent, 113);
        }
    }


}
