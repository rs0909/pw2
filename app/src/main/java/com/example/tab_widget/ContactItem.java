package com.example.tab_widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ContactItem implements Serializable {
    private String user_phNumber, user_Name;
    private long photo_id = 0, person_id = 0;
    private int id;
    Context context;

    public ContactItem(Context context){this.context = context;
    }
    public long getPhoto_id(){
        return photo_id;
    }
    public long getPerson_id(){
        return person_id;
    }
    public void setPhoto_id(long id){
        this.photo_id = id;
    }
    public void setPerson_id(long id){
        this.person_id = id;
    }
    public String getUser_phNumber(){
        return user_phNumber;
    }
    public  String getUser_Name(){
        return user_Name;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    public void setUser_phNumber(String string){
        this.user_phNumber = string;
    }
    public void setUser_Name(String string){
        this.user_Name = string;
    }
    @Override
    public String toString(){
        return this.user_phNumber;
    }
    @Override
    public int hashCode(){
        return getPhNumberChanged().hashCode();
    }
    public String getPhNumberChanged(){
        return user_phNumber.replace("-", "");
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof ContactItem)
            return getPhNumberChanged().equals((((ContactItem) o).getPhNumberChanged()));
        return false;
    }



    public ArrayList<ContactItem> getContactList(){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
        };
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                " COLLATE LOCALIZED ASC";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, selectionArgs, sortOrder);
        LinkedHashSet<ContactItem> hashlist = new LinkedHashSet<>();
        if (cursor.moveToFirst()){
            do{
                long photo_id = cursor.getLong(2);
                long person_id = cursor.getLong(3);
                ContactItem contactItem = new ContactItem(context);
                contactItem.setUser_phNumber(cursor.getString(0));
                contactItem.setUser_Name(cursor.getString(1));
                contactItem.setPhoto_id((photo_id));
                contactItem.setPerson_id((person_id));

                hashlist.add(contactItem);
            }while (cursor.moveToNext());
        }
        ArrayList<ContactItem> contactItems = new ArrayList<>(hashlist);
        for (int i = 0; i < contactItems.size(); i++){
            contactItems.get(i).setId(i);
        }
        return contactItems;

    }
}

