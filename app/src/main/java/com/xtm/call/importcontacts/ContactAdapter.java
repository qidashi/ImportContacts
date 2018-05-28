package com.xtm.call.importcontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Function:
 * Created by TianMing.Xiong on 18-5-26.
 */

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private List<Contact> contacts;
    private  LayoutInflater inflater;
    private Map<Contact,Boolean> mSelectedItemsIds ;

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        inflater = LayoutInflater.from(context);
        mSelectedItemsIds = new HashMap<>();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_item, parent, false);
            viewHolder.checkedTextView = convertView.findViewById(R.id.check_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contacts.get(position);
        viewHolder.checkedTextView.setText(""+contact);
        viewHolder.checkedTextView.setChecked(contact.isCheck);
        mSelectedItemsIds.put(contacts.get(position),contact.isCheck());
        return convertView;
    }

    public void setItemChecked(int position, boolean isCheck) {
        if (isCheck) {
            mSelectedItemsIds.put(contacts.get(position), isCheck);
            contacts.get(position).setCheck(true);
        } else {
            mSelectedItemsIds.remove(position);
            contacts.get(position).setCheck(false);
        }
        notifyDataSetChanged();
    }

    public List<Contact>  getSelectContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.clear();
        if(mSelectedItemsIds!=null && mSelectedItemsIds.size()>0){
            Set<Map.Entry<Contact, Boolean>> entries = mSelectedItemsIds.entrySet();
            Iterator<Map.Entry<Contact, Boolean>> it = entries.iterator();
            while (it.hasNext()){
                Map.Entry<Contact, Boolean> e = it.next();
                if(e.getValue()){
                    contacts.add(e.getKey());
                }
            }
            return contacts;
        }
        return null;
    }
    class ViewHolder{
        CheckedTextView checkedTextView;
    }
}
