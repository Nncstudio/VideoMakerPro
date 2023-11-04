package com.video.imagepicker.activity;

import static com.video.photoeditor.utils.BitmapProcess.calculateInSampleSize;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.video.imagepicker.Constants;
import com.video.imagepicker.adapters.AlbumAdapter;
import com.video.imagepicker.adapters.ListAlbumAdapter;
import com.video.imagepicker.model.ImageModel;
import com.video.imagepicker.myinterface.OnAlbum;
import com.video.imagepicker.myinterface.OnListAlbum;
import com.video.maker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImagePickerActivity extends AppCompatActivity implements OnClickListener, OnAlbum, OnListAlbum {
    public static final String KEY_DATA_RESULT = "KEY_DATA_RESULT";
    public static final String KEY_LIMIT_MAX_IMAGE = "KEY_LIMIT_MAX_IMAGE";
    public static final String KEY_LIMIT_MIN_IMAGE = "KEY_LIMIT_MIN_IMAGE";
    public static final int PICKER_REQUEST_CODE = 1001;
    AlbumAdapter albumAdapter;
    ArrayList<ImageModel> dataAlbum = new ArrayList();
    ArrayList<ImageModel> dataListPhoto = new ArrayList();
    GridView gridViewAlbum;
    GridView gridViewListAlbum;
    HorizontalScrollView horizontalScrollView;
    LinearLayout layoutListItemSelect;
    public static int limitImageMax = 30;
    int limitImageMin = 2;
    ListAlbumAdapter listAlbumAdapter;
    public static ArrayList<ImageModel> listItemSelect;
    int pWHBtnDelete;
    int pWHItemSelected;
    ArrayList<String> pathList;
    AlertDialog sortDialog;
    TextView txtTotalImage;
    private Handler mHandler;
    private ProgressDialog pd;
    private int position = 0;
    private static final int READ_STORAGE_CODE = 1001;
    private static final int WRITE_STORAGE_CODE = 1002;


    public static ArrayList<String> saveFiles;

    ProgressBar progress;

    GetItemAlbum getItemAlbum;
    GetItemListAlbum getItemListAlbum;

    public void OnItemListAlbumClick(ImageModel item) {
        if (this.listItemSelect.size() < this.limitImageMax) {
            addItemSelect(item);
        } else {
            Toast.makeText(this, "Limit " + this.limitImageMax + " images", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetItemAlbum extends AsyncTask<Void, Void, String> {
        private GetItemAlbum() {
        }

        protected String doInBackground(Void... params) {
            Cursor cursor = ImagePickerActivity.this.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "bucket_display_name"}, null, null, null);
            if (cursor != null) {
                int column_index_data = cursor.getColumnIndexOrThrow("_data");
                while (cursor.moveToNext()) {
                    String pathFile = cursor.getString(column_index_data);
                    File file = new File(pathFile);
                    if (file.exists()) {
                        boolean check = ImagePickerActivity.this.checkFile(file);
                        if (!ImagePickerActivity.this.Check(file.getParent(), ImagePickerActivity.this.pathList) && check) {
                            ImagePickerActivity.this.pathList.add(file.getParent());
                            ImagePickerActivity.this.dataAlbum.add(new ImageModel(file.getParentFile().getName(), pathFile, file.getParent()));
                        }
                    }
                }
                cursor.close();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            ImagePickerActivity.this.gridViewAlbum.setAdapter(ImagePickerActivity.this.albumAdapter);
            progress.setVisibility(View.GONE);
        }

        protected void onPreExecute() {
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    private class GetItemListAlbum extends AsyncTask<Void, Void, String> {
        String pathAlbum;

        GetItemListAlbum(String pathAlbum) {
            this.pathAlbum = pathAlbum;
        }

        protected String doInBackground(Void... params) {
            File file = new File(this.pathAlbum);
            if (file.isDirectory()) {
                for (File fileTmp : file.listFiles()) {
                    if (fileTmp.exists()) {
                        boolean check = ImagePickerActivity.this.checkFile(fileTmp);
                        if (!fileTmp.isDirectory() && check) {
                            ImagePickerActivity.this.dataListPhoto.add(new ImageModel(fileTmp.getName(), fileTmp.getAbsolutePath(), fileTmp.getAbsolutePath()));
                            publishProgress(new Void[0]);
                        }
                    }
                }
            }
            return "";
        }

        protected void onPostExecute(String result) {
            progress.setVisibility(View.GONE);
//            try {
//                Collections.sort(ImagePickerActivity.this.dataListPhoto, new Comparator<ImageModel>() {
//                    @Override
//                    public int compare(ImageModel item, ImageModel t1) {
//                        File fileI = new File(item.getPathFolder());
//                        File fileJ = new File(t1.getPathFolder());
//                        if (fileI.lastModified() > fileJ.lastModified()) {
//                            return -1;
//                        }
//                        if (fileI.lastModified() < fileJ.lastModified()) {
//                            return 1;
//                        }
//                        return 0;
//                    }
//                });
//            } catch (Exception e) {
//            }
            ImagePickerActivity.this.listAlbumAdapter.notifyDataSetChanged();
        }

        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        protected void onProgressUpdate(Void... values) {
        }
    }

    ImageView btnBack;

    CardView btnDone;
    RelativeLayout header;
    LinearLayout footer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        progress = findViewById(R.id.loader);
        setTV();
        saveFiles = new ArrayList<String>();

        listItemSelect = new ArrayList<>();
        pathList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.limitImageMax = bundle.getInt(KEY_LIMIT_MAX_IMAGE, 30);
            this.limitImageMin = bundle.getInt(KEY_LIMIT_MIN_IMAGE, 2);
            if (this.limitImageMin > this.limitImageMax) {
                finish();
            }
            if (this.limitImageMin < 1) {
                finish();
            }
//            Log.e("ImagePickerActivity", "limitImageMin = " + this.limitImageMin);
//            Log.e("ImagePickerActivity", "limitImageMax = " + this.limitImageMax);
        }
        this.pWHItemSelected = (((int) ((((float) getDisplayInfo(this).heightPixels) / 100.0f) * 25.0f)) / 100) * 80;
        this.pWHBtnDelete = (this.pWHItemSelected / 100) * 25;

        btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });


        header = (RelativeLayout) findViewById(R.id.header);

        footer = (LinearLayout) findViewById(R.id.footer);


        this.gridViewListAlbum = (GridView) findViewById(R.id.gridViewListAlbum);
        this.txtTotalImage = (TextView) findViewById(R.id.txtTotalImage);

        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);

        this.layoutListItemSelect = (LinearLayout) findViewById(R.id.layoutListItemSelect);
        this.horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        this.horizontalScrollView.getLayoutParams().height = this.pWHItemSelected;
        this.gridViewAlbum = (GridView) findViewById(R.id.gridViewAlbum);


        pd = new ProgressDialog(ImagePickerActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };

//        try {
//            Collections.sort(this.dataAlbum, new Comparator<ImageModel>() {
//                @Override
//                public int compare(ImageModel lhs, ImageModel rhs) {
//                    return lhs.getName().compareToIgnoreCase(rhs.getName());
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        this.albumAdapter = new AlbumAdapter(this, R.layout.piclist_row_album, this.dataAlbum);
        this.albumAdapter.setOnItem(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES) && isPermissionGranted(Manifest.permission.READ_MEDIA_AUDIO)) {
                getItemAlbum = new GetItemAlbum();
                getItemAlbum.execute(new Void[0]);
            } else {
                requestPermission(Manifest.permission.READ_MEDIA_IMAGES, READ_STORAGE_CODE);
                requestPermission(Manifest.permission.READ_MEDIA_AUDIO, READ_STORAGE_CODE);
            }
        }
        else {
            if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getItemAlbum = new GetItemAlbum();
                getItemAlbum.execute(new Void[0]);
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE);
            }
        }


        updateTxtTotalImage();


    }

    public void setTV() {
//        LinearLayout adContainer = findViewById(R.id.banner_container);
//
//        if (!AdManager.isloadFbMAXAd) {
//            //admob
//            AdManager.initAd(ImagePickerActivity.this);
//            AdManager.loadBannerAd(ImagePickerActivity.this, adContainer);
//            AdManager.loadInterAd(ImagePickerActivity.this);
//        } else {
//            //MAX + Fb banner Ads
//            AdManager.initMAX(ImagePickerActivity.this);
//            AdManager.maxBannerBg(ImagePickerActivity.this, adContainer, getColor(R.color.bottom_bg_color));
//            AdManager.maxInterstital(ImagePickerActivity.this);
//        }
    }

    @Override
    public void onBackPressed() {
//        AdManager.adCounter++;
//        if (AdManager.adCounter == AdManager.adDisplayCounter) {
//            if (!AdManager.isloadFbMAXAd) {
//                AdManager.showInterAd(ImagePickerActivity.this, null, 0);
//            } else {
//                AdManager.showMaxInterstitial(ImagePickerActivity.this, null, 0);
//            }
//        } else {

        if (getItemListAlbum != null && getItemListAlbum.getStatus() == AsyncTask.Status.RUNNING) {
            getItemListAlbum.cancel(true);
        }

        if (getItemAlbum != null && getItemAlbum.getStatus() == AsyncTask.Status.RUNNING) {
            getItemAlbum.cancel(true);
        }
            if (this.gridViewListAlbum.getVisibility() == View.VISIBLE) {
                this.dataListPhoto.clear();
                this.listAlbumAdapter.notifyDataSetChanged();
                this.gridViewListAlbum.setVisibility(View.GONE);
                return;
            }
            super.onBackPressed();
//        }
    }




    private boolean isPermissionGranted(String permission) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }


    //Requesting permission
    private void requestPermission(String permission, int code) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{permission}, code);
    }


    private boolean Check(String a, ArrayList<String> list) {
        if (!list.isEmpty() && list.contains(a)) {
            return true;
        }
        return false;
    }


    void addItemSelect(final ImageModel item) {
        item.setId(this.listItemSelect.size());
        this.listItemSelect.add(item);
        updateTxtTotalImage();
        final View viewItemSelected = View.inflate(this, R.layout.piclist_item_selected, null);
        FrameLayout layoutRoot = (FrameLayout) viewItemSelected.findViewById(R.id.layoutRoot);
        ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
        ImageView btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);

        imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with((Activity) this).load(item.getPathFile()).placeholder(R.drawable.piclist_icon_default).into(imageItem);


        btnDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ImagePickerActivity.this.layoutListItemSelect.removeView(viewItemSelected);
                ImagePickerActivity.this.listItemSelect.remove(item);
                ImagePickerActivity.this.updateTxtTotalImage();
                listAlbumAdapter.notifyDataSetChanged();

            }
        });

        ImagePickerActivity.this.layoutListItemSelect.addView(viewItemSelected);
        viewItemSelected.startAnimation(AnimationUtils.loadAnimation(ImagePickerActivity.this, R.anim.abc_fade_in));
        ImagePickerActivity.this.sendScroll();

    }


    void updateTxtTotalImage() {
        this.txtTotalImage.setText(String.format(getResources().getString(R.string.text_images), new Object[]{Integer.valueOf(this.listItemSelect.size())}));
    }

    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(() -> handler.post(() -> ImagePickerActivity.this.horizontalScrollView.fullScroll(66))).start();
    }

    void showListAlbum(String pathAlbum) {
        this.listAlbumAdapter = new ListAlbumAdapter(this, R.layout.piclist_row_list_album, this.dataListPhoto);
        this.listAlbumAdapter.setOnListAlbum(this);
        this.gridViewListAlbum.setAdapter(this.listAlbumAdapter);
        this.gridViewListAlbum.setVisibility(View.VISIBLE);
        getItemListAlbum = new GetItemListAlbum(pathAlbum);
        getItemListAlbum.execute(new Void[0]);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnDone) {
            ArrayList<String> listString = getListString(this.listItemSelect);
            if (listString.size() >= this.limitImageMin) {
                // Show a progress dialog to indicate image processing
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Processing Images...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Perform image processing on a background thread
                new Thread(() -> {
                    ArrayList<Bitmap> resizedImages = new ArrayList<>();
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    int targetWidth = displayMetrics.widthPixels;
                    int targetHeight = displayMetrics.heightPixels;

                    for (String imagePath : listString) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(imagePath, options);

                        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);

                        options.inJustDecodeBounds = false;
                        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath, options);

                        resizedImages.add(originalBitmap);
                    }

                    // Dismiss the progress dialog on the UI thread
                    runOnUiThread(() -> {
                        progressDialog.dismiss();

                        // Perform any further actions after image processing here
                        done(resizedImages);
                    });
                }).start();
            } else {
                Toast.makeText(this, "Please select at least " + this.limitImageMin + " images", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void done(ArrayList<Bitmap> bitmapList) {
        ArrayList<String> imagePathList = new ArrayList<>();

        for (Bitmap bitmap : bitmapList) {
            // Convert Bitmap to a file path
            String imagePath = saveBitmapToFile(bitmap, "image_" + System.currentTimeMillis() + ".jpg");
            imagePathList.add(imagePath);
        }

        Intent mIntent = new Intent();
        setResult(Activity.RESULT_OK, mIntent);
        mIntent.putStringArrayListExtra(KEY_DATA_RESULT, imagePathList);
        finish();
    }

    private String saveBitmapToFile(Bitmap bitmap, String filePath) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filePath);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Compress the bitmap as a JPEG with 100% quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    ArrayList<String> getListString(ArrayList<ImageModel> listItemSelect) {
        ArrayList<String> listString = new ArrayList();
        for (int i = 0; i < listItemSelect.size(); i++) {
            listString.add(((ImageModel) listItemSelect.get(i)).getPathFile());
        }
        return listString;
    }

    private boolean checkFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.isFile()) {
            return true;
        }
        String name = file.getName();
        if (name.startsWith(".") || file.length() == 0) {
            return false;
        }
        boolean isCheck = false;
        for (int k = 0; k < Constants.FORMAT_IMAGE.size(); k++) {
            if (name.endsWith((String) Constants.FORMAT_IMAGE.get(k))) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    public static DisplayMetrics getDisplayInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }


    public void OnItemAlbumClick(int position) {
        dataListPhoto.clear();
        showListAlbum(((ImageModel) this.dataAlbum.get(position)).getPathFolder());
    }


}
