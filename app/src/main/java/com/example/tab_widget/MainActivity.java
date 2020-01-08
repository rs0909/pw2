package com.example.tab_widget;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    //dialog in tab2
    View dialogView;
    View deleteDialogView;
    EditText dlgEdtName;

    private final int PERMISSIONS_READ_CONTACTS = 1000;
    private final int PERMISSIONS_READ_EXTERNAL_STORAGE = 1001;
    private final int PERMISSIONS_ACCESS_MEDIA_LOCATION = 1002;
    private boolean isPermission = false;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mLayoutManager;

    Button button1;
    Button button2;
    Button buttoneddit;

    ArrayList<Item> items = new ArrayList<>();
    private InputMethodManager imm;

    private TextView tmp_name;
    private TextView tmp_phone;
    private ContactListAdapter adapter;
    private ArrayList<ContactItem> contactitems = null;
    private ListView listView;
    private byte[] byteArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callPermission();

//        Intent facebook_intent = new Intent(this, LoginActivity.class);
//        startActivity(facebook_intent);


        Resources res = getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(R.mipmap.dog10) +
                '/' + res.getResourceTypeName(R.mipmap.dog10) + '/' + res.getResourceEntryName(R.mipmap.dog10));//??????????????????????????
        items.add(new Item(uri, "강아지"));
        Uri uri1 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(R.mipmap.dog11) +
                '/' + res.getResourceTypeName(R.mipmap.dog11) + '/' + res.getResourceEntryName(R.mipmap.dog11));//??????????????????????????
        items.add(new Item(uri1, "강아지"));
        Uri uri2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(R.mipmap.dog12) +
                '/' + res.getResourceTypeName(R.mipmap.dog12) + '/' + res.getResourceEntryName(R.mipmap.dog12));//??????????????????????????
        items.add(new Item(uri2, "강아지"));
        Uri uri3 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(R.mipmap.lion1) +
                '/' + res.getResourceTypeName(R.mipmap.lion1) + '/' + res.getResourceEntryName(R.mipmap.lion1));//??????????????????????????
        items.add(new Item(uri3, "사자"));
        Uri uri4 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(R.mipmap.cat1) +
                '/' + res.getResourceTypeName(R.mipmap.cat1) + '/' + res.getResourceEntryName(R.mipmap.cat1));//??????????????????????????
        items.add(new Item(uri4, "고양이"));
        Uri uri5 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(R.mipmap.cat2) +
                '/' + res.getResourceTypeName(R.mipmap.cat2) + '/' + res.getResourceEntryName(R.mipmap.cat2));//??????????????????????????
        items.add(new Item(uri5, "고양이"));


        TabHost tabHost1 = findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;//setup()함수를 호출하지 않으면 TabWidget이 정상적으로 표시되지 않는다

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.content1) ;
        ts1.setIndicator("Contacts") ;
        tabHost1.addTab(ts1)  ;

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.content2) ;
        ts2.setIndicator("Gallery") ;
        tabHost1.addTab(ts2) ;

        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3") ;
        ts3.setContent(R.id.content3) ;
        ts3.setIndicator("Diary") ;
        tabHost1.addTab(ts3) ;



        ////////////////for tab1
        ContactItem contactItem = new ContactItem(this);
        if (contactitems == null) {
            contactitems = contactItem.getContactList();

            adapter = new ContactListAdapter(this, contactitems);

            listView = findViewById(R.id.listview1);
            listView.setAdapter(adapter);
        }
        //for search

        EditText editTextFilter = (EditText)findViewById(R.id.editTextFilter) ;
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                if (filterText.length() > 0) {
                    listView.setFilterText(filterText) ;
                } else {
                    listView.clearTextFilter() ;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        }) ;

        //for add





        ///////////////////for tab2

        //button1
        button1 = findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        //button2
        button2 = findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long seed = System.nanoTime();
                Collections.shuffle(items, new Random(seed));
                mAdapter = new MyAdapter(items, getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(items, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        //for delete cardView
        Intent intent = getIntent();
        int deletePosition = intent.getIntExtra("position", 1);
        items.remove(deletePosition);

        mAdapter = new MyAdapter(items, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        /////////////////for tab3


        //키보드 올리기 내리기
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        ////////////Tab3
        //buttoneddit
        buttoneddit = findViewById(R.id.buttoneddit);

        buttoneddit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }


        });
    }

    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_READ_CONTACTS);

        } else {
            isPermission = true;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_EXTERNAL_STORAGE);
        }else{
            isPermission = true;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, PERMISSIONS_ACCESS_MEDIA_LOCATION);
        }else{
            isPermission = true;
        }
    }


    //연락처 추가화면
    public void OnClickHandler(View v){

        Intent intent = new Intent(MainActivity.this,subactivity.class);
        startActivityForResult(intent, Code.requestCode);
    }

    //버튼 누르면 갤러리로 가서 사진을 하나 선택 후 사진 편집으로 가기


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    Log.wtf("hi", "되나안되나");
                    uri = data.getData();
                    // 이미지 표시
                    Intent intent = new Intent(MainActivity.this, FaceActivity.class);
                    intent.putExtra("uri", uri.toString());
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }





        //new File(FileLib.getInstance().getFileDir(context),name+".png");

    /*public void edditbuttonClicked(View v){

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                try{
                    final Uri uri = data.getData();//이미지 표시
                }}
        }
        Intent intent = new Intent(MainActivity.this,FaceActivity.class);
        startActivityForResult(intent, Code.requestCode);
    }*/
    /*
    //when tab2 button is clicked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    final Uri uri = data.getData();
                    // 이미지 표시

                    //제목 입력 받음
                    dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog1, null);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("그림명 입력");
                    dlg.setView(dialogView);
                    dlgEdtName = (EditText) dialogView.findViewById(R.id.dlgEdt1);
                    dlg.setPositiveButton("이름 넣기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String tmp = dlgEdtName.getText().toString();
                            Item item = new Item(uri, tmp);
                            items.add(item);
                        }
                    });
                    dlg.show();

                    mAdapter = new MyAdapter(items, getApplicationContext());
                    mRecyclerView.setAdapter(mAdapter);
                    Log.d("태크", "" + items.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == Code.requestCode && resultCode == Code.resultCode) {
            Log.d("ahsdfhsdklfkl","되돌아오긴함");
            ContactItem tmp_contactItem = new ContactItem(this);
            tmp_contactItem.setUser_Name(data.getStringExtra("name"));
            tmp_contactItem.setUser_phNumber(data.getStringExtra("phone"));
            Log.d("ahsdfhsdklfkl",""+tmp_contactItem.getUser_Name());
            contactitems.add(tmp_contactItem);
            adapter = new ContactListAdapter(this, contactitems);
            listView.setAdapter(adapter);
        }
    } */
    /*
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_READ_CONTACTS);

        } else {
            isPermission = true;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_EXTERNAL_STORAGE);
        }else{
            isPermission = true;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, PERMISSIONS_ACCESS_MEDIA_LOCATION);
        }else{
            isPermission = true;
        }
    }*/

}