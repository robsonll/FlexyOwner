package com.example.flexyowner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.flexyowner.ControllerClasses.BusinessOwnerController;
import com.example.flexyowner.ModelClasses.Business;
import com.example.flexyowner.ModelClasses.BusinessConfiguration;
import com.example.flexyowner.ModelClasses.Message;
import com.example.flexyowner.ModelClasses.PaymentMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class HomeActivity extends Fragment {

/*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    private TextView textViewTitle;

    private FirebaseFirestore db;
    private ListView listViewPaymentMethod;
    private SelectPaymentMethodAdapter selectPaymentMethodAdapter;
    private List<PaymentMethod> paymentMethodList;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private Button btnChoose;
    private ImageView imageView;

    private TextView msgWelcome;
    private TextView msgAboutUs;
    private TextView msgProducts;
    private TextView msgCheckout;

    private CheckBox chkShowAddOns;
    private CheckBox chkShowDrinks;

    private String bgImage;


/*
    public HomeActivity() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeActivity newInstance(String param1, String param2) {
        HomeActivity fragment = new HomeActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_activity, container, false);

        db = FirebaseFirestore.getInstance();

        textViewTitle = view.findViewById(R.id.textViewTitle);
        listViewPaymentMethod = view.findViewById(R.id.listViewPaymentMethods);
        listViewPaymentMethod.setAdapter(selectPaymentMethodAdapter);

        msgWelcome = view.findViewById(R.id.editTextWelcome);
        msgAboutUs = view.findViewById(R.id.editTextAboutMsg);
        msgProducts = view.findViewById(R.id.editTextProductsMsg);
        msgCheckout = view.findViewById(R.id.editTextCheckOutMsg);

        chkShowAddOns = view.findViewById(R.id.checkBoxShowAddOns);
        chkShowDrinks = view.findViewById(R.id.checkBoxShowDrinks);

        btnChoose = (Button) view.findViewById(R.id.btnChooseBgImage);
        imageView = (ImageView) view.findViewById(R.id.imgViewBgImage);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        Button buttonSaveBusinessConfiguration= view.findViewById(R.id.buttonSaveBusinessConfig);

        buttonSaveBusinessConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //fetchPaymentMethods(view);
        retrieveBusinessConfiguration();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchPaymentMethods();

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.conten, filePath);

                Context applicationContext = NavigationMainActivity.getContextOfApplication();
                //applicationContext.getContentResolver();

                ImageDecoder.Source source = ImageDecoder.createSource(applicationContext.getContentResolver(), filePath);
                Bitmap bitmap = ImageDecoder.decodeBitmap(source);

                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        FirebaseStorage storage;
        StorageReference storageReference;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    saveBusinessConfig(downloadUrl.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            saveBusinessConfig("");
        }
    }

    private void retrieveBusinessConfiguration(){

        BusinessOwnerController businessOwnerController = new BusinessOwnerController();
        Business business = businessOwnerController.retrieveCurrentBusiness(getContext());
        BusinessConfiguration businessConfiguration = business.getBusinessConfiguration();

        if(businessConfiguration != null){

            List<Message> listMessages = businessConfiguration.getMessageList();

            for(Message message : listMessages){

                switch (message.getType()){
                    case "msgWelcome":
                        msgWelcome.setText(message.getContent());
                        break;
                    case "msgAboutUs":
                        msgAboutUs.setText(message.getContent());
                        break;
                    case "msgProducts":
                        msgProducts.setText(message.getContent());
                        break;
                    case "msgCheckout":
                        msgCheckout.setText(message.getContent());
                        break;
                }

            }

            Glide.with(getContext()).load(business.getBusinessConfiguration().getBackgroundImage()).into(imageView);
            chkShowAddOns.setChecked(business.getBusinessConfiguration().getShowAddOns());
            chkShowDrinks.setChecked(business.getBusinessConfiguration().getShowDrinks());

            bgImage = business.getBusinessConfiguration().getBackgroundImage();

        }

    }

    private void fetchPaymentMethods()
    {

        paymentMethodList = new ArrayList<PaymentMethod>();

        db.collection("paymentmethods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PaymentMethod pm = new PaymentMethod(document.get("id").toString(), document.get("name").toString(), document.get("description").toString());
                                paymentMethodList.add(pm);
                            }

                            if (paymentMethodList.isEmpty())
                            {
                                Toast.makeText(getContext(), "No payment methods stored.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                selectPaymentMethodAdapter = new SelectPaymentMethodAdapter(getContext(), paymentMethodList);
                                listViewPaymentMethod.setAdapter(selectPaymentMethodAdapter);
                            }
                        } else {
                            Log.d("FIREBASE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void saveBusinessConfig(String imageURL) {

        BusinessConfiguration businessConfiguration = new BusinessConfiguration();
        List<Message> listMessages = new ArrayList<Message>();
        List<PaymentMethod> acceptedPaymentMethods = new ArrayList<PaymentMethod>();

        listMessages.add(new Message("msgWelcome",msgWelcome.getText().toString().trim()));
        listMessages.add(new Message("msgAboutUs",msgAboutUs.getText().toString().trim()));
        listMessages.add(new Message("msgProducts",msgProducts.getText().toString().trim()));
        listMessages.add(new Message("msgCheckout",msgCheckout.getText().toString().trim()));

        businessConfiguration.setMessageList(listMessages);

        View v;
        CheckBox ck;
        for (int i = 0; i < listViewPaymentMethod.getCount(); i++) {
            v = listViewPaymentMethod.getChildAt(i);
            ck = (CheckBox) v.findViewById(R.id.checkBoxPaymentMethod);
            if (ck.isChecked()) {
                acceptedPaymentMethods.add((PaymentMethod) listViewPaymentMethod.getAdapter().getItem(i));
            }
        }

        businessConfiguration.setShowAddOns(chkShowAddOns.isChecked());
        businessConfiguration.setShowDrinks(chkShowDrinks.isChecked());
        businessConfiguration.setAcceptedPaymentMethods(acceptedPaymentMethods);
        if(!imageURL.equals(""))
            businessConfiguration.setBackgroundImage(imageURL);
        else
            businessConfiguration.setBackgroundImage(bgImage);

        //Start HUD
        final KProgressHUD hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Saving Business Configuration")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        BusinessOwnerController businessOwnerController = new BusinessOwnerController();
        Business business = businessOwnerController.retrieveCurrentBusiness(getContext());


        DocumentReference productRef = db.collection("business").document(business.getId());

        productRef
                .update("businessConfiguration",businessConfiguration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Business Configuration Updated !",
                                Toast.LENGTH_SHORT).show();

                        hud.dismiss();
                        //finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error !",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }



}