package com.example.noelroy.contactmanager;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int EDIT = 0;
    private static final int DELETE = 1;

    EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    Button submitBtn;
    List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;
    ImageView contactImageView;
    Uri imageUri=Uri.parse("android.resource://com.example.noelroy.contactmanager/drawable/no_user.png");  //to represent a drawable item as Uri
    DatabaseHandler dbHandler;
    ArrayAdapter<Contact> listAdapter;
    int longClickSelectedContactIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactListView = (ListView) findViewById(R.id.listView);
        contactImageView = (ImageView) findViewById(R.id.contactImageView);
        dbHandler = new DatabaseHandler(getApplicationContext());

        /*Setting onClickListener on the contact list to produce a action menu */
        registerForContextMenu(contactListView);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickSelectedContactIndex = position;
                return false;
            }
        });

        /*Setting up the TabHost*/
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        /*Setting first tab*/
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreate);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);
        /*Setting second tab*/
        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        nameTxt = (EditText) findViewById(R.id.name);
        phoneTxt = (EditText) findViewById(R.id.phone);
        emailTxt = (EditText) findViewById(R.id.email);
        addressTxt = (EditText) findViewById(R.id.address);

        submitBtn = (Button) findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()), imageUri);
                if (!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    Contacts.add(contact);
                    listAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Contact added successfully", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Contact exits. Use another name", Toast.LENGTH_SHORT).show();
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitBtn.setEnabled(String.valueOf(nameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent for choosing the image from gallery */
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select the image"), 1);
            }
        });
        /*
        *
        * Replacing this code with below one to prevent making another list
        *
        List<Contact> addableContact = dbHandler.getAllContacts();
        int contactCount = dbHandler.getContactsCount();
        for (int i = 0; i < contactCount ; i++ ) {
            Contacts.add(addableContact.get(i));
        }
        if (!addableContact.isEmpty()) {
            populateList();
        }
        */
        if (dbHandler.getContactsCount() != 0) {
            Contacts.addAll(dbHandler.getAllContacts());
        }
        populateList();
    }

    private boolean contactExists(Contact contact) {
        String name = contact.get_name();
        int contactCount = Contacts.size();
        for (int i = 0 ; i < contactCount ; i++) {
            if (name.compareToIgnoreCase(Contacts.get(i).get_name()) == 0) {
                return true;
            }
        }
        return false;
    }

    /*onActivityResult of image selection Intent*/
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                contactImageView.setImageURI(imageUri);
            }
        }
    }

    /*function to populate List into an array*/
    private void populateList() {
        listAdapter = new ContactListAdapter();
        contactListView.setAdapter(listAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderIcon(R.drawable.no_user);
        menu.setHeaderTitle("Contacts Settings");
        menu.add(Menu.NONE, EDIT, Menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, DELETE, Menu.NONE, "Delete Contact");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case EDIT:

                break;

            case DELETE:
                dbHandler.deleteContact(Contacts.get(longClickSelectedContactIndex));
                Contacts.remove(longClickSelectedContactIndex);
                listAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }


    /*Adapter class */
    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super(MainActivity.this, R.layout.listview_item, Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.get_name());
            TextView phone = (TextView) view.findViewById(R.id.contactPhone);
            phone.setText(currentContact.get_phone());
            TextView email = (TextView) view.findViewById(R.id.contactEmail);
            email.setText(currentContact.get_email());
            TextView address = (TextView) view.findViewById(R.id.contactAddress);
            address.setText(currentContact.get_address());
            ImageView contactImage = (ImageView) view.findViewById(R.id.contactImage);
            contactImage.setImageURI(currentContact.get_imageUri());

            return view;
        }
    }
}
