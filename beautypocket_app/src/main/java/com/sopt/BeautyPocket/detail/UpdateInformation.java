package com.sopt.BeautyPocket.detail;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.Session;
import com.sopt.BeautyPocket.CustomDialog;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.brandselect.BrandSelect;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.map.LocationService;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.model.WishList;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.register.RegisterActivity;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateInformation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** 네비게이션 드로어 **/
    LinearLayout btn_logout;
    LinearLayout nav_home;
    LinearLayout nav_wishlist;
    LinearLayout nav_notice;
    LinearLayout nav_customer;
    LinearLayout nav_setting;

    //위치알람버튼
    ImageView btn_alarm;

    CustomDialog photoDialog;
    private final int REQ_CODE_PICK_GALLERY = 0;
    private final int REQ_CODE_PICK_CAMERA = 1;
    private final int REQ_CODE_PICK_CROP = 2;

    /**
     * 네트워크
     **/
    NetworkService service;

    WishList itemData;
    EditText[] edittexts;
    ImageView img_register_click2;
    ImageView img_brand_inputimg2;
    int brandId;
    ImageView imageview;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    Uri data;
    String imgUrl = "";


    TextView tv_pocketcount;
    TextView tv_myname;
    CustomDialog dialog, updateDialog, backDialog;
    LinearLayout layout_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_update_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //네비게이션 레이아웃, 버튼
        btn_logout = (LinearLayout)findViewById(R.id.btn_logout);
        nav_home = (LinearLayout)findViewById(R.id.nav_home);
        nav_home.setOnClickListener(navClickListener);
        nav_wishlist = (LinearLayout)findViewById(R.id.nav_wishlist);
        nav_wishlist.setOnClickListener(navClickListener);
        nav_notice = (LinearLayout)findViewById(R.id.nav_notice);
        nav_notice.setOnClickListener(navClickListener);
        nav_customer = (LinearLayout)findViewById(R.id.nav_customer);
        nav_customer.setOnClickListener(navClickListener);
        nav_setting = (LinearLayout)findViewById(R.id.nav_setting);
        nav_setting.setOnClickListener(navClickListener);

        layout_click = (LinearLayout)findViewById(R.id.layout_click);
        layout_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittexts[0].getWindowToken(), 0);
            }
        });

        //위치알람
        btn_alarm = (ImageView) findViewById(R.id.btn_alarm);

        //위시갯수
        tv_pocketcount = (TextView) findViewById(R.id.tv_pocketcount);
        //네비게이션뷰
        tv_myname = (TextView) findViewById(R.id.tv_myname);
        tv_myname.setText(ApplicationController.user_name);
        tv_pocketcount.setText(String.valueOf(ApplicationController.count));

        //위치알람 여부 검사
        if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == false) {
            btn_alarm.setImageResource(R.drawable.off_ring);
        } else if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == true) {
            btn_alarm.setImageResource(R.drawable.on_ring);
        }

        //위치알람버튼
        btn_alarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == false) {
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    Log.d("위치알림켜버렸다~!", "안녕");
                    btn_alarm.setImageResource(R.drawable.on_ring);
                    startService(intent);
                } else if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == true) {
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    btn_alarm.setImageResource(R.drawable.off_ring);
                    Log.d("위치알림꺼버렸다~!", "잘가");
                    stopService(intent);
                }

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new CustomDialog(getApplicationContext(), logoutCancelListener, logoutOkListener);
                dialog.show();
                dialog.setTitle("로그아웃");
                dialog.setContent("로그아웃 하시겠습니까?");
            }
        });

        // ProgressDialog
        mProgressDialog = new ProgressDialog(UpdateInformation.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("등록 중...");
        mProgressDialog.setIndeterminate(true);

        //네트워크
        service = ApplicationController.getInstance().getNetworkService();

        img_register_click2 = (ImageView) findViewById(R.id.img_register_click2);
        img_register_click2.setOnClickListener(okClickListener);
        imageview = (ImageView) findViewById(R.id.img_register2);

        Intent intent = getIntent();
        itemData = (WishList) intent.getSerializableExtra("wishList");

        //브랜드 아이디 받아오기
        brandId = itemData.getBrand_id();

        ImageView imgview = (ImageView) findViewById(R.id.img_brand_inputimg2);
        imgview.setImageResource(ApplicationController.itemDataList3.get(brandId).getIcon());

        String name = itemData.getWish_title();
        String price = String.valueOf(itemData.getWish_price());
        String memo = itemData.getWish_memo();
        String img = itemData.getWish_image();

        edittexts = new EditText[3];

        edittexts[0] = (EditText) findViewById(R.id.et_productname2);
        edittexts[0].setText(name);
        edittexts[1] = (EditText) findViewById(R.id.et_price2);
        edittexts[1].setText(price);
        edittexts[2] = (EditText) findViewById(R.id.et_memo2);
        edittexts[2].setText(memo);


        Log.i("img", img);
        //img 등록
        Glide.with(getApplicationContext())
                .load(itemData.getWish_image())
                .into(imageview);

        if (itemData.getPro_id() != -1) {
            edittexts[0].setEnabled(false);
            edittexts[1].setEnabled(false);
            imageview.setEnabled(false);
        }


        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog = new CustomDialog(UpdateInformation.this, photoListener);
                photoDialog.show();
                photoDialog.cancelInVisible();
                photoDialog.setTitle("사진선택");
                photoDialog.setContent("사진을 선택해주세요.");
            }

        });


    }

    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doTakeAlbumAction();
            photoDialog.dismiss();
        }
    };

    private View.OnClickListener logoutCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private View.OnClickListener logoutOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Session.getCurrentSession().close();
            Intent intent = new Intent(UpdateInformation.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        }
    };

    /**
     * //    위시리스트 수정2
     *
     * @POST("/wishlists/modify") Call<Object> modifyWishList2(@Body WishList wishList);
     */

    ImageView.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateDialog = new CustomDialog(UpdateInformation.this, updateCancelListener, updateOkListener);
            updateDialog.show();
            updateDialog.setTitle("수정");
            updateDialog.setContent("수정하시겠습니까?");
        }
    };
    private View.OnClickListener updateCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateDialog.dismiss();
        }
    };

    private View.OnClickListener updateOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemData.getPro_id() == -1) {

                RequestBody wish_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(itemData.getWish_id()));
                RequestBody wish_title = RequestBody.create(MediaType.parse("multipart/form-data"), edittexts[0].getText().toString());
                RequestBody wish_price = RequestBody.create(MediaType.parse("multipart/form-data"), edittexts[1].getText().toString());
                RequestBody wish_memo = RequestBody.create(MediaType.parse("multipart/form-data"), edittexts[2].getText().toString());

                MultipartBody.Part body;

                if (imgUrl == "") {
                    body = null;
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    InputStream in = null;
                    try {
                        in = getContentResolver().openInputStream(data);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.i("YOONYOUNG 갤러리 data", data.toString());

                    Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                    // InputStream 으로부터 Bitmap 을 만들어 준다.
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos); // 압축 옵션( JPEG, PNG ) , 품질 설정 ( 0 - 100까지의 int형 ),
                    // 사진이 100mb이고 파라메타에 20 쓰면 5분의 1정도 줄이겠다는 뚯

                    RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpg"), baos.toByteArray());

                    File photo = new File(imgUrl); // 그저 블러온 파일의 이름을 알아내려고 사용..

                    // MultipartBody.Part is used to send also the actual file name
                    body = MultipartBody.Part.createFormData("wish_image", photo.getName(), photoBody); // 두번째 파라메타는 신경 안써도됨
                    //서버쪽에서 요청할때 타이틀,콘텐트,이미지를 보내달랫져
                }

                Call<Object> modifyWishList1 = service.modifyWishList1(body, wish_id, wish_title, wish_price, wish_memo);
                modifyWishList1.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            Log.d("YOONYOUNG", "response.code() : " + response.code());
                            finish();
                            mProgressDialog.dismiss();
                            intentCall();
                        } else
                            Log.i("왜안되냐고니ㅏㅇ릐ㅏㄷㅂ", response.message());
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        mProgressDialog.dismiss();
                    }
                });
            }
            else {
                WishList modifywish2 = new WishList(itemData.getWish_id(), edittexts[2].getText().toString());
                Call<Object> modifyWishList2 = service.modifyWishList2(modifywish2);
                modifyWishList2.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {

                            finish();
                            mProgressDialog.dismiss();
                            intentCall();
                        } else {

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                        mProgressDialog.dismiss();
                    }
                });
            }
            updateDialog.dismiss();
        }
    };

    public void intentCall() {
        Intent intent = new Intent(UpdateInformation.this, RegisterComplete.class);
        intent.putExtra("wish_id", itemData.getWish_id());
        intent.putExtra("brand_id", itemData.getBrand_id());
        startActivity(intent);
    }


    public void doTakeAlbumAction() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQ_CODE_PICK_GALLERY);
    }

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempImageFile()));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        } else if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
            // 갤러리의 경우 곧바로 data 에 uri가 넘어옴.
            Uri uri = data.getData();
            this.data = data.getData();
            String name_Str = getImageNameToUri(data.getData());
            Log.d("name_str", name_Str);
            copyUriToFile(uri, getTempImageFile());
            cropImage();
            doFinalProcess();
        } else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
            // 카메라의 경우 file 로 결과물이 돌아옴.
            // 카메라 회전 보정.
            cropImage();
            doFinalProcess();
        } else if (requestCode == REQ_CODE_PICK_CROP && resultCode == Activity.RESULT_OK) {
            // crop 한 결과는 file로 돌아옴.
            doFinalProcess();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        imgUrl = imgPath;
        Log.d("imgUrl", imgUrl);

        return imgName;
    }

    private File getTempImageFile() {
        File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/temp/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, "tempimage.jpg");
        this.data = Uri.fromFile(file);
        return file;
    }

    private void copyUriToFile(Uri srcUri, File target) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            // 스트림 생성
            inputStream = (FileInputStream) getContentResolver().openInputStream(srcUri);
            outputStream = new FileOutputStream(target);

            // 채널 생성
            fcin = inputStream.getChannel();
            fcout = outputStream.getChannel();

            // 채널을 통한 스트림 전송
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fcout.close();
            } catch (IOException ioe) {
            }
            try {
                fcin.close();
            } catch (IOException ioe) {
            }
            try {
                outputStream.close();
            } catch (IOException ioe) {
            }
            try {
                inputStream.close();
            } catch (IOException ioe) {
            }
        }
    }

    private void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> cropToolLists = getPackageManager().queryIntentActivities(intent, 0);
        int size = cropToolLists.size();
        if (size == 0) {
            // crop 을 처리할 앱이 없음. 곧바로 처리.
            doFinalProcess();
        } else {
            intent.setData(Uri.fromFile(getTempImageFile()));
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("output", Uri.fromFile(getTempImageFile()));
            Log.i("YOONYOUNG camera", this.data.toString());
            Intent i = new Intent(intent);
            ResolveInfo res = cropToolLists.get(0);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, REQ_CODE_PICK_CROP);
        }
    }

    private void doFinalProcess() {
        // sample size 를 적용하여 bitmap load.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        int longSide = Math.max(width, height);
        int sampleSize = 1;
        if (longSide > 500) {
            sampleSize = longSide / 500;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPurgeable = true;
        options.inDither = false;

        Bitmap bitmap = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath(), options);

        // image boundary size 에 맞도록 이미지 축소.
        if (bitmap.getWidth() > bitmap.getHeight()) {
            if (bitmap.getWidth() > 500) {
                bitmap = resizeBitmapWithWidth(bitmap, 500);
            }
        } else {
            if (bitmap.getHeight() > 500) {
                bitmap = resizeBitmapWithHeight(bitmap, 500);
            }
        }

        // 결과 file 을 얻어갈 수 있는 메서드 제공.
        saveBitmapToFile(bitmap);

        // show image on ImageView
        Bitmap bm = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath());
        ((ImageView) findViewById(R.id.img_register2)).setImageBitmap(bm);
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        File target = getTempImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(target, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap resizeBitmapWithHeight(Bitmap source, int wantedHeight) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedHeight * 1f / height;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    private Bitmap resizeBitmapWithWidth(Bitmap source, int wantedWidth) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedWidth * 1f / width;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    LinearLayout.OnClickListener navClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.nav_home :
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.nav_wishlist :
                    Intent intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.nav_notice :
                    Intent intent3 = new Intent(getApplicationContext(), Notice.class);
                    startActivity(intent3);
                    finish();
                    break;
                case R.id.nav_customer :
                    Intent intent4 = new Intent(getApplicationContext(), GogagCenter.class);
                    startActivity(intent4);
                    finish();
                    break;
                case R.id.nav_setting :
                    Intent intent5 = new Intent(getApplicationContext(), MoreActivity.class);
                    startActivity(intent5);
                    finish();
                    break;
            }
        }
    };

    public Boolean isServiceRunning(String serviceName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backDialog = new CustomDialog(UpdateInformation.this, backCancelListener,backOkListener);
            backDialog.show();
            backDialog.setTitle("수정취소");
            backDialog.setContent("수정을 취소하시겠습니까?");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(getApplicationContext(), BrandSelect.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private View.OnClickListener backCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            backDialog.dismiss();
        }
    };

    private View.OnClickListener backOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            backDialog.dismiss();
        }
    };

    public void top_click11(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home11:
                Intent top_intent1 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish11:
                Intent top_intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall11:
                ApplicationController.mapAll = true;
                Intent top_intent3 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar11:
                Intent top_intent4 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent4);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more11:
                Intent top_intent5 = new Intent(getApplicationContext(), MoreActivity.class);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent5);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }
}
