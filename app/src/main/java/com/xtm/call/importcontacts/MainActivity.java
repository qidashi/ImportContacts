package com.xtm.call.importcontacts;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvFilePath;
    private ListView lvContact;
    private List<Contact> contacts;
    private LinearLayout progressbar;
    private List<Contact> csv_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    // 自定义文件选择器选择文件按钮
    public void chooseFile(View view) {
        FilePickerDialog filePickerDialog = new FilePickerDialog(this);
        filePickerDialog.setOnFileSelectListener(new FilePickerDialog.OnFileSelectListener() {
            @Override
            public void onFileSelect(File file) {
                if (file != null && file.exists()) {
                    showImportContacts(file);
                } else {
                    Toast.makeText(MainActivity.this, "文件无效！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        filePickerDialog.show();
    }
    // 导入到通讯录按钮
    public void importContacts(View view) {
        setUpImportContacts();
    }

    private void initView() {
        tvFilePath = findViewById(R.id.tv_file_path);
        lvContact = findViewById(R.id.lv_contact);
        progressbar =  findViewById(R.id.progressbar);
    }

    private void hideProcess() {
        progressbar.setVisibility(View.GONE);
    }

    private void showProcess() {
        progressbar.setVisibility(View.VISIBLE);
    }

    private void showContacts(List<Contact> contacts) {
        final ContactAdapter contactAdapter = new ContactAdapter(this, contacts);
        lvContact.setAdapter(contactAdapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactAdapter.setItemChecked(position, !contactAdapter.getItem(position).isCheck);
            }
        });

    }

    private void setUpImportContacts() {
        ContactAdapter adapter = (ContactAdapter) lvContact.getAdapter();
        if (null != adapter) {
            contacts = adapter.getSelectContacts();
            try {
                batchAddContact(contacts);
                Toast.makeText(this, "导入联系人完成！", Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "导入失败！", Toast.LENGTH_SHORT).show();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
                Toast.makeText(this, "导入失败！", Toast.LENGTH_SHORT).show();
            }
            if (BuildConfig.DEBUG) Log.d(TAG, "contacts:" + contacts);
        } else {
            Toast.makeText(this, "导入失败！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 批量添加联系人
     * @param lists
     * @throws OperationApplicationException
     * @throws RemoteException
     */
    public void batchAddContact(List<Contact> lists)
            throws RemoteException, OperationApplicationException {
        if (BuildConfig.DEBUG) Log.d(TAG, "[GlobalVariables->]BatchAddContact begin");
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex;
        for (Contact contact : lists) {
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true)
                    .build());

            // 添加姓名
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                    .withYieldAllowed(true)
                    .build());
            // 添加号码
            ops.add(ContentProviderOperation
                    .newInsert(
                            ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhone1())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "")
                    .withYieldAllowed(true)
                    .build());
        }
        if (ops != null) {
            // 真正添加
            ContentProviderResult[] results = getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);
            for (ContentProviderResult result : results) {
                if (BuildConfig.DEBUG) Log.d(TAG, "[GlobalVariables->]BatchAddContact "
                        + result.uri.toString());
            }
        }
    }

    private void showImportContacts(File file) {
        String realPath = file.getAbsolutePath();
        if (realPath.endsWith(".csv")) {
            showProcess();
            tvFilePath.setText("" + realPath);
            String fileEncode= CharSetUtils.detect(realPath);
            csv_contacts = ExcelUtils.parseContactsByCsv(file,fileEncode);
            showContacts(csv_contacts);
            hideProcess();
        } else if(realPath.endsWith(".xlsx")) {
            showProcess();
            tvFilePath.setText("" + realPath);
            File file1 = new File(realPath);
            List<Contact> xlsx_contacts = ExcelUtils.parseContactsByXlsx(file1);
            if (BuildConfig.DEBUG) Log.d(TAG, "contacts:" + xlsx_contacts);
            showContacts(xlsx_contacts);
            hideProcess();
        }else {
            Toast.makeText(this, "请导入‘csv’或'xlsx'文件！", Toast.LENGTH_SHORT).show();
        }


    }
}
